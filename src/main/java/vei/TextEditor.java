package vei;

public class TextEditor {
    private final Key[] currentCommand = new Key[100];
    private int currentCommandIndex = 0;
    private EditorMode mode;
    private int cursorPosition;
    private int columnPosition;
    private int linePosition;
    private char[][] rows = new char[0][0];

    public void setMode(EditorMode mode) {
        this.mode = mode;
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
        cursorPosition += change;
    }

    public void jumpToStartOfLine() {

    }

    public void jumpToEndOfLine() {

    }

    public void changeLine(int change) {

    }
}
