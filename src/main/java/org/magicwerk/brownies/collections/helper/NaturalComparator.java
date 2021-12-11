package org.magicwerk.brownies.collections.helper;


public class NaturalComparator<T> extends SingletonComparator<T> {


    @SuppressWarnings("rawtypes")
    private static final NaturalComparator INSTANCE = new NaturalComparator();


    private NaturalComparator() {
    }


    @SuppressWarnings("unchecked")
    public static <T> NaturalComparator<T> INSTANCE() {
        return INSTANCE;
    }


    @SuppressWarnings("unchecked")
    public static <T> NaturalComparator<T> INSTANCE(Class<T> c) {
        return INSTANCE;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public int compare(T o1, T o2) {
        return ((Comparable) o1).compareTo(o2);
    }
}
