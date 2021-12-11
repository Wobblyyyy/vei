package org.magicwerk.brownies.collections;

import java.util.Map.Entry;


public class ImmutableMapEntry<K, E> implements Entry<K, E> {
    private final K key;
    private final E value;


    public ImmutableMapEntry(K key, E value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public E getValue() {
        return value;
    }


    @Override
    public E setValue(E value) {
        throw new AssertionError();
    }

    @Override
    public String toString() {
        return "MapEntry [key=" + key + ", value=" + value + "]";
    }

}