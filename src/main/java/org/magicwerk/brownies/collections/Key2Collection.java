package org.magicwerk.brownies.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


@SuppressWarnings("serial")
public class Key2Collection<E, K1, K2> extends KeyCollectionImpl<E> {


    protected Key2Collection() {
    }


    protected Builder<E, K1, K2> getBuilder() {
        return new Builder<>(this);
    }

    @Override
    public Key2Collection<E, K1, K2> copy() {
        return (Key2Collection<E, K1, K2>) super.copy();
    }

    @Override
    public Key2Collection<E, K1, K2> crop() {
        return (Key2Collection<E, K1, K2>) super.crop();
    }

    @Override
    public Key2Collection<E, K1, K2> getAll(E elem) {
        return (Key2Collection<E, K1, K2>) super.getAll(elem);
    }


    @Override
    public int getCount(E elem) {
        return super.getCount(elem);
    }

    @Override
    public Key2Collection<E, K1, K2> removeAll(E elem) {
        return (Key2Collection<E, K1, K2>) super.removeAll(elem);
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


    public Function<E, K1> getKey1Mapper() {
        return (Function<E, K1>) super.getKeyMapper(1);
    }


    public Map<K1, E> asMap1() {
        return new KeyCollectionAsMap<K1, E>(this, 1, false);
    }


    public boolean containsKey1(K1 key) {
        return super.containsKey(1, key);
    }


    public E getByKey1(K1 key) {
        return super.getByKey(1, key);
    }


    public Key2Collection<E, K1, K2> getAllByKey1(K1 key) {
        return (Key2Collection<E, K1, K2>) super.getAllByKey(1, key);
    }


    public int getCountByKey1(K1 key) {
        return super.getCountByKey(1, key);
    }


    public E removeByKey1(K1 key) {
        return super.removeByKey(1, key);
    }


    public Key2Collection<E, K1, K2> removeAllByKey1(K1 key) {
        return (Key2Collection<E, K1, K2>) super.removeAllByKey(1, key);
    }


    @SuppressWarnings("unchecked")
    public IList<K1> getAllKeys1() {
        return (IList<K1>) super.getAllKeys(1);
    }


    @SuppressWarnings("unchecked")
    public Set<K1> getDistinctKeys1() {
        return (Set<K1>) super.getDistinctKeys(1);
    }


    public E putByKey1(E elem) {
        return super.putByKey(1, elem);
    }


    public void invalidateKey1(K1 oldKey, K1 newKey, E elem) {
        super.invalidateKey(1, oldKey, newKey, elem);
    }


    public Function<E, K2> getKey2Mapper() {
        return (Function<E, K2>) super.getKeyMapper(2);
    }


    public Map<K2, E> asMap2() {
        return new KeyCollectionAsMap<K2, E>(this, 2, false);
    }


    public boolean containsKey2(K2 key) {
        return super.containsKey(2, key);
    }


    public E getByKey2(K2 key) {
        return super.getByKey(2, key);
    }


    public Key2Collection<E, K1, K2> getAllByKey2(K2 key) {
        return (Key2Collection<E, K1, K2>) super.getAllByKey(2, key);
    }


    public int getCountByKey2(K2 key) {
        return super.getCountByKey(2, key);
    }


    public E removeByKey2(K2 key) {
        return super.removeByKey(2, key);
    }


    public Key2Collection<E, K1, K2> removeAllByKey2(K2 key) {
        return (Key2Collection<E, K1, K2>) super.removeAllByKey(2, key);
    }


    @SuppressWarnings("unchecked")
    public IList<K2> getAllKeys2() {
        return (IList<K2>) super.getAllKeys(2);
    }


    @SuppressWarnings("unchecked")
    public Set<K2> getDistinctKeys2() {
        return (Set<K2>) super.getDistinctKeys(2);
    }


    public E putByKey2(E elem) {
        return super.putByKey(2, elem);
    }


    public void invalidateKey2(K2 oldKey, K2 newKey, E elem) {
        super.invalidateKey(2, oldKey, newKey, elem);
    }


    public static class Builder<E, K1, K2> extends BuilderImpl<E> {

        public Builder() {
            this(null);
        }


        Builder(Key2Collection<E, K1, K2> keyColl) {
            this.keyColl = keyColl;
            initKeyMapBuilder(2);
        }


        @SuppressWarnings("unchecked")
        public Key2Collection<E, K1, K2> build() {
            if (keyColl == null) {
                keyColl = new Key2Collection<>();
            }
            build(keyColl, false);
            init(keyColl);
            return (Key2Collection<E, K1, K2>) keyColl;
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
        public Builder<E, K1, K2> withSetBehavior(boolean setBehavior) {
            return (Builder<E, K1, K2>) super.withSetBehavior(setBehavior);
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
        public Builder<E, K1, K2> withElemDuplicates(boolean allowDuplicates) {
            return (Builder<E, K1, K2>) super.withElemDuplicates(allowDuplicates);
        }

        @Override
        public Builder<E, K1, K2> withElemDuplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            return (Builder<E, K1, K2>) super.withElemDuplicates(allowDuplicates, allowDuplicatesNull);
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


        public Builder<E, K1, K2> withKey1Map(Function<? super E, K1> mapper) {
            return (Builder<E, K1, K2>) super.withKeyMap(1, mapper);
        }


        public Builder<E, K1, K2> withPrimaryKey1Map(Function<? super E, K1> mapper) {
            return (Builder<E, K1, K2>) super.withPrimaryKeyMap(1, mapper);
        }


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


        public Builder<E, K1, K2> withKey1Sort(Comparator<? super K1> comparator) {
            return (Builder<E, K1, K2>) super.withKeySort(1, comparator);
        }


        public Builder<E, K1, K2> withKey1Sort(Comparator<? super K1> comparator, boolean sortNullsFirst) {
            return (Builder<E, K1, K2>) super.withKeySort(1, comparator, sortNullsFirst);
        }


        public Builder<E, K1, K2> withKey2Map(Function<? super E, K2> mapper) {
            return (Builder<E, K1, K2>) super.withKeyMap(2, mapper);
        }


        public Builder<E, K1, K2> withPrimaryKey2Map(Function<? super E, K2> mapper) {
            return (Builder<E, K1, K2>) super.withPrimaryKeyMap(2, mapper);
        }


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


        public Builder<E, K1, K2> withKey2Sort(Comparator<? super K2> comparator) {
            return (Builder<E, K1, K2>) super.withKeySort(2, comparator);
        }


        public Builder<E, K1, K2> withKey2Sort(Comparator<? super K2> comparator, boolean sortNullsFirst) {
            return (Builder<E, K1, K2>) super.withKeySort(2, comparator, sortNullsFirst);
        }

    }

}
