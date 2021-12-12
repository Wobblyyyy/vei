package vei.plugin.preloaded;

import org.magicwerk.brownies.collections.GapList;
import vei.*;
import vei.commands.Command;
import vei.plugin.VeiPlugin;
import vei.plugin.VeiPluginInformation;
import vei.plugin.VeiPluginKeyPress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MacroPlugin extends VeiPlugin {
    private static class MacroPluginInformation extends VeiPluginInformation {
        @Override
        public String getTitle() {
            return String.format(
                    "%s_macros",
                    VeiConstants.TITLE
            );
        }

        @Override
        public String getAuthor() {
            return VeiConstants.AUTHOR;
        }

        @Override
        public String getDescription() {
            return "vim-like macros: record, store, save, and play back keystrokes";
        }

        @Override
        public int getMajorVersion() {
            return VeiConstants.MAJOR;
        }

        @Override
        public int getMinorVersion() {
            return VeiConstants.MINOR;
        }

        @Override
        public int getPatchVersion() {
            return VeiConstants.PATCH;
        }
    }

    private static String getMacroString(char macroName) {
        return String.format("vei_macro_%s", macroName);
    }

    private static final String MODE_STRING = "RECORDING";
    private static final VeiPluginInformation info = new MacroPluginInformation();
    private final List<VeiPluginKeyPress> keyPresses = new GapList<>();
    private EditorMode mode;
    private boolean isRecording = false;
    private VeiWindow window;
    private TextEditor editor;
    private final Command commandRecord = new Command(
            new Key[]{
                    Key.Q
            },
            editor -> {
                if (isRecording) {
                    isRecording = false;
                    List<VeiPluginKeyPress> keyPressesCopy = new GapList<>(keyPresses);
                    Collections.copy(keyPressesCopy, keyPresses);
                    editor.getDataMap().put(
                            getMacroString('q'),
                            keyPressesCopy
                    );
                    keyPresses.clear();
                    editor.setModeString();
                } else {
                    isRecording = true;
                }
                System.out.println("Recording: " + isRecording);
            }
    );
    @SuppressWarnings("unchecked")
    private final Command commandPlayback = new Command(
            new Key[]{
                    Key.AT
            },
            editor -> {
                try {
                    System.out.println("Playback");
                    editor.resetCommand();
                    List<VeiPluginKeyPress> keyPresses =
                            (List<VeiPluginKeyPress>) editor.getDataMap().get(getMacroString('q'));

                    for (VeiPluginKeyPress keyPress : keyPresses) {
                        if (keyPress.getKey() != Key.AT) {
                            editor.onKeyDown(keyPress);
                        }
                    }

                    editor.setModeString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    );

    @Override
    public void onLoad(VeiWindow window,
                       TextEditor editor) {
        this.window = window;
        this.editor = editor;
    }

    @Override
    public VeiPluginInformation getPluginInformation() {
        return info;
    }

    @Override
    public void onKeyPress(VeiPluginKeyPress keyPress) {
        if (isRecording) {
            if (keyPress.getKey() != Key.Q) {
                keyPresses.add(keyPress);
                StringBuilder recordBuilder = new StringBuilder(keyPresses.size());
                for (VeiPluginKeyPress press : keyPresses) {
                    char character = press.getCharacter();
                    if (Character.isISOControl(character)) {
                        recordBuilder.append(String.format("<%s>", press.getKey()));
                    } else {
                        recordBuilder.append(press.getCharacter());
                    }
                }
                editor.setModeString(
                        String.format(
                                "%s: %s",
                                MODE_STRING,
                                recordBuilder
                        )
                );
            } else {
                editor.setModeString(MODE_STRING);
            }
        }
    }

    @Override
    public void onModeChange(EditorMode oldMode,
                             EditorMode newMode) {
        this.mode = newMode;
    }

    @Override
    public Iterable<Command> getNormalCommands() {
        return new ArrayList<>() {{
            add(commandRecord);
            add(commandPlayback);
        }};
    }
}
