package vei.commands;

import vei.Key;
import vei.commands.preloaded.ModeCommands;
import vei.commands.preloaded.MovementCommands;

import java.util.ArrayList;
import java.util.List;

public record CommandRegistry(List<Command> commands) {
    private static final List<Command> DEFAULT_COMMANDS = new ArrayList<>(50);
    public static final List<Command> DEFAULT_NORMAL_COMMANDS = new ArrayList<>(50) {{
        add(ModeCommands.COMMAND_NORMAL);
        add(ModeCommands.COMMAND_INSERT);
        add(ModeCommands.COMMAND_APPEND);
        add(ModeCommands.COMMAND_JUMP_INSERT);
        add(ModeCommands.COMMAND_JUMP_APPEND);
        add(MovementCommands.COMMAND_CURSOR_LEFT);
        add(MovementCommands.COMMAND_CURSOR_RIGHT);
        add(MovementCommands.COMMAND_CURSOR_UP);
        add(MovementCommands.COMMAND_CURSOR_DOWN);
        add(MovementCommands.COMMAND_WORD_FORWARDS);
        add(MovementCommands.COMMAND_WORD_BACKWARDS);
        add(MovementCommands.COMMAND_FILE_TOP);
        add(MovementCommands.COMMAND_FILE_BOTTOM);
    }};
    private static final List<Command> DEFAULT_INSERT_COMMANDS = new ArrayList<>(50);
    private static final List<Command> DEFAULT_VISUAL_COMMANDS = new ArrayList<>(50);

    public boolean anyMatches(Key[] keys) {
        for (Command command : commands) {
            if (command.doesMatch(keys)) {
                return true;
            }
        }
        return false;
    }
}
