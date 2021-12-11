package org.magicwerk.brownies.collections.primitive;

import org.magicwerk.brownies.collections.GapList;
import org.magicwerk.brownies.collections.IList;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;


@SuppressWarnings("serial")
public abstract class ICharList implements Cloneable, Serializable, CharSequence {

    private static final int TRANSFER_COPY = 0;
    private static final int TRANSFER_MOVE = 1;
    private static final int TRANSFER_REMOVE = 2;


    static char[] toArray(Collection<Character> coll) {
        Object[] values = coll.toArray();
        char[] v = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            v[i] = (Character) values[i];
        }
        return v;
    }


    static boolean equalsElem(char elem1, char elem2) {
        return elem1 == elem2;
    }


    static int hashCodeElem(char elem) {
        return elem;
    }


    public static void transferCopy(ICharList src, int srcIndex, int srcLen, ICharList dst, int dstIndex, int dstLen) {
        if (src == dst) {
            src.checkLengths(srcLen, dstLen);
            src.copy(srcIndex, dstIndex, srcLen);
        } else {
            src.doTransfer(TRANSFER_COPY, srcIndex, srcLen, dst, dstIndex, dstLen);
        }
    }


    public static void transferMove(ICharList src, int srcIndex, int srcLen, ICharList dst, int dstIndex, int dstLen) {
        if (src == dst) {
            src.checkLengths(srcLen, dstLen);
            src.move(srcIndex, dstIndex, srcLen);
        } else {
            src.doTransfer(TRANSFER_MOVE, srcIndex, srcLen, dst, dstIndex, dstLen);
        }
    }


    public static void transferRemove(ICharList src, int srcIndex, int srcLen, ICharList dst, int dstIndex, int dstLen) {
        if (src == dst) {
            src.checkLengths(srcLen, dstLen);
            src.drag(srcIndex, dstIndex, srcLen);
        } else {
            src.doTransfer(TRANSFER_REMOVE, srcIndex, srcLen, dst, dstIndex, dstLen);
        }
    }


    public static void transferSwap(ICharList src, int srcIndex, ICharList dst, int dstIndex, int len) {
        if (src == dst) {
            src.swap(srcIndex, dstIndex, len);
        } else {
            src.doTransferSwap(srcIndex, dst, dstIndex, len);
        }
    }


    @SuppressWarnings("unchecked")
    public ICharList copy() {
        return (ICharList) clone();
    }


    @SuppressWarnings("unchecked")

    public Object clone() {
        try {
            ICharList list = (ICharList) super.clone();
            list.doClone(this);
            return list;
        } catch (CloneNotSupportedException e) {

            throw new AssertionError(e);
        }
    }


    abstract public ICharList unmodifiableList();


    abstract protected void doClone(ICharList that);

    public void clear() {
        doClear();
    }

    protected void doClear() {
        doRemoveAll(0, size());
    }


    public void resize(int len, char elem) {
        checkLength(len);
        int size = size();
        if (len < size) {
            remove(len, size - len);
        } else {
            for (int i = size; i < len; i++) {
                add(elem);
            }
        }
        assert (size() == len);
    }

    abstract public int size();


    abstract public int capacity();

    public char get(int index) {
        checkIndex(index);
        return doGet(index);
    }


    abstract protected char doGet(int index);


    abstract protected char doSet(int index, char elem);

    public char set(int index, char elem) {
        checkIndex(index);
        return doSet(index, elem);
    }


    public char put(int index, char elem) {
        checkIndexAdd(index);
        if (index < size()) {
            return doSet(index, elem);
        } else {
            doAdd(-1, elem);
            return (char) 0;
        }
    }


    abstract protected char doReSet(int index, char elem);


    abstract protected char getDefaultElem();


    protected void doModify() {
    }

    public boolean add(char elem) {
        return doAdd(-1, elem);
    }

    public void add(int index, char elem) {
        checkIndexAdd(index);
        doAdd(index, elem);
    }


    abstract protected boolean doAdd(int index, char elem);

    public char remove(int index) {
        checkIndex(index);
        return doRemove(index);
    }


    abstract protected char doRemove(int index);


    public void ensureCapacity(int minCapacity) {
        doModify();
        doEnsureCapacity(minCapacity);
    }


    abstract protected void doEnsureCapacity(int minCapacity);


    abstract public void trimToSize();

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CharObjGapList) {
            obj = ((CharObjGapList) obj).list;
        } else if (obj instanceof CharObjBigList) {
            obj = ((CharObjBigList) obj).list;
        }
        if (!(obj instanceof ICharList)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        ICharList list = (ICharList) obj;
        int size = size();
        if (size != list.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!equalsElem(doGet(i), list.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        int size = size();
        for (int i = 0; i < size; i++) {
            char elem = doGet(i);
            hashCode = 31 * hashCode + hashCodeElem(elem);
        }
        return hashCode;
    }

    public String toString() {
        return new String(toArray());
    }

    public boolean isEmpty() {
        return size() == 0;
    }


    public int getCount(char elem) {
        int count = 0;
        int size = size();
        for (int i = 0; i < size; i++) {
            if (equalsElem(doGet(i), elem)) {
                count++;
            }
        }
        return count;
    }


    public char getSingle() {
        if (size() != 1) {
            throw new NoSuchElementException();
        }
        return doGet(0);
    }


    public char getSingleOrEmpty() {
        int size = size();
        if (size == 0) {
            return (char) 0;
        } else if (size == 1) {
            return doGet(0);
        } else {
            throw new NoSuchElementException();
        }
    }


    public ICharList getAll(char elem) {
        ICharList list = doCreate(-1);
        int size = size();
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            if (equalsElem(e, elem)) {
                list.add(e);
            }
        }
        return list;
    }


    public char getIf(Predicate<Character> predicate) {
        int size = size();
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            if (predicate.test(e)) {
                return e;
            }
        }
        return (char) 0;
    }


    public boolean removeIf(Predicate<Character> predicate) {
        boolean removed = false;
        int size = size();
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            if (predicate.test(e)) {
                doRemove(i);
                size--;
                i--;
                removed = true;
            }
        }
        return removed;
    }


    public boolean retainIf(Predicate<Character> predicate) {
        boolean modified = false;
        int size = size();
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            if (!predicate.test(e)) {
                doRemove(i);
                size--;
                i--;
                modified = true;
            }
        }
        return modified;
    }


    public ICharList extractIf(Predicate<Character> predicate) {
        ICharList list = doCreate(-1);
        int size = size();
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            if (predicate.test(e)) {
                list.add(e);
                doRemove(i);
                size--;
                i--;
            }
        }
        return list;
    }


    public Set getDistinct() {
        Set set = new HashSet();
        int size = size();
        for (int i = 0; i < size; i++) {
            set.add(doGet(i));
        }
        return set;
    }


    public <R> IList<R> mappedList(Function<Character, R> func) {
        int size = size();
        @SuppressWarnings("unchecked")
        IList<R> list = new GapList<R>(size);
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            list.add(func.apply(e));
        }
        return list;
    }


    public ICharList transformedList(UnaryOperator<Character> op) {
        int size = size();
        ICharList list = doCreate(size);
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            list.add(op.apply(e));
        }
        return list;
    }


    public void transform(UnaryOperator<Character> op) {
        int size = size();
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            e = op.apply(e);
            doSet(i, e);
        }
    }


    public ICharList filteredList(Predicate<Character> predicate) {
        ICharList list = doCreate(-1);
        int size = size();
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            if (predicate.test(e)) {
                list.add(e);
            }
        }
        return list;
    }


    public void filter(Predicate<Character> predicate) {


        ICharList list = filteredList(predicate);
        doAssign(list);
    }

    public int indexOf(char elem) {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (equalsElem(doGet(i), elem)) {
                return i;
            }
        }
        return -1;
    }


    public int indexOfIf(Predicate<Character> predicate) {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (predicate.test(doGet(i))) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(char elem) {
        for (int i = size() - 1; i >= 0; i--) {
            if (equalsElem(doGet(i), elem)) {
                return i;
            }
        }
        return -1;
    }


    public int indexOf(char elem, int fromIndex) {
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        int size = size();
        for (int i = fromIndex; i < size; i++) {
            if (equalsElem(doGet(i), elem)) {
                return i;
            }
        }
        return -1;
    }


    public int lastIndexOf(char elem, int fromIndex) {
        int size = size();
        if (fromIndex >= size) {
            fromIndex = size - 1;
        }
        for (int i = fromIndex; i >= 0; i--) {
            if (equalsElem(doGet(i), elem)) {
                return i;
            }
        }
        return -1;
    }

    public boolean removeElem(char elem) {
        int index = indexOf(elem);
        if (index == -1) {
            return false;
        }
        doRemove(index);
        return true;
    }

    public boolean contains(char elem) {
        return indexOf(elem) != -1;
    }


    public boolean containsIf(Predicate<Character> predicate) {
        return indexOfIf(predicate) != -1;
    }


    public boolean addIfAbsent(char elem) {
        if (contains(elem)) {
            return false;
        }
        return add(elem);
    }


    public boolean containsAny(Collection<Character> coll) {


        for (char elem : coll) {
            if (contains(elem)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(Collection<Character> coll) {


        for (char elem : coll) {
            if (!contains(elem)) {
                return false;
            }
        }
        return true;
    }


    public ICharList removeAll(char elem) {
        ICharList list = doCreate(-1);
        int size = size();
        for (int i = 0; i < size; i++) {
            char e = doGet(i);
            if (equalsElem(elem, e)) {
                list.add(e);
                doRemove(i);
                size--;
                i--;
            }
        }
        return list;
    }

    public boolean removeAll(Collection<Character> coll) {


        checkNonNull(coll);
        boolean modified = false;
        int size = size();
        for (int i = 0; i < size; i++) {
            if (coll.contains(doGet(i))) {
                doRemove(i);
                size--;
                i--;
                modified = true;
            }
        }
        return modified;
    }


    public boolean removeAll(ICharList coll) {


        checkNonNull(coll);
        boolean modified = false;
        int size = size();
        for (int i = 0; i < size; i++) {
            if (coll.contains(doGet(i))) {
                doRemove(i);
                size--;
                i--;
                modified = true;
            }
        }
        return modified;
    }

    public boolean retainAll(Collection<Character> coll) {


        checkNonNull(coll);
        boolean modified = false;
        int size = size();
        for (int i = 0; i < size; i++) {
            if (!coll.contains(doGet(i))) {
                doRemove(i);
                size--;
                i--;
                modified = true;
            }
        }
        return modified;
    }


    public boolean retainAll(ICharList coll) {


        checkNonNull(coll);
        boolean modified = false;
        int size = size();
        for (int i = 0; i < size; i++) {
            if (!coll.contains(doGet(i))) {
                doRemove(i);
                size--;
                i--;
                modified = true;
            }
        }
        return modified;
    }

    public char[] toArray() {
        return toArray(0, size());
    }

    public char[] toArray(char[] array) {
        return toArray(array, 0, size());
    }


    public char[] toArray(Class clazz) {
        return toArray(clazz, 0, size());
    }


    public char[] toArray(int index, int len) {
        char[] array = new char[len];
        doGetAll(array, index, len);
        return array;
    }


    @SuppressWarnings("unchecked")
    public char[] toArray(char[] array, int index, int len) {
        if (array.length < len) {
            array = doCreateArray(array.getClass().getComponentType(), len);
        }
        doGetAll(array, index, len);
        if (array.length > len) {
            array[len] = (char) 0;
        }
        return array;
    }


    public char[] toArray(Class clazz, int index, int len) {
        char[] array = doCreateArray(clazz, len);
        doGetAll(array, index, len);
        return array;
    }


    @SuppressWarnings("unchecked")
    protected char[] doCreateArray(Class clazz, int len) {
        return (char[]) java.lang.reflect.Array.newInstance(clazz, len);
    }


    @SuppressWarnings("unchecked")
    protected void doGetAll(char[] array, int index, int len) {
        for (int i = 0; i < len; i++) {
            array[i] = doGet(index + i);
        }
    }


    protected boolean doAddAll(int index, ICharList list) {
        int listSize = list.size();
        doEnsureCapacity(size() + listSize);
        if (listSize == 0) {
            return false;
        }
        boolean changed = false;
        int prevSize = size();
        for (int i = 0; i < listSize; i++) {
            char elem = list.get(i);
            if (doAdd(index, elem)) {
                changed = true;
                if (index != -1) {
                    if (prevSize != size()) {
                        prevSize = size();
                        index++;
                    }
                }
            }
        }
        return changed;
    }

    public char peek() {
        if (size() == 0) {
            return (char) 0;
        }
        return getFirst();
    }

    public char element() {

        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doGet(0);
    }

    public char poll() {
        if (size() == 0) {
            return (char) 0;
        }
        return doRemove(0);
    }

    public char remove() {

        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doRemove(0);
    }

    public boolean offer(char elem) {

        return doAdd(-1, elem);
    }

    public char getFirst() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doGet(0);
    }

    public char getLast() {
        int size = size();
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return doGet(size - 1);
    }

    public void addFirst(char elem) {
        doAdd(0, elem);
    }

    public void addLast(char elem) {

        doAdd(-1, elem);
    }

    public char removeFirst() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doRemove(0);
    }

    public char removeLast() {
        int size = size();
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return doRemove(size - 1);
    }

    public boolean offerFirst(char elem) {

        doAdd(0, elem);
        return true;
    }

    public boolean offerLast(char elem) {

        doAdd(-1, elem);
        return true;
    }

    public char peekFirst() {
        if (size() == 0) {
            return (char) 0;
        }
        return doGet(0);
    }

    public char peekLast() {
        int size = size();
        if (size == 0) {
            return (char) 0;
        }
        return doGet(size - 1);
    }

    public char pollFirst() {
        if (size() == 0) {
            return (char) 0;
        }
        return doRemove(0);
    }

    public char pollLast() {
        int size = size();
        if (size == 0) {
            return (char) 0;
        }
        return doRemove(size - 1);
    }

    public char pop() {

        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doRemove(0);
    }

    public void push(char elem) {

        doAdd(0, elem);
    }

    public boolean removeFirstOccurrence(char elem) {
        int index = indexOf(elem);
        if (index == -1) {
            return false;
        }
        doRemove(index);
        return true;
    }

    public boolean removeLastOccurrence(char elem) {
        int index = lastIndexOf(elem);
        if (index == -1) {
            return false;
        }
        doRemove(index);
        return true;
    }

    void doTransfer(int transferMode, int srcIndex, int srcLen, ICharList dst, int dstIndex, int dstLen) {

        if (srcLen == -1) {
            srcLen = size() - srcIndex;
        }
        checkRange(srcIndex, srcLen);
        if (dstIndex == -1) {
            dstIndex = dst.size();
        } else {
            dst.checkIndexAdd(dstIndex);
        }
        if (dstLen == -1) {
            dstLen = dst.size() - dstIndex;
        } else {
            dst.checkLength(dstLen);
        }
        char defaultElem = getDefaultElem();
        if (dstLen > srcLen) {

            dst.remove(dstIndex, dstLen - srcLen);
        } else if (srcLen > dstLen) {

            dst.addMult(dstIndex, srcLen - dstLen, defaultElem);
        }

        if (transferMode == TRANSFER_MOVE) {

            for (int i = 0; i < srcLen; i++) {
                char elem = doReSet(srcIndex + i, defaultElem);
                dst.doSet(dstIndex + i, elem);
            }
        } else {

            for (int i = 0; i < srcLen; i++) {
                char elem = doGet(srcIndex + i);
                dst.doSet(dstIndex + i, elem);
            }
            if (transferMode == TRANSFER_REMOVE) {

                remove(srcIndex, srcLen);
            }
        }
    }

    void doTransferSwap(int srcIndex, ICharList dst, int dstIndex, int len) {
        checkRange(srcIndex, len);
        dst.checkRange(dstIndex, len);
        for (int i = 0; i < len; i++) {
            char swap = doGet(srcIndex + i);
            swap = dst.doSet(dstIndex + i, swap);
            doSet(srcIndex + i, swap);
        }
    }


    abstract protected ICharList doCreate(int capacity);


    abstract protected void doAssign(ICharList that);


    public ICharList getAll(int index, int len) {
        checkRange(index, len);
        ICharList list = doCreate(len);
        for (int i = 0; i < len; i++) {
            list.add(doGet(index + i));
        }
        return list;
    }


    public ICharList extract(int index, int len) {
        checkRange(index, len);
        ICharList list = doCreate(len);
        for (int i = 0; i < len; i++) {
            list.add(doGet(index + i));
        }
        remove(index, len);
        return list;
    }


    public void remove(int index, int len) {
        checkRange(index, len);
        doRemoveAll(index, len);
    }


    protected void doRemoveAll(int index, int len) {
        for (int i = index + len - 1; i >= index; i--) {
            doRemove(i);
        }
    }


    public boolean addAll(ICharList list) {
        return doAddAll(-1, list);
    }


    public boolean addAll(int index, ICharList list) {
        checkIndexAdd(index);
        return doAddAll(index, list);
    }


    public boolean addAll(Collection<Character> coll) {
        if (coll instanceof List) {
            return doAddAll(-1, new IReadOnlyCharListFromList((List<Character>) coll));
        } else {
            return doAddAll(-1, new IReadOnlyCharListFromCollection(coll));
        }
    }


    public boolean addAll(int index, Collection<Character> coll) {
        checkIndexAdd(index);
        if (coll instanceof List) {
            return doAddAll(index, new IReadOnlyCharListFromList((List<Character>) coll));
        } else {
            return doAddAll(index, new IReadOnlyCharListFromCollection(coll));
        }
    }


    public boolean addArray(char... elems) {
        return doAddAll(-1, new IReadOnlyCharListFromArray(elems));
    }

    public boolean addArray(char[] elems, int offset, int length) {
        return doAddAll(-1, new IReadOnlyCharListFromArray(elems, offset, length));
    }

    public boolean addArray(int index, char[] elems, int offset, int length) {
        return doAddAll(index, new IReadOnlyCharListFromArray(elems, offset, length));
    }


    public boolean addArray(int index, char... elems) {
        checkIndexAdd(index);
        return doAddAll(index, new IReadOnlyCharListFromArray(elems));
    }


    public boolean addMult(int len, char elem) {
        return doAddAll(-1, new IReadOnlyCharListFromMult(len, elem));
    }


    public boolean addMult(int index, int len, char elem) {
        checkIndexAdd(index);
        return doAddAll(index, new IReadOnlyCharListFromMult(len, elem));
    }


    public void setAll(int index, ICharList list) {
        int listSize = list.size();
        checkRange(index, listSize);
        doReplaceAll(index, listSize, list);
    }


    public void setAll(int index, Collection<Character> coll) {
        int collSize = coll.size();
        checkRange(index, collSize);
        if (coll instanceof List) {
            doReplaceAll(index, collSize, new IReadOnlyCharListFromList((List<Character>) coll));
        } else {
            doReplaceAll(index, collSize, new IReadOnlyCharListFromCollection(coll));
        }
    }


    public void setArray(int index, char... elems) {
        int arrayLen = elems.length;
        checkRange(index, arrayLen);
        doReplaceAll(index, arrayLen, new IReadOnlyCharListFromArray(elems));
    }

    public void setArray(int index, char[] elems, int offset, int length) {
        int arrayLen = elems.length;
        checkRange(index, arrayLen);
        doReplaceAll(index, arrayLen, new IReadOnlyCharListFromArray(elems, offset, length));
    }


    public void setMult(int index, int len, char elem) {
        checkRange(index, len);
        doReplaceAll(index, len, new IReadOnlyCharListFromMult(len, elem));
    }


    public void putAll(int index, ICharList list) {
        checkIndexAdd(index);
        checkNonNull(list);
        int len = size() - index;
        if (list != null) {
            if (list.size() < len) {
                len = list.size();
            }
        }

        doReplaceAll(index, len, list);
    }


    public void putAll(int index, Collection<Character> coll) {
        if (coll instanceof ICharList) {
            putAll(index, (ICharList) coll);
        } else if (coll instanceof List) {
            putAll(index, new IReadOnlyCharListFromList((List<Character>) coll));
        } else {
            putAll(index, new IReadOnlyCharListFromCollection(coll));
        }
    }


    public void putArray(int index, char... elems) {
        putAll(index, new IReadOnlyCharListFromArray(elems));
    }


    public void putMult(int index, int len, char elem) {
        putAll(index, new IReadOnlyCharListFromMult(len, elem));
    }


    public void initAll(ICharList list) {
        checkNonNull(list);
        doClear();
        doAddAll(-1, list);
    }


    public void initAll(Collection<Character> coll) {
        if (coll instanceof ICharList) {
            initAll((ICharList) coll);
        } else if (coll instanceof List) {
            initAll(new IReadOnlyCharListFromList((List<Character>) coll));
        } else {
            initAll(new IReadOnlyCharListFromCollection(coll));
        }
    }


    public void initArray(char... elems) {
        initAll(new IReadOnlyCharListFromArray(elems));
    }


    public void initMult(int len, char elem) {
        checkLength(len);
        initAll(new IReadOnlyCharListFromMult(len, elem));
    }


    public void replaceAll(int index, int len, Collection<Character> coll) {
        if (coll instanceof ICharList) {
            replaceAll(index, len, (ICharList) coll);
        } else if (coll instanceof List) {
            replaceAll(index, len, new IReadOnlyCharListFromList((List<Character>) coll));
        } else {
            replaceAll(index, len, new IReadOnlyCharListFromCollection(coll));
        }
    }


    public void replaceArray(int index, int len, char... elems) {
        replaceAll(index, len, new IReadOnlyCharListFromArray(elems));
    }


    public void replaceMult(int index, int len, int numElems, char elem) {
        replaceAll(index, len, new IReadOnlyCharListFromMult(numElems, elem));
    }


    public void replaceAll(int index, int len, ICharList list) {

        if (index == -1) {
            index = size();
        } else {
            checkIndexAdd(index);
        }
        if (len == -1) {
            len = size() - index;
            if (list != null) {
                if (list.size() < len) {
                    len = list.size();
                }
            }
        } else {
            checkRange(index, len);
        }

        doReplaceAll(index, len, list);
    }

    protected boolean doReplaceAll(int index, int len, ICharList list) {


        assert (index >= 0 && index <= size());
        assert (len >= 0 && index + len <= size());
        int srcLen = 0;
        if (list != null) {
            srcLen = list.size();
        }
        doEnsureCapacity(size() - len + srcLen);

        doRemoveAll(index, len);

        for (int i = 0; i < srcLen; i++) {
            if (!doAdd(index + i, list.doGet(i))) {
                index--;
            }
        }
        return len > 0 || srcLen > 0;
    }


    public void fill(char elem) {
        int size = size();
        for (int i = 0; i < size; i++) {
            doSet(i, elem);
        }
    }


    public void copy(int srcIndex, int dstIndex, int len) {
        checkRange(srcIndex, len);
        checkRange(dstIndex, len);
        if (srcIndex < dstIndex) {
            for (int i = len - 1; i >= 0; i--) {
                doReSet(dstIndex + i, doGet(srcIndex + i));
            }
        } else if (srcIndex > dstIndex) {
            for (int i = 0; i < len; i++) {
                doReSet(dstIndex + i, doGet(srcIndex + i));
            }
        }
    }


    public void move(int srcIndex, int dstIndex, int len) {
        checkRange(srcIndex, len);
        checkRange(dstIndex, len);

        if (srcIndex < dstIndex) {
            for (int i = len - 1; i >= 0; i--) {
                doReSet(dstIndex + i, doGet(srcIndex + i));
            }
        } else if (srcIndex > dstIndex) {
            for (int i = 0; i < len; i++) {
                doReSet(dstIndex + i, doGet(srcIndex + i));
            }
        }

        if (srcIndex < dstIndex) {
            int fill = Math.min(len, dstIndex - srcIndex);
            setMult(srcIndex, fill, (char) 0);
        } else if (srcIndex > dstIndex) {
            int fill = Math.min(len, srcIndex - dstIndex);
            setMult(srcIndex + len - fill, fill, (char) 0);
        }
    }


    public void drag(int srcIndex, int dstIndex, int len) {
        checkRange(srcIndex, len);
        checkRange(dstIndex, len);
        if (srcIndex < dstIndex) {
            doRotate(srcIndex, len + (dstIndex - srcIndex), dstIndex - srcIndex);
        } else if (srcIndex > dstIndex) {
            doRotate(dstIndex, len + (srcIndex - dstIndex), dstIndex - srcIndex);
        }
    }


    public void swap(int index1, int index2, int len) {
        checkRange(index1, len);
        checkRange(index2, len);
        if ((index1 < index2 && index1 + len > index2) || index1 > index2 && index2 + len > index1) {
            throw new IndexOutOfBoundsException("Swap ranges overlap");
        }
        for (int i = 0; i < len; i++) {
            char swap = doGet(index1 + i);
            swap = doReSet(index2 + i, swap);
            doReSet(index1 + i, swap);
        }
    }


    public void reverse() {
        reverse(0, size());
    }


    public void reverse(int index, int len) {
        checkRange(index, len);
        int pos1 = index;
        int pos2 = index + len - 1;
        int mid = len / 2;
        for (int i = 0; i < mid; i++) {
            char swap = doGet(pos1);
            swap = doReSet(pos2, swap);
            doReSet(pos1, swap);
            pos1++;
            pos2--;
        }
    }


    public void rotate(int distance) {
        rotate(0, size(), distance);
    }


    public void rotate(int index, int len, int distance) {
        checkRange(index, len);
        doRotate(index, len, distance);
    }


    protected void doRotate(int index, int len, int distance) {
        distance = distance % len;
        if (distance < 0) {
            distance += len;
        }
        if (distance == 0) {
            return;
        }
        assert (distance >= 0 && distance < len);
        int num = 0;
        for (int start = 0; num != len; start++) {
            char elem = doGet(index + start);
            int i = start;
            do {
                i += distance;
                if (i >= len) {
                    i -= len;
                }
                elem = doReSet(index + i, elem);
                num++;
            } while (i != start);
        }
    }


    public void sort() {
        sort(0, size());
    }


    abstract public void sort(int index, int len);


    public int binarySearch(char key) {
        return binarySearch(0, size(), key);
    }


    abstract public int binarySearch(int index, int len, char key);


    protected void checkIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + " (size: " + size() + ")");
        }
    }


    protected void checkIndexAdd(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + " (size: " + size() + ")");
        }
    }


    protected void checkRange(int index, int len) {
        if (index < 0 || len < 0 || index + len > size()) {
            throw new IndexOutOfBoundsException("Invalid range: " + index + "/" + len + " (size: " + size() + ")");
        }
    }


    protected void checkLength(int len) {
        if (len < 0) {
            throw new IndexOutOfBoundsException("Invalid length: " + len);
        }
    }


    protected void checkLengths(int len1, int len2) {
        if (len1 != len2) {
            throw new IndexOutOfBoundsException("Invalid lengths: " + len1 + ", " + len2);
        }
        if (len1 < 0) {
            throw new IndexOutOfBoundsException("Invalid length: " + len1);
        }
    }


    protected void checkNonNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException("Argument may not be (char) 0");
        }
    }

    @Override
    public int length() {
        return size();
    }

    @Override
    public char charAt(int index) {
        return get(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return getAll(start, end - start);
    }


    protected static abstract class IReadOnlyCharList extends ICharList {


        public ICharList unmodifiableList() {
            error();
            return null;
        }


        protected void doClone(ICharList that) {
            error();
        }


        public int capacity() {
            error();
            return 0;
        }


        protected char doSet(int index, char elem) {
            error();
            return (char) 0;
        }


        protected char doReSet(int index, char elem) {
            error();
            return (char) 0;
        }


        protected char getDefaultElem() {
            error();
            return (char) 0;
        }


        protected boolean doAdd(int index, char elem) {
            error();
            return false;
        }


        protected char doRemove(int index) {
            error();
            return (char) 0;
        }


        protected void doEnsureCapacity(int minCapacity) {
            error();
        }


        public void trimToSize() {
            error();
        }


        protected ICharList doCreate(int capacity) {
            error();
            return null;
        }


        protected void doAssign(ICharList that) {
            error();
        }


        public void sort(int index, int len) {
            error();
        }


        public int binarySearch(int index, int len, char key) {
            error();
            return 0;
        }


        private void error() {
            throw new UnsupportedOperationException("list is read-only");
        }
    }

    protected static class IReadOnlyCharListFromArray extends IReadOnlyCharList {

        char[] array;

        int offset;

        int length;

        IReadOnlyCharListFromArray(char[] array) {
            this.array = array;
            this.offset = 0;
            this.length = array.length;
        }

        IReadOnlyCharListFromArray(char[] array, int offset, int length) {
            this.array = array;
            this.offset = offset;
            this.length = length;
        }


        public int size() {
            return length;
        }


        protected char doGet(int index) {
            return array[offset + index];
        }
    }


    protected static class IReadOnlyCharListFromMult extends IReadOnlyCharList {

        int len;

        char elem;

        IReadOnlyCharListFromMult(int len, char elem) {
            checkLength(len);
            this.len = len;
            this.elem = elem;
        }


        public int size() {
            return len;
        }


        protected char doGet(int index) {
            return elem;
        }
    }

    protected static class IReadOnlyCharListFromCollection extends IReadOnlyCharList {

        char[] array;

        IReadOnlyCharListFromCollection(Collection<Character> coll) {
            array = toArray(coll);
        }


        public int size() {
            return array.length;
        }

        @SuppressWarnings("unchecked")

        protected char doGet(int index) {
            return array[index];
        }
    }

    protected static class IReadOnlyCharListFromList extends IReadOnlyCharList {

        List<Character> list2;

        @SuppressWarnings("unchecked")
        IReadOnlyCharListFromList(List<Character> list) {
            this.list2 = list;
        }


        public int size() {
            return list2.size();
        }


        protected char doGet(int index) {
            return list2.get(index);
        }
    }
}
