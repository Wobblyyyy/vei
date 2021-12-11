package org.magicwerk.brownies.collections.helper;

import org.magicwerk.brownies.collections.BigList;
import org.magicwerk.brownies.collections.GapList;
import org.magicwerk.brownies.collections.IList;
import org.magicwerk.brownies.collections.primitive.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;


public class GapLists extends GapListPrimitives {


    public static IList<?> createWrapperList(Class<?> type) {
        if (type == int.class) {
        } else if (type == long.class) {
        } else if (type == double.class) {
        } else if (type == float.class) {
        } else if (type == boolean.class) {
        } else if (type == byte.class) {
        } else if (type == char.class) {
            return new CharObjGapList();
        } else if (type == short.class) {
        } else {
        }
        throw new IllegalArgumentException("Primitive type expected: " + type);
    }


    public static IList<?> createWrapperList(Class<?> type, int capacity) {
        if (type == int.class) {
        } else if (type == long.class) {
        } else if (type == double.class) {
        } else if (type == float.class) {
        } else if (type == boolean.class) {
        } else if (type == byte.class) {
        } else if (type == char.class) {
            return new CharObjGapList(capacity);
        } else if (type == short.class) {
        } else {
        }
        throw new IllegalArgumentException("Primitive type expected: " + type);
    }


    public static <T> Collector<T, ?, IList<T>> toGapList() {
        return new CollectorImpl<>((Supplier<List<T>>) GapList::new, List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                CollectorImpl.CH_ID);
    }


    public static <T> Collector<T, ?, IList<T>> toBigList() {
        return new CollectorImpl<>((Supplier<List<T>>) BigList::new, List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                CollectorImpl.CH_ID);
    }


    public static int read(Reader reader, CharGapList list, int len) throws IOException {
        return GapListPrimitives.read(reader, list, len);
    }


    public static void add(CharSequence str, CharGapList list, int start, int end) {
        GapListPrimitives.add(str, list, start, end);
    }

    private static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        static final Set<Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Function<A, R> finisher,
                      Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        @SuppressWarnings("unchecked")
        private static <I, R> Function<I, R> castingIdentity() {
            return i -> (R) i;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }

}
