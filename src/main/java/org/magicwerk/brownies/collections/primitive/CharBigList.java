package org.magicwerk.brownies.collections.primitive;

import org.magicwerk.brownies.collections.helper.primitive.CharBinarySearch;
import org.magicwerk.brownies.collections.helper.primitive.CharMergeSort;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;


public class CharBigList extends ICharList {


    private static final long serialVersionUID = 3715838828540564836L;


    private static final int DEFAULT_BLOCK_SIZE = 1000;


    private static final float MERGE_THRESHOLD = 0.35f;


    private static final float FILL_THRESHOLD = 0.95f;


    private static final boolean CHECK = false;


    private static final CharBigList EMPTY = CharBigList.create().unmodifiableList();

    private int blockSize;

    private int size;

    private CharBlockNode rootNode;

    private CharBlockNode currNode;

    private int currCharBlockStart;


    private int currCharBlockEnd;

    private int currModify;


    protected CharBigList(boolean copy, CharBigList that) {
        if (copy) {
            this.blockSize = that.blockSize;
            this.currCharBlockStart = that.currCharBlockStart;
            this.currCharBlockEnd = that.currCharBlockEnd;
            this.currNode = that.currNode;
            this.rootNode = that.rootNode;
            this.size = that.size;
        }
    }


    public CharBigList() {
        this(DEFAULT_BLOCK_SIZE);
    }


    public CharBigList(int blockSize) {
        if (blockSize < 2) {
            throw new IndexOutOfBoundsException("Invalid blockSize: " + blockSize);
        }
        doInit(blockSize, -1);
    }


    public CharBigList(Collection<Character> coll) {
        if (coll instanceof CharBigList) {
            doAssign((CharBigList) coll);
            doClone((CharBigList) coll);
        } else {
            blockSize = DEFAULT_BLOCK_SIZE;
            addCharBlock(0, new CharBlock());
            for (Object obj : coll.toArray()) {
                add((Character) obj);
            }
            assert (size() == coll.size());
        }
    }


    private CharBigList(int blockSize, int firstCharBlockSize) {
        doInit(blockSize, firstCharBlockSize);
    }

    public CharBigList(String str) {
        init(str);
    }


    public static CharBigList EMPTY() {
        return EMPTY;
    }


    public static CharBigList create() {
        return new CharBigList();
    }


    public static CharBigList create(Collection<Character> coll) {
        return new CharBigList((coll != null) ? coll : Collections.emptyList());
    }


    public static CharBigList create(char... elems) {
        CharBigList list = new CharBigList();
        if (elems != null) {
            for (char elem : elems) {
                list.add(elem);
            }
        }
        return list;
    }

    public static CharBigList create(String str) {
        return new CharBigList(str);
    }


    public int blockSize() {
        return blockSize;
    }


    private void doInit(int blockSize, int firstCharBlockSize) {
        this.blockSize = blockSize;

        CharBlock block;
        if (firstCharBlockSize <= 1) {
            block = new CharBlock();
        } else {
            block = new CharBlock(firstCharBlockSize);
        }
        addCharBlock(0, block);
    }


    @Override

    public CharBigList copy() {
        return (CharBigList) clone();
    }


    @Override
    public Object clone() {
        if (this instanceof ImmutableCharBigList) {
            CharBigList list = new CharBigList(false, null);
            list.doClone(this);
            return list;
        } else {
            return super.clone();
        }
    }

    @Override
    protected void doAssign(ICharList that) {
        CharBigList list = (CharBigList) that;
        this.blockSize = list.blockSize;
        this.currCharBlockEnd = list.currCharBlockEnd;
        this.currCharBlockStart = list.currCharBlockStart;
        this.currNode = list.currNode;
        this.rootNode = list.rootNode;
        this.size = list.size;
    }

    @Override
    protected void doClone(ICharList that) {
        CharBigList bigList = (CharBigList) that;
        bigList.releaseCharBlock();
        rootNode = copy(bigList.rootNode);
        currNode = null;
        currModify = 0;
        if (CHECK)
            check();
    }


    private CharBlockNode copy(CharBlockNode node) {
        CharBlockNode newNode = node.min();
        int index = newNode.block.size();
        CharBlockNode newRoot = new CharBlockNode(null, index, newNode.block.ref(), null, null);
        while (true) {
            newNode = newNode.next();
            if (newNode == null) {
                return newRoot;
            }
            index += newNode.block.size();
            newRoot = newRoot.insert(index, newNode.block.ref());
            newRoot.parent = null;
        }
    }

    @Override
    public char getDefaultElem() {
        return (char) 0;
    }

    @Override
    protected void finalize() {


        CharBlockNode node = rootNode.min();
        while (node != null) {
            node.block.unref();
            node = node.next();
        }
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public int capacity() {
        return -1;
    }

    @Override
    protected char doGet(int index) {
        int pos = getCharBlockIndex(index, false, 0);
        return currNode.block.doGet(pos);
    }

    @Override
    protected char doSet(int index, char elem) {
        int pos = getCharBlockIndex(index, true, 0);
        char oldElem = currNode.block.doGet(pos);
        currNode.block.doSet(pos, elem);
        return oldElem;
    }

    @Override
    protected char doReSet(int index, char elem) {
        int pos = getCharBlockIndex(index, true, 0);
        char oldElem = currNode.block.doGet(pos);
        currNode.block.doSet(pos, elem);
        return oldElem;
    }


    private void releaseCharBlock() {
        if (currModify != 0) {
            int modify = currModify;
            currModify = 0;
            modify(currNode, modify);
        }
        currNode = null;
    }


    private int getCharBlockIndex(int index, boolean write, int modify) {

        if (currNode != null) {
            if (index >= currCharBlockStart && (index < currCharBlockEnd || index == currCharBlockEnd && size == index)) {

                if (write) {
                    if (currNode.block.isShared()) {
                        currNode.block.unref();
                        currNode.setCharBlock(new CharBlock(currNode.block));
                    }
                }
                currModify += modify;
                return index - currCharBlockStart;
            }
            releaseCharBlock();
        }
        if (index == size) {
            if (currNode == null || currCharBlockEnd != size) {
                currNode = rootNode.max();
                currCharBlockEnd = size;
                currCharBlockStart = size - currNode.block.size();
            }
            if (modify != 0) {
                currNode.relPos += modify;
                CharBlockNode leftNode = currNode.getLeftSubTree();
                if (leftNode != null) {
                    leftNode.relPos -= modify;
                }
            }
        } else if (index == 0) {
            if (currNode == null || currCharBlockStart != 0) {
                currNode = rootNode.min();
                currCharBlockEnd = currNode.block.size();
                currCharBlockStart = 0;
            }
            if (modify != 0) {
                rootNode.relPos += modify;
            }
        }
        if (currNode == null) {
            doGetCharBlock(index, modify);
        }
        assert (index >= currCharBlockStart && index <= currCharBlockEnd);
        if (write) {
            if (currNode.block.isShared()) {
                currNode.block.unref();
                currNode.setCharBlock(new CharBlock(currNode.block));
            }
        }
        return index - currCharBlockStart;
    }


    private boolean isOnlyRootCharBlock() {
        return rootNode.left == null && rootNode.right == null;
    }


    private void doGetCharBlock(int index, int modify) {
        currNode = rootNode;
        currCharBlockEnd = rootNode.relPos;
        if (currNode.relPos == 0) {

            if (modify != 0) {
                currNode.relPos += modify;
            }
        } else {

            boolean wasLeft = false;
            while (true) {
                assert (index >= 0);
                int leftIndex = currCharBlockEnd - currNode.block.size();
                assert (leftIndex >= 0);
                if (index >= leftIndex && index < currCharBlockEnd) {

                    if (modify != 0) {
                        CharBlockNode leftNode = currNode.getLeftSubTree();
                        if (currNode.relPos > 0) {
                            currNode.relPos += modify;
                            if (leftNode != null) {
                                leftNode.relPos -= modify;
                            }
                        } else {
                            if (leftNode != null) {
                                leftNode.relPos -= modify;
                            }
                        }
                    }
                    break;
                }

                CharBlockNode nextNode;
                if (index < currCharBlockEnd) {

                    nextNode = currNode.getLeftSubTree();
                    if (modify != 0) {
                        if (nextNode == null || !wasLeft) {
                            if (currNode.relPos > 0) {
                                currNode.relPos += modify;
                            } else {
                                currNode.relPos -= modify;
                            }
                            wasLeft = true;
                        }
                    }
                    if (nextNode == null) {
                        break;
                    }
                } else {

                    nextNode = currNode.getRightSubTree();
                    if (modify != 0) {
                        if (nextNode == null || wasLeft) {
                            if (currNode.relPos > 0) {
                                currNode.relPos += modify;
                                CharBlockNode left = currNode.getLeftSubTree();
                                if (left != null) {
                                    left.relPos -= modify;
                                }
                            } else {
                                currNode.relPos -= modify;
                            }
                            wasLeft = false;
                        }
                    }
                    if (nextNode == null) {
                        break;
                    }
                }
                currCharBlockEnd += nextNode.relPos;
                currNode = nextNode;
            }
        }
        currCharBlockStart = currCharBlockEnd - currNode.block.size();
    }


    private void addCharBlock(int index, CharBlock obj) {
        if (rootNode == null) {
            rootNode = new CharBlockNode(null, index, obj, null, null);
        } else {
            rootNode = rootNode.insert(index, obj);
            rootNode.parent = null;
        }
    }

    @Override
    protected boolean doAdd(int index, char element) {
        if (index == -1) {
            index = size;
        }

        int pos = getCharBlockIndex(index, true, 1);

        int maxSize = (index == size || index == 0) ? (int) (blockSize * FILL_THRESHOLD) : blockSize;


        if (currNode.block.size() < maxSize || (currNode.block.size() == 1 && currNode.block.size() < blockSize)) {
            currNode.block.doAdd(pos, element);
            currCharBlockEnd++;
        } else {

            CharBlock newCharBlock = new CharBlock(blockSize);
            if (index == size) {

                newCharBlock.doAdd(0, element);

                modify(currNode, -1);
                addCharBlock(size + 1, newCharBlock);
                CharBlockNode lastNode = currNode.next();
                currNode = lastNode;
                currCharBlockStart = currCharBlockEnd;
                currCharBlockEnd++;
            } else if (index == 0) {

                newCharBlock.doAdd(0, element);

                modify(currNode, -1);
                addCharBlock(1, newCharBlock);
                CharBlockNode firstNode = currNode.previous();
                currNode = firstNode;
                currCharBlockStart = 0;
                currCharBlockEnd = 1;
            } else {

                int nextCharBlockLen = blockSize / 2;
                int blockLen = blockSize - nextCharBlockLen;
                CharGapList.transferRemove(currNode.block, blockLen, nextCharBlockLen, newCharBlock, 0, 0);

                modify(currNode, -nextCharBlockLen - 1);
                addCharBlock(currCharBlockEnd - nextCharBlockLen, newCharBlock);
                if (pos < blockLen) {

                    currNode.block.doAdd(pos, element);
                    currCharBlockEnd = currCharBlockStart + blockLen + 1;
                    modify(currNode, 1);
                } else {

                    currNode = currNode.next();
                    modify(currNode, 1);
                    currNode.block.doAdd(pos - blockLen, element);
                    currCharBlockStart += blockLen;
                    currCharBlockEnd++;
                }
            }
        }
        size++;
        if (CHECK)
            check();
        return true;
    }


    private void modify(CharBlockNode node, int modify) {
        if (node == currNode) {
            modify += currModify;
            currModify = 0;
        } else {
            releaseCharBlock();
        }
        if (modify == 0) {
            return;
        }
        if (node.relPos < 0) {

            CharBlockNode leftNode = node.getLeftSubTree();
            if (leftNode != null) {
                leftNode.relPos -= modify;
            }
            CharBlockNode pp = node.parent;
            assert (pp.getLeftSubTree() == node);
            boolean parentRight = true;
            while (true) {
                CharBlockNode p = pp.parent;
                if (p == null) {
                    break;
                }
                boolean pRight = (p.getLeftSubTree() == pp);
                if (parentRight != pRight) {
                    if (pp.relPos > 0) {
                        pp.relPos += modify;
                    } else {
                        pp.relPos -= modify;
                    }
                }
                pp = p;
                parentRight = pRight;
            }
            if (parentRight) {
                rootNode.relPos += modify;
            }
        } else {

            node.relPos += modify;
            CharBlockNode leftNode = node.getLeftSubTree();
            if (leftNode != null) {
                leftNode.relPos -= modify;
            }
            CharBlockNode parent = node.parent;
            if (parent != null) {
                assert (parent.getRightSubTree() == node);
                boolean parentLeft = true;
                while (true) {
                    CharBlockNode p = parent.parent;
                    if (p == null) {
                        break;
                    }
                    boolean pLeft = (p.getRightSubTree() == parent);
                    if (parentLeft != pLeft) {
                        if (parent.relPos > 0) {
                            parent.relPos += modify;
                        } else {
                            parent.relPos -= modify;
                        }
                    }
                    parent = p;
                    parentLeft = pLeft;
                }
                if (!parentLeft) {
                    rootNode.relPos += modify;
                }
            }
        }
    }

    private CharBlockNode doRemove(CharBlockNode node) {
        CharBlockNode p = node.parent;
        CharBlockNode newNode = node.removeSelf();
        CharBlockNode n = newNode;
        while (p != null) {
            assert (p.left == node || p.right == node);
            if (p.left == node) {
                p.left = newNode;
            } else {
                p.right = newNode;
            }
            node = p;
            node.recalcHeight();
            newNode = node.balance();
            p = newNode.parent;
        }
        rootNode = newNode;
        return n;
    }

    @Override
    protected boolean doAddAll(int index, ICharList list) {
        if (list.size() == 0) {
            return false;
        }
        if (index == -1) {
            index = size;
        }
        if (CHECK)
            check();
        int oldSize = size;
        if (list.size() == 1) {
            return doAdd(index, list.get(0));
        }
        int addPos = getCharBlockIndex(index, true, 0);
        CharBlock addCharBlock = currNode.block;
        int space = blockSize - addCharBlock.size();
        int addLen = list.size();
        if (addLen <= space) {

            currNode.block.addAll(addPos, list);
            modify(currNode, addLen);
            size += addLen;
            currCharBlockEnd += addLen;
        } else {
            if (index == size) {

                for (int i = 0; i < space; i++) {
                    currNode.block.add(addPos + i, list.get(i));
                }
                modify(currNode, space);
                int done = space;
                int todo = addLen - space;
                while (todo > 0) {
                    CharBlock nextCharBlock = new CharBlock(blockSize);
                    int add = Math.min(todo, blockSize);
                    for (int i = 0; i < add; i++) {
                        nextCharBlock.add(i, list.get(done + i));
                    }
                    done += add;
                    todo -= add;
                    addCharBlock(size + done, nextCharBlock);
                    currNode = currNode.next();
                }
                size += addLen;
                currCharBlockEnd = size;
                currCharBlockStart = currCharBlockEnd - currNode.block.size();
            } else if (index == 0) {

                assert (addPos == 0);
                for (int i = 0; i < space; i++) {
                    currNode.block.add(addPos + i, list.get(addLen - space + i));
                }
                modify(currNode, space);
                int done = space;
                int todo = addLen - space;
                while (todo > 0) {
                    CharBlock nextCharBlock = new CharBlock(blockSize);
                    int add = Math.min(todo, blockSize);
                    for (int i = 0; i < add; i++) {
                        nextCharBlock.add(i, list.get(addLen - done - add + i));
                    }
                    done += add;
                    todo -= add;
                    addCharBlock(0, nextCharBlock);
                    currNode = currNode.previous();
                }
                size += addLen;
                currCharBlockStart = 0;
                currCharBlockEnd = currNode.block.size();
            } else {


                CharGapList list2 = CharGapList.create();
                list2.addAll(list);
                int remove = currNode.block.size() - addPos;
                if (remove > 0) {
                    list2.addAll(currNode.block.getAll(addPos, remove));
                    currNode.block.remove(addPos, remove);
                    modify(currNode, -remove);
                    size -= remove;
                    currCharBlockEnd -= remove;
                }

                int numElems = currNode.block.size() + list2.size();
                int numCharBlocks = (numElems - 1) / blockSize + 1;
                assert (numCharBlocks > 1);
                int has = currNode.block.size();
                int should = numElems / numCharBlocks;
                int listPos = 0;
                if (has < should) {

                    int add = should - has;
                    ICharList sublist = list2.getAll(0, add);
                    listPos += add;
                    currNode.block.addAll(addPos, sublist);
                    modify(currNode, add);
                    assert (currNode.block.size() == should);
                    numElems -= should;
                    numCharBlocks--;
                    size += add;
                    currCharBlockEnd += add;
                } else if (has > should) {

                    CharBlock nextCharBlock = new CharBlock(blockSize);
                    int move = has - should;
                    nextCharBlock.addAll(currNode.block.getAll(currNode.block.size() - move, move));
                    currNode.block.remove(currNode.block.size() - move, move);
                    modify(currNode, -move);
                    assert (currNode.block.size() == should);
                    numElems -= should;
                    numCharBlocks--;
                    currCharBlockEnd -= move;
                    should = numElems / numCharBlocks;
                    int add = should - move;
                    assert (add >= 0);
                    ICharList sublist = list2.getAll(0, add);
                    nextCharBlock.addAll(move, sublist);
                    listPos += add;
                    assert (nextCharBlock.size() == should);
                    numElems -= should;
                    numCharBlocks--;
                    size += add;
                    addCharBlock(currCharBlockEnd, nextCharBlock);
                    currNode = currNode.next();
                    assert (currNode.block == nextCharBlock);
                    assert (currNode.block.size() == add + move);
                    currCharBlockStart = currCharBlockEnd;
                    currCharBlockEnd += add + move;
                } else {

                    numElems -= should;
                    numCharBlocks--;
                }
                if (CHECK)
                    check();
                while (numCharBlocks > 0) {
                    int add = numElems / numCharBlocks;
                    assert (add > 0);
                    ICharList sublist = list2.getAll(listPos, add);
                    listPos += add;
                    CharBlock nextCharBlock = new CharBlock();
                    nextCharBlock.addAll(sublist);
                    assert (nextCharBlock.size() == add);
                    numElems -= add;
                    addCharBlock(currCharBlockEnd, nextCharBlock);
                    currNode = currNode.next();
                    assert (currNode.block == nextCharBlock);
                    assert (currNode.block.size() == add);
                    currCharBlockStart = currCharBlockEnd;
                    currCharBlockEnd += add;
                    size += add;
                    numCharBlocks--;
                    if (CHECK)
                        check();
                }
            }
        }
        assert (oldSize + addLen == size);
        if (CHECK)
            check();
        return true;
    }

    @Override
    protected void doClear() {
        finalize();
        rootNode = null;
        currCharBlockStart = 0;
        currCharBlockEnd = 0;
        currModify = 0;
        currNode = null;
        size = 0;
        doInit(blockSize, 0);
    }

    @Override
    protected void doRemoveAll(int index, int len) {

        if (len == 0) {
            return;
        }
        if (index == 0 && len == size) {
            doClear();
            return;
        }
        if (len == 1) {
            doRemove(index);
            return;
        }

        int startPos = getCharBlockIndex(index, true, 0);
        CharBlockNode startNode = currNode;

        int endPos = getCharBlockIndex(index + len - 1, true, 0);
        CharBlockNode endNode = currNode;
        if (startNode == endNode) {

            getCharBlockIndex(index, true, -len);
            currNode.block.remove(startPos, len);
            if (currNode.block.isEmpty()) {
                CharBlockNode oldCurrNode = currNode;
                releaseCharBlock();
                CharBlockNode node = doRemove(oldCurrNode);
                merge(node);
            } else {
                currCharBlockEnd -= len;
                merge(currNode);
            }
            size -= len;
        } else {

            if (CHECK)
                check();
            int startLen = startNode.block.size() - startPos;
            getCharBlockIndex(index, true, -startLen);
            startNode.block.remove(startPos, startLen);
            assert (startNode == currNode);
            if (currNode.block.isEmpty()) {
                releaseCharBlock();
                doRemove(startNode);
                startNode = null;
            }
            len -= startLen;
            size -= startLen;
            while (len > 0) {
                currNode = null;
                getCharBlockIndex(index, true, 0);
                int s = currNode.block.size();
                if (s <= len) {
                    modify(currNode, -s);
                    CharBlockNode oldCurrNode = currNode;
                    releaseCharBlock();
                    doRemove(oldCurrNode);
                    if (oldCurrNode == endNode) {
                        endNode = null;
                    }
                    len -= s;
                    size -= s;
                    if (CHECK)
                        check();
                } else {
                    modify(currNode, -len);
                    currNode.block.remove(0, len);
                    size -= len;
                    break;
                }
            }
            releaseCharBlock();
            if (CHECK)
                check();
            getCharBlockIndex(index, false, 0);
            merge(currNode);
        }
        if (CHECK)
            check();
    }


    private void merge(CharBlockNode node) {
        if (node == null) {
            return;
        }
        final int minCharBlockSize = Math.max((int) (blockSize * MERGE_THRESHOLD), 1);
        if (node.block.size() >= minCharBlockSize) {
            return;
        }
        CharBlockNode oldCurrNode = node;
        CharBlockNode leftNode = node.previous();
        if (leftNode != null && leftNode.block.size() < minCharBlockSize) {

            int len = node.block.size();
            int dstSize = leftNode.getCharBlock().size();
            for (int i = 0; i < len; i++) {
                leftNode.block.add((char) 0);
            }
            CharGapList.transferCopy(node.block, 0, len, leftNode.block, dstSize, len);
            assert (leftNode.block.size() <= blockSize);
            modify(leftNode, +len);
            modify(oldCurrNode, -len);
            releaseCharBlock();
            doRemove(oldCurrNode);
        } else {
            CharBlockNode rightNode = node.next();
            if (rightNode != null && rightNode.block.size() < minCharBlockSize) {

                int len = node.block.size();
                for (int i = 0; i < len; i++) {
                    rightNode.block.add(0, (char) 0);
                }
                CharGapList.transferCopy(node.block, 0, len, rightNode.block, 0, len);
                assert (rightNode.block.size() <= blockSize);
                modify(rightNode, +len);
                modify(oldCurrNode, -len);
                releaseCharBlock();
                doRemove(oldCurrNode);
            }
        }
    }

    @Override
    protected char doRemove(int index) {
        int pos = getCharBlockIndex(index, true, -1);
        char oldElem = currNode.block.doRemove(pos);
        currCharBlockEnd--;
        final int minCharBlockSize = Math.max(blockSize / 3, 1);
        if (currNode.block.size() < minCharBlockSize) {
            if (currNode.block.size() == 0) {
                if (!isOnlyRootCharBlock()) {
                    CharBlockNode oldCurrNode = currNode;
                    releaseCharBlock();
                    doRemove(oldCurrNode);
                }
            } else if (index != 0 && index != size - 1) {


                merge(currNode);
            }
        }
        size--;
        if (CHECK)
            check();
        return oldElem;
    }

    @Override
    public CharBigList unmodifiableList() {

        if (this instanceof ImmutableCharBigList) {
            return this;
        } else {
            return new ImmutableCharBigList(this);
        }
    }

    @Override
    protected void doEnsureCapacity(int minCapacity) {
        if (isOnlyRootCharBlock()) {
            if (minCapacity > blockSize) {
                minCapacity = blockSize;
            }
            rootNode.block.doEnsureCapacity(minCapacity);
        }
    }


    @Override
    public void trimToSize() {
        doModify();
        if (isOnlyRootCharBlock()) {
            rootNode.block.trimToSize();
        } else {
            CharBigList newList = new CharBigList(blockSize);
            CharBlockNode node = rootNode.min();
            while (node != null) {
                newList.addAll(node.block);
                remove(0, node.block.size());
                node = node.next();
            }
            doAssign(newList);
        }
    }

    @Override
    protected ICharList doCreate(int capacity) {
        if (capacity <= blockSize) {
            return new CharBigList(this.blockSize);
        } else {
            return new CharBigList(this.blockSize, capacity);
        }
    }

    @Override
    public void sort(int index, int len) {
        checkRange(index, len);
        if (isOnlyRootCharBlock()) {
            rootNode.block.sort(index, len);
        } else {
            CharMergeSort.sort(this, index, index + len);
        }
    }

    @Override
    public int binarySearch(int index, int len, char key) {
        checkRange(index, len);
        if (isOnlyRootCharBlock()) {
            return rootNode.block.binarySearch(key);
        } else {
            return CharBinarySearch.binarySearch(this, key, 0, size());
        }
    }


    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(blockSize);
        int size = size();
        oos.writeInt(size);
        for (int i = 0; i < size; i++) {
            oos.writeChar(doGet(i));
        }
    }


    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        int blockSize = ois.readInt();
        int size = ois.readInt();
        int firstCharBlockSize = (size <= blockSize) ? size : -1;
        doInit(blockSize, firstCharBlockSize);
        for (int i = 0; i < size; i++) {
            add(ois.readChar());
        }
    }

    private void checkNode(CharBlockNode node) {
        assert ((node.block.size() > 0 || node == rootNode) && node.block.size() <= blockSize);
        CharBlockNode child = node.getLeftSubTree();
        assert (child == null || child.parent == node);
        child = node.getRightSubTree();
        assert (child == null || child.parent == node);
    }


    private void checkHeight(CharBlockNode node) {
        CharBlockNode left = node.getLeftSubTree();
        CharBlockNode right = node.getRightSubTree();
        if (left == null) {
            if (right == null) {
                assert (node.height == 0);
            } else {
                assert (right.height == node.height - 1);
                checkHeight(right);
            }
        } else {
            if (right == null) {
                assert (left.height == node.height - 1);
            } else {
                assert (left.height == node.height - 1 || left.height == node.height - 2);
                assert (right.height == node.height - 1 || right.height == node.height - 2);
                assert (right.height == node.height - 1 || left.height == node.height - 1);
            }
            checkHeight(left);
        }
    }


    private void check() {
        if (currNode != null) {
            assert (currCharBlockStart >= 0 && currCharBlockEnd <= size && currCharBlockStart <= currCharBlockEnd);
            assert (currCharBlockStart + currNode.block.size() == currCharBlockEnd);
        }
        if (rootNode == null) {
            assert (size == 0);
            return;
        }
        checkHeight(rootNode);
        CharBlockNode oldCurrNode = currNode;
        int oldCurrModify = currModify;
        if (currModify != 0) {
            currNode = null;
            currModify = 0;
            modify(oldCurrNode, oldCurrModify);
        }
        CharBlockNode node = rootNode;
        checkNode(node);
        int index = node.relPos;
        while (node.left != null) {
            node = node.left;
            checkNode(node);
            assert (node.relPos < 0);
            index += node.relPos;
        }
        CharBlock block = node.getCharBlock();
        assert (block.size() == index);
        int lastIndex = index;
        while (lastIndex < size()) {
            node = rootNode;
            index = node.relPos;
            int searchIndex = lastIndex + 1;
            while (true) {
                checkNode(node);
                block = node.getCharBlock();
                assert (block.size() > 0);
                if (searchIndex > index - block.size() && searchIndex <= index) {
                    break;
                } else if (searchIndex < index) {
                    if (node.left != null && node.left.height < node.height) {
                        node = node.left;
                    } else {
                        break;
                    }
                } else {
                    if (node.right != null && node.right.height < node.height) {
                        node = node.right;
                    } else {
                        break;
                    }
                }
                index += node.relPos;
            }
            block = node.getCharBlock();
            assert (block.size() == index - lastIndex);
            lastIndex = index;
        }
        assert (index == size());
        if (oldCurrModify != 0) {
            modify(oldCurrNode, -oldCurrModify);
        }
        currNode = oldCurrNode;
        currModify = oldCurrModify;
    }


    public void init(String str) {
        char[] array = str.toCharArray();
        initArray(array);
    }


    static class CharBlock extends CharGapList {

        private final AtomicInteger refCount = new AtomicInteger(1);

        public CharBlock() {
        }

        public CharBlock(int capacity) {
            super(capacity);
        }

        public CharBlock(CharBlock that) {
            super(that.capacity());
            addAll(that);
        }


        public boolean isShared() {
            return refCount.get() > 1;
        }


        public CharBlock ref() {
            refCount.incrementAndGet();
            return this;
        }


        public void unref() {
            refCount.decrementAndGet();
        }
    }


    static class CharBlockNode {


        CharBlockNode parent;


        CharBlockNode left;


        boolean leftIsPrevious;


        CharBlockNode right;


        boolean rightIsNext;


        int height;


        int relPos;


        CharBlock block;


        private CharBlockNode(CharBlockNode parent, int relPos, CharBlock block, CharBlockNode rightFollower, CharBlockNode leftFollower) {
            this.parent = parent;
            this.relPos = relPos;
            this.block = block;
            rightIsNext = true;
            leftIsPrevious = true;
            right = rightFollower;
            left = leftFollower;
        }


        private CharBlock getCharBlock() {
            return block;
        }


        private void setCharBlock(CharBlock block) {
            this.block = block;
        }


        private CharBlockNode next() {
            if (rightIsNext || right == null) {
                return right;
            }
            return right.min();
        }


        private CharBlockNode previous() {
            if (leftIsPrevious || left == null) {
                return left;
            }
            return left.max();
        }


        private CharBlockNode insert(int index, CharBlock obj) {
            assert (relPos != 0);
            int relIndex = index - relPos;
            if (relIndex < 0) {
                return insertOnLeft(relIndex, obj);
            } else {
                return insertOnRight(relIndex, obj);
            }
        }


        private CharBlockNode insertOnLeft(int relIndex, CharBlock obj) {
            if (getLeftSubTree() == null) {
                int pos;
                if (relPos >= 0) {
                    pos = -relPos;
                } else {
                    pos = -block.size();
                }
                setLeft(new CharBlockNode(this, pos, obj, this, left), null);
            } else {
                setLeft(left.insert(relIndex, obj), null);
            }
            if (relPos >= 0) {
                relPos += obj.size();
            }
            CharBlockNode ret = balance();
            recalcHeight();
            return ret;
        }


        private CharBlockNode insertOnRight(int relIndex, CharBlock obj) {
            if (getRightSubTree() == null) {
                setRight(new CharBlockNode(this, obj.size(), obj, right, this), null);
            } else {
                setRight(right.insert(relIndex, obj), null);
            }
            if (relPos < 0) {
                relPos -= obj.size();
            }
            CharBlockNode ret = balance();
            recalcHeight();
            return ret;
        }


        private CharBlockNode getLeftSubTree() {
            return leftIsPrevious ? null : left;
        }


        private CharBlockNode getRightSubTree() {
            return rightIsNext ? null : right;
        }


        private CharBlockNode max() {
            return getRightSubTree() == null ? this : right.max();
        }


        private CharBlockNode min() {
            return getLeftSubTree() == null ? this : left.min();
        }

        private CharBlockNode removeMax() {
            if (getRightSubTree() == null) {
                return removeSelf();
            }
            setRight(right.removeMax(), right.right);
            recalcHeight();
            return balance();
        }

        private CharBlockNode removeMin(int size) {
            if (getLeftSubTree() == null) {
                return removeSelf();
            }
            setLeft(left.removeMin(size), left.left);
            if (relPos > 0) {
                relPos -= size;
            }
            recalcHeight();
            return balance();
        }


        private CharBlockNode removeSelf() {
            CharBlockNode p = parent;
            CharBlockNode n = doRemoveSelf();
            if (n != null) {
                assert (p != n);
                n.parent = p;
            }
            return n;
        }

        private CharBlockNode doRemoveSelf() {
            if (getRightSubTree() == null && getLeftSubTree() == null) {
                return null;
            }
            if (getRightSubTree() == null) {
                if (relPos > 0) {
                    left.relPos += relPos + (relPos > 0 ? 0 : 1);
                } else {
                    left.relPos += relPos;
                }
                left.max().setRight(null, right);
                return left;
            }
            if (getLeftSubTree() == null) {
                if (relPos < 0) {
                    right.relPos += relPos - (relPos < 0 ? 0 : 1);
                }
                right.min().setLeft(null, left);
                return right;
            }
            if (heightRightMinusLeft() > 0) {

                final CharBlockNode rightMin = right.min();
                block = rightMin.block;
                int bs = block.size();
                if (leftIsPrevious) {
                    left = rightMin.left;
                }
                right = right.removeMin(bs);
                relPos += bs;
                left.relPos -= bs;
            } else {

                final CharBlockNode leftMax = left.max();
                block = leftMax.block;
                if (rightIsNext) {
                    right = leftMax.right;
                }
                final CharBlockNode leftPrevious = left.left;
                left = left.removeMax();
                if (left == null) {


                    left = leftPrevious;
                    leftIsPrevious = true;
                } else {
                    if (left.relPos == 0) {
                        left.relPos = -1;
                    }
                }
            }
            recalcHeight();
            return this;
        }


        private CharBlockNode balance() {
            switch (heightRightMinusLeft()) {
                case 1:
                case 0:
                case -1:
                    return this;
                case -2:
                    if (left.heightRightMinusLeft() > 0) {
                        setLeft(left.rotateLeft(), null);
                    }
                    return rotateRight();
                case 2:
                    if (right.heightRightMinusLeft() < 0) {
                        setRight(right.rotateRight(), null);
                    }
                    return rotateLeft();
                default:
                    throw new RuntimeException("tree inconsistent!");
            }
        }


        private int getOffset(CharBlockNode node) {
            if (node == null) {
                return 0;
            }
            return node.relPos;
        }


        private int setOffset(CharBlockNode node, int newOffest) {
            if (node == null) {
                return 0;
            }
            final int oldOffset = getOffset(node);
            node.relPos = newOffest;
            return oldOffset;
        }


        private void recalcHeight() {
            height = Math.max(getLeftSubTree() == null ? -1 : getLeftSubTree().height, getRightSubTree() == null ? -1 : getRightSubTree().height) + 1;
        }


        private int getHeight(final CharBlockNode node) {
            return node == null ? -1 : node.height;
        }


        private int heightRightMinusLeft() {
            return getHeight(getRightSubTree()) - getHeight(getLeftSubTree());
        }


        private CharBlockNode rotateLeft() {
            assert (!rightIsNext);

            final CharBlockNode newTop = right;
            final CharBlockNode movedNode = getRightSubTree().getLeftSubTree();
            final int newTopPosition = relPos + getOffset(newTop);
            final int myNewPosition = -newTop.relPos;
            final int movedPosition = getOffset(newTop) + getOffset(movedNode);
            CharBlockNode p = this.parent;
            setRight(movedNode, newTop);
            newTop.setLeft(this, null);
            newTop.parent = p;
            this.parent = newTop;
            setOffset(newTop, newTopPosition);
            setOffset(this, myNewPosition);
            setOffset(movedNode, movedPosition);
            assert (newTop.getLeftSubTree() == null || newTop.getLeftSubTree().relPos < 0);
            assert (newTop.getRightSubTree() == null || newTop.getRightSubTree().relPos > 0);
            return newTop;
        }


        private CharBlockNode rotateRight() {
            assert (!leftIsPrevious);

            final CharBlockNode newTop = left;
            final CharBlockNode movedNode = getLeftSubTree().getRightSubTree();
            final int newTopPosition = relPos + getOffset(newTop);
            final int myNewPosition = -newTop.relPos;
            final int movedPosition = getOffset(newTop) + getOffset(movedNode);
            CharBlockNode p = this.parent;
            setLeft(movedNode, newTop);
            newTop.setRight(this, null);
            newTop.parent = p;
            this.parent = newTop;
            setOffset(newTop, newTopPosition);
            setOffset(this, myNewPosition);
            setOffset(movedNode, movedPosition);
            assert (newTop.getLeftSubTree() == null || newTop.getLeftSubTree().relPos < 0);
            assert (newTop.getRightSubTree() == null || newTop.getRightSubTree().relPos > 0);
            return newTop;
        }


        private void setLeft(CharBlockNode node, CharBlockNode previous) {
            assert (node != this && previous != this);
            leftIsPrevious = node == null;
            if (leftIsPrevious) {
                left = previous;
            } else {
                left = node;
                left.parent = this;
            }
            recalcHeight();
        }


        private void setRight(CharBlockNode node, CharBlockNode next) {
            assert (node != this && next != this);
            rightIsNext = node == null;
            if (rightIsNext) {
                right = next;
            } else {
                right = node;
                right.parent = this;
            }
            recalcHeight();
        }


        @Override
        public String toString() {
            return new StringBuilder().append("CharBlockNode(").append(relPos).append(',').append(getRightSubTree() != null).append(',').append(block).append(',').append(getRightSubTree() != null).append(", height ").append(height).append(" )").toString();
        }
    }


    protected static class ImmutableCharBigList extends CharBigList {


        private static final long serialVersionUID = -1352274047348922584L;


        protected ImmutableCharBigList(CharBigList that) {
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
