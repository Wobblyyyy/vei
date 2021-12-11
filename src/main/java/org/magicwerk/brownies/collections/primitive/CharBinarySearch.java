package org.magicwerk.brownies.collections.primitive;

import org.magicwerk.brownies.collections.helper.ArraysHelper;


public class CharBinarySearch {

    public static int binarySearch(ICharList list, char key, int lower, int upper) {
        while (lower <= upper) {
            int middle = (lower + upper) >>> 1;
            int c = ArraysHelper.compare(key, list.get(middle));
            if (c < 0) {
                upper = middle - 1;
            } else if (c > 0) {
                lower = middle + 1;
            } else {


                while (lower < upper) {
                    middle = (lower + upper) >>> 1;
                    c = ArraysHelper.compare(list.get(middle), key);
                    if (c < 0) {
                        lower = middle + 1;
                    } else {
                        upper = middle;
                    }
                }
                return lower;
            }
        }
        return ~lower;
    }
}
