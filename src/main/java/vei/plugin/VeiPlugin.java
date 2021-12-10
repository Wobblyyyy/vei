package vei.plugin;

import vei.commands.CommandRegistry;

public interface VeiPlugin {
    VeiPluginInformation getPluginInformation();
    void onKeyPress(VeiPluginKeyPress keyPress);
    void onFileOpen(VeiFileOpen fileOpen);
    void onFileClose(VeiFileClose fileClose);
    void onUnload(VeiPluginConfiguration configuration);
    void registerNormalCommands(CommandRegistry registry);
    void registerInsertCommands(CommandRegistry registry);
    void registerVisualCommands(CommandRegistry registry);
}
