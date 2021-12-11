package org.magicwerk.brownies.collections;

public class Option<T> {
    @SuppressWarnings("rawtypes")
    private static final Option EMPTY = new Option();
    private boolean hasValue;
    private T value;

    private Option() {
    }


    public Option(T value) {
        this.hasValue = true;
        this.value = value;
    }


    @SuppressWarnings("unchecked")
    public static <EE> Option<EE> EMPTY() {
        return EMPTY;
    }


    public boolean hasValue() {
        return hasValue;
    }


    public T getValueOrNull() {
        return value;
    }


    public T getValue() {
        if (!hasValue) {
            throw new IllegalArgumentException("No value stored");
        }
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        Option other = (Option) obj;
        if (hasValue != other.hasValue)
            return false;
        if (value == null) {
            return other.value == null;
        } else return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (hasValue ? 1231 : 1237);
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Option [hasValue=" + hasValue + ", value=" + value + "]";
    }

}
