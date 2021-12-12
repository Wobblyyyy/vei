package vei;

@FunctionalInterface
public interface KeyPressHandler {
    void onKeyPress(Key key,
                    char character);
}
