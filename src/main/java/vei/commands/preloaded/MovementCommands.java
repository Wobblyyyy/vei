package vei.commands.preloaded;

import vei.TextEditor;
import vei.commands.Command;
import vei.EditorAction;
import vei.Key;

public class MovementCommands {
    private static final Key[] KEYS_CURSOR_RIGHT = new Key[] {
            Key.H
    };
    private static final Key[] KEYS_CURSOR_LEFT = new Key[] {
            Key.L
    };
    private static final Key[] KEYS_CURSOR_DOWN = new Key[] {
            Key.J
    };
    private static final Key[] KEYS_CURSOR_UP = new Key[] {
            Key.K
    };
    private static final Key[] KEYS_END_OF_LINE = new Key[] {
            Key.DOLLAR_SIGN
    };
    private static final Key[] KEYS_START_OF_LINE = new Key[] {
            Key.N_0
    };
    private static final Key[] KEYS_WORD_FORWARDS = new Key[] {
            Key.W
    };
    private static final Key[] KEYS_WORD_BACKWARDS = new Key[] {
            Key.B
    };
    private static final Key[] KEYS_FILE_TOP = new Key[] {
            Key.G,
            Key.G
    };
    private static final Key[] KEYS_FILE_BOTTOM = new Key[] {
            Key.S_G
    };

    private static final EditorAction ACTION_CURSOR_LEFT = editor -> {
        editor.changeCursorPosition(-1);
    };
    private static final EditorAction ACTION_CURSOR_RIGHT = editor -> {
        editor.changeCursorPosition(1);
    };
    private static final EditorAction ACTION_CURSOR_DOWN = editor -> {
        editor.changeLine(1);
    };
    private static final EditorAction ACTION_CURSOR_UP = editor -> {
        editor.changeLine(-1);
    };
    private static final EditorAction ACTION_LINE_START = TextEditor::jumpToStartOfLine;
    private static final EditorAction ACTION_LINE_END = TextEditor::jumpToEndOfLine;
    private static final EditorAction ACTION_WORD_FORWARDS = editor -> {

    };
    private static final EditorAction ACTION_WORD_BACKWARDS = editor -> {

    };
    private static final EditorAction ACTION_FILE_TOP = editor -> {

    };
    private static final EditorAction ACTION_FILE_BOTTOM = editor -> {

    };

    public static final Command COMMAND_CURSOR_LEFT = new Command(
            KEYS_CURSOR_LEFT,
            ACTION_CURSOR_LEFT
    );
    public static final Command COMMAND_CURSOR_RIGHT = new Command(
            KEYS_CURSOR_RIGHT,
            ACTION_CURSOR_RIGHT
    );
    public static final Command COMMAND_CURSOR_UP = new Command(
            KEYS_CURSOR_UP,
            ACTION_CURSOR_UP
    );
    public static final Command COMMAND_CURSOR_DOWN = new Command(
            KEYS_CURSOR_DOWN,
            ACTION_CURSOR_DOWN
    );
    public static final Command COMMAND_LINE_START = new Command(
            KEYS_START_OF_LINE,
            ACTION_LINE_START
    );
    public static final Command COMMAND_LINE_END = new Command(
            KEYS_END_OF_LINE,
            ACTION_LINE_END
    );
    public static final Command COMMAND_WORD_FORWARDS = new Command(
            KEYS_WORD_FORWARDS,
            ACTION_WORD_FORWARDS
    );
    public static final Command COMMAND_WORD_BACKWARDS = new Command(
            KEYS_WORD_BACKWARDS,
            ACTION_WORD_BACKWARDS
    );
    public static final Command COMMAND_FILE_TOP = new Command(
            KEYS_FILE_TOP,
            ACTION_FILE_TOP
    );
    public static final Command COMMAND_FILE_BOTTOM = new Command(
            KEYS_FILE_BOTTOM,
            ACTION_FILE_BOTTOM
    );
}
