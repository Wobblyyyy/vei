package org.magicwerk.brownies.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


@SuppressWarnings("serial")
public class Key1Set<E, K> extends Key1Collection<E, K> implements Set<E> {


    protected Key1Set() {
    }


    @Override
    protected Builder<E, K> getBuilder() {
        return new Builder<>(this);
    }

    @Override
    public Key1Set<E, K> copy() {
        return (Key1Set<E, K>) super.copy();
    }

    @Override
    public Key1Set<E, K> crop() {
        return (Key1Set<E, K>) super.crop();
    }

    @Override
    public Key1Set<E, K> getAll(E elem) {
        return (Key1Set<E, K>) super.getAll(elem);
    }

    @Override
    public Key1Set<E, K> getAllByKey1(K key) {
        return (Key1Set<E, K>) super.getAllByKey(1, key);
    }


    public static class Builder<E, K> extends Key1Collection.Builder<E, K> {

        public Builder() {
            this(null);
        }


        Builder(Key1Set<E, K> keySet) {
            this.keyColl = keySet;
            initKeyMapBuilder(0);
        }


        @SuppressWarnings("unchecked")
        @Override
        public Key1Set<E, K> build() {
            withSetBehavior(true);
            withElemDuplicates(false);

            if (keyColl == null) {
                keyColl = new Key1Set<>();
            }
            build(keyColl, false);
            init(keyColl);
            return (Key1Set<E, K>) keyColl;
        }


        @Override
        public Builder<E, K> withSetBehavior(boolean setBehavior) {
            if (!setBehavior) {
                KeyCollectionImpl.errorInvalidSetBehavior();
            }
            return (Builder<E, K>) super.withSetBehavior(setBehavior);
        }

        @Override
        public Builder<E, K> withElemDuplicates(boolean allowDuplicates) {
            if (allowDuplicates) {
                KeyCollectionImpl.errorInvaliDuplicates();
            }
            return (Builder<E, K>) super.withElemDuplicates(allowDuplicates);
        }

        @Override
        public Builder<E, K> withElemDuplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            if (allowDuplicates || allowDuplicatesNull) {
                KeyCollectionImpl.errorInvaliDuplicates();
            }
            return (Builder<E, K>) super.withElemDuplicates(allowDuplicates, allowDuplicatesNull);
        }


        @Override
        public Builder<E, K> withNull(boolean allowNull) {
            return (Builder<E, K>) super.withNull(allowNull);
        }

        @Override
        public Builder<E, K> withConstraint(Predicate<E> constraint) {
            return (Builder<E, K>) super.withConstraint(constraint);
        }


        @Override
        public Builder<E, K> withBeforeInsertTrigger(Consumer<E> trigger) {
            return (Builder<E, K>) super.withBeforeInsertTrigger(trigger);
        }

        @Override
        public Builder<E, K> withAfterInsertTrigger(Consumer<E> trigger) {
            return (Builder<E, K>) super.withAfterInsertTrigger(trigger);
        }

        @Override
        public Builder<E, K> withBeforeDeleteTrigger(Consumer<E> trigger) {
            return (Builder<E, K>) super.withBeforeDeleteTrigger(trigger);
        }

        @Override
        public Builder<E, K> withAfterDeleteTrigger(Consumer<E> trigger) {
            return (Builder<E, K>) super.withAfterDeleteTrigger(trigger);
        }


        @Override
        public Builder<E, K> withCapacity(int capacity) {
            return (Builder<E, K>) super.withCapacity(capacity);
        }

        @Override
        public Builder<E, K> withContent(Collection<? extends E> elements) {
            return (Builder<E, K>) super.withContent(elements);
        }

        @Override
        public Builder<E, K> withContent(E... elements) {
            return (Builder<E, K>) super.withContent(elements);
        }

        @Override
        public Builder<E, K> withMaxSize(int maxSize) {
            return (Builder<E, K>) super.withMaxSize(maxSize);
        }


        @Override
        public Builder<E, K> withElemSet() {
            return (Builder<E, K>) super.withElemSet();
        }

        @Override
        public Builder<E, K> withOrderByElem(boolean orderBy) {
            return (Builder<E, K>) super.withOrderByElem(orderBy);
        }

        @Override
        public Builder<E, K> withElemNull(boolean allowNull) {
            return (Builder<E, K>) super.withElemNull(allowNull);
        }

        @Override
        public Builder<E, K> withElemSort(boolean sort) {
            return (Builder<E, K>) super.withElemSort(sort);
        }

        @Override
        public Builder<E, K> withElemSort(Comparator<? super E> comparator) {
            return (Builder<E, K>) super.withElemSort(comparator);
        }

        @Override
        public Builder<E, K> withElemSort(Comparator<? super E> comparator, boolean sortNullsFirst) {
            return (Builder<E, K>) super.withElemSort(comparator, sortNullsFirst);
        }

        @Override
        public Builder<E, K> withPrimaryElem() {
            return (Builder<E, K>) super.withPrimaryElem();
        }

        @Override
        public Builder<E, K> withUniqueElem() {
            return (Builder<E, K>) super.withUniqueElem();
        }


        @Override
        public Builder<E, K> withKey1Map(Function<? super E, K> mapper) {
            return (Builder<E, K>) withKeyMap(1, mapper);
        }


        @Override
        public Builder<E, K> withPrimaryKey1Map(Function<? super E, K> mapper) {
            return (Builder<E, K>) super.withPrimaryKeyMap(1, mapper);
        }


        @Override
        public Builder<E, K> withUniqueKey1Map(Function<? super E, K> mapper) {
            return (Builder<E, K>) super.withUniqueKeyMap(1, mapper);
        }

        @Override
        public Builder<E, K> withOrderByKey1(boolean orderBy) {
            return (Builder<E, K>) super.withOrderByKey1(orderBy);
        }

        @Override
        public Builder<E, K> withKey1Null(boolean allowNull) {
            return (Builder<E, K>) super.withKey1Null(allowNull);
        }

        @Override
        public Builder<E, K> withKey1Duplicates(boolean allowDuplicates) {
            return (Builder<E, K>) super.withKey1Duplicates(allowDuplicates);
        }

        @Override
        public Builder<E, K> withKey1Duplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            return (Builder<E, K>) super.withKey1Duplicates(allowDuplicates, allowDuplicatesNull);
        }

        @Override
        public Builder<E, K> withKey1Sort(boolean sort) {
            return (Builder<E, K>) super.withKey1Sort(sort);
        }


        @Override
        public Builder<E, K> withKey1Sort(Comparator<? super K> comparator) {
            return (Builder<E, K>) super.withKeySort(1, comparator);
        }


        @Override
        public Builder<E, K> withKey1Sort(Comparator<? super K> comparator, boolean sortNullsFirst) {
            return (Builder<E, K>) super.withKeySort(1, comparator, sortNullsFirst);
        }

    }

}
