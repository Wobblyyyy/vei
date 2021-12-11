package org.magicwerk.brownies.collections.exceptions;


@SuppressWarnings("serial")
public class KeyException extends RuntimeException {

    public KeyException() {
    }

    public KeyException(String msg) {
        super(msg);
    }

    public KeyException(Throwable t) {
        super(t);
    }

    public KeyException(String msg, Throwable t) {
        super(msg, t);
    }

}
