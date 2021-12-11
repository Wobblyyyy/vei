package org.magicwerk.brownies.collections.primitive;

import org.magicwerk.brownies.collections.GapList;
import org.magicwerk.brownies.collections.helper.ArraysHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;


public class CharGapList extends ICharList {


    public static final int DEFAULT_CAPACITY = 10;

    private static final boolean DEBUG_CHECK = false;

    private static final boolean DEBUG_TRACE = false;

    private static final boolean DEBUG_DUMP = false;


    private static final CharGapList EMPTY = CharGapList.create().unmodifiableList();

    private static final long serialVersionUID = -4477005565661968383L;

    private static final char[] EMPTY_VALUES = new char[0];

    private char[] values;

    private int size;

    private int start;

    private int end;

    private int gapSize;

    private int gapIndex;

    private int gapStart;


    protected CharGapList(boolean copy, CharGapList that) {
        if (copy) {
            doAssign(that);
        }
    }


    public CharGapList() {
        init();
    }


    public CharGapList(int capacity) {
        init(new char[capacity], 0);
    }


    public CharGapList(Collection<Character> coll) {
        init(coll);
    }

    public CharGapList(String str) {
        init(str);
    }


    public static CharGapList EMPTY() {
        return EMPTY;
    }


    public static CharGapList create() {
        return new CharGapList();
    }


    public static CharGapList create(Collection<Character> coll) {
        return new CharGapList(((coll != null)) ? coll : Collections.emptyList());
    }


    @SafeVarargs
    public static CharGapList create(char... elems) {
        CharGapList list = new CharGapList();
        if (elems != null) {
            if (elems != null) {
                list.init(elems);
            }
        }
        return list;
    }

    public static CharGapList create(String str) {
        return new CharGapList(str);
    }


    private final int physIndex(int idx) {
        int physIdx = idx + start;
        if (idx >= gapIndex) {
            physIdx += gapSize;
        }
        if (physIdx >= values.length) {
            physIdx -= values.length;
        }
        return physIdx;
    }


    private int[] physIndex(int idx0, int idx1) {
        assert (idx0 >= 0 && idx1 <= size && idx0 <= idx1);
        if (idx0 == idx1) {
            return new int[0];
        }


        idx1--;
        int pidx0 = physIndex(idx0);
        if (idx1 == idx0) {
            return new int[]{pidx0, pidx0 + 1};
        }
        int pidx1 = physIndex(idx1);
        if (pidx0 < pidx1) {
            if (gapSize > 0 && pidx0 < gapStart && pidx1 > gapStart) {
                assert (pidx0 < gapStart);
                assert (gapStart + gapSize < pidx1 + 1);
                return new int[]{pidx0, gapStart, gapStart + gapSize, pidx1 + 1};
            } else {
                return new int[]{pidx0, pidx1 + 1};
            }
        } else {
            assert (start != 0);
            return doPhysIndex(pidx0, pidx1);
        }
    }

    private int[] doPhysIndex(int pidx0, int pidx1) {
        assert (pidx0 > pidx1);
        if (gapSize > 0 && pidx1 > gapStart && gapStart > 0) {
            assert (pidx0 < values.length);
            assert (0 < gapStart);
            assert (gapStart + gapSize < pidx1 + 1);
            return new int[]{pidx0, values.length, 0, gapStart, gapStart + gapSize, pidx1 + 1};
        } else if (gapSize > 0 && pidx0 < gapStart && gapStart + gapSize < values.length) {
            assert (pidx0 < gapStart);
            assert (gapStart + gapSize < values.length);
            assert (0 < pidx1 + 1);
            return new int[]{pidx0, gapStart, gapStart + gapSize, values.length, 0, pidx1 + 1};
        } else {
            return doPhysIndex2(pidx0, pidx1);
        }
    }

    private int[] doPhysIndex2(int pidx0, int pidx1) {
        assert (pidx0 < values.length);
        assert (0 < pidx1 + 1);
        int end = values.length;
        if (gapSize > 0 && gapStart > pidx0) {
            end = gapStart;
        }
        int start = 0;
        if (gapSize > 0 && (gapStart + gapSize) % values.length < pidx1 + 1) {
            start = (gapStart + gapSize) % values.length;
        }
        return new int[]{pidx0, end, start, pidx1 + 1};
    }

    @Override
    protected void doAssign(ICharList that) {
        CharGapList list = (CharGapList) that;
        this.values = list.values;
        this.size = list.size;
        this.start = list.start;
        this.end = list.end;
        this.gapSize = list.gapSize;
        this.gapIndex = list.gapIndex;
        this.gapStart = list.gapStart;
    }


    void init() {
        init(new char[0], 0);
    }


    void init(Collection<Character> coll) {
        char[] array = toArray(coll);
        init(array, array.length);
    }


    void init(char... elems) {
        char[] array = elems.clone();
        init(array, array.length);
    }

    @Override
    public char getDefaultElem() {
        return (char) 0;
    }

    @Override

    public CharGapList copy() {
        return (CharGapList) clone();
    }

    @Override
    public Object clone() {
        if (this instanceof ImmutableCharGapList) {
            CharGapList list = new CharGapList(false, null);
            list.doClone(this);
            return list;
        } else {
            return super.clone();
        }
    }


    @Override
    public void ensureCapacity(int minCapacity) {
        super.ensureCapacity(minCapacity);
    }

    @Override
    public CharGapList unmodifiableList() {
        if (this instanceof ImmutableCharGapList) {
            return this;
        } else {
            return new ImmutableCharGapList(this);
        }
    }

    @Override
    protected void doClone(ICharList that) {

        init(that.toArray(), that.size());
    }


    void ensureNormalized(int minCapacity) {
        int oldCapacity = values.length;
        int newCapacity = calculateNewCapacity(minCapacity);
        boolean capacityFits = (newCapacity <= oldCapacity);
        boolean alreadyNormalized = isNormalized();
        if (capacityFits && alreadyNormalized) {
            return;
        }

        char[] newValues = new char[newCapacity];
        doGetAll(newValues, 0, size);
        init(newValues, size);
    }


    boolean isNormalized() {
        return start == 0 && gapSize == 0 && gapStart == 0 && gapIndex == 0;
    }


    void init(char[] values, int size) {
        this.values = values;
        this.size = size;
        start = 0;
        end = size;
        if (end >= values.length) {
            end -= values.length;
        }
        gapSize = 0;
        gapStart = 0;
        gapIndex = 0;
        if (DEBUG_CHECK)
            debugCheck();
    }

    @Override
    protected void doClear() {
        init(values, 0);
        for (int i = 0; i < values.length; i++) {
            values[i] = (char) 0;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int capacity() {
        return values.length;
    }

    @Override
    public char get(int index) {


        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + " (size: " + size() + ")");
        }
        return doGet(index);
    }

    @Override
    protected char doGet(int index) {
        assert (index >= 0 && index < size);

        int physIdx = index + start;
        if (index >= gapIndex) {
            physIdx += gapSize;
        }
        if (physIdx >= values.length) {
            physIdx -= values.length;
        }
        return values[physIdx];
    }

    @Override
    protected char doSet(int index, char elem) {
        assert (index >= 0 && index < size);
        int physIdx = physIndex(index);
        char oldElem = values[physIdx];
        values[physIdx] = elem;
        return oldElem;
    }

    @Override
    protected char doReSet(int index, char elem) {
        assert (index >= 0 && index < size);
        int physIdx = physIndex(index);
        char oldElem = values[physIdx];
        values[physIdx] = elem;
        return oldElem;
    }

    @Override
    public boolean add(char elem) {
        if (DEBUG_TRACE) {
            debugLog("add: " + elem);
            if (DEBUG_DUMP)
                debugDump();
        }
        return doAdd(-1, elem);
    }

    @Override
    public void add(int index, char elem) {
        if (DEBUG_TRACE) {
            debugLog("add: " + index + ", " + elem);
            if (DEBUG_DUMP)
                debugDump();
        }
        checkIndexAdd(index);
        doAdd(index, elem);
    }

    @Override
    public CharGapList getAll(int index, int len) {
        checkRange(index, len);
        CharGapList list = doCreate(len);
        list.size = len;
        doGetAll(list.values, index, len);
        return list;
    }

    @Override
    public CharGapList getAll(char elem) {
        return (CharGapList) super.getAll(elem);
    }

    @Override
    public <R> GapList<R> mappedList(Function<Character, R> mapper) {
        return (GapList<R>) super.mappedList(mapper);
    }


    char[] prepareAddBuffer(int index, int len) {
        assert (index == size);
        assert (len >= 0);
        if (len > 0) {
            ensureNormalized(index + len);
            size += len;
            end += len;
        }
        if (DEBUG_DUMP)
            debugDump();
        if (DEBUG_CHECK)
            debugCheck();
        return values;
    }


    void releaseAddBuffer(int index, int len) {
        assert (isNormalized());
        assert (index + len <= size);
        if (index + len < size) {
            size = index + len;
            end = size;
        }
        if (DEBUG_DUMP)
            debugDump();
        if (DEBUG_CHECK)
            debugCheck();
    }

    @Override
    protected boolean doAdd(int index, char elem) {
        doEnsureCapacity(size + 1);
        if (index == -1) {
            index = size;
        }
        assert (index >= 0 && index <= size);
        int physIdx;

        if (index == size && (end != start || size == 0)) {
            if (DEBUG_TRACE)
                debugLog("Case A0");
            physIdx = end;
            end++;
            if (end >= values.length) {
                end -= values.length;
            }

        } else if (index == 0 && (end != start || size == 0)) {
            if (DEBUG_TRACE)
                debugLog("Case A1");
            start--;
            if (start < 0) {
                start += values.length;
            }
            physIdx = start;
            if (gapSize > 0) {
                gapIndex++;
            }

        } else if (gapSize > 0 && index == gapIndex) {
            if (DEBUG_TRACE)
                debugLog("Case A2");
            physIdx = gapStart + gapSize - 1;
            if (physIdx >= values.length) {
                physIdx -= values.length;
            }
            gapSize--;

        } else {
            physIdx = physIndex(index);
            if (gapSize == 0) {

                if (start < end && start > 0) {

                    assert (debugState() == 4);
                    int len1 = physIdx - start;
                    int len2 = end - physIdx;
                    if (len1 <= len2) {
                        if (DEBUG_TRACE)
                            debugLog("Case A3");
                        moveData(start, 0, len1);
                        gapSize = start - 1;
                        gapStart = len1;
                        gapIndex = len1;
                        start = 0;
                        physIdx--;
                    } else {
                        if (DEBUG_TRACE)
                            debugLog("Case A4");
                        moveData(physIdx, values.length - len2, len2);
                        gapSize = values.length - end - 1;
                        gapStart = physIdx + 1;
                        gapIndex = index + 1;
                        end = 0;
                    }
                } else if (physIdx < end) {
                    assert (debugState() == 2 || debugState() == 5);
                    if (DEBUG_TRACE)
                        debugLog("Case A5");
                    int len = end - physIdx;
                    int rightSize = (start - end + values.length) % values.length;
                    moveData(physIdx, end + rightSize - len, len);
                    end = start;
                    gapSize = rightSize - 1;
                    gapStart = physIdx + 1;
                    gapIndex = index + 1;
                } else {
                    assert (debugState() == 3 || debugState() == 5);
                    assert (physIdx > end);
                    if (DEBUG_TRACE)
                        debugLog("Case A6");
                    int len = physIdx - start;
                    int rightSize = start - end;
                    moveData(start, end, len);
                    start -= rightSize;
                    end = start;
                    gapSize = rightSize - 1;
                    gapStart = start + len;
                    gapIndex = index;
                    physIdx--;
                }
            } else {

                boolean moveLeft;
                int gapEnd = (gapStart + gapSize - 1) % values.length + 1;
                if (gapEnd < gapStart) {
                    assert (debugState() == 9 || debugState() == 12);

                    int len1 = physIdx - gapEnd;
                    int len2 = gapStart - physIdx - 1;
                    if (len1 <= len2) {
                        if (DEBUG_TRACE)
                            debugLog("Case A7a");
                        moveLeft = true;
                    } else {
                        if (DEBUG_TRACE)
                            debugLog("Case A8a");
                        moveLeft = false;
                    }
                } else {
                    assert (debugState() == 6 || debugState() == 7 || debugState() == 8 || debugState() == 9 || debugState() == 10 || debugState() == 11 || debugState() == 12 || debugState() == 13 || debugState() == 14 || debugState() == 15);
                    if (physIdx > gapStart) {
                        if (DEBUG_TRACE)
                            debugLog("Case A7b");
                        moveLeft = true;
                    } else {
                        if (DEBUG_TRACE)
                            debugLog("Case A8b");
                        moveLeft = false;
                    }
                }
                if (moveLeft) {
                    int src = gapStart + gapSize;
                    int dst = gapStart;
                    int len = physIdx - gapEnd;
                    moveDataWithGap(src, dst, len);
                    physIdx--;
                    gapSize--;
                    gapIndex = index;
                    gapStart += len;
                    if (gapStart >= values.length) {
                        gapStart -= values.length;
                    }
                    if (index == 0) {
                        start = physIdx;
                        if ((gapStart + gapSize) % values.length == start) {
                            end = gapStart;
                            gapSize = 0;
                        }
                    }
                } else {
                    int src = physIdx;
                    int dst = physIdx + gapSize;
                    int len = gapStart - physIdx;
                    moveDataWithGap(src, dst, len);
                    gapSize--;
                    gapStart = physIdx + 1;
                    gapIndex = index + 1;
                    if (index == 0) {
                        start = physIdx;
                        end = physIdx;
                    } else if (index == size) {
                        if ((gapStart + gapSize) % values.length == start) {
                            end = gapStart;
                            gapSize = 0;
                        }
                    }
                }
            }
        }
        values[physIdx] = elem;
        size++;
        if (DEBUG_DUMP)
            debugDump();
        if (DEBUG_CHECK)
            debugCheck();
        return true;
    }


    private void moveDataWithGap(int src, int dst, int len) {
        if (DEBUG_TRACE) {
            debugLog("moveGap: " + src + "-" + src + len + " -> " + dst + "-" + dst + len);
        }
        if (src > values.length) {
            src -= values.length;
        }
        if (dst > values.length) {
            dst -= values.length;
        }
        assert (len >= 0);
        assert (src + len <= values.length);
        if (start >= src && start < src + len) {
            start += dst - src;
            if (start >= values.length) {
                start -= values.length;
            }
        }
        if (end >= src && end < src + len) {
            end += dst - src;
            if (end >= values.length) {
                end -= values.length;
            }
        }
        if (dst + len <= values.length) {
            moveData(src, dst, len);
        } else {


            int len2 = dst + len - values.length;
            int len1 = len - len2;
            if (!(src <= len2 && len2 < dst)) {
                moveData(src + len1, 0, len2);
                moveData(src, dst, len1);
            } else {
                moveData(src, dst, len1);
                moveData(src + len1, 0, len2);
            }
        }
    }


    private void moveData(int src, int dst, int len) {
        if (DEBUG_TRACE) {
            debugLog("moveData: " + src + "-" + src + len + " -> " + dst + "-" + dst + len);
            if (DEBUG_DUMP) {
                debugLog(debugPrint(values));
            }
        }
        System.arraycopy(values, src, values, dst, len);


        int start;
        int end;
        if (src <= dst) {
            start = src;
            end = (dst < src + len) ? dst : src + len;
        } else {
            start = (src > dst + len) ? src : dst + len;
            end = src + len;
        }

        assert (end - start <= len);
        for (int i = start; i < end; i++) {
            values[i] = (char) 0;
        }
        if (DEBUG_TRACE) {
            if (DEBUG_DUMP) {
                debugLog(debugPrint(values));
            }
        }
    }

    @Override
    public char remove(int index) {
        checkIndex(index);
        if (DEBUG_TRACE) {
            debugLog("remove: " + index);
            if (DEBUG_DUMP)
                debugDump();
        }
        return doRemove(index);
    }

    @Override
    protected char doRemove(int index) {
        int physIdx;

        if (index == size - 1) {
            if (DEBUG_TRACE)
                debugLog("Case R0");
            end--;
            if (end < 0) {
                end += values.length;
            }
            physIdx = end;

            if (gapSize > 0) {
                if (gapIndex == index) {

                    end = gapStart;
                    gapSize = 0;
                }
            }

        } else if (index == 0) {
            if (DEBUG_TRACE)
                debugLog("Case R1");
            physIdx = start;
            start++;
            if (start >= values.length) {

                start -= values.length;
            }

            if (gapSize > 0) {
                if (gapIndex == 1) {
                    start += gapSize;
                    if (start >= values.length) {

                        start -= values.length;
                    }
                    gapSize = 0;
                } else {
                    gapIndex--;
                }
            }
        } else {

            physIdx = physIndex(index);

            if (gapSize == 0) {
                if (DEBUG_TRACE)
                    debugLog("Case R2");
                gapIndex = index;
                gapStart = physIdx;
                gapSize = 1;

            } else if (index == gapIndex) {
                if (DEBUG_TRACE)
                    debugLog("Case R3");
                gapSize++;

            } else if (index == gapIndex - 1) {
                if (DEBUG_TRACE)
                    debugLog("Case R4");
                gapStart--;
                if (gapStart < 0) {
                    gapStart += values.length;
                }
                gapSize++;
                gapIndex--;
            } else {

                assert (gapSize > 0);
                boolean moveLeft;
                int gapEnd = (gapStart + gapSize - 1) % values.length + 1;
                if (gapEnd < gapStart) {


                    int len1 = physIdx - gapEnd;
                    int len2 = gapStart - physIdx - 1;
                    if (len1 <= len2) {
                        if (DEBUG_TRACE)
                            debugLog("Case R5a");
                        moveLeft = true;
                    } else {
                        if (DEBUG_TRACE)
                            debugLog("Case R6a");
                        moveLeft = false;
                    }
                } else {
                    if (physIdx > gapStart) {

                        if (DEBUG_TRACE)
                            debugLog("Case R5b");
                        moveLeft = true;
                    } else {

                        if (DEBUG_TRACE)
                            debugLog("Case R6b");
                        moveLeft = false;
                    }
                }
                if (moveLeft) {
                    int src = gapStart + gapSize;
                    int dst = gapStart;
                    int len = physIdx - gapEnd;
                    moveDataWithGap(src, dst, len);
                    gapStart += len;
                    if (gapStart >= values.length) {
                        gapStart -= values.length;
                    }
                    gapSize++;
                } else {
                    int src = physIdx + 1;
                    int dst = physIdx + gapSize + 1;
                    int len = gapStart - physIdx - 1;
                    moveDataWithGap(src, dst, len);
                    gapStart = physIdx;
                    gapSize++;
                }
                gapIndex = index;
            }
        }
        char removed = values[physIdx];
        values[physIdx] = (char) 0;
        size--;
        if (DEBUG_DUMP)
            debugDump();
        if (DEBUG_CHECK)
            debugCheck();
        return removed;
    }

    @Override
    protected void doEnsureCapacity(int minCapacity) {
        int newCapacity = calculateNewCapacity(minCapacity);
        if (newCapacity == values.length) {
            return;
        }
        char[] newValues = new char[newCapacity];
        if (size == 0) {
        } else if (start == 0) {

            System.arraycopy(values, 0, newValues, 0, values.length);
        } else if (start > 0) {
            int grow = newCapacity - values.length;
            newValues = new char[newCapacity];
            System.arraycopy(values, 0, newValues, 0, start);
            System.arraycopy(values, start, newValues, start + grow, values.length - start);
            if (gapStart > start && gapSize > 0) {
                gapStart += grow;
            }
            if (end > start) {
                end += grow;
            }
            start += grow;
        }
        if (end == 0 && start == 0 && size != 0) {

            end = values.length;
        }
        values = newValues;
        if (DEBUG_DUMP)
            debugDump();
        if (DEBUG_CHECK)
            debugCheck();
    }


    int calculateNewCapacity(int minCapacity) {

        int oldCapacity = values.length;
        if (minCapacity <= oldCapacity) {

            return values.length;
        }
        minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        int newCapacity = (oldCapacity * 3) / 2 + 1;
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        return newCapacity;
    }


    @Override
    public void trimToSize() {
        doModify();
        if (size < values.length) {
            init(toArray(), size);
        }
    }

    @Override
    protected void doGetAll(char[] array, int index, int len) {
        int[] physIdx = physIndex(index, index + len);
        int pos = 0;
        for (int i = 0; i < physIdx.length; i += 2) {
            int num = physIdx[i + 1] - physIdx[i];
            System.arraycopy(values, physIdx[i], array, pos, num);
            pos += num;
        }
        assert (pos == len);
    }


    private void writeObject(ObjectOutputStream oos) throws IOException {

        int size = size();
        oos.writeInt(size);

        for (int i = 0; i < size; i++) {
            oos.writeChar(doGet(i));
        }
    }


    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

        size = ois.readInt();
        values = new char[size];

        for (int i = 0; i < size; i++) {
            values[i] = ois.readChar();
        }
    }

    @Override
    public CharGapList doCreate(int capacity) {
        if (capacity == -1) {
            capacity = DEFAULT_CAPACITY;
        }
        return new CharGapList(capacity);
    }

    @Override
    protected void doRemoveAll(int index, int len) {
        if (len > 0 && len == size()) {
            doModify();
            doClear();
        } else {
            if (!doRemoveAllFast(index, len)) {
                for (int i = 0; i < len; i++) {
                    doRemove(index);
                }
            }
        }
    }


    protected boolean doRemoveAllFast(int index, int len) {

        if (gapSize > 0) {
            return false;
        }
        if (index != 0 && index + len != size) {
            return false;
        }
        assert (gapSize == 0);
        int[] physIdx = physIndex(index, index + len);
        for (int i = 0; i < physIdx.length; i += 2) {
            for (int j = physIdx[i]; j < physIdx[i + 1]; j++) {
                values[j] = (char) 0;
            }
        }
        if (index + len == size) {

            end -= len;
            if (end < 0) {
                end += values.length;
            }
        } else if (index == 0) {

            start += len;
            if (start >= values.length) {
                start -= values.length;
            }
        } else {
            assert (false);
        }
        size -= len;
        if (DEBUG_DUMP)
            debugDump();
        if (DEBUG_CHECK)
            debugCheck();
        return true;
    }

    @Override
    public void sort(int index, int len) {
        checkRange(index, len);
        ensureNormalized(size);
        ArraysHelper.sort(values, index, index + len);
    }

    @Override
    public int binarySearch(int index, int len, char key) {
        checkRange(index, len);
        ensureNormalized(size);
        return ArraysHelper.binarySearch(values, index, index + len, key);
    }


    private void debugCheck() {

        if (values == null) {
            assert (size == 0 && start == 0 && end == 0);
            assert (gapSize == 0 && gapStart == 0 && gapIndex == 0);
            return;
        }
        assert (size >= 0 && size <= values.length);
        assert (start >= 0 && (start < values.length || values.length == 0));
        assert (end >= 0 && (end < values.length || values.length == 0));
        assert (values.length == 0 || (start + size + gapSize) % values.length == end);

        assert (gapSize >= 0);
        if (gapSize > 0) {
            assert (gapStart >= 0 && gapStart < values.length);

            assert (gapIndex > 0 && gapIndex < size);

            assert (gapStart != start && gapStart != end);

            assert (physIndex(gapIndex) == (gapStart + gapSize) % values.length);
        }

        if (gapSize > 0) {
            for (int i = gapStart; i < gapStart + gapSize; i++) {
                int pos = (i % values.length);
                assert (values[pos] == (char) 0);
            }
        }

        if (start < end) {
            for (int i = 0; i < start; i++) {
                assert (values[i] == (char) 0);
            }
            for (int i = end; i < values.length; i++) {
                assert (values[i] == (char) 0);
            }
        } else if (end < start) {
            for (int i = end; i < start; i++) {
                assert (values[i] == (char) 0);
            }
        }
    }


    private int debugState() {
        if (size == 0) {
            return 0;
        } else if (size == values.length) {
            return 1;
        } else if (gapSize == 0) {
            if (start == 0) {
                return 2;
            } else if (end == 0) {
                return 3;
            } else if (start < end) {
                return 4;
            } else if (start > end) {
                return 5;
            }
        } else if (gapSize > 0) {
            if (start == end) {
                if (start == 0) {
                    return 6;
                } else if (gapStart < start) {
                    return 7;
                } else if (gapStart > start) {
                    int gapEnd = (gapStart + gapSize) % values.length;
                    if (gapEnd > gapStart) {
                        return 8;
                    } else if (gapEnd < gapStart) {
                        return 9;
                    }
                }
            } else if (start != end) {
                if (start == 0) {
                    return 10;
                } else if (gapStart < start) {

                    return 14;
                } else if (gapStart > start) {
                    int gapEnd = (gapStart + gapSize) % values.length;
                    if (gapEnd < gapStart) {
                        return 12;
                    } else {
                        if (end == 0) {
                            return 11;
                        } else if (end > start) {
                            return 13;
                        } else if (end < start) {
                            return 15;
                        }
                    }
                }
            }
        }
        assert (false);
        return -1;
    }


    private void debugDump() {
        debugLog("values: size= " + values.length + ", data= " + debugPrint(values));
        debugLog("size=" + size + ", start=" + start + ", end=" + end + ", gapStart=" + gapStart + ", gapSize=" + gapSize + ", gapIndex=" + gapIndex);
        debugLog(toString());
    }


    private String debugPrint(char[] values) {
        StringBuilder buf = new StringBuilder();
        buf.append("[ ");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(values[i]);
        }
        buf.append(" ]");
        return buf.toString();
    }


    private void debugLog(String msg) {
    }

    public void init(String str) {
        char[] array = str.toCharArray();
        init(array, array.length);
    }


    protected static class ImmutableCharGapList extends CharGapList {


        private static final long serialVersionUID = -1352274047348922584L;


        protected ImmutableCharGapList(CharGapList that) {
            super(true, that);
        }

        @Override
        protected boolean doAdd(int index, char elem) {
            error();
            return false;
        }

        @Override
        protected char doSet(int index, char elem) {
            error();
            return (char) 0;
        }

        @Override
        protected char doReSet(int index, char elem) {
            error();
            return (char) 0;
        }

        @Override
        protected char doRemove(int index) {
            error();
            return (char) 0;
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
