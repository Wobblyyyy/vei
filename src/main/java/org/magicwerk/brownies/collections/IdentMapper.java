package org.magicwerk.brownies.collections;

import java.io.Serializable;
import java.util.function.Function;


@SuppressWarnings("serial")
public class IdentMapper<E> implements Function<E, E>, Serializable {


    @SuppressWarnings("rawtypes")
    public static final IdentMapper INSTANCE = new IdentMapper();


    private IdentMapper() {
    }

    @Override
    public E apply(E v) {
        return v;
    }
}