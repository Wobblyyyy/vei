package org.magicwerk.brownies.collections.helper;

import org.magicwerk.brownies.collections.IList;
import org.magicwerk.brownies.collections.primitive.*;


public class BigLists {

    public static IList<?> createWrapperList(Class<?> type) {
        if (type == int.class) {
        } else if (type == long.class) {
        } else if (type == double.class) {
        } else if (type == float.class) {
        } else if (type == boolean.class) {
        } else if (type == byte.class) {
        } else if (type == char.class) {
            return new CharObjBigList();
        } else if (type == short.class) {
        } else {
        }
        throw new IllegalArgumentException("Primitive type expected: " + type);
    }

}
