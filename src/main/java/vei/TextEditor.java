package vei;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.screen.Screen;
import org.magicwerk.brownies.collections.primitive.CharGapList;
import vei.commands.Command;
import vei.commands.CommandRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    public TextEditor(Screen screen) {
        this.screen = screen;
        Arrays.fill(currentCommand, Key.NONE);
        normalRegistry = new CommandRegistry(CommandRegistry.DEFAULT_NORMAL_COMMANDS);
        insertRegistry = new CommandRegistry(CommandRegistry.DEFAULT_INSERT_COMMANDS);
        visualRegistry = new CommandRegistry(CommandRegistry.DEFAULT_VISUAL_COMMANDS);
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
            strings[i] = lines.get(firstIdx + i).toString();
        }
        return strings;
    }

    public void onKeyDown(Key key,
                          char character) {
        switch (mode) {
            case NORMAL:
                if (key == Key.ENTER) {
                    executeCommand();
                } else {
                    currentCommand[currentCommandIndex] = key;
                    currentCommandIndex += 1;
                    commandBuffer.append(Key.reverseMatch(key));
                    tryExactMatch(normalRegistry);
                }
                break;
            case INSERT:
                currentCommand[currentCommandIndex] = key;
                currentCommandIndex += 1;
                commandBuffer.append(Key.reverseMatch(key));
                if (insertRegistry.anyMatches(
                        currentCommand,
                        currentCommandIndex
                )) {
                    tryExactMatch(insertRegistry);
                } else {
                    CharGapList currentLine = lines.get(cursorRow - 1);
                    for (int i = 0; i < commandBuffer.length() - 1; i++) {
                        char c = commandBuffer.charAt(i);
                        currentLine.add(cursorCol - 1, c);
                        changeCursorPosition(-1);
                    }
                    if (key == Key.BACKSPACE) {
                        if (cursorCol - 2 >= 0) {
                            currentLine.remove(cursorCol - 2);
                            changeCursorPosition(1);
                        }
                    } else {
                        currentLine.add(cursorCol - 1, character);
                        changeCursorPosition(-1);
                    }
                    resetCommand();
                }
                break;
            case VISUAL:
                break;
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

    public String getCommandArrayString() {
        if (shouldDisplayError) {
            shouldDisplayError = false;
            return NOT_A_COMMAND;
        } else return commandBuffer.toString();
    }

    public void setMode(EditorMode mode) {
        this.mode = mode;
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
        int currentRowLength = lines.get(cursorRow - 1).size();
        if (currentRowLength == 0) currentRowLength = 1;
        cursorCol = Math.min(Math.max(1, cursorCol - change), currentRowLength);
    }

    public void jumpToStartOfLine() {
        cursorCol = 1;
    }

    public void jumpToEndOfLine() {
        cursorCol = lines.get(cursorRow - 1).size();
    }

    public void changeLine(int change) {
        cursorRow = Math.min(Math.max(1, cursorRow + change), getLineCount());
    }

    public TerminalPosition getCursorPosition(int startRow,
                                              int startCol) {
        int currentRowLength = lines.get(cursorRow - 1).size();
        if (currentRowLength == 0) currentRowLength = 1;
        return new TerminalPosition(
                Math.min(currentRowLength, cursorCol) + startCol - 1,
                cursorRow - startRow - 1
        );
    }

    public char getCurrentChar() {
        CharGapList currentLine = lines.get(cursorRow - 1);
        int currentLineLength = currentLine.size();
        if (currentLineLength == 0) return '\0';
        if (cursorCol >= currentLineLength) return currentLine.get(currentLineLength - 1);
        return currentLine.get(cursorCol);
    }

    public char getPreviousChar() {
        CharGapList currentLine = lines.get(cursorRow - 1);
        if (currentLine.length() == 0 || cursorCol == 0) return '\0';
        return currentLine.get(cursorCol - 1);
    }

    public boolean isAfterOnlyWhitespace() {
        for (int i = 0; i < cursorCol; i++) {
            if (getCharAt(i) != ' ') return false;
        }
        return true;
    }

    public char getCharAt(int idx) {
        CharGapList currentLine = lines.get(cursorRow - 1);
        if (currentLine.length() == 0) return '\0';
        return currentLine.get(idx);
    }

    public CharGapList getCurrentLine() {
        return lines.get(cursorRow - 1);
    }

    public String getCurrentLineString() {
        return lines.get(cursorRow - 1).toString();
    }

    public int getCurrentLineLength() {
        return lines.get(cursorRow - 1).size();
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

    public void scroll(int scroll) {
        if (startRow + scroll < 0) {
            int size = stopRow - startRow;
            startRow = 0;
            stopRow = size;
        } else {
            int rows = screen.getTerminalSize().getRows();
            if (stopRow + scroll > rows) {
                int size = stopRow - startRow;
                startRow = rows - size;
                stopRow = rows - 1;
            } else {
                startRow += scroll;
                stopRow += scroll;
            }
        }
    }

    public Screen getScreen() {
        return screen;
    }
}
