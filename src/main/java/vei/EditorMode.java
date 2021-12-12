package vei;

public enum EditorMode {
    NORMAL,
    INSERT,
    VISUAL;

    @Override
    public String toString() {
        return String.format("mode: %s", super.toString());
    }
}
