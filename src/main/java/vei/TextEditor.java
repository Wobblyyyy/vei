package vei;

import com.googlecode.lanterna.TerminalPosition;
import vei.commands.Command;
import vei.commands.CommandRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"UnusedReturnValue", "FieldMayBeFinal"})
public class TextEditor {
    private static final String NOT_A_COMMAND =
            "Not a valid editor command!";

    private final Key[] currentCommand = new Key[100];
    private int currentCommandIndex = 0;
    private EditorMode mode = EditorMode.NORMAL;
    private CommandRegistry normalRegistry;
    private CommandRegistry insertRegistry;
    private CommandRegistry visualRegistry;
    private StringBuilder commandBuffer = new StringBuilder(100);
    private boolean shouldDisplayError = true;
    private List<char[]> lines = new ArrayList<>(1) {{
        add(new char[] { ' ' });
    }};
    int cursorRow = 1;
    int cursorCol = 1;

    public TextEditor() {
        Arrays.fill(currentCommand, Key.NONE);
        normalRegistry = new CommandRegistry(CommandRegistry.DEFAULT_NORMAL_COMMANDS);
        insertRegistry = new CommandRegistry(CommandRegistry.DEFAULT_INSERT_COMMANDS);
        visualRegistry = new CommandRegistry(CommandRegistry.DEFAULT_VISUAL_COMMANDS);
    }

    public void setText(String text) {
        lines.clear();
        String[] lineStrings = text.split("\n");
        for (String str : lineStrings) {
            lines.add(str.toCharArray());
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

    public String[] getLineStrings(int firstIdx,
                                   int lastIdx) {
        int size = lastIdx - firstIdx;
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            strings[i] = new String(lines.get(firstIdx + i));
        }
        return strings;
    }

    public void onKeyDown(Key key) {
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
            System.out.println(match);
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
        }
        else return commandBuffer.toString();
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
        int currentRowLength = lines.get(cursorRow - 1).length;
        cursorCol = Math.min(Math.max(1, cursorCol - change), currentRowLength);
    }

    public void jumpToStartOfLine() {
        cursorCol = 1;
    }

    public void jumpToEndOfLine() {
        int currentRowLength = lines.get(cursorRow - 1).length;
        cursorCol = currentRowLength;
    }

    public void changeLine(int change) {
        cursorRow = Math.min(Math.max(1, cursorRow + change), getLineCount());
    }

    public TerminalPosition getCursorPosition(int startRow,
                                              int startCol) {
        int currentRowLength = lines.get(cursorRow - 1).length;
        return new TerminalPosition(
                Math.min(currentRowLength, cursorCol) + startCol - 1,
                cursorRow - startRow - 1
        );
    }

    public int getRow() {
        return cursorRow;
    }

    public int getCol() {
        return cursorCol;
    }
}
