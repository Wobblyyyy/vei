package org.magicwerk.brownies.collections;

import org.magicwerk.brownies.collections.exceptions.DuplicateKeyException;

import java.io.Serializable;


@SuppressWarnings("serial")
public class KeyCollectionAsSet<E> extends CollectionAsSet<E> implements Serializable {

    public KeyCollectionAsSet(KeyCollectionImpl<E> coll, boolean immutable) {
        super(coll, immutable);

        coll.checkAsSet();
    }

    @Override
    public boolean add(E e) {
        checkMutable();
        try {
            return coll.add(e);
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }

}