package org.magicwerk.brownies.collections;

import java.io.Serializable;
import java.util.Comparator;


abstract class SingletonComparator<T> implements Comparator<T>, Serializable {

    @Override
    public boolean equals(Object that) {
        if (this == null || that == null) {
            return this == that;
        }
        return this.getClass().getName().equals(that.getClass().getName());
    }

    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }

}
