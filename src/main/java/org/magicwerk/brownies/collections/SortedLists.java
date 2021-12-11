package org.magicwerk.brownies.collections;

import java.util.Comparator;
import java.util.List;


public class SortedLists {
    private SortedLists() {
    }


    public static <E> int binarySearch(List<? extends E> list, E key,
                                       Comparator<? super E> comparator, KeyPresentBehavior presentBehavior,
                                       KeyAbsentBehavior absentBehavior) {
        int lower = 0;
        int upper = list.size() - 1;

        while (lower <= upper) {
            int middle = (lower + upper) >>> 1;
            int c = comparator.compare(key, list.get(middle));
            if (c < 0) {
                upper = middle - 1;
            } else if (c > 0) {
                lower = middle + 1;
            } else {
                return lower + presentBehavior.resultIndex(
                        comparator, key, list.subList(lower, upper + 1), middle - lower);
            }
        }
        return absentBehavior.resultIndex(lower);
    }


    public static <E> int binarySearchAdd(List<? extends E> list, E key, Comparator<? super E> comparator) {
        return binarySearch(list, key, comparator, KeyPresentBehavior.FIRST_AFTER, KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
    }


    public static <E> int binarySearchGet(List<? extends E> list, E key, Comparator<? super E> comparator) {
        return binarySearch(list, key, comparator, KeyPresentBehavior.FIRST_PRESENT, KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
    }


    public enum KeyPresentBehavior {

        ANY_PRESENT {
            @Override
            public <E> int resultIndex(
                    Comparator<? super E> comparator, E key, List<? extends E> list, int foundIndex) {
                return foundIndex;
            }
        },

        LAST_PRESENT {
            @Override
            public <E> int resultIndex(
                    Comparator<? super E> comparator, E key, List<? extends E> list, int foundIndex) {


                int lower = foundIndex;
                int upper = list.size() - 1;

                while (lower < upper) {
                    int middle = (lower + upper + 1) >>> 1;
                    int c = comparator.compare(list.get(middle), key);
                    if (c > 0) {
                        upper = middle - 1;
                    } else {
                        lower = middle;
                    }
                }
                return lower;
            }
        },

        FIRST_PRESENT {
            @Override
            public <E> int resultIndex(
                    Comparator<? super E> comparator, E key, List<? extends E> list, int foundIndex) {


                int lower = 0;
                int upper = foundIndex;


                while (lower < upper) {
                    int middle = (lower + upper) >>> 1;
                    int c = comparator.compare(list.get(middle), key);
                    if (c < 0) {
                        lower = middle + 1;
                    } else {
                        upper = middle;
                    }
                }
                return lower;
            }
        },

        FIRST_AFTER {
            @Override
            public <E> int resultIndex(
                    Comparator<? super E> comparator, E key, List<? extends E> list, int foundIndex) {
                return LAST_PRESENT.resultIndex(comparator, key, list, foundIndex) + 1;
            }
        },

        LAST_BEFORE {
            @Override
            public <E> int resultIndex(
                    Comparator<? super E> comparator, E key, List<? extends E> list, int foundIndex) {
                return FIRST_PRESENT.resultIndex(comparator, key, list, foundIndex) - 1;
            }
        };

        public abstract <E> int resultIndex(
                Comparator<? super E> comparator, E key, List<? extends E> list, int foundIndex);
    }


    public enum KeyAbsentBehavior {

        NEXT_LOWER {
            @Override
            public int resultIndex(int higherIndex) {
                return higherIndex - 1;
            }
        },

        NEXT_HIGHER {
            @Override
            public int resultIndex(int higherIndex) {
                return higherIndex;
            }
        },

        INVERTED_INSERTION_INDEX {
            @Override
            public int resultIndex(int higherIndex) {
                return ~higherIndex;
            }
        };

        public abstract int resultIndex(int higherIndex);
    }
}
