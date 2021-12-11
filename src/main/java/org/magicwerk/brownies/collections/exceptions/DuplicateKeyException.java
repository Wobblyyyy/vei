package org.magicwerk.brownies.collections.exceptions;


@SuppressWarnings("serial")
public class DuplicateKeyException extends KeyException {

    public static final String MESSAGE = "Constraint violation: duplicate key not allowed";


    Object key;


    public DuplicateKeyException(Object key) {
        super(MESSAGE + ": " + key);

        this.key = key;
    }

    public Object getKey() {
        return key;
    }

}
