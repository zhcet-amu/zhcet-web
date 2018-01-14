package amu.zhcet.common.utils;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DiffUtils<V> {

    public static class Builder<V> {
        private Class<V> clazz;
        private BiPredicate<V, V> sameItemPredicate;

        private Builder(Class<V> clazz) {
            this.clazz = clazz;
        }

        public Builder<V> areItemsSame(BiPredicate<V, V> sameItemPredicate) {
            this.sameItemPredicate = sameItemPredicate;
            return this;
        }

        public DiffUtils<V> build() {
            return new DiffUtils<>(this);
        }
    }

    public static class Calculator<V> {
        private final Builder<V> builder;
        private final Set<V> oldSet;
        private final Set<V> newSet;

        private Calculator(Builder<V> builder, Set<V> oldSet, Set<V> newSet) {
            this.builder = builder;
            this.oldSet = oldSet;
            this.newSet = newSet;
        }

        private boolean checkEdgesAndReturn(Set<V> setA, Set<V> setB, Consumer<V> consumer) {
            if (setA == null || setA.isEmpty()) {
                if (setB != null && !setB.isEmpty())
                    setB.forEach(consumer);

                return true;
            }

            return false;
        }

        public void calculate(Consumer<V> itemAdded, Consumer<V> itemDeleted, Consumer<V> itemSame, BiConsumer<V, V> itemChanged) {
            // TODO: Optimize
            // If old set is null or empty, pass all new items to be added
            if (checkEdgesAndReturn(oldSet, newSet, itemAdded))
                return;

            // If new set is null or empty, pass all old items to be deleted
            if (checkEdgesAndReturn(newSet, oldSet, itemDeleted))
                return;

            Set<V> deletedItems = Sets.difference(oldSet, newSet);
            Set<V> addedItems = Sets.difference(newSet, oldSet);

            Set<V> unchangedItems = Sets.difference(Sets.union(newSet, oldSet), Sets.union(addedItems, deletedItems));

            Set<V> changedItems = new HashSet<>();
            Set<V> changedOldItems = new HashSet<>();
            for (V deleted : deletedItems) {
                for (V added : addedItems) {
                    if (builder.sameItemPredicate.test(deleted, added)) {
                        changedItems.add(added);
                        changedOldItems.add(deleted);
                        itemChanged.accept(deleted, added);
                    }
                }
            }

            addedItems = addedItems.stream()
                    .filter(a -> !changedItems.contains(a))
                    .collect(Collectors.toSet());
            deletedItems = deletedItems.stream()
                    .filter(a -> !changedOldItems.contains(a))
                    .collect(Collectors.toSet());

            addedItems.forEach(itemAdded);
            deletedItems.forEach(itemDeleted);
            unchangedItems.forEach(itemSame);
        }
    }

    private final Builder<V> builder;

    private DiffUtils(Builder<V> builder) {
        this.builder = builder;
    }

    public static <V> DiffUtils.Builder<V> of(Class<V> clazz) {
        return new DiffUtils.Builder<>(clazz);
    }

    public Calculator<V> sets(Set<V> oldSet, Set<V> newSet) {
        return new Calculator<>(builder, oldSet, newSet);
    }

}
