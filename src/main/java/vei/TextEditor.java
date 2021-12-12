package vei;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import org.magicwerk.brownies.collections.primitive.CharGapList;
import vei.commands.Command;
import vei.commands.CommandRegistry;
import vei.plugin.VeiPluginKeyPress;
import vei.plugin.VeiPluginKeyPressHandler;
import vei.plugin.VeiPluginLoader;

import java.io.IOException;
import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "FieldMayBeFinal"})
public class TextEditor {
    private static final String NOT_A_COMMAND =
            "Not a valid editor command!";

    private final Key[] currentCommand = new Key[100];
    private final Map<String, Object> dataMap = new HashMap<>();
    private final Map<Character, VeiPosition> markerMap = new HashMap<>();
    int cursorRow = 1;
    int cursorCol = 1;
    private int currentCommandIndex = 0;
    private EditorMode mode = EditorMode.NORMAL;
    private CommandRegistry normalRegistry;
    private CommandRegistry insertRegistry;
    private CommandRegistry visualRegistry;
    private StringBuilder commandBuffer = new StringBuilder(100);
    private boolean shouldDisplayError = true;
    private LineList lines = new LineList() {{
        add(new CharGapList());
    }};
    private int startRow = 0;
    private int stopRow = 0;
    private final Screen screen;
    private final TextGraphics graphics;
    private final List<KeyPressHandler> keyPressHandlers = new ArrayList<>();
    private String modeString = "NORMAL";
    private final VeiPluginLoader pluginLoader;
    private boolean canRender = true;

    public TextEditor(Screen screen,
                      TextGraphics graphics,
                      VeiPluginLoader pluginLoader) {
        this.screen = screen;
        this.graphics = graphics;
        this.pluginLoader = pluginLoader;
        Arrays.fill(currentCommand, Key.NONE);
        normalRegistry = new CommandRegistry(CommandRegistry.DEFAULT_NORMAL_COMMANDS);
        insertRegistry = new CommandRegistry(CommandRegistry.DEFAULT_INSERT_COMMANDS);
        visualRegistry = new CommandRegistry(CommandRegistry.DEFAULT_VISUAL_COMMANDS);
    }

    public void preventRendering() {
        canRender = false;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public Map<Character, VeiPosition> getMarkerMap() {
        return markerMap;
    }

    public void setText(String text) {
        lines.clear();
        String[] lineStrings = text.split("\n");
        for (String str : lineStrings) {
            lines.add(new CharGapList(str));
        }
    }

    public int getLineCount() {
        return lines.size();
    }

    public void resetCommand() {
        commandBuffer.setLength(0);
        for (int i = 0; i < currentCommandIndex; i++) {
            currentCommand[i] = Key.NONE;
        }

        currentCommandIndex = 0;
    }

    public String[] getLineStrings() {
        return getLineStrings(
                startRow,
                stopRow
        );
    }

    private String[] getLineStrings(int firstIdx,
                                    int lastIdx) {
        int size = lastIdx - firstIdx;
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            int targetIdx = firstIdx + i;
            if (targetIdx >= lines.size()) {
                strings[i] = "";
            } else {
                strings[i] = lines.get(targetIdx).toString();
            }
        }
        return strings;
    }

    public String getModeString() {
        return modeString;
    }

    public void onKeyDown(VeiPluginKeyPress keyPress) {
        onKeyDown(
                keyPress.getKey(),
                keyPress.getCharacter()
        );

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getRelativeCursorRow() {
        return (cursorRow - 1) + startRow;
    }

    public void onKeyDown(Key key,
                          char character) {
        if (!canRender) canRender = true;
        boolean shouldAppend = false;
        switch (mode) {
            case NORMAL:
                if (key == Key.ENTER) {
                    executeCommand();
                } else {
                    currentCommand[currentCommandIndex] = key;
                    currentCommandIndex += 1;
                    if (!tryExactMatch(normalRegistry)) {
                        shouldAppend = true;
                    }
                }
                break;
            case INSERT:
                currentCommand[currentCommandIndex] = key;
                currentCommandIndex += 1;
                if (insertRegistry.anyMatches(
                        currentCommand,
                        currentCommandIndex
                )) {
                    if (!tryExactMatch(insertRegistry)) {
                        shouldAppend = true;
                    }
                } else {
                    CharGapList currentLine = lines.get(getRelativeCursorRow());
                    if (key == Key.ENTER) {
                        int lineLen = currentLine.length();
                        int startRemoveCol = cursorCol - 1;
                        int stopRemoveCol = Math.max(0, lineLen - cursorCol + 1);
                        final CharGapList newLine;
                        if (startRemoveCol >= lineLen) {
                            newLine = new CharGapList();
                        } else {
                            newLine = currentLine.getAll(
                                    startRemoveCol,
                                    stopRemoveCol
                            );
                            currentLine.remove(
                                    startRemoveCol,
                                    stopRemoveCol
                            );
                        }
                        lines.add(getRelativeCursorRow() + 1, newLine);
                        changeLine(1);
                        int newLen = newLine.length();
                        cursorCol = newLen + 1;
                        try {
                            screen.clear();
                            screen.refresh();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        for (int i = 0; i < commandBuffer.length() - 1; i++) {
                            char c = commandBuffer.charAt(i);
                            currentLine.add(cursorCol - 1, c);
                            changeCursorPosition(-1);
                        }
                        if (key == Key.BACKSPACE) {
                            if (isAfterOnlyWhitespace()) {
                                int lineLen = currentLine.length();
                                int startRemoveCol = cursorCol - 1;
                                int stopRemoveCol = Math.max(0, lineLen - cursorCol + 1);
                                if (getRelativeCursorRow() != 0) {
                                    CharGapList aboveLine = lines.get(getRelativeCursorRow() - 1);
                                    int previousLength = aboveLine.length();
                                    if (currentLine.length() > 0) {
                                        aboveLine.addAll(
                                                currentLine.getAll(
                                                        startRemoveCol,
                                                        stopRemoveCol
                                                )
                                        );
                                    }
                                    int size = stopRow - startRow;
                                    lines.remove(getRelativeCursorRow());
                                    cursorCol = previousLength + 1;
                                    changeLine(-1);
                                    try {
                                        screen.clear();
                                        screen.refresh();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (cursorCol - 2 >= 0) {
                                    if (cursorCol > getCurrentLineLength()) {
                                        cursorCol = getCurrentLineLength() + 1;
                                    }
                                    currentLine.remove(cursorCol - 2);
                                    changeCursorPosition(1);
                                }
                            }
                        } else {
                            if (cursorCol - 1 > currentLine.length()) {
                                currentLine.add(character);
                            } else {
                                currentLine.add(cursorCol - 1, character);
                            }
                            changeCursorPosition(-1);
                        }
                    }
                    resetCommand();
                }
                break;
            case VISUAL:
                break;
        }

        for (KeyPressHandler handler : keyPressHandlers) {
            handler.onKeyPress(
                    key,
                    character
            );
        }

        if (shouldAppend) {
            if (Character.isISOControl(character)) {
                commandBuffer.append(String.format("<%s>", key));
            } else {
                commandBuffer.append(character);
            }
        }
    }

    public boolean tryExactMatch(CommandRegistry commands) {
        Command match = commands.getExactMatch(currentCommand);
        if (match != null) {
            match.action().act(this);
            resetCommand();
            return true;
        }
        return false;
    }

    public void executeCommand() {
        CommandRegistry commands = switch (mode) {
            case NORMAL -> normalRegistry;
            case INSERT -> insertRegistry;
            case VISUAL -> visualRegistry;
        };
        Command match = commands.getExactMatch(currentCommand);
        if (match != null) {
            System.out.println(match);
            match.action().act(this);
        } else {
            shouldDisplayError = true;
        }
        resetCommand();
    }

    public void setModeString(String modeString) {
        this.modeString = modeString;
    }

    public void setModeString() {
        setModeString(mode.toString());
    }

    public String getCommandArrayString() {
        if (shouldDisplayError) {
            shouldDisplayError = false;
            return NOT_A_COMMAND;
        } else return commandBuffer.toString();
    }

    public void setMode(EditorMode mode) {
        this.mode = mode;
        this.modeString = mode.toString();
    }

    public EditorMode getEditorMode() {
        return mode;
    }

    public void enterInsertMode() {
        setMode(EditorMode.INSERT);
    }

    public void enterAppendMode() {
        setMode(EditorMode.INSERT);
    }

    public void enterNormalMode() {
        setMode(EditorMode.NORMAL);
    }

    public void enterVisualMode() {
        setMode(EditorMode.VISUAL);
    }

    public void changeCursorPosition(int change) {
        int currentRowLength = lines.get(getRelativeCursorRow()).size();
        if (currentRowLength == 0) currentRowLength = 1;
        cursorCol = Math.min(Math.max(1, cursorCol - change), currentRowLength + 1);
    }

    public void jumpToStartOfLine() {
        cursorCol = 1;
    }

    public void jumpToEndOfLine() {
        cursorCol = lines.get(getRelativeCursorRow()).size() + 1;
    }

    public void changeLine(int change) {
        int maxRow = screen.getTerminalSize().getRows() - 2;
        int newCursorRow = cursorRow + change;
        if (newCursorRow <= 0 || newCursorRow > maxRow) {
            scroll(change);
        }
        cursorRow = Math.min(Math.max(1, newCursorRow), maxRow);
    }

    public TerminalPosition getCursorPosition(int startRow,
                                              int startCol) {
        int currentRowLength = 0;
        int cursorRow = getRelativeCursorRow();
        if (cursorRow < lines.size()) {
            currentRowLength = lines.get(cursorRow).size();
        }
        return new TerminalPosition(
                Math.min(currentRowLength + 1, cursorCol) + startCol - 1,
                this.cursorRow - startRow - 1
        );
    }

    public char getCurrentChar() {
        CharGapList currentLine = lines.get(getRelativeCursorRow());
        int currentLineLength = currentLine.size();
        if (currentLineLength == 0) return '\0';
        if (cursorCol >= currentLineLength) return currentLine.get(currentLineLength - 1);
        return currentLine.get(cursorCol);
    }

    public char getPreviousChar() {
        CharGapList currentLine = lines.get(getRelativeCursorRow());
        if (currentLine.length() == 0 || cursorCol == 0) return '\0';
        return currentLine.get(cursorCol - 1);
    }

    public boolean isAfterOnlyWhitespace() {
        if (getCurrentLineLength() == 0) return true;
        for (int i = 0; i < cursorCol - 1; i++) {
            if (getCharAt(i) != ' ') return false;
        }
        return true;
    }

    public char getCharAt(int idx) {
        CharGapList currentLine = lines.get(getRelativeCursorRow());
        if (currentLine.length() == 0) return '\0';
        return currentLine.get(idx);
    }

    public CharGapList getCurrentLine() {
        return lines.get(getRelativeCursorRow());
    }

    public String getCurrentLineString() {
        return lines.get(getRelativeCursorRow()).toString();
    }

    public int getCurrentLineLength() {
        return lines.get(getRelativeCursorRow()).size();
    }

    public void setRow(int row) {
        cursorRow = row;
    }

    public void setCol(int col) {
        cursorCol = col;
    }

    public void setCursorPos(int row,
                             int col) {
        cursorRow = row;
        cursorCol = col;
    }

    public int getRow() {
        return cursorRow;
    }

    public int getCol() {
        return cursorCol;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStopRowRow() {
        return stopRow;
    }

    public void setStopRow(int stopRow) {
        this.stopRow = stopRow;
    }

    public void scrollToBottom() {
        scrollTo(lines.size());
        setRow(lines.size() - startRow);
        jumpToEndOfLine();
    }

    public void scrollToTop() {
        scrollTo(1);
        setRow(1);
        jumpToStartOfLine();
    }

    public void scrollTo(int targetLine) {
        int currentLine = getRelativeCursorRow();
        int delta = targetLine - currentLine;
        scroll(delta);
    }

    public void scroll(int scroll) {
        int previousStartRow = startRow;
        if (startRow + scroll < 1) {
            int size = stopRow - startRow;
            startRow = 0;
            stopRow = size;
        } else {
            int rows = screen.getTerminalSize().getRows();
            int targetRow = startRow + scroll;
            if (stopRow + scroll > getLineCount()) {
                int size = stopRow - startRow;
                stopRow = getLineCount();
                startRow = stopRow - size;
            } else {
                startRow += scroll;
                stopRow += scroll;
            }
        }
        if (startRow != previousStartRow) {
            try {
                screen.clear();
                screen.refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Screen getScreen() {
        return screen;
    }

    public CommandRegistry getNormalRegistry() {
        return normalRegistry;
    }

    public CommandRegistry getInsertRegistry() {
        return insertRegistry;
    }

    public CommandRegistry getVisualRegistry() {
        return visualRegistry;
    }

    public void registerKeyPressHandler(KeyPressHandler handler) {
        keyPressHandlers.add(handler);
    }

    public void registerKeyPressHandler(VeiPluginKeyPressHandler handler) {
        registerKeyPressHandler(
                (key, character) ->
                        handler.onKeyPress(new VeiPluginKeyPress(key, character))
        );
    }

    public VeiPluginLoader getPluginLoader() {
        return pluginLoader;
    }

    public TextGraphics getGraphics() {
        return graphics;
    }

    public boolean canRender() {
        return canRender;
    }
}
