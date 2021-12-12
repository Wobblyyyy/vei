package vei;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import vei.plugin.VeiPluginLoader;
import vei.plugin.preloaded.MacroPlugin;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("FieldCanBeLocal")
public class VeiWindow {
    private static final DefaultTerminalFactory factory =
            new DefaultTerminalFactory();
    public final AtomicInteger commandRow = new AtomicInteger(0);
    public final AtomicInteger modeRow = new AtomicInteger(0);
    public final AtomicReference<TerminalSize> size = new AtomicReference<>();
    TextGraphics graphics;
    private Terminal terminal;
    private Screen screen;
    private TextEditor editor;
    private VeiPluginLoader loader;

    public VeiWindow() {

    }

    public void start() throws IOException {
        terminal = factory.createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.flush();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);
        graphics = screen.newTextGraphics();
        loader = new VeiPluginLoader();
        editor = new TextEditor(screen, graphics, loader);
        loader.loadPlugin(
                this,
                editor,
                new MacroPlugin()
        );
    }

    public void loop() throws IOException {
        KeyStroke stroke;
        Key key;
        size.set(terminal.getTerminalSize());
        commandRow.set(size.get().getRows() - 1);
        modeRow.set(size.get().getRows() - 2);
        terminal.addResizeListener((terminal, newSize) -> {
            size.set(newSize);
            commandRow.set(newSize.getRows() - 1);
            modeRow.set(newSize.getRows() - 2);
            screen.clear();
        });
        editor.setText(
                """
                        package vei;

                        import com.googlecode.lanterna.TerminalPosition;
                        import com.googlecode.lanterna.screen.Screen;
                        import org.magicwerk.brownies.collections.primitive.CharGapList;
                        import vei.commands.Command;
                        import vei.commands.CommandRegistry;
                        import vei.plugin.VeiPluginKeyPress;
                        import vei.plugin.VeiPluginKeyPressHandler;

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
                            private final List<KeyPressHandler> keyPressHandlers = new ArrayList<>();
                            private String modeString = "NORMAL";

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
                                String[] lineStrings = text.split("\\n");
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

                            public void onKeyDown(Key key,
                                                  char character) {
                                switch (mode) {
                                    case NORMAL:
                                        if (key == Key.ENTER) {
                                            executeCommand();
                                        } else {
                                            currentCommand[currentCommandIndex] = key;
                                            currentCommandIndex += 1;
                                            commandBuffer.append(character);
                                            tryExactMatch(normalRegistry);
                                        }
                                        break;
                                    case INSERT:
                                        currentCommand[currentCommandIndex] = key;
                                        currentCommandIndex += 1;
                                        commandBuffer.append(character);
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

                                for (KeyPressHandler handler : keyPressHandlers) {
                                    handler.onKeyPress(
                                            key,
                                            character
                                    );
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
                                int maxRow = screen.getTerminalSize().getRows() - 2;
                                int newCursorRow = cursorRow + change;
                                if (newCursorRow <= 0 || newCursorRow > maxRow) {
                                    scroll(change);
                                }
                                cursorRow = Math.min(Math.max(1, newCursorRow), maxRow);
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
                                if (currentLineLength == 0) return '\\0';
                                if (cursorCol >= currentLineLength) return currentLine.get(currentLineLength - 1);
                                return currentLine.get(cursorCol);
                            }

                            public char getPreviousChar() {
                                CharGapList currentLine = lines.get(cursorRow - 1);
                                if (currentLine.length() == 0 || cursorCol == 0) return '\\0';
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
                                if (currentLine.length() == 0) return '\\0';
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
                                int previousStartRow = startRow;
                                if (startRow + scroll < 1) {
                                    int size = stopRow - startRow;
                                    startRow = 0;
                                    stopRow = size;
                                } else {
                                    int rows = screen.getTerminalSize().getRows();
                                    if (stopRow + scroll > getLineCount()) {
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
                        }
                                                """
        );
        int lineCount = editor.getLineCount();
        int startColumn = String.valueOf(lineCount).length() + 1;
        int minRow = 0;
        int maxRow = modeRow.get() - 1;
        editor.setStartRow(0);
        editor.setStopRow(modeRow.get());
        String lastModeString = "NORMAL";
        int lastPosLength = 0;
        editor.setModeString();
        while (true) {
            stroke = terminal.readInput();
            KeyType type = stroke.getKeyType();
            if (type == KeyType.Escape) break;
            char character = stroke.getCharacter();
            key = Key.match(
                    character,
                    stroke.isCtrlDown(),
                    type == KeyType.Backspace
            );
            screen.doResizeIfNecessary();
            editor.onKeyDown(key, character);
            graphics.putString(
                    0,
                    commandRow.get(),
                    String.format(
                            "%s                                         ",
                            editor.getCommandArrayString()
                    )
            );
            String modeString = editor.getModeString();
            if (!lastModeString.equals(modeString)) {
                graphics.drawLine(
                        0,
                        modeRow.get(),
                        lastModeString.length(),
                        modeRow.get(),
                        ' '
                );
                lastModeString = modeString;
            }
            graphics.putString(
                    0,
                    modeRow.get(),
                    modeString
            );
            TerminalPosition cursorPosition = editor.getCursorPosition(minRow, startColumn);
            String posString = String.format(
                    "row: %s col: %s (%s)",
                    editor.getRelativeCursorRow() + 1,
                    cursorPosition.getColumn() - startColumn + 1,
                    editor.getCol()
            );
            int posStringLength = posString.length();
            if (posStringLength < lastPosLength) {
                graphics.drawLine(
                        size.get().getColumns() - lastPosLength,
                        modeRow.get(),
                        size.get().getColumns(),
                        modeRow.get(),
                        ' '
                );
            }
            lastPosLength = posStringLength;
            graphics.putString(
                    size.get().getColumns() - posStringLength,
                    modeRow.get(),
                    posString
            );
            if (type == KeyType.Backspace) {
                graphics.drawLine(
                        0,
                        editor.getRow() - 1,
                        size.get().getColumns() - 1,
                        editor.getRow() - 1,
                        ' '
                );
            }
            String[] lines = editor.getLineStrings();
            for (int i = 0; i < lines.length && i < modeRow.get(); i++) {
                graphics.putString(
                        0,
                        i,
                        String.valueOf(editor.getStartRow() + i + 1)
                );
                graphics.putString(
                        startColumn,
                        i,
                        lines[i]
                );
            }
            screen.setCursorPosition(cursorPosition);
            if (editor.canRender()) screen.refresh();
            Thread.yield();
        }
    }

    public void stop() throws IOException {
        terminal.exitPrivateMode();
        screen.close();
        terminal.close();
    }
}
