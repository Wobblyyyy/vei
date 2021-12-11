package vei.plugin;

public abstract class VeiPluginInformation {
    public abstract String getTitle();

    public abstract String getAuthor();

    public abstract String getDescription();

    public abstract int getMajorVersion();

    public abstract int getMinorVersion();

    public abstract int getPatchVersion();

    public String getVersion() {
        return String.format(
                "v%s.%s.%s",
                getMajorVersion(),
                getMinorVersion(),
                getPatchVersion()
        );
    }
}
