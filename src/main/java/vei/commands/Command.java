package vei.commands;

import vei.EditorAction;
import vei.Key;

public record Command(Key[] keys,
                      EditorAction action) {
    public boolean doesMatch(Key[] currentKeys) {
        for (int i = 0; i < currentKeys.length; i++) {
            Key a = keys[i];
            Key b = currentKeys[i];
            if (a != b) return false;
        }
        return true;
    }
}
