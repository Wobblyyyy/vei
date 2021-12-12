package vei.plugin;

import vei.EditorMode;
import vei.TextEditor;
import vei.VeiWindow;
import vei.commands.Command;

public abstract class VeiPlugin {
    public abstract VeiPluginInformation getPluginInformation();

    public void onLoad(VeiWindow window,
                       TextEditor editor) {

    }

    public void onKeyPress(VeiPluginKeyPress keyPress) {

    }

    public void onModeChange(EditorMode oldMode,
                             EditorMode newMode) {

    }

    public void onFileOpen(VeiFileOpen fileOpen) {

    }

    public void onFileClose(VeiFileClose fileClose) {

    }

    public Iterable<Command> getNormalCommands() {
        return null;
    }

    public Iterable<Command> getInsertCommands() {
        return null;
    }

    public Iterable<Command> getVisualCommands() {
        return null;
    }
}
