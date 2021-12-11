package org.magicwerk.brownies.collections;

import java.util.Comparator;


public class NullComparator<T> extends SingletonComparator<T> {
    private final Comparator<T> comparator;
    private final boolean nullsFirst;


    public NullComparator(Comparator<T> comparator, boolean nullsFirst) {
        this.comparator = comparator;
        this.nullsFirst = nullsFirst;
    }

    @Override
    public int compare(T key1, T key2) {
        if (key1 != null && key2 != null) {
            return comparator.compare(key1, key2);
        }
        if (key1 == null) {
            if (key2 == null) {
                return 0;
            } else {
                return nullsFirst ? -1 : 1;
            }
        } else {
            assert (key2 == null);
            return nullsFirst ? 1 : -1;
        }
    }
}