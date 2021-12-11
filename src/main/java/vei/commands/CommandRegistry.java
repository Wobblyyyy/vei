package vei.commands;

import vei.Key;
import vei.commands.preloaded.ModeCommands;
import vei.commands.preloaded.MovementCommands;

import java.util.ArrayList;
import java.util.List;

public record CommandRegistry(List<Command> commands) {
    public static final List<Command> DEFAULT_NORMAL_COMMANDS = new ArrayList<>(50) {{
        add(ModeCommands.COMMAND_INSERT);
        add(ModeCommands.COMMAND_APPEND);
        add(ModeCommands.COMMAND_JUMP_INSERT);
        add(ModeCommands.COMMAND_JUMP_APPEND);
        add(MovementCommands.COMMAND_CURSOR_LEFT);
        add(MovementCommands.COMMAND_CURSOR_RIGHT);
        add(MovementCommands.COMMAND_CURSOR_UP);
        add(MovementCommands.COMMAND_CURSOR_DOWN);
        add(MovementCommands.COMMAND_LINE_START);
        add(MovementCommands.COMMAND_LINE_END);
        add(MovementCommands.COMMAND_WORD_FORWARDS);
        add(MovementCommands.COMMAND_WORD_BACKWARDS);
        add(MovementCommands.COMMAND_FILE_TOP);
        add(MovementCommands.COMMAND_FILE_BOTTOM);
        add(MovementCommands.COMMAND_SCROLL_UP);
        add(MovementCommands.COMMAND_SCROLL_DOWN);
    }};
    public static final List<Command> DEFAULT_INSERT_COMMANDS = new ArrayList<>(50) {{
        add(ModeCommands.COMMAND_NORMAL);
    }};
    public static final List<Command> DEFAULT_VISUAL_COMMANDS = new ArrayList<>(50);
    private static final List<Command> DEFAULT_COMMANDS = new ArrayList<>(50);

    public Command getExactMatch(Key[] currentCommand) {
        for (Command command : commands) {
            if (command.isExactMatch(currentCommand)) {
                return command;
            }
        }

        return null;
    }

    public boolean anyMatches(Key[] keys,
                              int maxIndex) {
        for (Command command : commands) {
            if (command.doesMatch(keys, maxIndex)) {
                return true;
            }
        }
        return false;
    }
}
