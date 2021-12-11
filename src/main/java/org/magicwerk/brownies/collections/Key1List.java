package org.magicwerk.brownies.collections;

import org.magicwerk.brownies.collections.KeyCollectionImpl.BuilderImpl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


@SuppressWarnings("serial")
public class Key1List<E, K> extends KeyListImpl<E> {


    protected Key1List() {
    }

    protected Key1List(boolean copy, Key1List<E, K> that) {
        if (copy) {
            doAssign(that);
        }
    }


    protected Builder<E, K> getBuilder() {
        return new Builder<E, K>(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Key1List<E, K> copy() {
        return (Key1List<E, K>) clone();
    }

    @Override
    public Object clone() {
        if (this instanceof ImmutableKey1List) {
            Key1List<E, K> list = new Key1List<>(false, null);
            list.initCopy(this);
            return list;
        } else {
            return super.clone();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Key1List<E, K> crop() {
        if (this instanceof ImmutableKey1List) {
            Key1List<E, K> list = new Key1List<>(false, null);
            list.initCrop(this);
            return list;
        } else {
            return (Key1List<E, K>) super.crop();
        }
    }


    @Override
    public IList<E> getAll(E elem) {
        return super.getAll(elem);
    }

    @Override
    public int getCount(E elem) {
        return super.getCount(elem);
    }

    @Override
    public IList<E> removeAll(E elem) {
        return super.removeAll(elem);
    }

    @Override
    public Set<E> getDistinct() {
        return super.getDistinct();
    }

    @Override
    public E put(E elem) {
        return super.put(elem);
    }


    public Function<E, K> getKey1Mapper() {
        return (Function<E, K>) super.getKeyMapper(1);
    }


    public Map<K, E> asMap1() {
        return new KeyCollectionAsMap<K, E>(this, 1, false);
    }


    public int indexOfKey1(K key) {
        return super.indexOfKey(1, key);
    }


    public boolean containsKey1(K key) {
        return super.containsKey(1, key);
    }


    public E getByKey1(K key) {
        return super.getByKey(1, key);
    }


    @SuppressWarnings("unchecked")
    public Key1List<E, K> getAllByKey1(K key) {
        return (Key1List<E, K>) super.getAllByKey(1, key);
    }


    public int getCountByKey1(K key) {
        return super.getCountByKey(1, key);
    }


    public E removeByKey1(K key) {
        return super.removeByKey(1, key);
    }


    public Key1List<E, K> removeAllByKey1(K key) {
        return (Key1List<E, K>) super.removeAllByKey(1, key);
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
        return super.putByKey(1, elem, true);
    }

    public E putIfAbsentByKey1(E elem) {
        return super.putByKey(1, elem, false);
    }


    public void invalidateKey1(K oldKey, K newKey, E elem) {
        super.invalidateKey(1, oldKey, newKey, elem);
    }


    @Override
    public Key1List<E, K> unmodifiableList() {
        if (this instanceof ImmutableKey1List) {
            return this;
        } else {
            return new ImmutableKey1List<E, K>(this);
        }
    }


    public static class Builder<E, K> extends BuilderImpl<E> {

        public Builder() {
            this(null);
        }


        Builder(Key1List<E, K> keyList) {
            this.keyList = keyList;
            initKeyMapBuilder(1);
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
        public Builder<E, K> withWindowSize(int maxSize) {
            return (Builder<E, K>) super.withWindowSize(maxSize);
        }

        @Override
        public Builder<E, K> withListBig(boolean bigList) {
            return (Builder<E, K>) super.withListBig(bigList);
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
            return (Builder<E, K>) super.withKeyMap(1, mapper);
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
        public Builder<E, K> withOrderByKey1(Class<?> type) {
            return (Builder<E, K>) super.withOrderByKey1(type);
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


        @SuppressWarnings("unchecked")
        public Key1List<E, K> build() {
            if (keyColl == null) {
                keyColl = new KeyCollectionImpl<E>();
            }
            build(keyColl, true);
            if (keyList == null) {
                keyList = new Key1List<E, K>();
            }
            init(keyColl, keyList);
            return (Key1List<E, K>) keyList;
        }
    }


    protected static class ImmutableKey1List<E, K> extends Key1List<E, K> {


        private static final long serialVersionUID = -1352274047348922584L;


        protected ImmutableKey1List(Key1List<E, K> that) {
            super(true, that);
        }

        @Override
        protected void doEnsureCapacity(int capacity) {
            error();
        }

        @Override
        protected boolean doAdd(int index, E elem) {
            error();
            return false;
        }

        @Override
        protected E doSet(int index, E elem) {
            error();
            return null;
        }

        @Override
        protected E doReSet(int index, E elem) {
            error();
            return null;
        }

        @Override
        protected E doRemove(int index) {
            error();
            return null;
        }

        @Override
        protected void doRemoveAll(int index, int len) {
            error();
        }

        @Override
        protected void doClear() {
            error();
        }

        @Override
        protected void doModify() {
            error();
        }


        private void error() {
            throw new UnsupportedOperationException("list is immutable");
        }
    }

}
