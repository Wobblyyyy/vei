package vei.commands.preloaded;

import vei.*;
import vei.commands.Command;

@SuppressWarnings("Convert2MethodRef")
public class ModeCommands {
    private static final Key[] KEYS_NORMAL = new Key[] {
            Key.J,
            Key.K
    };
    private static final Key[] KEYS_INSERT = new Key[] {
            Key.I,
    };
    private static final Key[] KEYS_APPEND = new Key[] {
            Key.A
    };
    private static final Key[] KEYS_JUMP_INSERT = new Key[] {
            Key.S_I
    };
    private static final Key[] KEYS_JUMP_APPEND = new Key[] {
            Key.S_A
    };

    private static final EditorAction ACTION_NORMAL = editor -> {
        editor.enterNormalMode();
    };
    private static final EditorAction ACTION_INSERT = editor -> {
        editor.enterInsertMode();
    };
    private static final EditorAction ACTION_APPEND = editor -> {
        editor.changeCursorPosition(1);
        editor.enterInsertMode();
    };
    private static final EditorAction ACTION_JUMP_INSERT = editor -> {
        editor.jumpToStartOfLine();
        editor.enterInsertMode();
    };
    private static final EditorAction ACTION_JUMP_APPEND = editor -> {
        editor.jumpToEndOfLine();
        editor.enterAppendMode();
    };

    public static final Command COMMAND_NORMAL = new Command(
            KEYS_NORMAL,
            ACTION_NORMAL
    );
    public static final Command COMMAND_INSERT = new Command(
            KEYS_INSERT,
            ACTION_INSERT
    );
    public static final Command COMMAND_APPEND = new Command(
            KEYS_APPEND,
            ACTION_APPEND
    );
    public static final Command COMMAND_JUMP_INSERT = new Command(
            KEYS_JUMP_INSERT,
            ACTION_JUMP_INSERT
    );
    public static final Command COMMAND_JUMP_APPEND = new Command(
            KEYS_JUMP_APPEND,
            ACTION_JUMP_APPEND
    );
}
