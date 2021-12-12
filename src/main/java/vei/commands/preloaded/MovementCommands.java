package vei.commands.preloaded;

import vei.EditorAction;
import vei.Key;
import vei.TextEditor;
import vei.commands.Command;

import java.io.IOException;

@SuppressWarnings("Convert2MethodRef")
public class MovementCommands {
    private static final Key[] KEYS_CURSOR_RIGHT = new Key[]{
            Key.H
    };
    private static final Key[] KEYS_CURSOR_LEFT = new Key[]{
            Key.L
    };
    private static final Key[] KEYS_CURSOR_DOWN = new Key[]{
            Key.J
    };
    private static final Key[] KEYS_CURSOR_UP = new Key[]{
            Key.K
    };
    private static final Key[] KEYS_END_OF_LINE = new Key[]{
            Key.DOLLAR_SIGN
    };
    private static final Key[] KEYS_START_OF_LINE = new Key[]{
            Key.N_0
    };
    private static final Key[] KEYS_WORD_FORWARDS = new Key[]{
            Key.W
    };
    private static final Key[] KEYS_WORD_BACKWARDS = new Key[]{
            Key.B
    };
    private static final Key[] KEYS_FILE_TOP = new Key[]{
            Key.G,
            Key.G
    };
    private static final Key[] KEYS_FILE_BOTTOM = new Key[]{
            Key.S_G
    };

    private static final EditorAction ACTION_CURSOR_LEFT = editor -> {
        editor.changeCursorPosition(-1);
    };
    public static final Command COMMAND_CURSOR_LEFT = new Command(
            KEYS_CURSOR_LEFT,
            ACTION_CURSOR_LEFT
    );
    private static final EditorAction ACTION_CURSOR_RIGHT = editor -> {
        editor.changeCursorPosition(1);
    };
    public static final Command COMMAND_CURSOR_RIGHT = new Command(
            KEYS_CURSOR_RIGHT,
            ACTION_CURSOR_RIGHT
    );
    private static final EditorAction ACTION_CURSOR_DOWN = editor -> {
        editor.changeLine(1);
    };
    public static final Command COMMAND_CURSOR_DOWN = new Command(
            KEYS_CURSOR_DOWN,
            ACTION_CURSOR_DOWN
    );
    private static final EditorAction ACTION_CURSOR_UP = editor -> {
        editor.changeLine(-1);
    };
    public static final Command COMMAND_CURSOR_UP = new Command(
            KEYS_CURSOR_UP,
            ACTION_CURSOR_UP
    );
    private static final EditorAction ACTION_LINE_START = TextEditor::jumpToStartOfLine;
    public static final Command COMMAND_LINE_START = new Command(
            KEYS_START_OF_LINE,
            ACTION_LINE_START
    );
    private static final EditorAction ACTION_LINE_END = TextEditor::jumpToEndOfLine;
    public static final Command COMMAND_LINE_END = new Command(
            KEYS_END_OF_LINE,
            ACTION_LINE_END
    );
    private static final EditorAction ACTION_WORD_FORWARDS = editor -> {
        boolean startedInWord = false;
        char c = editor.getCurrentChar();
        if (c != ' ') startedInWord = true;
        while (true) {
            int lineLength = editor.getCurrentLineLength();
            if (lineLength == 0 || editor.getCol() >= lineLength - 1) {
                editor.setCol(1);
                editor.changeLine(1);
                c = editor.getCurrentChar();
            }
            int col = editor.getCol();
            if (col == lineLength && editor.getRow() == editor.getLineCount()) {
                break;
            }
            editor.changeCursorPosition(-1);
            if (c != ' ' && !startedInWord) break;
            if (c == ' ') startedInWord = false;
            c = editor.getCurrentChar();
        }
    };
    public static final Command COMMAND_WORD_FORWARDS = new Command(
            KEYS_WORD_FORWARDS,
            ACTION_WORD_FORWARDS
    );
    private static final EditorAction ACTION_WORD_BACKWARDS = editor -> {
        boolean isFirstChar = false;
        boolean startedInWord = false;
        char c = editor.getCurrentChar();
        char p = editor.getPreviousChar();
        if (c != ' ') startedInWord = true;
        while (!isFirstChar) {
            if (editor.getRow() == 1 && editor.getCol() == 1) {
                editor.setCursorPos(1, 1);
                return;
            }
            if (editor.isAfterOnlyWhitespace() || c == '\0' || p == '\0') {
                if (editor.getRow() == 1) {
                    editor.setCursorPos(1, 1);
                    break;
                } else {
                    editor.changeLine(-1);
                    ACTION_LINE_END.act(editor);
                }
            }
            if (editor.getCol() != 1) editor.changeCursorPosition(1);
            c = editor.getCurrentChar();
            p = editor.getPreviousChar();
            if (c != ' ' && p == ' ') {
                isFirstChar = !startedInWord;
                if (startedInWord) startedInWord = false;
            }
        }
        if (p != '\0') editor.changeCursorPosition(-1);
    };
    public static final Command COMMAND_WORD_BACKWARDS = new Command(
            KEYS_WORD_BACKWARDS,
            ACTION_WORD_BACKWARDS
    );
    private static final EditorAction ACTION_FILE_TOP = editor -> {
        editor.scrollToTop();
    };
    public static final Command COMMAND_FILE_TOP = new Command(
            KEYS_FILE_TOP,
            ACTION_FILE_TOP
    );
    private static final EditorAction ACTION_FILE_BOTTOM = editor -> {
        editor.scrollToBottom();
    };
    public static final Command COMMAND_FILE_BOTTOM = new Command(
            KEYS_FILE_BOTTOM,
            ACTION_FILE_BOTTOM
    );
    private static final Key[] KEYS_SCROLL_UP = new Key[]{
            Key.S,
            Key.K
    };
    private static final Key[] KEYS_SCROLL_DOWN = new Key[]{
            Key.S,
            Key.J
    };
    private static final EditorAction ACTION_SCROLL_UP = editor -> {
        editor.scroll(-1);
        editor.getScreen().clear();
    };
    private static final EditorAction ACTION_SCROLL_DOWN = editor -> {
        editor.scroll(1);
        editor.getScreen().clear();
    };
    public static final Command COMMAND_SCROLL_UP = new Command(
            KEYS_SCROLL_UP,
            ACTION_SCROLL_UP
    );
    public static final Command COMMAND_SCROLL_DOWN = new Command(
            KEYS_SCROLL_DOWN,
            ACTION_SCROLL_DOWN
    );
}
