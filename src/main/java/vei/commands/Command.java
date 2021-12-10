package vei.commands;

import vei.EditorAction;
import vei.Key;

import java.util.Arrays;

public record Command(Key[] keys,
                      EditorAction action) {
    public boolean isExactMatch(Key[] currentKeys) {
        for (int i = 0; i < keys.length; i++) {
            Key a = keys[i];
            Key b = currentKeys[i];
            if (a != b) return false;
            if (i == keys.length - 1) {
                Key c = currentKeys[i + 1];
                if (c == Key.NONE) return true;
            }
        }
        return false;
    }

    public boolean doesMatch(Key[] currentKeys,
                             int maxIndex) {
        if (maxIndex > keys.length) return false;
        for (int i = 0; i < maxIndex; i++) {
            Key a = keys[i];
            Key b = currentKeys[i];
            if (a != b) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return Arrays.toString(keys);
    }
}
