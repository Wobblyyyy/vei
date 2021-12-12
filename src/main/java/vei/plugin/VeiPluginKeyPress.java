package vei.plugin;

import vei.Key;

public record VeiPluginKeyPress(Key key,
                                char character) {
    public Key getKey() {
        return key;
    }

    public char getCharacter() {
        return character;
    }
}
