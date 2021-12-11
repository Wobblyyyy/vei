package org.magicwerk.brownies.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;


@SuppressWarnings("serial")
public class KeyCollection<E> extends KeyCollectionImpl<E> {


    protected KeyCollection() {
    }


    protected Builder<E> getBuilder() {
        return new Builder<>(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public KeyCollection<E> copy() {
        return (KeyCollection<E>) super.copy();
    }

    @SuppressWarnings("unchecked")
    @Override
    public KeyCollection<E> crop() {
        return (KeyCollection<E>) super.crop();
    }

    @Override
    public KeyCollection<E> getAll(E elem) {
        return (KeyCollection<E>) super.getAll(elem);
    }


    @Override
    public int getCount(E elem) {
        return super.getCount(elem);
    }

    @Override
    public KeyCollection<E> removeAll(E elem) {
        return (KeyCollection<E>) super.removeAll(elem);
    }

    @Override
    public Set<E> getDistinct() {
        return super.getDistinct();
    }

    @Override
    public E put(E elem) {
        return super.put(elem);
    }

    @Override
    public void invalidate(E elem) {
        super.invalidate(elem);
    }


    public static class Builder<E> extends BuilderImpl<E> {

        public Builder() {
            this(null);
        }


        Builder(KeyCollection<E> keyColl) {
            this.keyColl = keyColl;
            initKeyMapBuilder(0);
        }


        public KeyCollection<E> build() {
            if (keyColl == null) {
                keyColl = new KeyCollection<>();
            }
            build(keyColl, false);
            init(keyColl);
            return (KeyCollection<E>) keyColl;
        }


        @Override
        public Builder<E> withNull(boolean allowNull) {
            return (Builder<E>) super.withNull(allowNull);
        }

        @Override
        public Builder<E> withConstraint(Predicate<E> constraint) {
            return (Builder<E>) super.withConstraint(constraint);
        }


        @Override
        public Builder<E> withBeforeInsertTrigger(Consumer<E> trigger) {
            return (Builder<E>) super.withBeforeInsertTrigger(trigger);
        }

        @Override
        public Builder<E> withAfterInsertTrigger(Consumer<E> trigger) {
            return (Builder<E>) super.withAfterInsertTrigger(trigger);
        }

        @Override
        public Builder<E> withBeforeDeleteTrigger(Consumer<E> trigger) {
            return (Builder<E>) super.withBeforeDeleteTrigger(trigger);
        }

        @Override
        public Builder<E> withAfterDeleteTrigger(Consumer<E> trigger) {
            return (Builder<E>) super.withAfterDeleteTrigger(trigger);
        }


        @Override
        public Builder<E> withCapacity(int capacity) {
            return (Builder<E>) super.withCapacity(capacity);
        }

        @Override
        public Builder<E> withContent(Collection<? extends E> elements) {
            return (Builder<E>) super.withContent(elements);
        }

        @Override
        public Builder<E> withContent(E... elements) {
            return (Builder<E>) super.withContent(elements);
        }

        @Override
        public Builder<E> withMaxSize(int maxSize) {
            return (Builder<E>) super.withMaxSize(maxSize);
        }


        @Override
        public Builder<E> withSetBehavior(boolean setBehavior) {
            return (Builder<E>) super.withSetBehavior(setBehavior);
        }

        @Override
        public Builder<E> withElemCount(boolean count) {
            return (Builder<E>) super.withElemCount(count);
        }


        @Override
        public Builder<E> withElemSet() {
            return (Builder<E>) super.withElemSet();
        }

        @Override
        public Builder<E> withOrderByElem(boolean orderBy) {
            return (Builder<E>) super.withOrderByElem(orderBy);
        }

        @Override
        public Builder<E> withElemNull(boolean allowNull) {
            return (Builder<E>) super.withElemNull(allowNull);
        }

        @Override
        public Builder<E> withElemDuplicates(boolean allowDuplicates) {
            return (Builder<E>) super.withElemDuplicates(allowDuplicates);
        }

        @Override
        public Builder<E> withElemDuplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            return (Builder<E>) super.withElemDuplicates(allowDuplicates, allowDuplicatesNull);
        }

        @Override
        public Builder<E> withElemSort(boolean sort) {
            return (Builder<E>) super.withElemSort(sort);
        }

        @Override
        public Builder<E> withElemSort(Comparator<? super E> comparator) {
            return (Builder<E>) super.withElemSort(comparator);
        }

        @Override
        public Builder<E> withElemSort(Comparator<? super E> comparator, boolean sortNullsFirst) {
            return (Builder<E>) super.withElemSort(comparator, sortNullsFirst);
        }

        @Override
        public Builder<E> withPrimaryElem() {
            return (Builder<E>) super.withPrimaryElem();
        }

        @Override
        public Builder<E> withUniqueElem() {
            return (Builder<E>) super.withUniqueElem();
        }

    }

}
