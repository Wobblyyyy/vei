package org.magicwerk.brownies.collections;

import org.magicwerk.brownies.collections.helper.Option;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Function;


@SuppressWarnings("serial")
public abstract class KeyListImpl<E> extends IList<E> {


    private static final boolean DEBUG_CHECK = false;

    KeyCollectionImpl<E> keyColl;

    IList<E> list;


    KeyListImpl() {
    }

    protected KeyListImpl(boolean copy, KeyListImpl<E> that) {
        if (copy) {
            doAssign(that);
        }
    }


    private void debugCheck() {
        keyColl.debugCheck();

        assert (keyColl.keyList == this);
        assert (list.size() == keyColl.size() || (keyColl.size() == 0 && keyColl.keyMaps == null));
    }

    @Override
    protected void doAssign(IList<E> that) {
        KeyListImpl<E> list = (KeyListImpl<E>) that;
        this.keyColl = list.keyColl;
        this.list = list.list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public KeyListImpl<E> copy() {
        return (KeyListImpl<E>) clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        KeyListImpl<E> copy = (KeyListImpl<E>) super.clone();
        copy.initCopy(this);
        return copy;
    }


    @SuppressWarnings("unchecked")
    public KeyListImpl<E> crop() {
        KeyListImpl<E> copy = (KeyListImpl<E>) super.clone();
        copy.initCrop(this);
        return copy;
    }


    public IList<E> unwrap() {
        IList<E> l = list;
        this.initCrop(this);
        return l;
    }


    @SuppressWarnings("unchecked")
    void initCrop(KeyListImpl<E> that) {

        keyColl = new KeyCollectionImpl<E>();
        keyColl.initCrop(that.keyColl);
        if (keyColl.keyList != null) {
            keyColl.keyList = this;
        }


        if (that.keyColl.keyMaps != null && that.keyColl.keyMaps[0] != null && that.list == that.keyColl.keyMaps[0].keysList) {
            list = (IList<E>) keyColl.keyMaps[0].keysList;
        } else {
            list = that.list.doCreate(-1);
        }

        if (DEBUG_CHECK)
            debugCheck();
    }


    @SuppressWarnings("unchecked")
    void initCopy(KeyListImpl<E> that) {

        keyColl = new KeyCollectionImpl<E>();
        keyColl.initCopy(that.keyColl);
        if (keyColl.keyList != null) {
            keyColl.keyList = this;
        }


        if (that.keyColl.keyMaps != null && that.keyColl.keyMaps[0] != null && that.list == that.keyColl.keyMaps[0].keysList) {
            list = (IList<E>) keyColl.keyMaps[0].keysList;
        } else {
            list = that.list.doCreate(-1);
            list.addAll(that.list);
        }

        if (DEBUG_CHECK)
            debugCheck();
    }

    @Override
    protected void doClone(IList<E> that) {

    }


    public Set<E> asSet() {
        return new CollectionAsSet<E>(this, false);
    }


    public boolean isSorted() {
        return keyColl.isSorted();
    }

    @Override
    public int capacity() {
        return list.capacity();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    protected E doGet(int index) {
        return list.doGet(index);
    }

    @Override
    protected <T> void doGetAll(T[] array, int index, int len) {
        list.doGetAll(array, index, len);
    }

    @Override
    public boolean contains(Object elem) {
        if (keyColl.keyMaps != null) {
            return keyColl.contains(elem);
        } else {
            return super.contains(elem);
        }
    }


    @Override
    public boolean add(E elem) {
        return super.add(elem);
    }


    public boolean addIf(E elem) {
        try {
            return super.add(elem);
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void add(int index, E elem) {
        super.add(index, elem);
    }


    @Override
    public E set(int index, E elem) {
        return super.set(index, elem);
    }

    @Override
    public void clear() {
        keyColl.clear();
        list.clear();
    }

    @Override
    public void ensureCapacity(int minCapacity) {


        if (keyColl.maxSize != 0) {
            minCapacity = Math.min(minCapacity, keyColl.maxSize);
        }
        super.ensureCapacity(minCapacity);
    }

    @Override
    protected boolean doAdd(int index, E elem) {

        keyColl.checkElemAllowed(elem);


        if (keyColl.maxSize != 0 && size() >= keyColl.maxSize) {
            if (keyColl.movingWindow) {
                if (index == 0) {

                    return false;
                }
                if (index == -1) {
                    index = size();
                }
                doRemove(0);
                index = index - 1;
            } else {
                KeyCollectionImpl.errorMaxSize();
            }
        }


        if (keyColl.isSorted()) {

            if (index == -1) {
                index = keyColl.binarySearchSorted(elem);
                if (index < 0) {
                    index = -index - 1;
                }
            }
            keyColl.addSorted(index, elem);

            if (!keyColl.isSortedByElem()) {
                list.doAdd(index, elem);
            }
        } else {

            keyColl.addUnsorted(elem);
            if (index == -1) {

                index = list.size();
            }
            list.doAdd(index, elem);
        }

        if (DEBUG_CHECK)
            debugCheck();
        return true;
    }

    @Override
    protected E doSet(int index, E elem) {
        keyColl.checkElemAllowed(elem);

        E remove = doGet(index);
        if (keyColl.isSorted()) {
            keyColl.setSorted(index, elem, remove);

        } else {
            keyColl.remove(remove);
            try {
                keyColl.add(elem);
            } catch (RuntimeException e) {
                keyColl.add(remove);
                throw e;
            }
        }
        list.doSet(index, elem);
        if (DEBUG_CHECK)
            debugCheck();
        return remove;
    }

    @Override
    protected E doRemove(int index) {
        E removed = list.get(index);
        keyColl.remove(removed);
        if (!keyColl.isSortedByElem()) {
            list.remove(index);
        }
        if (DEBUG_CHECK)
            debugCheck();
        return removed;
    }

    @Override
    protected void doRemoveAll(int index, int len) {
        if (keyColl.isSortedByElem()) {
            for (int i = 0; i < len; i++) {
                E removed = list.get(index);
                keyColl.remove(removed);
            }
        } else {
            for (int i = 0; i < len; i++) {
                E removed = list.get(index + i);
                keyColl.remove(removed);
            }
            list.doRemoveAll(index, len);
        }
        if (DEBUG_CHECK)
            debugCheck();
    }

    @Override
    protected E doReSet(int index, E elem) {
        return list.doReSet(index, elem);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int indexOf(Object elem) {
        if (keyColl.isSorted()) {
            return keyColl.indexOfSorted((E) elem);
        } else {
            return super.indexOf(elem);
        }
    }


    public int indexOfKey(int keyIndex, Object key) {
        return indexOfKey(keyIndex, key, 0);
    }


    public int indexOfKey(int keyIndex, Object key, int start) {
        int size = size();
        for (int i = start; i < size; i++) {
            Object elemKey = keyColl.getKey(keyIndex, doGet(i));
            if (equalsElem(elemKey, key)) {
                return i;
            }
        }
        return -1;
    }


    public boolean containsKey(int keyIndex, Object key) {
        return indexOfKey(keyIndex, key) != -1;
    }


    public Function<E, Object> getKeyMapper(int keyIndex) {
        return keyColl.getKeyMapper(keyIndex);
    }


    public E getByKey(int keyIndex, Object key) {
        return keyColl.getByKey(keyIndex, key);
    }


    public IList<E> getAllByKey(int keyIndex, Object key) {
        IList<E> list = crop();
        keyColl.getAllByKey(keyIndex, key, list);
        return list;
    }


    public int getCountByKey(int keyIndex, Object key) {
        return keyColl.getCountByKey(keyIndex, key);
    }


    protected E removeByKey(int keyIndex, Object key) {
        Option<E> removed = keyColl.doRemoveByKey(keyIndex, key);
        if (removed.hasValue()) {
            int index = list.indexOf(removed.getValue());
            if (index == -1) {
                KeyCollectionImpl.errorInvalidData();
            }
            list.doRemove(index);
        }
        if (DEBUG_CHECK)
            debugCheck();
        return removed.getValueOrNull();
    }


    protected E putByKey(int keyIndex, E elem, boolean replace) {
        int index;
        if (keyIndex == 0 && (keyColl.keyMaps == null || keyColl.keyMaps[0] == null)) {
            index = indexOf(elem);
        } else {
            Object key = keyColl.getKey(keyIndex, elem);
            index = indexOfKey(keyIndex, key);
        }
        E replaced = null;
        if (index == -1) {
            doAdd(-1, elem);
        } else {
            if (replace) {
                replaced = doSet(index, elem);
            }
        }
        if (DEBUG_CHECK)
            debugCheck();
        return replaced;
    }


    protected IList<E> removeAllByKey(int keyIndex, Object key) {
        IList<E> removeds = crop();
        keyColl.removeAllByKey(keyIndex, key, removeds);
        if (!removeds.isEmpty()) {
            if (!keyColl.isSortedByElem()) {
                if (!list.removeAll(removeds)) {
                    KeyCollectionImpl.errorInvalidData();
                }
            }
        }
        if (DEBUG_CHECK)
            debugCheck();
        return removeds;
    }


    protected IList<Object> getAllKeys(int keyIndex) {
        Function<E, Object> mapper = keyColl.getKeyMap(keyIndex).mapper;
        GapList<Object> list = GapList.create();
        for (E obj : this) {
            list.add(mapper.apply(obj));
        }
        return list;
    }


    public Set<?> getDistinctKeys(int keyIndex) {
        return keyColl.getDistinctKeys(keyIndex);
    }

    @Override
    public <K> int binarySearch(int index, int len, K key, Comparator<? super K> comparator) {


        return list.binarySearch(index, len, key, comparator);
    }

    @Override
    public void sort(int index, int len, Comparator<? super E> comparator) {
        if (keyColl.isSorted()) {

            if (keyColl.isSortedByElem()) {
                if (GapList.equalsElem(comparator, keyColl.getElemSortComparator())) {
                    return;
                }
            }
            throw new IllegalArgumentException("Different comparator specified for sorted list");
        } else {
            list.sort(index, len, comparator);
        }
    }


    @Override
    public IList<E> getAll(E elem) {
        if (keyColl.hasElemSet()) {
            return getAllByKey(0, elem);
        } else {
            return list.getAll(elem);
        }
    }

    @Override
    public int getCount(E elem) {
        if (keyColl.hasElemSet()) {
            return getCountByKey(0, elem);
        } else {
            return list.getCount(elem);
        }
    }

    @Override
    public IList<E> removeAll(E elem) {
        if (keyColl.hasElemSet()) {
            return removeAllByKey(0, elem);
        } else {
            return list.removeAll(elem);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<E> getDistinct() {
        if (keyColl.hasElemSet()) {
            return (Set<E>) getDistinctKeys(0);
        } else {
            return super.getDistinct();
        }
    }


    protected E put(E elem) {
        int index = indexOf(elem);
        if (index != -1) {
            return set(index, elem);
        } else {
            add(elem);
            return null;
        }
    }


    protected void invalidate(E elem) {
        keyColl.invalidate(elem);
        if (keyColl.isSorted() && !keyColl.isSortedByElem()) {
            int oldIndex = super.indexOf(elem);
            int newIndex = keyColl.indexOfSorted(elem);
            if (oldIndex != newIndex) {
                list.doRemove(oldIndex);
                if (oldIndex < newIndex) {
                    newIndex--;
                }
                list.doAdd(newIndex, elem);
            }
        }
        if (DEBUG_CHECK)
            debugCheck();
    }


    protected void invalidateKey(int keyIndex, Object oldKey, Object newKey, E elem) {
        elem = keyColl.doInvalidateKey(keyIndex, oldKey, newKey, elem);
        if (keyColl.orderByKey == keyIndex && list != null) {
            list.doRemove(super.indexOf(elem));
            int index = keyColl.indexOfSorted(elem);
            list.doAdd(index, elem);
        }
        if (DEBUG_CHECK)
            debugCheck();
    }

    @Override
    protected E getDefaultElem() {
        return null;
    }

    @Override
    protected void doEnsureCapacity(int minCapacity) {
        list.doEnsureCapacity(minCapacity);
    }

    @Override
    public void trimToSize() {
        list.trimToSize();
    }

    @Override
    protected IList<E> doCreate(int capacity) {
        return list.doCreate(capacity);
    }


}
