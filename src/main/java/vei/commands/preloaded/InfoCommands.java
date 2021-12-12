package vei.commands.preloaded;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import vei.EditorAction;
import vei.Key;
import vei.commands.Command;
import vei.plugin.VeiPlugin;
import vei.plugin.VeiPluginInformation;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class InfoCommands {
    private static final Key[] KEYS_PLUGINS = Key.translateString(
            ":plugins"
    );
    private static final EditorAction ACTION_PLUGINS = editor -> {
        List<VeiPlugin> plugins = editor.getPluginLoader().getAllPlugins();
        List<VeiPluginInformation> infoList = plugins.stream().map(
                VeiPlugin::getPluginInformation
        ).collect(Collectors.toList());
        editor.getScreen().clear();
        TerminalPosition cursorPosition = editor.getScreen().getCursorPosition();
        editor.getScreen().setCursorPosition(null);
        editor.preventRendering();
        TextGraphics graphics = editor.getGraphics();
        graphics.putString(
                2,
                0,
                "listing all currently loaded plugins..."
        );
        graphics.putString(
                2,
                1,
                "(press any key to close this menu)"
        );
        for (int i = 0; i < infoList.size(); i++) {
            VeiPluginInformation info = infoList.get(i);
            graphics.putString(
                    2,
                    3 + (i * 2),
                    String.format(
                            "%s by %s, version %s",
                            info.getTitle(),
                            info.getAuthor(),
                            info.getVersion()
                    )
            );
            graphics.putString(
                    4,
                    4 + (i * 2),
                    String.format("> %s", info.getDescription())
            );
        }
        try {
            editor.getScreen().refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.getScreen().clear();
        editor.getScreen().setCursorPosition(cursorPosition);
    };
    public static final Command COMMAND_PLUGINS = new Command(
            KEYS_PLUGINS,
            ACTION_PLUGINS
    );
}
