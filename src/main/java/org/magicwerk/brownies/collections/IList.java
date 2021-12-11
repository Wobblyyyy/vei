package org.magicwerk.brownies.collections;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;


@SuppressWarnings("serial")
public abstract class IList<E>
        extends AbstractList<E> implements

        List<E>, RandomAccess, Cloneable, Serializable,

        Deque<E> {

    private static final int TRANSFER_COPY = 0;
    private static final int TRANSFER_MOVE = 1;
    private static final int TRANSFER_REMOVE = 2;


    static Object[] toArray(Collection<?> coll) {
        Object[] values = coll.toArray();

        if (values.getClass() != Object[].class) {
            values = Arrays.copyOf(values, values.length, Object[].class);
        }
        return values;
    }


    static boolean equalsElem(Object elem1, Object elem2) {
        if (elem1 == null) {
            return elem2 == null;
        } else {
            return elem1.equals(elem2);
        }
    }


    static int hashCodeElem(Object elem) {
        if (elem == null) {
            return 0;
        } else {
            return elem.hashCode();
        }
    }


    public static <E> void transferCopy(IList<E> src, int srcIndex, int srcLen, IList<? super E> dst, int dstIndex, int dstLen) {
        if (src == dst) {
            src.checkLengths(srcLen, dstLen);
            src.copy(srcIndex, dstIndex, srcLen);
        } else {
            src.doTransfer(TRANSFER_COPY, srcIndex, srcLen, dst, dstIndex, dstLen);
        }
    }


    public static <E> void transferMove(IList<E> src, int srcIndex, int srcLen, IList<? super E> dst, int dstIndex, int dstLen) {
        if (src == dst) {
            src.checkLengths(srcLen, dstLen);
            src.move(srcIndex, dstIndex, srcLen);
        } else {
            src.doTransfer(TRANSFER_MOVE, srcIndex, srcLen, dst, dstIndex, dstLen);
        }
    }


    public static <E> void transferRemove(IList<E> src, int srcIndex, int srcLen, IList<? super E> dst, int dstIndex, int dstLen) {
        if (src == dst) {
            src.checkLengths(srcLen, dstLen);
            src.drag(srcIndex, dstIndex, srcLen);
        } else {
            src.doTransfer(TRANSFER_REMOVE, srcIndex, srcLen, dst, dstIndex, dstLen);
        }
    }


    public static <E> void transferSwap(IList<E> src, int srcIndex, IList<E> dst, int dstIndex, int len) {
        if (src == dst) {
            src.swap(srcIndex, dstIndex, len);
        } else {
            src.doTransferSwap(srcIndex, dst, dstIndex, len);
        }
    }


    @SuppressWarnings("unchecked")
    public IList<E> copy() {
        return (IList<E>) clone();
    }


    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        try {
            IList<E> list = (IList<E>) super.clone();
            list.doClone(this);
            return list;
        } catch (CloneNotSupportedException e) {

            throw new AssertionError(e);
        }
    }


    abstract public IList<E> unmodifiableList();


    abstract protected void doClone(IList<E> that);

    @Override
    public void clear() {
        doClear();
    }

    protected void doClear() {
        doRemoveAll(0, size());
    }


    public void resize(int len, E elem) {
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

    @Override
    abstract public int size();


    abstract public int capacity();

    @Override
    public E get(int index) {
        checkIndex(index);
        return doGet(index);
    }


    abstract protected E doGet(int index);


    abstract protected E doSet(int index, E elem);

    @Override
    public E set(int index, E elem) {
        checkIndex(index);
        return doSet(index, elem);
    }


    public E put(int index, E elem) {
        checkIndexAdd(index);

        if (index < size()) {
            return doSet(index, elem);
        } else {
            doAdd(-1, elem);
            return null;
        }
    }


    abstract protected E doReSet(int index, E elem);

    abstract protected E getDefaultElem();


    protected void doModify() {
    }

    @Override
    public boolean add(E elem) {
        return doAdd(-1, elem);
    }

    @Override
    public void add(int index, E elem) {
        checkIndexAdd(index);
        doAdd(index, elem);
    }


    abstract protected boolean doAdd(int index, E elem);

    @Override
    public E remove(int index) {
        checkIndex(index);
        return doRemove(index);
    }


    abstract protected E doRemove(int index);


    public void ensureCapacity(int minCapacity) {
        doModify();
        doEnsureCapacity(minCapacity);
    }


    abstract protected void doEnsureCapacity(int minCapacity);


    abstract public void trimToSize();

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof List<?>)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) obj;
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

    @Override
    public int hashCode() {
        int hashCode = 1;
        int size = size();
        for (int i = 0; i < size; i++) {
            E elem = doGet(i);
            hashCode = 31 * hashCode + hashCodeElem(elem);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        int size = size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(doGet(i));
        }
        buf.append("]");
        return buf.toString();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }


    public int getCount(E elem) {
        int count = 0;
        int size = size();
        for (int i = 0; i < size; i++) {
            if (equalsElem(doGet(i), elem)) {
                count++;
            }
        }
        return count;
    }


    public E getSingle() {
        if (size() != 1) {
            throw new NoSuchElementException();
        }
        return doGet(0);
    }


    public E getSingleOrEmpty() {
        int size = size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            return doGet(0);
        } else {
            throw new NoSuchElementException();
        }
    }


    public IList<E> getAll(E elem) {
        IList<E> list = doCreate(-1);
        int size = size();
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            if (equalsElem(e, elem)) {
                list.add(e);
            }
        }
        return list;
    }


    public E getIf(Predicate<? super E> predicate) {
        int size = size();
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            if (predicate.test(e)) {
                return e;
            }
        }
        return null;
    }


    @Override
    public boolean removeIf(Predicate<? super E> predicate) {
        boolean removed = false;
        int size = size();
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            if (predicate.test(e)) {
                doRemove(i);
                size--;
                i--;
                removed = true;
            }
        }
        return removed;
    }


    public boolean retainIf(Predicate<? super E> predicate) {
        boolean modified = false;
        int size = size();
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            if (!predicate.test(e)) {
                doRemove(i);
                size--;
                i--;
                modified = true;
            }
        }
        return modified;
    }


    public IList<E> extractIf(Predicate<? super E> predicate) {
        IList<E> list = doCreate(-1);
        int size = size();
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            if (predicate.test(e)) {
                list.add(e);
                doRemove(i);
                size--;
                i--;
            }
        }
        return list;
    }


    public Set<E> getDistinct() {
        Set<E> set = new HashSet<E>();
        int size = size();
        for (int i = 0; i < size; i++) {
            set.add(doGet(i));
        }
        return set;
    }


    public <R> IList<R> mappedList(Function<E, R> func) {
        int size = size();
        @SuppressWarnings("unchecked")
        IList<R> list = (IList<R>) doCreate(size);
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            list.add(func.apply(e));
        }
        return list;
    }


    public IList<E> transformedList(UnaryOperator<E> op) {
        int size = size();
        IList<E> list = doCreate(size);
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            list.add(op.apply(e));
        }
        return list;
    }


    public void transform(UnaryOperator<E> op) {
        int size = size();
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            e = op.apply(e);
            doSet(i, e);
        }
    }


    public IList<E> filteredList(Predicate<? super E> predicate) {
        IList<E> list = doCreate(-1);
        int size = size();
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            if (predicate.test(e)) {
                list.add(e);
            }
        }
        return list;
    }


    public void filter(Predicate<? super E> predicate) {


        IList<E> list = filteredList(predicate);
        doAssign(list);
    }

    @Override
    public int indexOf(Object elem) {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (equalsElem(doGet(i), elem)) {
                return i;
            }
        }
        return -1;
    }


    public int indexOfIf(Predicate<? super E> predicate) {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (predicate.test(doGet(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object elem) {
        for (int i = size() - 1; i >= 0; i--) {
            if (equalsElem(doGet(i), elem)) {
                return i;
            }
        }
        return -1;
    }


    public int indexOf(Object elem, int fromIndex) {
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


    public int lastIndexOf(Object elem, int fromIndex) {
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

    @Override
    public boolean remove(Object elem) {
        int index = indexOf(elem);
        if (index == -1) {
            return false;
        }
        doRemove(index);
        return true;
    }

    @Override
    public boolean contains(Object elem) {
        return indexOf(elem) != -1;
    }


    public boolean containsIf(Predicate<? super E> predicate) {
        return indexOfIf(predicate) != -1;
    }


    public boolean addIfAbsent(E elem) {
        if (contains(elem)) {
            return false;
        }
        return add(elem);
    }


    public boolean containsAny(Collection<?> coll) {


        for (Object elem : coll) {
            if (contains(elem)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> coll) {


        for (Object elem : coll) {
            if (!contains(elem)) {
                return false;
            }
        }
        return true;
    }


    public IList<E> removeAll(E elem) {
        IList<E> list = doCreate(-1);
        int size = size();
        for (int i = 0; i < size; i++) {
            E e = doGet(i);
            if (equalsElem(elem, e)) {
                list.add(e);
                doRemove(i);
                size--;
                i--;
            }
        }
        return list;
    }

    @Override
    public boolean removeAll(Collection<?> coll) {


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


    public boolean removeAll(IList<?> coll) {


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

    @Override
    public boolean retainAll(Collection<?> coll) {


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


    public boolean retainAll(IList<?> coll) {


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

    @Override
    public Object[] toArray() {
        return toArray(0, size());
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return toArray(array, 0, size());
    }


    public <T> T[] toArray(Class<T> clazz) {
        return toArray(clazz, 0, size());
    }


    public Object[] toArray(int index, int len) {
        Object[] array = new Object[len];
        doGetAll(array, index, len);
        return array;
    }


    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] array, int index, int len) {
        if (array.length < len) {
            array = (T[]) doCreateArray(array.getClass().getComponentType(), len);
        }
        doGetAll(array, index, len);
        if (array.length > len) {
            array[len] = null;
        }
        return array;
    }


    public <T> T[] toArray(Class<T> clazz, int index, int len) {
        T[] array = doCreateArray(clazz, len);
        doGetAll(array, index, len);
        return array;
    }


    @SuppressWarnings("unchecked")
    protected <T> T[] doCreateArray(Class<T> clazz, int len) {
        return (T[]) java.lang.reflect.Array.newInstance(clazz, len);
    }


    @SuppressWarnings("unchecked")
    protected <T> void doGetAll(T[] array, int index, int len) {
        for (int i = 0; i < len; i++) {
            array[i] = (T) doGet(index + i);
        }
    }


    protected boolean doAddAll(int index, IList<? extends E> list) {
        int listSize = list.size();
        doEnsureCapacity(size() + listSize);

        if (listSize == 0) {
            return false;
        }

        boolean changed = false;
        int prevSize = size();
        for (int i = 0; i < listSize; i++) {
            E elem = list.get(i);
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

    @Override
    public Iterator<E> iterator() {
        return new Iter(true);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIter(0);
    }


    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListIter(index);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new Iter(false);
    }

    @Override
    public E peek() {
        if (size() == 0) {
            return null;
        }
        return getFirst();
    }

    @Override
    public E element() {

        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doGet(0);
    }

    @Override
    public E poll() {
        if (size() == 0) {
            return null;
        }
        return doRemove(0);
    }

    @Override
    public E remove() {

        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doRemove(0);
    }

    @Override
    public boolean offer(E elem) {

        return doAdd(-1, elem);
    }

    @Override
    public E getFirst() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doGet(0);
    }

    @Override
    public E getLast() {
        int size = size();
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return doGet(size - 1);
    }

    @Override
    public void addFirst(E elem) {
        doAdd(0, elem);
    }

    @Override
    public void addLast(E elem) {

        doAdd(-1, elem);
    }

    @Override
    public E removeFirst() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doRemove(0);
    }

    @Override
    public E removeLast() {
        int size = size();
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return doRemove(size - 1);
    }

    @Override
    public boolean offerFirst(E elem) {

        doAdd(0, elem);
        return true;
    }

    @Override
    public boolean offerLast(E elem) {

        doAdd(-1, elem);
        return true;
    }

    @Override
    public E peekFirst() {
        if (size() == 0) {
            return null;
        }
        return doGet(0);
    }


    @Override
    public E peekLast() {
        int size = size();
        if (size == 0) {
            return null;
        }
        return doGet(size - 1);
    }

    @Override
    public E pollFirst() {
        if (size() == 0) {
            return null;
        }
        return doRemove(0);
    }

    @Override
    public E pollLast() {
        int size = size();
        if (size == 0) {
            return null;
        }
        return doRemove(size - 1);
    }

    @Override
    public E pop() {

        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return doRemove(0);

    }

    @Override
    public void push(E elem) {

        doAdd(0, elem);
    }

    @Override
    public boolean removeFirstOccurrence(Object elem) {
        int index = indexOf(elem);
        if (index == -1) {
            return false;
        }
        doRemove(index);
        return true;
    }

    @Override
    public boolean removeLastOccurrence(Object elem) {
        int index = lastIndexOf(elem);
        if (index == -1) {
            return false;
        }
        doRemove(index);
        return true;
    }

    void doTransfer(int transferMode, int srcIndex, int srcLen, IList<? super E> dst, int dstIndex, int dstLen) {

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

        E defaultElem = getDefaultElem();
        if (dstLen > srcLen) {

            dst.remove(dstIndex, dstLen - srcLen);
        } else if (srcLen > dstLen) {

            dst.addMult(dstIndex, srcLen - dstLen, defaultElem);
        }


        if (transferMode == TRANSFER_MOVE) {

            for (int i = 0; i < srcLen; i++) {
                E elem = doReSet(srcIndex + i, defaultElem);
                dst.doSet(dstIndex + i, elem);
            }
        } else {

            for (int i = 0; i < srcLen; i++) {
                E elem = doGet(srcIndex + i);
                dst.doSet(dstIndex + i, elem);
            }
            if (transferMode == TRANSFER_REMOVE) {

                remove(srcIndex, srcLen);
            }
        }
    }

    void doTransferSwap(int srcIndex, IList<E> dst, int dstIndex, int len) {
        checkRange(srcIndex, len);
        dst.checkRange(dstIndex, len);

        for (int i = 0; i < len; i++) {
            E swap = doGet(srcIndex + i);
            swap = dst.doSet(dstIndex + i, swap);
            doSet(srcIndex + i, swap);
        }
    }


    abstract protected IList<E> doCreate(int capacity);


    abstract protected void doAssign(IList<E> that);


    public IList<E> getAll(int index, int len) {
        checkRange(index, len);

        IList<E> list = doCreate(len);
        for (int i = 0; i < len; i++) {
            list.add(doGet(index + i));
        }
        return list;
    }


    public IList<E> extract(int index, int len) {
        checkRange(index, len);

        IList<E> list = doCreate(len);
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


    public boolean addAll(IList<? extends E> list) {
        return doAddAll(-1, list);
    }


    public boolean addAll(int index, IList<? extends E> list) {
        checkIndexAdd(index);

        return doAddAll(index, list);
    }


    @Override
    public boolean addAll(Collection<? extends E> coll) {
        if (coll instanceof List) {
            return doAddAll(-1, new IReadOnlyListFromList<E>((List<? extends E>) coll));
        } else {
            return doAddAll(-1, new IReadOnlyListFromCollection<E>(coll));
        }
    }


    @Override
    public boolean addAll(int index, Collection<? extends E> coll) {
        checkIndexAdd(index);

        if (coll instanceof List) {
            return doAddAll(index, new IReadOnlyListFromList<E>((List<? extends E>) coll));
        } else {
            return doAddAll(index, new IReadOnlyListFromCollection<E>(coll));
        }
    }


    public boolean addArray(E... elems) {
        return doAddAll(-1, new IReadOnlyListFromArray<E>(elems));
    }

    public boolean addArray(E[] elems, int offset, int length) {
        return doAddAll(-1, new IReadOnlyListFromArray<E>(elems, offset, length));
    }

    public boolean addArray(int index, E[] elems, int offset, int length) {
        return doAddAll(index, new IReadOnlyListFromArray<E>(elems, offset, length));
    }


    public boolean addArray(int index, E... elems) {
        checkIndexAdd(index);

        return doAddAll(index, new IReadOnlyListFromArray<E>(elems));
    }


    public boolean addMult(int len, E elem) {
        return doAddAll(-1, new IReadOnlyListFromMult<E>(len, elem));
    }


    public boolean addMult(int index, int len, E elem) {
        checkIndexAdd(index);

        return doAddAll(index, new IReadOnlyListFromMult<E>(len, elem));
    }


    public void setAll(int index, IList<? extends E> list) {
        int listSize = list.size();
        checkRange(index, listSize);

        doReplaceAll(index, listSize, list);
    }


    public void setAll(int index, Collection<? extends E> coll) {
        int collSize = coll.size();
        checkRange(index, collSize);

        if (coll instanceof List) {
            doReplaceAll(index, collSize, new IReadOnlyListFromList<E>((List<? extends E>) coll));
        } else {
            doReplaceAll(index, collSize, new IReadOnlyListFromCollection<E>(coll));
        }
    }


    public void setArray(int index, E... elems) {
        int arrayLen = elems.length;
        checkRange(index, arrayLen);

        doReplaceAll(index, arrayLen, new IReadOnlyListFromArray<E>(elems));
    }

    public void setArray(int index, E[] elems, int offset, int length) {
        int arrayLen = elems.length;
        checkRange(index, arrayLen);

        doReplaceAll(index, arrayLen, new IReadOnlyListFromArray<E>(elems, offset, length));
    }


    public void setMult(int index, int len, E elem) {
        checkRange(index, len);

        doReplaceAll(index, len, new IReadOnlyListFromMult<E>(len, elem));
    }


    public void putAll(int index, IList<? extends E> list) {
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


    public void putAll(int index, Collection<? extends E> coll) {
        if (coll instanceof IList) {
            putAll(index, (IList<? extends E>) coll);
        } else if (coll instanceof List) {
            putAll(index, new IReadOnlyListFromList<E>((List<? extends E>) coll));
        } else {
            putAll(index, new IReadOnlyListFromCollection<E>(coll));
        }
    }


    public void putArray(int index, E... elems) {
        putAll(index, new IReadOnlyListFromArray<E>(elems));
    }


    public void putMult(int index, int len, E elem) {
        putAll(index, new IReadOnlyListFromMult<E>(len, elem));
    }


    public void initAll(IList<? extends E> list) {
        checkNonNull(list);
        doClear();
        doAddAll(-1, list);
    }


    public void initAll(Collection<? extends E> coll) {
        if (coll instanceof IList) {
            initAll((IList<? extends E>) coll);
        } else if (coll instanceof List) {
            initAll(new IReadOnlyListFromList<E>((List<? extends E>) coll));
        } else {
            initAll(new IReadOnlyListFromCollection<E>(coll));
        }
    }


    public void initArray(E... elems) {
        initAll(new IReadOnlyListFromArray<E>(elems));
    }


    public void initMult(int len, E elem) {
        checkLength(len);

        initAll(new IReadOnlyListFromMult<E>(len, elem));
    }


    public void replaceAll(int index, int len, Collection<? extends E> coll) {
        if (coll instanceof IList) {
            replaceAll(index, len, (IList<? extends E>) coll);
        } else if (coll instanceof List) {
            replaceAll(index, len, new IReadOnlyListFromList<E>((List<? extends E>) coll));
        } else {
            replaceAll(index, len, new IReadOnlyListFromCollection<E>(coll));
        }
    }


    public void replaceArray(int index, int len, E... elems) {
        replaceAll(index, len, new IReadOnlyListFromArray<E>(elems));
    }


    public void replaceMult(int index, int len, int numElems, E elem) {
        replaceAll(index, len, new IReadOnlyListFromMult<E>(numElems, elem));
    }


    public void replaceAll(int index, int len, IList<? extends E> list) {

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


    protected boolean doReplaceAll(int index, int len, IList<? extends E> list) {


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


    public void fill(E elem) {
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
            setMult(srcIndex, fill, null);
        } else if (srcIndex > dstIndex) {
            int fill = Math.min(len, srcIndex - dstIndex);
            setMult(srcIndex + len - fill, fill, null);
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
            E swap = doGet(index1 + i);
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
            E swap = doGet(pos1);
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
            E elem = doGet(index + start);
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


    @Override
    public void sort(Comparator<? super E> comparator) {
        sort(0, size(), comparator);
    }


    abstract public void sort(int index, int len, Comparator<? super E> comparator);


    public <K> int binarySearch(K key, Comparator<? super K> comparator) {
        return binarySearch(0, size(), key, comparator);
    }


    abstract public <K> int binarySearch(int index, int len, K key, Comparator<? super K> comparator);


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
            throw new NullPointerException("Argument may not be null");
        }
    }


    protected static abstract class IReadOnlyList<E> extends IList<E> {

        @Override
        public IList<E> unmodifiableList() {
            error();
            return null;
        }

        @Override
        protected void doClone(IList<E> that) {
            error();
        }

        @Override
        public int capacity() {
            error();
            return 0;
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
        protected E getDefaultElem() {
            error();
            return null;
        }

        @Override
        protected boolean doAdd(int index, E elem) {
            error();
            return false;
        }

        @Override
        protected E doRemove(int index) {
            error();
            return null;
        }

        @Override
        protected void doEnsureCapacity(int minCapacity) {
            error();
        }

        @Override
        public void trimToSize() {
            error();
        }

        @Override
        protected IList<E> doCreate(int capacity) {
            error();
            return null;
        }

        @Override
        protected void doAssign(IList<E> that) {
            error();
        }

        @Override
        public void sort(int index, int len, Comparator<? super E> comparator) {
            error();
        }

        @Override
        public <K> int binarySearch(int index, int len, K key, Comparator<? super K> comparator) {
            error();
            return 0;
        }


        private void error() {
            throw new UnsupportedOperationException("list is read-only");
        }
    }


    protected static class IReadOnlyListFromArray<E> extends IReadOnlyList<E> {
        E[] array;
        int offset;
        int length;

        IReadOnlyListFromArray(E[] array) {
            this.array = array;
            this.offset = 0;
            this.length = array.length;
        }

        IReadOnlyListFromArray(E[] array, int offset, int length) {
            this.array = array;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public int size() {
            return length;
        }

        @Override
        protected E doGet(int index) {
            return array[offset + index];
        }
    }


    protected static class IReadOnlyListFromMult<E> extends IReadOnlyList<E> {
        int len;
        E elem;

        IReadOnlyListFromMult(int len, E elem) {
            checkLength(len);

            this.len = len;
            this.elem = elem;
        }

        @Override
        public int size() {
            return len;
        }

        @Override
        protected E doGet(int index) {
            return elem;
        }
    }

    protected static class IReadOnlyListFromCollection<E> extends IReadOnlyList<E> {
        Object[] array;

        IReadOnlyListFromCollection(Collection<? extends E> coll) {
            array = coll.toArray();
        }

        @Override
        public int size() {
            return array.length;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected E doGet(int index) {
            return (E) array[index];
        }
    }

    protected static class IReadOnlyListFromList<E> extends IReadOnlyList<E> {
        List<E> list2;

        @SuppressWarnings("unchecked")
        IReadOnlyListFromList(List<? extends E> list) {
            this.list2 = (List<E>) list;
        }

        @Override
        public int size() {
            return list2.size();
        }

        @Override
        protected E doGet(int index) {
            return list2.get(index);
        }
    }


    class Iter implements Iterator<E> {

        boolean forward;

        int index;

        int remove;


        public Iter(boolean forward) {
            this.forward = forward;

            if (forward) {
                index = 0;
            } else {
                index = size() - 1;
            }
            remove = -1;
        }

        @Override
        public boolean hasNext() {
            if (forward) {
                return index != size();
            } else {
                return index != -1;
            }
        }

        @Override
        public E next() {
            if (forward) {
                if (index >= size()) {
                    throw new NoSuchElementException();
                }
            } else {
                if (index < 0) {
                    throw new NoSuchElementException();
                }
            }
            E elem = get(index);
            remove = index;
            if (forward) {
                index++;
            } else {
                index--;
            }
            return elem;
        }

        @Override
        public void remove() {
            if (remove == -1) {
                throw new IllegalStateException("No current element to remove");
            }
            IList.this.remove(remove);
            if (index > remove) {
                index--;
            }
            remove = -1;
        }
    }


    class ListIter implements ListIterator<E> {

        int index;

        int remove;


        public ListIter(int index) {
            checkIndexAdd(index);
            this.index = index;
            this.remove = -1;
        }

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public E next() {
            if (index >= size()) {
                throw new NoSuchElementException();
            }
            E elem = IList.this.get(index);
            remove = index;
            index++;
            return elem;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public E previous() {
            if (index <= 0) {
                throw new NoSuchElementException();
            }
            index--;
            E elem = IList.this.get(index);
            remove = index;
            return elem;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            if (remove == -1) {
                throw new IllegalStateException("No current element to remove");
            }
            IList.this.remove(remove);
            if (index > remove) {
                index--;
            }
            remove = -1;
        }

        @Override
        public void set(E e) {
            if (remove == -1) {
                throw new IllegalStateException("No current element to set");
            }
            IList.this.set(remove, e);
        }

        @Override
        public void add(E e) {
            IList.this.add(index, e);
            index++;
            remove = -1;
        }
    }

}
