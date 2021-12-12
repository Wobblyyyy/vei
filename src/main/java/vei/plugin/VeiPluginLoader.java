package vei.plugin;

import vei.TextEditor;
import vei.VeiWindow;
import vei.commands.Command;
import vei.commands.CommandRegistry;

import java.util.ArrayList;
import java.util.List;

public class VeiPluginLoader {
    private final List<VeiPlugin> allPlugins = new ArrayList<>();

    public VeiPluginLoader() {

    }

    public void loadPlugin(VeiWindow window,
                           TextEditor editor,
                           VeiPlugin plugin) {
        allPlugins.add(plugin);
        plugin.onLoad(window, editor);
        editor.registerKeyPressHandler(plugin::onKeyPress);

        Iterable<Command> pluginNormalCommands = plugin.getNormalCommands();
        Iterable<Command> pluginInsertCommands = plugin.getInsertCommands();
        Iterable<Command> pluginVisualCommands = plugin.getVisualCommands();

        CommandRegistry normalRegistry = editor.getNormalRegistry();
        CommandRegistry insertRegistry = editor.getInsertRegistry();
        CommandRegistry visualRegistry = editor.getVisualRegistry();

        if (pluginNormalCommands != null) {
            pluginNormalCommands.forEach(normalRegistry::addCommand);
        }

        if (pluginInsertCommands != null) {
            pluginInsertCommands.forEach(insertRegistry::addCommand);
        }

        if (pluginVisualCommands != null) {
            pluginVisualCommands.forEach(visualRegistry::addCommand);
        }
    }

    public List<VeiPlugin> getAllPlugins() {
        return allPlugins;
    }
}
