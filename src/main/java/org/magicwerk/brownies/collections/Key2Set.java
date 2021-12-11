package org.magicwerk.brownies.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


@SuppressWarnings("serial")
public class Key2Set<E, K1, K2> extends Key2Collection<E, K1, K2> implements Set<E> {


    protected Key2Set() {
    }


    @Override
    protected Builder<E, K1, K2> getBuilder() {
        return new Builder<>(this);
    }

    @Override
    public Key2Set<E, K1, K2> copy() {
        return (Key2Set<E, K1, K2>) super.copy();
    }

    @Override
    public Key2Set<E, K1, K2> crop() {
        return (Key2Set<E, K1, K2>) super.crop();
    }

    @Override
    public Key2Set<E, K1, K2> getAll(E elem) {
        return (Key2Set<E, K1, K2>) super.getAll(elem);
    }

    @Override
    public Key2Set<E, K1, K2> getAllByKey1(K1 key) {
        return (Key2Set<E, K1, K2>) super.getAllByKey(1, key);
    }

    @Override
    public Key2Set<E, K1, K2> getAllByKey2(K2 key) {
        return (Key2Set<E, K1, K2>) super.getAllByKey(2, key);
    }


    public static class Builder<E, K1, K2> extends Key2Collection.Builder<E, K1, K2> {

        public Builder() {
            this(null);
        }


        Builder(Key2Set<E, K1, K2> keySet) {
            this.keyColl = keySet;
            initKeyMapBuilder(0);
        }


        @SuppressWarnings("unchecked")
        @Override
        public Key2Set<E, K1, K2> build() {
            withSetBehavior(true);
            withElemDuplicates(false);

            if (keyColl == null) {
                keyColl = new Key2Set<>();
            }
            build(keyColl, false);
            init(keyColl);
            return (Key2Set<E, K1, K2>) keyColl;
        }


        @Override
        public Builder<E, K1, K2> withSetBehavior(boolean setBehavior) {
            if (!setBehavior) {
                KeyCollectionImpl.errorInvalidSetBehavior();
            }
            return (Builder<E, K1, K2>) super.withSetBehavior(setBehavior);
        }

        @Override
        public Builder<E, K1, K2> withElemDuplicates(boolean allowDuplicates) {
            if (allowDuplicates) {
                KeyCollectionImpl.errorInvaliDuplicates();
            }
            return (Builder<E, K1, K2>) super.withElemDuplicates(allowDuplicates);
        }

        @Override
        public Builder<E, K1, K2> withElemDuplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            if (allowDuplicates || allowDuplicatesNull) {
                KeyCollectionImpl.errorInvaliDuplicates();
            }
            return (Builder<E, K1, K2>) super.withElemDuplicates(allowDuplicates, allowDuplicatesNull);
        }


        @Override
        public Builder<E, K1, K2> withNull(boolean allowNull) {
            return (Builder<E, K1, K2>) super.withNull(allowNull);
        }

        @Override
        public Builder<E, K1, K2> withConstraint(Predicate<E> constraint) {
            return (Builder<E, K1, K2>) super.withConstraint(constraint);
        }


        @Override
        public Builder<E, K1, K2> withBeforeInsertTrigger(Consumer<E> trigger) {
            return (Builder<E, K1, K2>) super.withBeforeInsertTrigger(trigger);
        }

        @Override
        public Builder<E, K1, K2> withAfterInsertTrigger(Consumer<E> trigger) {
            return (Builder<E, K1, K2>) super.withAfterInsertTrigger(trigger);
        }

        @Override
        public Builder<E, K1, K2> withBeforeDeleteTrigger(Consumer<E> trigger) {
            return (Builder<E, K1, K2>) super.withBeforeDeleteTrigger(trigger);
        }

        @Override
        public Builder<E, K1, K2> withAfterDeleteTrigger(Consumer<E> trigger) {
            return (Builder<E, K1, K2>) super.withAfterDeleteTrigger(trigger);
        }


        @Override
        public Builder<E, K1, K2> withCapacity(int capacity) {
            return (Builder<E, K1, K2>) super.withCapacity(capacity);
        }

        @Override
        public Builder<E, K1, K2> withContent(Collection<? extends E> elements) {
            return (Builder<E, K1, K2>) super.withContent(elements);
        }

        @Override
        public Builder<E, K1, K2> withContent(E... elements) {
            return (Builder<E, K1, K2>) super.withContent(elements);
        }

        @Override
        public Builder<E, K1, K2> withMaxSize(int maxSize) {
            return (Builder<E, K1, K2>) super.withMaxSize(maxSize);
        }


        @Override
        public Builder<E, K1, K2> withElemSet() {
            return (Builder<E, K1, K2>) super.withElemSet();
        }

        @Override
        public Builder<E, K1, K2> withOrderByElem(boolean orderBy) {
            return (Builder<E, K1, K2>) super.withOrderByElem(orderBy);
        }

        @Override
        public Builder<E, K1, K2> withElemNull(boolean allowNull) {
            return (Builder<E, K1, K2>) super.withElemNull(allowNull);
        }

        @Override
        public Builder<E, K1, K2> withElemSort(boolean sort) {
            return (Builder<E, K1, K2>) super.withElemSort(sort);
        }

        @Override
        public Builder<E, K1, K2> withElemSort(Comparator<? super E> comparator) {
            return (Builder<E, K1, K2>) super.withElemSort(comparator);
        }

        @Override
        public Builder<E, K1, K2> withElemSort(Comparator<? super E> comparator, boolean sortNullsFirst) {
            return (Builder<E, K1, K2>) super.withElemSort(comparator, sortNullsFirst);
        }

        @Override
        public Builder<E, K1, K2> withPrimaryElem() {
            return (Builder<E, K1, K2>) super.withPrimaryElem();
        }

        @Override
        public Builder<E, K1, K2> withUniqueElem() {
            return (Builder<E, K1, K2>) super.withUniqueElem();
        }


        @Override
        public Builder<E, K1, K2> withKey1Map(Function<? super E, K1> mapper) {
            return (Builder<E, K1, K2>) super.withKeyMap(1, mapper);
        }


        @Override
        public Builder<E, K1, K2> withPrimaryKey1Map(Function<? super E, K1> mapper) {
            return (Builder<E, K1, K2>) super.withPrimaryKeyMap(1, mapper);
        }


        @Override
        public Builder<E, K1, K2> withUniqueKey1Map(Function<? super E, K1> mapper) {
            return (Builder<E, K1, K2>) super.withUniqueKeyMap(1, mapper);
        }

        @Override
        public Builder<E, K1, K2> withOrderByKey1(boolean orderBy) {
            return (Builder<E, K1, K2>) super.withOrderByKey1(orderBy);
        }

        @Override
        public Builder<E, K1, K2> withKey1Null(boolean allowNull) {
            return (Builder<E, K1, K2>) super.withKey1Null(allowNull);
        }

        @Override
        public Builder<E, K1, K2> withKey1Duplicates(boolean allowDuplicates) {
            return (Builder<E, K1, K2>) super.withKey1Duplicates(allowDuplicates);
        }

        @Override
        public Builder<E, K1, K2> withKey1Duplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            return (Builder<E, K1, K2>) super.withKey1Duplicates(allowDuplicates, allowDuplicatesNull);
        }

        @Override
        public Builder<E, K1, K2> withKey1Sort(boolean sort) {
            return (Builder<E, K1, K2>) super.withKey1Sort(sort);
        }


        @Override
        public Builder<E, K1, K2> withKey1Sort(Comparator<? super K1> comparator) {
            return (Builder<E, K1, K2>) super.withKeySort(1, comparator);
        }


        @Override
        public Builder<E, K1, K2> withKey1Sort(Comparator<? super K1> comparator, boolean sortNullsFirst) {
            return (Builder<E, K1, K2>) super.withKeySort(1, comparator, sortNullsFirst);
        }


        @Override
        public Builder<E, K1, K2> withKey2Map(Function<? super E, K2> mapper) {
            return (Builder<E, K1, K2>) super.withKeyMap(2, mapper);
        }


        @Override
        public Builder<E, K1, K2> withPrimaryKey2Map(Function<? super E, K2> mapper) {
            return (Builder<E, K1, K2>) super.withPrimaryKeyMap(2, mapper);
        }


        @Override
        public Builder<E, K1, K2> withUniqueKey2Map(Function<? super E, K2> mapper) {
            return (Builder<E, K1, K2>) super.withUniqueKeyMap(2, mapper);
        }

        @Override
        public Builder<E, K1, K2> withOrderByKey2(boolean orderBy) {
            return (Builder<E, K1, K2>) super.withOrderByKey2(orderBy);
        }

        @Override
        public Builder<E, K1, K2> withKey2Null(boolean allowNull) {
            return (Builder<E, K1, K2>) super.withKey2Null(allowNull);
        }

        @Override
        public Builder<E, K1, K2> withKey2Duplicates(boolean allowDuplicates) {
            return (Builder<E, K1, K2>) super.withKey2Duplicates(allowDuplicates);
        }

        @Override
        public Builder<E, K1, K2> withKey2Duplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            return (Builder<E, K1, K2>) super.withKey2Duplicates(allowDuplicates, allowDuplicatesNull);
        }

        @Override
        public Builder<E, K1, K2> withKey2Sort(boolean sort) {
            return (Builder<E, K1, K2>) super.withKey2Sort(sort);
        }


        @Override
        public Builder<E, K1, K2> withKey2Sort(Comparator<? super K2> comparator) {
            return (Builder<E, K1, K2>) super.withKeySort(2, comparator);
        }


        @Override
        public Builder<E, K1, K2> withKey2Sort(Comparator<? super K2> comparator, boolean sortNullsFirst) {
            return (Builder<E, K1, K2>) super.withKeySort(2, comparator, sortNullsFirst);
        }

    }

}
