package org.magicwerk.brownies.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;


public class CollectionAsSet<K> implements Set<K> {
    Collection<K> coll;
    boolean immutable;

    public CollectionAsSet(Collection<K> coll, boolean immutable) {
        if (coll == null) {
            throw new IllegalArgumentException("Collection may not be null");
        }
        this.coll = coll;
        this.immutable = immutable;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Set)) {
            return false;
        }
        return coll.equals(obj);
    }

    @Override
    public int hashCode() {
        return coll.hashCode();
    }

    @Override
    public String toString() {
        return coll.toString();
    }


    @Override
    public int size() {
        return coll.size();
    }

    @Override
    public boolean isEmpty() {
        return coll.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return coll.contains(o);
    }

    @Override
    public Object[] toArray() {
        return coll.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return coll.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return coll.containsAll(c);
    }


    @Override
    public Iterator<K> iterator() {
        if (immutable) {
            return Collections.unmodifiableCollection(coll).iterator();
        } else {
            return coll.iterator();
        }
    }


    void checkMutable() {
        if (immutable) {
            throw new UnsupportedOperationException("Set is immutable");
        }
    }

    @Override
    public boolean add(K e) {
        checkMutable();
        if (coll.contains(e)) {
            return false;
        }
        return coll.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends K> c) {
        checkMutable();
        boolean changed = false;
        for (K e : c) {
            changed = add(e) || changed;
        }
        return changed;
    }

    @Override
    public void clear() {
        checkMutable();
        coll.clear();
    }

    @Override
    public boolean remove(Object o) {
        checkMutable();
        return coll.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkMutable();
        return coll.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        checkMutable();
        return coll.retainAll(c);
    }

}