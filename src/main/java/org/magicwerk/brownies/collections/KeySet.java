package org.magicwerk.brownies.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;


@SuppressWarnings("serial")
public class KeySet<E> extends KeyCollection<E> implements Set<E> {


    protected KeySet() {
    }


    @Override
    protected Builder<E> getBuilder() {
        return new Builder<>(this);
    }

    @Override
    public KeySet<E> copy() {
        return (KeySet<E>) super.copy();
    }

    @Override
    public KeySet<E> crop() {
        return (KeySet<E>) super.crop();
    }

    @Override
    public KeySet<E> getAll(E elem) {
        return (KeySet<E>) super.getAll(elem);
    }


    public static class Builder<E> extends KeyCollection.Builder<E> {

        public Builder() {
            this(null);
        }


        Builder(KeySet<E> keySet) {
            this.keyColl = keySet;
            initKeyMapBuilder(0);
        }


        @Override
        public KeySet<E> build() {
            withSetBehavior(true);
            withElemDuplicates(false);

            if (keyColl == null) {
                keyColl = new KeySet<>();
            }
            build(keyColl, false);
            init(keyColl);
            return (KeySet<E>) keyColl;
        }


        @Override
        public Builder<E> withSetBehavior(boolean setBehavior) {
            if (!setBehavior) {
                KeyCollectionImpl.errorInvalidSetBehavior();
            }
            return (Builder<E>) super.withSetBehavior(setBehavior);
        }

        @Override
        public Builder<E> withElemDuplicates(boolean allowDuplicates) {
            if (allowDuplicates) {
                KeyCollectionImpl.errorInvaliDuplicates();
            }
            return (Builder<E>) super.withElemDuplicates(allowDuplicates);
        }

        @Override
        public Builder<E> withElemDuplicates(boolean allowDuplicates, boolean allowDuplicatesNull) {
            if (allowDuplicates || allowDuplicatesNull) {
                KeyCollectionImpl.errorInvaliDuplicates();
            }
            return (Builder<E>) super.withElemDuplicates(allowDuplicates, allowDuplicatesNull);
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
