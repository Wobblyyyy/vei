package vei.plugin;

@FunctionalInterface
public interface VeiPluginKeyPressHandler {
    void onKeyPress(VeiPluginKeyPress keyPress);
}
