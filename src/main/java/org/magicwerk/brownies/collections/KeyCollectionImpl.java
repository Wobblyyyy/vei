package org.magicwerk.brownies.collections;

import org.magicwerk.brownies.collections.exceptions.DuplicateKeyException;
import org.magicwerk.brownies.collections.exceptions.KeyException;
import org.magicwerk.brownies.collections.helper.BigLists;
import org.magicwerk.brownies.collections.helper.GapLists;
import org.magicwerk.brownies.collections.helper.IdentMapper;
import org.magicwerk.brownies.collections.helper.NaturalComparator;
import org.magicwerk.brownies.collections.helper.NullComparator;
import org.magicwerk.brownies.collections.helper.Option;
import org.magicwerk.brownies.collections.helper.SortedLists;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


@SuppressWarnings("serial")
public class KeyCollectionImpl<E> implements Collection<E>, Serializable, Cloneable {


    private static final boolean DEBUG_CHECK = false;

    int size;

    int maxSize;


    boolean movingWindow;

    KeyMap<E, Object>[] keyMaps;

    int orderByKey;

    boolean allowNullElem;

    boolean setBehavior;

    Predicate<E> constraint;

    Consumer<E> beforeInsertTrigger;
    Consumer<E> afterInsertTrigger;
    Consumer<E> beforeDeleteTrigger;
    Consumer<E> afterDeleteTrigger;

    KeyListImpl<E> keyList;

    KeyCollectionImpl() {
    }

    static void errorNullElement() {
        throw new KeyException("Constraint violation: null element not allowed");
    }

    static void errorConstraintElement() {
        throw new KeyException("Constraint violation: element not allowed");
    }


    static void errorNullKey() {
        throw new KeyException("Constraint violation: null key not allowed");
    }

    static void errorMaxSize() {
        throw new KeyException("Constraint violation: maximum size reached");
    }

    static void errorDuplicateKey(Object key) {
        throw new DuplicateKeyException(key);
    }

    static void errorInvalidData() {
        throw new IllegalStateException("Invalid data: call update() on change of key data");
    }

    static void errorInvalidIndex() {
        throw new IllegalStateException("Invalid index for sorted list");
    }


    static void errorInvalidateNotSupported() {
        throw new IllegalStateException("Invalidate is not support if elemCount is true");
    }

    static void errorInvalidSetBehavior() {
        throw new IllegalStateException("Invalid configuration: setBehavior must be true");
    }

    static void errorInvaliDuplicates() {
        throw new IllegalStateException("Invalid configuration: duplicates are not allowed");
    }


    void initCopy(KeyCollectionImpl<E> that) {
        size = that.size;

        keyList = that.keyList;

        if (that.keyMaps != null) {
            keyMaps = new KeyMap[that.keyMaps.length];
            for (int i = 0; i < keyMaps.length; i++) {
                if (that.keyMaps[i] != null) {
                    keyMaps[i] = that.keyMaps[i].copy();
                }
            }
        }
        maxSize = that.maxSize;
        movingWindow = that.movingWindow;
        allowNullElem = that.allowNullElem;
        constraint = that.constraint;
        orderByKey = that.orderByKey;
        beforeInsertTrigger = that.beforeInsertTrigger;
        afterInsertTrigger = that.afterInsertTrigger;
        beforeDeleteTrigger = that.beforeDeleteTrigger;
        afterDeleteTrigger = that.afterDeleteTrigger;
    }


    void initCrop(KeyCollectionImpl<E> that) {
        size = 0;

        keyList = that.keyList;

        if (that.keyMaps != null) {
            keyMaps = new KeyMap[that.keyMaps.length];
            for (int i = 0; i < keyMaps.length; i++) {
                if (that.keyMaps[i] != null) {
                    keyMaps[i] = that.keyMaps[i].crop();
                }
            }
        }
        maxSize = that.maxSize;
        movingWindow = that.movingWindow;
        allowNullElem = that.allowNullElem;
        constraint = that.constraint;
        orderByKey = that.orderByKey;
        beforeInsertTrigger = that.beforeInsertTrigger;
        afterInsertTrigger = that.afterInsertTrigger;
        beforeDeleteTrigger = that.beforeDeleteTrigger;
        afterDeleteTrigger = that.afterDeleteTrigger;
    }


    void debugCheck() {
        if (keyMaps != null) {
            for (KeyMap<E, ?> keyMap : keyMaps) {
                if (keyMap != null) {
                    doDebugCheck(keyMap);
                }
            }
        }
    }

    private void doDebugCheck(KeyMap keyMap) {
        if (keyMap.keysMap != null) {
            int count = 0;
            if (keyMap.count) {
                for (Object val : keyMap.keysMap.values()) {
                    count += ((Integer) val);
                }
            } else {
                for (Object obj : keyMap.keysMap.values()) {
                    if (obj instanceof KeyMapList) {
                        count += ((KeyMapList) obj).size();
                    } else {
                        count++;
                    }
                }
            }
            assert (count == size());
        } else if (keyMap.keysList != null) {
            assert (keyMap.keysList.size() == size());
            IList<?> copy = keyMap.keysList.copy();
            copy.sort(keyMap.comparator);
            assert (copy.equals(keyMap.keysList));
        } else {
            assert (false);
        }
    }

    Object getKey(int keyIndex, E elem) {
        return keyMaps[keyIndex].getKey(elem);
    }


    public boolean isSorted() {
        return orderByKey != -1;
    }

    boolean isSortedByElem() {
        return orderByKey == 0;
    }

    Comparator getElemSortComparator() {
        Comparator comparator = keyMaps[orderByKey].comparator;
        if (comparator instanceof NaturalComparator) {
            return null;
        }
        return comparator;
    }


    boolean hasElemSet() {
        return keyMaps != null && keyMaps[0] != null;
    }


    void checkIndex(int loIndex, int hiIndex, E elem) {
        KeyMap keyMap = keyMaps[orderByKey];
        Object key = keyMap.getKey(elem);
        IList<Object> list = keyMap.keysList;
        Comparator<Object> comp = keyMap.comparator;
        if (loIndex >= 0) {
            int cmp = comp.compare(list.doGet(loIndex), key);
            if (cmp == 0) {
                if (elem != null) {
                    if (!keyMap.allowDuplicates) {
                        cmp = 1;
                    }
                } else {
                    if (!keyMap.allowDuplicatesNull) {
                        cmp = 1;
                    }
                }
            }
            if (cmp > 0) {
                errorInvalidIndex();
            }
        }
        if (hiIndex < list.size()) {
            int cmp = comp.compare(key, list.doGet(hiIndex));
            if (cmp == 0) {
                if (elem != null) {
                    if (!keyMap.allowDuplicates) {
                        errorDuplicateKey(key);
                    }
                } else {
                    if (!keyMap.allowDuplicatesNull) {
                        errorDuplicateKey(key);
                    }
                }
            }
            if (cmp > 0) {
                errorInvalidIndex();
            }
        }
    }


    void addSorted(int index, E elem) {

        checkIndex(index - 1, index, elem);

        beforeInsert(elem);


        KeyMap keyMap = keyMaps[orderByKey];
        Object key = keyMap.getKey(elem);
        IList<Object> list = keyMap.keysList;

        if (doAdd(elem, keyMap)) {
            size++;
        }
        list.doAdd(index, key);

        afterInsert(elem);
    }


    void addUnsorted(E elem) {
        beforeInsert(elem);
        if (doAdd(elem, null)) {
            size++;
        }
        afterInsert(elem);
    }


    void setSorted(int index, E elem, E oldElem) {

        checkIndex(index - 1, index + 1, elem);


        KeyMap keyMap = keyMaps[orderByKey];
        Object key = keyMap.getKey(elem);
        IList<Object> list = keyMap.keysList;

        beforeDelete(oldElem);
        beforeInsert(elem);
        doRemove(oldElem, keyMap);
        try {
            doAdd(elem, keyMap);
        } catch (RuntimeException e) {

            doAdd(oldElem, keyMap);
            throw e;
        }
        list.doSet(index, key);
        afterDelete(elem);
        afterInsert(elem);
    }

    int binarySearchSorted(E elem) {
        KeyMap<E, Object> keyMap = keyMaps[orderByKey];
        Object key = keyMap.getKey(elem);
        int index = keyMap.keysList.binarySearch(key, keyMap.comparator);
        if (index >= 0) {
            index++;
            while (index < keyMap.keysList.size()) {
                if (keyMap.comparator.compare(keyMap.keysList.get(index), key) != 0) {
                    break;
                }
                index++;
            }
        }
        return index;
    }

    int indexOfSorted(E elem) {
        KeyMap<E, Object> keyMap = keyMaps[orderByKey];
        Object key = keyMap.getKey(elem);
        int index = keyMap.keysList.binarySearch(key, keyMap.comparator);
        return (index < 0) ? -1 : index;
    }


    void checkElemAllowed(E elem) {
        if (elem == null) {
            if (!allowNullElem) {
                errorNullElement();
            }
        }
        if (constraint != null) {
            if (!constraint.test(elem)) {
                errorConstraintElement();
            }
        }
    }


    private void beforeInsert(E elem) {
        if (beforeInsertTrigger != null) {
            beforeInsertTrigger.accept(elem);
        }
    }


    private void afterInsert(E elem) {
        if (afterInsertTrigger != null) {
            afterInsertTrigger.accept(elem);
        }
    }


    private void beforeDelete(E elem) {
        if (beforeDeleteTrigger != null) {
            beforeDeleteTrigger.accept(elem);
        }
    }


    private void afterDelete(E elem) {
        if (afterDeleteTrigger != null) {
            afterDeleteTrigger.accept(elem);
        }
    }

    @Override
    public boolean add(E elem) {

        checkAddElem(elem);
        beforeInsert(elem);

        try {
            if (doAdd(elem, null)) {
                size++;
            }
        } catch (DuplicateKeyException ex) {
            if (setBehavior) {
                return false;
            }
            errorDuplicateKey(ex.getKey());
        }

        if (DEBUG_CHECK)
            debugCheck();
        afterInsert(elem);
        return true;
    }

    @Override
    public boolean remove(Object elem) {
        return remove(elem, null);
    }

    void checkAddElem(E elem) {
        checkElemAllowed(elem);
        if (maxSize != 0 && size >= maxSize) {
            errorMaxSize();
        }
    }


    boolean remove(Object elem, KeyMap ignore) {
        beforeDelete((E) elem);
        Option<E> removed = doRemove(elem, ignore);
        if (removed.hasValue() || ignore != null) {
            size--;
        }
        if (DEBUG_CHECK)
            debugCheck();
        afterDelete((E) elem);
        return removed.hasValue();
    }


    protected E put(E elem) {
        return putByKey(0, elem);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (keyMaps[0] != null) {
            return keyMaps[0].containsKey(o);
        } else {

            for (int i = 1; i < keyMaps.length; i++) {
                KeyMap<E, Object> km = keyMaps[i];
                if (km == null) {
                    continue;
                }
                try {
                    Object key = km.getKey((E) o);
                    if (km.isPrimaryMap()) {
                        return km.containsKey(key);
                    } else {
                        return km.containsValue(key, o);
                    }
                } catch (Exception e) {

                }
            }

            return keyMaps[1].containsValue(o);
        }
    }

    @Override
    public Iterator<E> iterator() {
        if (orderByKey >= 0) {
            return keyMaps[orderByKey].iteratorValues(this);
        } else {
            return keyList.iterator();
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean added = false;
        for (E e : c) {
            if (add(e)) {
                added = true;
            }
        }
        return added;
    }


    public GapList<E> toList() {
        GapList<E> list = new GapList<>(size());
        for (E e : this) {
            list.add(e);
        }
        return list;
    }

    @Override
    public Object[] toArray() {
        GapList<Object> list = new GapList<>(size());
        for (E e : this) {
            list.add(e);
        }
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        GapList<Object> list = new GapList<>(size());
        for (E e : this) {
            list.add(e);
        }
        return list.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        if (c.size() < size()) {
            for (Iterator<?> i = c.iterator(); i.hasNext(); ) {
                if (remove(i.next())) {
                    changed = true;
                }
            }
        } else {
            for (Iterator<?> i = iterator(); i.hasNext(); ) {
                if (c.contains(i.next())) {
                    i.remove();
                    changed = true;
                }
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (Iterator<?> i = iterator(); i.hasNext(); ) {
            if (!c.contains(i.next())) {
                i.remove();
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        boolean first = true;
        for (Iterator<E> iter = iterator(); iter.hasNext(); ) {
            if (!first) {
                buf.append(", ");
            } else {
                first = false;
            }
            buf.append(iter.next());
        }
        buf.append("]");
        return buf.toString();
    }


    Option<E> doRemove(Object elem, KeyMap ignore) {
        Option<E> removed = Option.EMPTY();
        boolean first = true;
        if (keyMaps != null) {
            for (int i = 0; i < keyMaps.length; i++) {
                if (keyMaps[i] != null && keyMaps[i] != ignore) {
                    Object key = keyMaps[i].getKey((E) elem);
                    Option<E> obj = keyMaps[i].remove(key, true, elem, this);
                    if (first) {
                        if (!obj.hasValue()) {
                            return Option.EMPTY();
                        } else {
                            removed = obj;
                        }
                        first = false;
                    } else {
                        if (!obj.hasValue() || !obj.getValue().equals(removed.getValue())) {
                            errorInvalidData();
                        }
                    }
                }
            }
        }
        return removed;
    }


    public KeyCollectionImpl copy() {
        try {
            KeyCollectionImpl copy = (KeyCollectionImpl) super.clone();
            copy.initCopy(this);
            if (DEBUG_CHECK)
                copy.debugCheck();
            return copy;
        } catch (CloneNotSupportedException e) {

            throw new AssertionError(e);
        }
    }


    public KeyCollectionImpl crop() {
        try {
            KeyCollectionImpl copy = (KeyCollectionImpl) super.clone();
            copy.initCrop(this);
            if (DEBUG_CHECK)
                copy.debugCheck();
            return copy;
        } catch (CloneNotSupportedException e) {

            throw new AssertionError(e);
        }
    }

    @Override
    protected Object clone() {
        return copy();
    }


    protected void initClone(Object that) {
    }


    public Set<E> asSet() {
        return new KeyCollectionAsSet(this, false);
    }

    @Override
    public void clear() {
        if (keyMaps != null) {
            for (KeyMap<E, Object> keyMap : keyMaps) {
                if (keyMap != null) {
                    doClear(keyMap);
                }
            }
        }
        size = 0;
    }

    private void doClear(KeyMap<E, ?> keyMap) {
        if (keyMap.keysMap != null) {
            keyMap.keysMap.clear();
        } else {
            keyMap.keysList.clear();
        }
    }


    boolean doAdd(E elem, KeyMap ignore) {
        if (keyMaps == null) {
            return false;
        }
        RuntimeException error = null;
        int i = 0;
        try {
            for (i = 0; i < keyMaps.length; i++) {
                if (keyMaps[i] != null && keyMaps[i] != ignore) {
                    Object key = keyMaps[i].getKey(elem);
                    keyMaps[i].add(key, elem);
                }
            }
        } catch (RuntimeException e) {
            error = e;
        }


        if (error != null) {
            for (i--; i >= 0; i--) {
                if (keyMaps[i] != null) {
                    Object key = keyMaps[i].getKey(elem);
                    keyMaps[i].remove(key, true, elem, this);
                }
            }
            if (DEBUG_CHECK)
                debugCheck();
            throw error;
        }
        return true;
    }


    protected <K> boolean containsKey(int keyIndex, K key) {
        return getKeyMap(keyIndex).containsKey(key);
    }


    protected Set<?> getDistinctKeys(int keyIndex) {
        return getKeyMap(keyIndex).getDistinctKeys();
    }


    protected IList<?> getAllKeys(int keyIndex) {
        Function mapper = getKeyMap(keyIndex).mapper;
        GapList<Object> list = GapList.create();
        for (Object obj : this) {
            list.add(mapper.apply(obj));
        }
        return list;
    }


    void checkKeyMap(int keyIndex) {
        if (keyMaps == null || keyIndex >= keyMaps.length || keyIndex < 0 || keyMaps[keyIndex] == null) {
            throw new IllegalArgumentException("Invalid key index: " + keyIndex);
        }
    }


    void checkAsMap(int keyIndex) {
        if (keyMaps == null || keyIndex >= keyMaps.length || keyIndex <= 0 || keyMaps[keyIndex] == null) {
            throw new IllegalArgumentException("Invalid key index: " + keyIndex);
        }
        if (keyMaps[keyIndex].allowDuplicates || keyMaps[keyIndex].allowDuplicatesNull) {
            throw new IllegalArgumentException("Key map must not allow duplicates");
        }
    }


    void checkAsSet() {
        if (keyMaps == null || keyMaps[0] == null) {
            throw new IllegalArgumentException("No element set");
        }
        if (keyMaps[0].allowDuplicates || keyMaps[0].allowDuplicatesNull) {
            throw new IllegalArgumentException("Element set must not allow duplicates");
        }
    }

    KeyMap<E, Object> getKeyMap(int keyIndex) {
        checkKeyMap(keyIndex);
        return keyMaps[keyIndex];
    }


    protected Function<E, Object> getKeyMapper(int keyIndex) {
        return getKeyMap(keyIndex).mapper;
    }


    protected E getByKey(int keyIndex, Object key) {
        return getByKey(getKeyMap(keyIndex), key);
    }

    private <K> E getByKey(KeyMap<E, K> keyMap, K key) {

        if (key == null) {
            if (!keyMap.allowNull) {
                return null;
            }
        }

        if (keyMap.keysMap != null) {

            Object obj = keyMap.keysMap.get(key);
            if (obj instanceof KeyMapList) {
                GapList<E> list = (GapList<E>) obj;
                return list.getFirst();
            } else {
                return (E) obj;
            }

        } else {

            int index = SortedLists.binarySearchGet(keyMap.keysList, key, keyMap.comparator);
            if (index >= 0) {
                return keyList.doGet(index);
            } else {
                return null;
            }
        }
    }


    protected Collection<E> getAllByKey(int keyIndex, Object key) {
        Collection<E> coll = crop();
        getAllByKey(keyIndex, key, coll);
        return coll;
    }


    protected void getAllByKey(int keyIndex, Object key, Collection<E> coll) {
        doGetAllByKey(getKeyMap(keyIndex), key, coll);
    }

    private <K> void doGetAllByKey(KeyMap<E, K> keyMap, K key, Collection<E> coll) {

        if (key == null) {
            if (!keyMap.allowNull) {
                return;
            }
        }

        if (keyMap.keysMap != null) {

            Object obj = keyMap.keysMap.get(key);
            if (obj == null) {
            } else if (obj instanceof KeyMapList) {
                coll.addAll((GapList<E>) obj);
            } else {
                coll.add((E) obj);
            }

        } else {


            int index = SortedLists.binarySearchGet(keyMap.keysList, key, keyMap.comparator);
            if (index >= 0) {
                while (true) {
                    coll.add(keyList.doGet(index));
                    index++;
                    if (index == keyMap.keysList.size()) {
                        break;
                    }
                    if (!GapList.equalsElem(keyMap.keysList.get(index), key)) {
                        break;
                    }
                }
            }
        }
    }


    protected int getCountByKey(int keyIndex, Object key) {
        return getCountByKey(getKeyMap(keyIndex), key);
    }

    private <K> int getCountByKey(KeyMap<E, K> keyMap, K key) {

        if (key == null) {
            if (!keyMap.allowNull) {
                return 0;
            }
        }

        if (keyMap.keysMap != null) {

            if (keyMap.count) {
                Integer val = (Integer) keyMap.keysMap.get(key);
                if (val == null) {
                    return 0;
                } else {
                    return val;
                }
            } else {
                Object obj = keyMap.keysMap.get(key);
                if (obj == null) {
                    return 0;
                } else if (obj instanceof KeyMapList) {
                    GapList<E> list = (GapList<E>) obj;
                    return list.size();
                } else {
                    return 1;
                }
            }
        } else {

            int index = SortedLists.binarySearchGet(keyMap.keysList, key, keyMap.comparator);
            if (index >= 0) {
                int count = 0;
                while (true) {
                    count++;
                    index++;
                    if (index == keyMap.keysList.size()) {
                        break;
                    }
                    if (!GapList.equalsElem(keyMap.keysList.get(index), key)) {
                        break;
                    }
                }
                return count;
            } else {
                return 0;
            }
        }
    }


    protected void invalidate(E elem) {
        if (keyMaps != null) {
            for (int i = 0; i < keyMaps.length; i++) {
                if (keyMaps[i] != null) {
                    if (i == 0 && keyMaps[0].count) {
                        errorInvalidateNotSupported();
                    }
                    Option<Object> key = invalidate(keyMaps[i], elem);
                    if (key.hasValue()) {
                        keyMaps[i].add(key.getValue(), elem);
                    }
                }
            }
        }
        if (DEBUG_CHECK)
            debugCheck();
    }


    protected void invalidateKey(int keyIndex, Object oldKey, Object newKey, E elem) {
        doInvalidateKey(keyIndex, oldKey, newKey, elem);
    }

    E doInvalidateKey(int keyIndex, Object oldKey, Object newKey, E elem) {
        KeyMap keyMap = getKeyMap(keyIndex);
        Option<Object> removed;
        if (elem == null) {
            removed = keyMap.remove(oldKey, false, null, this);
        } else {
            removed = keyMap.remove(oldKey, true, elem, this);
        }
        if (!removed.hasValue()) {
            errorInvalidData();
        }
        keyMap.add(newKey, removed.getValue());
        if (DEBUG_CHECK)
            debugCheck();
        return (E) removed.getValue();
    }


    private Option<Object> invalidate(KeyMap keyMap, Object elem) {
        boolean allowDuplicates = keyMap.allowDuplicates;
        Object key = keyMap.getKey(elem);

        if (keyMap.keysMap != null) {
            Iterator<Entry> iter = keyMap.keysMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry entry = iter.next();
                if (GapList.equalsElem(elem, entry.getValue())) {
                    if (GapList.equalsElem(key, entry.getKey())) {
                        return Option.EMPTY();
                    }
                    iter.remove();
                    if (!allowDuplicates) {
                        break;
                    }
                }
            }
        } else {
            assert (keyMap.keysList != null);
            for (int i = 0; i < keyMap.keysList.size(); i++) {
                if (GapList.equalsElem(elem, keyList.doGet(i))) {
                    if (GapList.equalsElem(key, keyMap.keysList.get(i))) {
                        return Option.EMPTY();
                    }
                    keyMap.keysList.remove(i);
                    if (!allowDuplicates) {
                        break;
                    }
                }
            }
        }
        return new Option(key);
    }

    protected E removeByKey(int keyIndex, Object key) {
        return doRemoveByKey(keyIndex, key).getValueOrNull();
    }


    protected Option<E> doRemoveByKey(int keyIndex, Object key) {
        checkKeyMap(keyIndex);
        Option<E> removed = keyMaps[keyIndex].remove(key, false, null, this);
        if (removed.hasValue()) {
            E elem = removed.getValue();
            try {
                beforeDelete(elem);
            } catch (RuntimeException e) {
                keyMaps[keyIndex].add(key, elem);
                throw e;
            }
            for (int i = 0; i < keyMaps.length; i++) {
                if (i != keyIndex && keyMaps[i] != null) {
                    E value = removed.getValue();
                    Object k = keyMaps[i].getKey(value);
                    keyMaps[i].remove(k, true, value, this);
                }
            }
            size--;
            afterDelete(elem);
        }
        if (DEBUG_CHECK)
            debugCheck();
        return removed;
    }


    protected Collection<E> removeAllByKey(int keyIndex, Object key) {
        Collection<E> removeds = crop();
        removeAllByKey(keyIndex, key, removeds);
        return removeds;
    }

    @SuppressWarnings("unchecked")
    protected void removeAllByKey(int keyIndex, Object key, Collection<E> removeds) {
        checkKeyMap(keyIndex);
        keyMaps[keyIndex].doRemoveAllByKey(key, this, removeds);

        for (E elem : removeds) {
            try {
                beforeDelete(elem);
            } catch (RuntimeException e) {
                for (E elem2 : removeds) {
                    keyMaps[keyIndex].add(key, elem2);
                }
                throw e;
            }
            for (int i = 0; i < keyMaps.length; i++) {
                if (i != keyIndex && keyMaps[i] != null) {
                    Object k = keyMaps[i].getKey(elem);
                    keyMaps[i].doRemoveAllByKey(k, this, null);
                }
            }
            afterDelete(elem);
            size--;
        }
        if (DEBUG_CHECK)
            debugCheck();
    }

    protected E putByKey(int keyIndex, E elem) {

        Option<E> removed;
        if (keyIndex == 0) {
            removed = doRemove(elem, null);
            if (removed.hasValue()) {
                size--;
            }
        } else {
            Object key = getKey(keyIndex, elem);
            removed = doRemoveByKey(keyIndex, key);
        }
        if (removed.hasValue()) {
            try {
                beforeDelete(removed.getValue());
            } catch (RuntimeException e) {
                doAdd(removed.getValue(), null);
                throw e;
            }
        }

        try {
            beforeInsert(elem);
        } catch (RuntimeException e) {
            size++;
            doAdd(removed.getValue(), null);
            throw e;
        }


        try {
            checkAddElem(elem);
            doAdd(elem, null);
            size++;
        } catch (RuntimeException e) {
            size++;
            doAdd(removed.getValue(), null);
            throw e;
        }


        if (removed.hasValue()) {
            afterDelete(removed.getValue());
        }
        afterInsert(elem);

        if (DEBUG_CHECK)
            debugCheck();
        return removed.getValueOrNull();
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (setBehavior) {
            if (!(o instanceof Set)) {
                return false;
            }
        } else {
            if (!(o instanceof Collection)) {
                return false;
            }
        }
        Collection c = (Collection) o;
        if (c.size() != size()) {
            return false;
        }
        try {
            return containsAll(c);
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
    }


    @Override
    public int hashCode() {
        int h = 0;
        Iterator<E> i = iterator();
        while (i.hasNext()) {
            E obj = i.next();
            if (obj != null) {
                h += obj.hashCode();
            }
        }
        return h;
    }


    protected Collection<E> getAll(E elem) {
        return getAllByKey(0, elem);
    }


    protected int getCount(E elem) {
        return getCountByKey(0, elem);
    }


    protected Collection<E> removeAll(E elem) {
        return removeAllByKey(0, elem);
    }


    protected Set<E> getDistinct() {
        return (Set<E>) getDistinctKeys(0);
    }


    public static class BuilderImpl<E> {


        KeyCollectionImpl<E> keyColl;
        KeyListImpl<E> keyList;

        boolean allowNullElem = true;
        Predicate<E> constraint;

        Consumer<E> beforeInsertTrigger;
        Consumer<E> afterInsertTrigger;
        Consumer<E> beforeDeleteTrigger;
        Consumer<E> afterDeleteTrigger;

        GapList<KeyMapBuilder<E, Object>> keyMapBuilders = GapList.create();

        Collection<? extends E> collection;
        E[] array;
        int capacity;
        int maxSize;
        Boolean movingWindow;

        boolean setBehavior;

        boolean count;

        boolean useBigList;


        protected BuilderImpl<E> withNull(boolean allowNull) {
            this.allowNullElem = allowNull;
            if (hasElemMapBuilder()) {
                getKeyMapBuilder(0).allowNull = allowNull;
            }
            return this;
        }


        protected BuilderImpl<E> withConstraint(Predicate<E> constraint) {
            this.constraint = constraint;
            return this;
        }


        protected BuilderImpl<E> withBeforeInsertTrigger(Consumer<E> trigger) {
            this.beforeInsertTrigger = trigger;
            return this;
        }


        protected BuilderImpl<E> withAfterInsertTrigger(Consumer<E> trigger) {
            this.afterInsertTrigger = trigger;
            return this;
        }


        protected BuilderImpl<E> withBeforeDeleteTrigger(Consumer<E> trigger) {
            this.beforeDeleteTrigger = trigger;
            return this;
        }


        protected BuilderImpl<E> withAfterDeleteTrigger(Consumer<E> trigger) {
            this.afterDeleteTrigger = trigger;
            return this;
        }


        protected BuilderImpl<E> withCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }


        protected BuilderImpl<E> withContent(Collection<? extends E> elements) {
            this.collection = elements;
            return this;
        }


        protected BuilderImpl<E> withContent(E... elements) {
            this.array = elements;
            return this;
        }


        protected BuilderImpl<E> withMaxSize(int maxSize) {
            if (movingWindow != null) {
                throw new IllegalArgumentException("maximum or window size alreay set");
            }
            this.maxSize = maxSize;
            this.movingWindow = false;
            return this;
        }


        protected BuilderImpl<E> withWindowSize(int maxSize) {
            if (movingWindow != null) {
                throw new IllegalArgumentException("maximum or window size alreay set");
            }
            this.maxSize = maxSize;
            this.movingWindow = true;
            return this;
        }


        protected BuilderImpl<E> withSetBehavior(boolean setBehavior) {
            this.setBehavior = setBehavior;
            return this;
        }


        protected BuilderImpl<E> withElemCount(boolean count) {
            this.count = count;
            return this;
        }


        protected BuilderImpl<E> withElemSet() {
            return withKeyMap(0, IdentMapper.INSTANCE);
        }


        protected BuilderImpl<E> withOrderByElem(boolean orderBy) {
            return withOrderByKey(0, orderBy);
        }


        protected BuilderImpl<E> withOrderByElem(Class<?> type) {
            return withOrderByKey(0, type);
        }


        protected BuilderImpl<E> withElemNull(boolean allowNull) {
            return withKeyNull(0, allowNull);
        }


        protected BuilderImpl<E> withElemDuplicates(boolean allowDuplicates) {
            return withElemDuplicates(allowDuplicates, allowDuplicates);
        }


        protected BuilderImpl<E> withElemDuplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            return withKeyDuplicates(0, allowDuplicates, allowDuplicatesNull);
        }


        protected BuilderImpl<E> withElemSort(boolean sort) {
            return withKeySort(0, sort);
        }


        protected BuilderImpl<E> withElemSort(Comparator<? super E> comparator) {
            return withKeySort(0, comparator);
        }


        protected BuilderImpl<E> withElemSort(Comparator<? super E> comparator, boolean sortNullsFirst) {
            return withKeySort(0, comparator, sortNullsFirst);
        }


        protected BuilderImpl<E> withPrimaryElem() {
            return withPrimaryKeyMap(0, null);
        }


        protected BuilderImpl<E> withUniqueElem() {
            return withUniqueKeyMap(0, null);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        protected BuilderImpl<E> withKeyMap(int keyIndex, Function mapper) {
            if (mapper == null) {
                throw new IllegalArgumentException("Mapper may not be null");
            }
            KeyMapBuilder<?, ?> kmb = getKeyMapBuilder(keyIndex);
            if (kmb.mapper != null) {
                throw new IllegalArgumentException("Mapper already set");
            }
            kmb.mapper = mapper;
            return this;
        }


        protected BuilderImpl<E> withOrderByKey(int keyIndex, boolean orderBy) {
            KeyMapBuilder<?, ?> kmb = getKeyMapBuilder(keyIndex);
            if (kmb.orderBy != null) {
                throw new IllegalArgumentException("Order by already set");
            }
            kmb.orderBy = orderBy;
            return this;
        }

        protected BuilderImpl<E> withOrderByKey(int keyIndex, Class<?> type) {
            if (type == null) {
                throw new IllegalArgumentException("Order by type may not be null");
            }
            if (!type.isPrimitive()) {
                throw new IllegalArgumentException("Class type must be primitive");
            }
            KeyMapBuilder<?, ?> kmb = getKeyMapBuilder(keyIndex);
            if (kmb.orderBy != null) {
                throw new IllegalArgumentException("Order by already set");
            }
            kmb.orderBy = true;
            kmb.primitiveListType = type;
            return this;
        }


        protected BuilderImpl<E> withListType(Class<?> type) {
            if (type == null) {
                throw new IllegalArgumentException("Class type may not be null");
            }
            if (!type.isPrimitive()) {
                throw new IllegalArgumentException("Class type must be primitive");
            }
            KeyMapBuilder<?, ?> kmb = getKeyMapBuilder(0);
            kmb.primitiveListType = type;
            return this;
        }


        protected BuilderImpl<E> withListBig(boolean big) {
            this.useBigList = big;
            return this;
        }

        protected BuilderImpl<E> withKeyNull(int keyIndex, boolean allowNull) {
            KeyMapBuilder<?, ?> kmb = getKeyMapBuilder(keyIndex);
            if (kmb.allowNull != null) {
                throw new IllegalArgumentException("AllowNull already set");
            }
            kmb.allowNull = allowNull;
            if (keyIndex == 0) {
                allowNullElem = allowNull;
            }
            return this;
        }

        protected BuilderImpl<E> withKeyDuplicates(int keyIndex, boolean allowDuplicates, boolean allowDuplicatesNull) {
            KeyMapBuilder<?, ?> kmb = getKeyMapBuilder(keyIndex);
            if (kmb.allowDuplicates != null) {
                throw new IllegalArgumentException("AllowDuplicates already set");
            }
            kmb.allowDuplicates = allowDuplicates;
            kmb.allowDuplicatesNull = allowDuplicatesNull;
            return this;
        }

        protected BuilderImpl<E> withKeySort(int keyIndex, boolean sort) {
            KeyMapBuilder<?, ?> kmb = getKeyMapBuilder(keyIndex);
            if (kmb.sort != null) {
                throw new IllegalArgumentException("Sort already set");
            }
            kmb.sort = sort;
            kmb.comparator = null;
            kmb.comparatorSortsNull = false;
            kmb.sortNullsFirst = false;
            return this;
        }

        protected BuilderImpl<E> withKeySort(int keyIndex, Comparator<?> comparator) {
            KeyMapBuilder<?, ?> kmb = getKeyMapBuilder(keyIndex);
            if (kmb.sort != null) {
                throw new IllegalArgumentException("Sort already set");
            }
            kmb.sort = true;
            kmb.comparator = comparator;
            kmb.comparatorSortsNull = true;
            kmb.sortNullsFirst = false;
            return this;
        }

        protected BuilderImpl<E> withKeySort(int keyIndex, Comparator<?> comparator, boolean sortNullsFirst) {
            KeyMapBuilder<?, ?> kmb = getKeyMapBuilder(keyIndex);
            if (kmb.sort != null) {
                throw new IllegalArgumentException("Sort already set");
            }
            kmb.sort = true;
            kmb.comparator = comparator;
            kmb.comparatorSortsNull = false;
            kmb.sortNullsFirst = sortNullsFirst;
            return this;
        }

        @SuppressWarnings("rawtypes")
        protected BuilderImpl<E> withPrimaryKeyMap(int keyIndex, Function mapper) {
            if (mapper != null) {

                withKeyMap(keyIndex, mapper);
            }
            withKeyNull(keyIndex, false);
            withKeyDuplicates(keyIndex, false, false);
            return this;
        }

        @SuppressWarnings("rawtypes")
        protected BuilderImpl<E> withUniqueKeyMap(int keyIndex, Function mapper) {
            if (mapper != null) {
                withKeyMap(keyIndex, mapper);
            }
            withKeyNull(keyIndex, true);
            withKeyDuplicates(keyIndex, false, true);
            return this;
        }


        protected BuilderImpl<E> withOrderByKey1(boolean orderBy) {
            return withOrderByKey(1, orderBy);
        }


        protected BuilderImpl<E> withOrderByKey1(Class<?> type) {
            return withOrderByKey(1, type);
        }


        protected BuilderImpl<E> withKey1Null(boolean allowNull) {
            return withKeyNull(1, allowNull);
        }


        protected BuilderImpl<E> withKey1Duplicates(boolean allowDuplicates) {
            return withKeyDuplicates(1, allowDuplicates, allowDuplicates);
        }


        protected BuilderImpl<E> withKey1Duplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            return withKeyDuplicates(1, allowDuplicates, allowDuplicatesNull);
        }


        protected BuilderImpl<E> withKey1Sort(boolean sort) {
            return withKeySort(1, sort);
        }


        protected BuilderImpl<E> withOrderByKey2(boolean orderBy) {
            return withOrderByKey(2, orderBy);
        }


        protected BuilderImpl<E> withOrderByKey2(Class<?> type) {
            return withOrderByKey(2, type);
        }


        protected BuilderImpl<E> withKey2Null(boolean allowNull) {
            return withKeyNull(2, allowNull);
        }


        protected BuilderImpl<E> withKey2Duplicates(boolean allowDuplicates) {
            return withKeyDuplicates(2, allowDuplicates, allowDuplicates);
        }


        protected BuilderImpl<E> withKey2Duplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            return withKeyDuplicates(2, allowDuplicates, allowDuplicatesNull);
        }


        protected BuilderImpl<E> withKey2Sort(boolean sort) {
            return withKeySort(2, sort);
        }


        void initKeyMapBuilder(int numKeys) {
            assert (numKeys >= 0);

            keyMapBuilders.initMult(numKeys + 1, null);
        }


        boolean hasElemMapBuilder() {
            return keyMapBuilders.size() > 0 && keyMapBuilders.get(0) != null;
        }

        KeyMapBuilder<E, Object> getKeyMapBuilder(int index) {
            int size = keyMapBuilders.size();
            for (int i = size; i <= index; i++) {
                keyMapBuilders.add(i, null);
            }
            KeyMapBuilder kmb = keyMapBuilders.get(index);
            if (kmb == null) {
                kmb = new KeyMapBuilder();
                keyMapBuilders.set(index, kmb);
            }
            return kmb;
        }

        boolean isTrue(Boolean b) {
            return b != null && b;
        }

        boolean isFalse(Boolean b) {
            return !(b != null && !b);
        }


        KeyMap buildKeyMap(KeyMapBuilder keyMapBuilder, boolean list) {
            KeyMap<E, Object> keyMap = new KeyMap<E, Object>();
            keyMap.mapper = keyMapBuilder.mapper;
            keyMap.allowNull = isFalse(keyMapBuilder.allowNull);
            keyMap.allowDuplicates = isFalse(keyMapBuilder.allowDuplicates);
            keyMap.allowDuplicatesNull = isFalse(keyMapBuilder.allowDuplicatesNull);

            if (isTrue(keyMapBuilder.sort) || isTrue(keyMapBuilder.orderBy)) {
                if (keyMapBuilder.comparator == null) {
                    if (keyMap.allowNull) {
                        keyMap.comparator = new NullComparator(NaturalComparator.INSTANCE(), keyMapBuilder.sortNullsFirst);
                    } else {
                        keyMap.comparator = NaturalComparator.INSTANCE();
                    }
                } else {
                    if (!keyMapBuilder.comparatorSortsNull && keyMap.allowNull) {
                        keyMap.comparator = new NullComparator(keyMapBuilder.comparator, keyMapBuilder.sortNullsFirst);
                    } else {
                        keyMap.comparator = keyMapBuilder.comparator;
                    }
                }
            }

            if (list && isTrue(keyMapBuilder.orderBy)) {
                if (keyMapBuilder.primitiveListType == null) {
                    if (useBigList) {
                        keyMap.keysList = new BigList<Object>();
                    } else {
                        keyMap.keysList = new GapList<Object>();
                    }
                } else {
                    if (keyMapBuilder.comparator != null && keyMapBuilder.comparator != NaturalComparator.INSTANCE()) {
                        throw new IllegalArgumentException("Only natural comparator supported for list type");
                    }
                    if (isTrue(keyMapBuilder.allowNull)) {
                        throw new IllegalArgumentException("Null values are not supported for primitive list type");
                    }
                    keyMap.comparator = NaturalComparator.INSTANCE();
                    if (useBigList) {
                        keyMap.keysList = (IList<Object>) BigLists.createWrapperList(keyMapBuilder.primitiveListType);
                    } else {
                        keyMap.keysList = (IList<Object>) GapLists.createWrapperList(keyMapBuilder.primitiveListType);
                    }
                }
            } else if (keyMap.comparator != null) {
                keyMap.keysMap = new TreeMap(keyMap.comparator);
            } else {
                keyMap.keysMap = new HashMap();
            }

            return keyMap;
        }


        void build(KeyCollectionImpl keyColl, boolean list) {
            keyColl.setBehavior = setBehavior;
            keyColl.allowNullElem = allowNullElem;
            keyColl.constraint = constraint;
            keyColl.beforeInsertTrigger = beforeInsertTrigger;
            keyColl.afterInsertTrigger = afterInsertTrigger;
            keyColl.beforeDeleteTrigger = beforeDeleteTrigger;
            keyColl.afterDeleteTrigger = afterDeleteTrigger;
            keyColl.maxSize = maxSize;
            keyColl.movingWindow = isTrue(movingWindow);

            int orderByKey = -1;
            int size = keyMapBuilders.size();
            if (size == 1) {
                KeyMapBuilder kmb = keyMapBuilders.get(0);
                if (kmb == null) {
                    if (!list) {
                        withElemSet();
                    } else {
                        size = 0;
                    }
                } else {

                    if (list && kmb.primitiveListType != null && kmb.orderBy == null && kmb.mapper == null && kmb.allowDuplicates == null
                            && kmb.allowNull == null && kmb.sort == null) {
                        size = 0;
                    }
                }
            }
            if (size > 0) {
                keyColl.keyMaps = new KeyMap[size];
                for (int i = 0; i < size; i++) {
                    KeyMapBuilder kmb = keyMapBuilders.get(i);
                    if (kmb == null) {
                        if (i != 0) {
                            throw new IllegalArgumentException("Key " + i + " is not defined");
                        }
                    } else {
                        if (isTrue(kmb.orderBy)) {
                            if (orderByKey != -1) {
                                throw new IllegalArgumentException("Only one order by key allowed");
                            }
                            orderByKey = i;
                        }
                        if (kmb.mapper == null) {
                            if (i == 0) {
                                kmb.mapper = IdentMapper.INSTANCE;
                            } else {
                                throw new IllegalArgumentException("No mapper for key " + i + " defined");
                            }
                        }
                        keyColl.keyMaps[i] = buildKeyMap(kmb, list);
                        if (i == 0) {
                            keyColl.keyMaps[i].count = count;
                        }
                    }
                }
            }


            if (orderByKey == -1 && !list) {
                if (keyColl.keyMaps != null) {
                    if (keyColl.keyMaps[0] != null) {
                        orderByKey = 0;
                    } else {
                        assert (keyColl.keyMaps[1] != null);
                        orderByKey = 1;
                    }
                }
            }
            keyColl.orderByKey = orderByKey;
        }


        void init(KeyCollectionImpl keyColl) {
            if (collection != null) {
                keyColl.addAll(collection);
            } else if (array != null) {
                keyColl.addAll(Arrays.asList(array));
            }
        }


        void init(KeyCollectionImpl keyColl, KeyListImpl keyList) {
            keyList.keyColl = keyColl;
            keyColl.keyList = keyList;
            if (keyColl.orderByKey == 0) {
                keyList.list = keyColl.keyMaps[0].keysList;
                if (keyList.list == null) {
                    keyList.list = initList();
                }
                if (collection != null) {
                    keyColl.addAll(collection);
                } else if (array != null) {
                    keyColl.addAll(Arrays.asList(array));
                }
            } else {
                keyList.list = initList();
                if (collection != null) {
                    keyList.ensureCapacity(capacity);
                    keyList.addAll(collection);
                } else if (array != null) {
                    keyList.ensureCapacity(capacity);
                    keyList.addArray(array);
                } else if (capacity != 0) {
                    keyList.ensureCapacity(capacity);
                }
            }
        }

        IList<?> initList() {
            Class<?> primitiveListType = null;
            KeyMapBuilder<?, ?> kmb = keyMapBuilders.get(0);
            if (kmb != null) {
                primitiveListType = kmb.primitiveListType;
            }

            if (primitiveListType == null) {
                if (useBigList) {
                    return new BigList<Object>();
                } else {
                    return new GapList<Object>();
                }
            } else {
                if (useBigList) {
                    return BigLists.createWrapperList(primitiveListType);
                } else {
                    return GapLists.createWrapperList(primitiveListType);
                }
            }
        }

        public static class KeyMapBuilder<E, K> {

            Boolean orderBy;


            Class<?> primitiveListType;

            Function<E, K> mapper;

            Boolean allowNull;

            Boolean allowDuplicates;
            boolean allowDuplicatesNull;


            Boolean sort;

            Comparator<?> comparator;

            boolean comparatorSortsNull;

            boolean sortNullsFirst;
        }
    }

    static class KeyMap<E, K> implements Serializable {

        Function<E, K> mapper;

        boolean allowNull;

        boolean allowDuplicates;

        boolean allowDuplicatesNull;

        Comparator<K> comparator;

        Map<K, Object> keysMap;

        IList<K> keysList;

        boolean count;

        KeyMap() {
        }

        KeyMap<E, K> copy() {
            KeyMap<E, K> copy = new KeyMap<E, K>();
            copy.mapper = mapper;
            copy.allowNull = allowNull;
            copy.allowDuplicates = allowDuplicates;
            copy.allowDuplicatesNull = allowDuplicatesNull;
            copy.comparator = comparator;
            copy.count = count;
            if (keysMap != null) {
                if (keysMap instanceof HashMap) {
                    copy.keysMap = (Map) ((HashMap) keysMap).clone();
                } else {
                    copy.keysMap = (Map) ((TreeMap) keysMap).clone();
                }


                for (Object obj : copy.keysMap.entrySet()) {
                    Entry entry = (Entry) obj;
                    Object val = entry.getValue();
                    if (val instanceof KeyMapList) {
                        val = new KeyMapList((KeyMapList) val);
                        entry.setValue(val);
                    }
                }
            } else {
                copy.keysList = keysList.copy();
            }
            return copy;
        }

        KeyMap<E, K> crop() {
            KeyMap<E, K> copy = new KeyMap<E, K>();
            copy.mapper = mapper;
            copy.allowNull = allowNull;
            copy.allowDuplicates = allowDuplicates;
            copy.allowDuplicatesNull = allowDuplicatesNull;
            copy.comparator = comparator;
            copy.count = count;
            if (keysMap != null) {
                if (keysMap instanceof HashMap) {
                    copy.keysMap = new HashMap<K, Object>();
                } else {
                    TreeMap<K, Object> treeMap = (TreeMap<K, Object>) keysMap;
                    copy.keysMap = new TreeMap<K, Object>(treeMap.comparator());
                }
            } else {
                copy.keysList = new GapList<K>();
            }
            return copy;
        }

        boolean isPrimaryMap() {
            return !allowDuplicates && !allowDuplicatesNull && !allowNull;
        }

        K getKey(E elem) {
            if (elem == null) {
                return null;
            }
            return mapper.apply(elem);
        }

        boolean containsKey(Object key) {
            if (key == null) {
                if (!allowNull) {
                    return false;
                }
            }
            if (keysMap != null) {
                return keysMap.containsKey(key);
            } else {
                return keysList.binarySearch(key, (Comparator<Object>) comparator) >= 0;
            }
        }

        boolean containsValue(Object key, Object value) {
            if (keysMap == null) {
                return keysList.contains(key);
            }

            assert (count == false);
            Object obj = keysMap.get(key);
            if (obj == null && value == null) {
                return keysMap.containsKey(key);
            }

            if (obj instanceof KeyMapList) {
                GapList<E> list = (GapList<E>) obj;
                return list.contains(value);
            } else {
                return Objects.equals(obj, value);
            }
        }

        boolean containsValue(Object value) {
            if (keysMap == null) {
                return keysList.contains(value);
            }

            assert (count == false);
            for (Object obj : keysMap.values()) {
                if (obj instanceof KeyMapList) {
                    GapList<E> list = (GapList<E>) obj;
                    if (list.contains(value)) {
                        return true;
                    }
                } else {
                    if (Objects.equals(obj, value)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        Option<E> getContainedKey(Object key) {
            if (key == null) {
                if (!allowNull) {
                    return Option.EMPTY();
                }
            }
            if (keysMap != null) {
                Object val = keysMap.get(key);
                if (val != null) {
                    return new Option(val);
                } else if (keysMap.containsKey(key)) {
                    return new Option(val);
                }
            } else {
                int index = keysList.binarySearch(key, (Comparator<Object>) comparator);
                if (index >= 0) {
                    return new Option(keysList.get(index));
                }
            }
            return Option.EMPTY();
        }

        @SuppressWarnings("unchecked")
        Option<E> getContainedValue(Object value) {
            assert (count == false);
            for (Entry<?, ?> entry : keysMap.entrySet()) {
                if ((entry.getValue() == null && value == null) || (entry.getValue() != null && entry.getValue().equals(value))) {
                    return new Option(entry.getValue());
                }
            }
            return Option.EMPTY();
        }

        Iterator<E> iteratorValues(KeyCollectionImpl<E> keyColl) {
            assert (keysMap != null);
            if (count) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Map<E, Integer> keysMapCount = (Map) keysMap;
                return new KeyMapCountIter<E, K>(keyColl, this, keysMapCount);
            } else {
                return new KeyMapIter<E, K>(keyColl, this, keysMap);
            }
        }


        void add(K key, E elem) {
            if (key == null) {
                if (!allowNull) {
                    errorNullKey();
                }
            }
            if (keysMap != null) {

                Object newElem = (count ? 1 : elem);
                int oldSize = keysMap.size();
                Object oldElem = keysMap.put(key, newElem);
                boolean hasOldElem;
                if (oldElem != null) {
                    hasOldElem = true;
                } else {
                    if (key == null) {
                        hasOldElem = (keysMap.size() == oldSize);
                    } else {
                        hasOldElem = false;
                    }
                }

                if (!hasOldElem) {


                } else {
                    if (!(allowDuplicates || (key == null && allowDuplicatesNull))) {

                        keysMap.put(key, oldElem);
                        errorDuplicateKey(key);
                    }

                    if (count) {
                        if (oldElem != null) {
                            Integer val = (Integer) oldElem;
                            keysMap.put(key, val + 1);
                        }
                    } else {
                        GapList<E> list;
                        if (oldElem instanceof KeyMapList) {
                            list = (GapList<E>) oldElem;
                            list.add(elem);
                        } else {
                            list = new KeyMapList<E>();
                            list.addArray((E) oldElem, elem);
                        }
                        keysMap.put(key, list);
                    }
                }

            } else {

                int addIndex = 0;
                if (!keysList.isEmpty()) {
                    if (comparator.compare(key, keysList.getLast()) > 0) {
                        addIndex = -keysList.size() - 1;
                    } else if (comparator.compare(key, keysList.getFirst()) < 0) {
                        addIndex = -1;
                    }
                }
                if (addIndex == 0) {
                    addIndex = SortedLists.binarySearchAdd(keysList, key, comparator);
                }
                boolean add = false;
                if (addIndex < 0) {

                    addIndex = -addIndex - 1;
                    add = true;
                } else {

                    if (allowDuplicates || (key == null && allowDuplicatesNull)) {
                        add = true;
                    }
                }
                if (!add) {
                    errorDuplicateKey(key);
                }
                keysList.doAdd(addIndex, key);
            }
        }


        Option<E> remove(Object key, boolean matchValue, Object value, KeyCollectionImpl keyColl) {

            if (key == null) {
                if (!allowNull) {
                    return Option.EMPTY();
                }
            }

            if (keysMap != null) {

                if (!keysMap.containsKey(key)) {
                    return Option.EMPTY();
                }
                if (count) {
                    assert (!matchValue || key == value);
                    Integer val = (Integer) keysMap.get(key);
                    if (val == 1) {
                        keysMap.remove(key);
                    } else {
                        keysMap.put((K) key, val - 1);
                    }
                    return new Option(key);
                } else {
                    E elem = null;
                    Object obj = keysMap.get(key);
                    if (obj instanceof KeyMapList) {
                        GapList<E> list = (GapList<E>) obj;
                        if (matchValue) {
                            if (!list.remove(value)) {
                                return Option.EMPTY();
                            } else {
                                elem = (E) value;
                            }
                        } else {
                            elem = list.removeFirst();
                        }
                        if (list.isEmpty()) {
                            keysMap.remove(key);
                        }
                    } else {
                        elem = (E) keysMap.remove(key);
                    }
                    return new Option(elem);
                }
            } else {

                int index = keysList.binarySearch(key, (Comparator<Object>) comparator);
                E elem = null;
                if (index < 0) {
                    return Option.EMPTY();
                }
                elem = (E) keyColl.keyList.doGet(index);
                keysList.remove(index);
                return new Option(elem);
            }
        }


        private void doRemoveAllByKey(K key, KeyCollectionImpl<E> keyColl, Collection<E> coll) {

            if (key == null) {
                if (!allowNull) {
                    return;
                }
            }
            if (keysMap != null) {

                if (!keysMap.containsKey(key)) {
                    return;
                }
                Object obj = keysMap.remove(key);
                if (coll != null) {
                    if (obj instanceof KeyMapList) {
                        coll.addAll((GapList<E>) obj);
                    } else {
                        coll.add((E) obj);
                    }
                }

            } else {

                int index = SortedLists.binarySearchGet(keysList, key, comparator);
                if (index < 0) {
                    return;
                }
                int start = index;
                while (true) {
                    index++;
                    if (index == keysList.size()) {
                        break;
                    }
                    if (!GapList.equalsElem(keysList.get(index), key)) {
                        break;
                    }
                }
                if (coll != null) {
                    coll.addAll(keyColl.keyList.list.getAll(start, index - start));
                }
                keysList.remove(start, index - start);
            }
        }

        Set<K> getDistinctKeys() {
            if (keysMap != null) {
                Set<K> set = keysMap.keySet();
                if (comparator != null) {
                    TreeSet treeSet = new TreeSet(comparator);
                    treeSet.addAll(set);
                    return treeSet;
                } else {
                    return new HashSet(set);
                }
            } else {
                K lastKey = null;
                TreeSet<K> set = new TreeSet<K>(comparator);
                for (int i = 0; i < keysList.size(); i++) {
                    K key = keysList.get(i);
                    boolean add = false;
                    if (set.isEmpty()) {
                        add = true;
                    } else {
                        if (key != null) {
                            add = !key.equals(lastKey);
                        } else {
                            add = (lastKey != null);
                        }
                    }
                    if (add) {
                        set.add(key);
                        lastKey = key;
                    }
                }
                return set;
            }
        }

        static class KeyMapIter<E, K> implements Iterator<E> {

            KeyCollectionImpl<E> keyColl;
            KeyMap<E, K> keyMap;
            Iterator<Object> mapIter;
            Iterator<E> listIter;
            boolean hasElem;
            E elem;

            public KeyMapIter(KeyCollectionImpl<E> keyColl, KeyMap<E, K> keyMap, Map<K, Object> map) {
                this.keyColl = keyColl;
                this.keyMap = keyMap;
                this.mapIter = map.values().iterator();
            }

            @Override
            public boolean hasNext() {
                boolean hasNext = false;
                if (listIter != null) {
                    hasNext = listIter.hasNext();
                }
                if (!hasNext) {
                    hasNext = mapIter.hasNext();
                }
                return hasNext;
            }

            @Override
            public E next() {
                boolean hasNext = false;
                if (listIter != null) {
                    if (listIter.hasNext()) {
                        hasNext = true;
                        elem = listIter.next();
                    } else {
                        listIter = null;
                        elem = null;
                    }
                }
                if (!hasNext) {

                    Object o = mapIter.next();
                    if (o instanceof KeyMapList) {
                        listIter = ((KeyMapList<E>) o).iterator();
                        elem = listIter.next();
                    } else {
                        elem = (E) o;
                    }
                }
                hasElem = true;
                return elem;
            }

            @Override
            public void remove() {
                if (!hasElem) {
                    throw new IllegalStateException("No current element to remove");
                }
                hasElem = false;

                if (listIter != null) {
                    listIter.remove();
                } else {
                    mapIter.remove();
                }
                keyColl.remove(elem, keyMap);
            }
        }

        static class KeyMapCountIter<E, K> implements Iterator<E> {

            KeyCollectionImpl<E> keyColl;
            KeyMap<E, K> keyMap;
            Map<E, Integer> map;
            Iterator<Entry<E, Integer>> mapIter;
            E elem;
            int count;
            boolean hasElem;

            public KeyMapCountIter(KeyCollectionImpl<E> keyColl, KeyMap<E, K> keyMap, Map<E, Integer> map) {
                this.keyColl = keyColl;
                this.keyMap = keyMap;
                this.map = map;
                this.mapIter = map.entrySet().iterator();
            }

            @Override
            public boolean hasNext() {
                boolean hasNext = count > 0;
                if (!hasNext) {
                    hasNext = mapIter.hasNext();
                }
                return hasNext;
            }

            @Override
            public E next() {
                if (count > 0) {
                    count--;
                } else {

                    Entry<E, Integer> o = mapIter.next();
                    elem = o.getKey();
                    count = o.getValue();
                    count--;
                }

                hasElem = true;
                return elem;
            }

            @Override
            public void remove() {
                if (!hasElem) {
                    throw new IllegalStateException("No current element to remove");
                }
                hasElem = false;

                Integer val = map.get(elem);
                if (val == 1) {
                    mapIter.remove();
                } else {
                    map.put(elem, val - 1);
                }
                keyColl.size--;
            }
        }
    }


    static class KeyMapList<E> extends GapList<E> {
        public KeyMapList() {
            super();
        }

        public KeyMapList(KeyMapList that) {
            super(that);
        }
    }


}
