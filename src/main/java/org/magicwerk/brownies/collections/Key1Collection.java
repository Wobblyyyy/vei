package org.magicwerk.brownies.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


@SuppressWarnings("serial")
public class Key1Collection<E, K> extends KeyCollectionImpl<E> {


    protected Key1Collection() {
    }


    protected Builder<E, K> getBuilder() {
        return new Builder<>(this);
    }

    @Override
    public Key1Collection<E, K> copy() {
        return (Key1Collection<E, K>) super.copy();
    }

    @Override
    public Key1Collection<E, K> crop() {
        return (Key1Collection<E, K>) super.crop();
    }

    @Override
    public Key1Collection<E, K> getAll(E elem) {
        return (Key1Collection<E, K>) super.getAll(elem);
    }


    @Override
    public int getCount(E elem) {
        return super.getCount(elem);
    }

    @Override
    public Key1Collection<E, K> removeAll(E elem) {
        return (Key1Collection<E, K>) super.removeAll(elem);
    }

    @Override
    public Set<E> getDistinct() {
        return super.getDistinct();
    }

    @Override
    public E put(E elem) {
        return super.put(elem);
    }

    @Override
    public void invalidate(E elem) {
        super.invalidate(elem);
    }


    public Function<E, K> getKey1Mapper() {
        return (Function<E, K>) super.getKeyMapper(1);
    }


    public Map<K, E> asMap1() {
        return new KeyCollectionAsMap<K, E>(this, 1, false);
    }


    public boolean containsKey1(K key) {
        return super.containsKey(1, key);
    }


    public E getByKey1(K key) {
        return super.getByKey(1, key);
    }


    public Key1Collection<E, K> getAllByKey1(K key) {
        return (Key1Collection<E, K>) super.getAllByKey(1, key);
    }


    public int getCountByKey1(K key) {
        return super.getCountByKey(1, key);
    }


    public E removeByKey1(K key) {
        return super.removeByKey(1, key);
    }


    public Key1Collection<E, K> removeAllByKey1(K key) {
        return (Key1Collection<E, K>) super.removeAllByKey(1, key);
    }


    @SuppressWarnings("unchecked")
    public IList<K> getAllKeys1() {
        return (GapList<K>) super.getAllKeys(1);
    }


    @SuppressWarnings("unchecked")
    public Set<K> getDistinctKeys1() {
        return (Set<K>) super.getDistinctKeys(1);
    }


    public E putByKey1(E elem) {
        return super.putByKey(1, elem);
    }


    public void invalidateKey1(K oldKey, K newKey, E elem) {
        super.invalidateKey(1, oldKey, newKey, elem);
    }


    public static class Builder<E, K> extends BuilderImpl<E> {

        public Builder() {
            this(null);
        }


        Builder(Key1Collection<E, K> keyColl) {
            this.keyColl = keyColl;
            initKeyMapBuilder(1);
        }


        @SuppressWarnings("unchecked")
        public Key1Collection<E, K> build() {
            if (keyColl == null) {
                keyColl = new Key1Collection<>();
            }
            build(keyColl, false);
            init(keyColl);
            return (Key1Collection<E, K>) keyColl;
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
        public Builder<E, K> withSetBehavior(boolean setBehavior) {
            return (Builder<E, K>) super.withSetBehavior(setBehavior);
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
        public Builder<E, K> withElemDuplicates(boolean allowDuplicates) {
            return (Builder<E, K>) super.withElemDuplicates(allowDuplicates);
        }

        @Override
        public Builder<E, K> withElemDuplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            return (Builder<E, K>) super.withElemDuplicates(allowDuplicates, allowDuplicatesNull);
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


        public Builder<E, K> withKey1Map(Function<? super E, K> mapper) {
            return (Builder<E, K>) withKeyMap(1, mapper);
        }


        public Builder<E, K> withPrimaryKey1Map(Function<? super E, K> mapper) {
            return (Builder<E, K>) super.withPrimaryKeyMap(1, mapper);
        }


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


        public Builder<E, K> withKey1Sort(Comparator<? super K> comparator) {
            return (Builder<E, K>) super.withKeySort(1, comparator);
        }


        public Builder<E, K> withKey1Sort(Comparator<? super K> comparator, boolean sortNullsFirst) {
            return (Builder<E, K>) super.withKeySort(1, comparator, sortNullsFirst);
        }

    }

}
