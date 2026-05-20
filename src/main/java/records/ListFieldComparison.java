package main.java.records;

import java.util.Collections;
import java.util.List;

public record ListFieldComparison(String fieldName, List<String> currentValues, List<String> newValues) {

    // If there are values in the lists, make a copy to avoid modifying the originals
    // Otherwise, use an empty list
    public ListFieldComparison {
        currentValues = (currentValues != null)
                ? List.copyOf(currentValues)
                : Collections.emptyList();
        newValues = (newValues != null)
                ? List.copyOf(newValues)
                : Collections.emptyList();
    }

    /**
     * Returns true if the list contents are different from each other
     * Item reorder does not count as change
     */
    public boolean isChanged() {
        // Sort copies before comparing so order differences do not count as changes
        // Example: [A, B, C] and [C, A, B] are the same; no change
        List<String> sortedCurrent = currentValues.stream().sorted().toList();
        List<String> sortedNew = newValues.stream().sorted().toList();
        return !sortedCurrent.equals(sortedNew);
    }

    /**
     * Get the items that are newly added into the list
     */
    public List<String> getAddedItems() {
        return newValues.stream()
                .filter(item -> !currentValues.contains(item))
                .toList();
    }

    /**
     * Get the items that are removed from the list
     */
    public List<String> getRemovedItems() {
        return currentValues.stream()
                .filter(item -> !newValues.contains(item))
                .toList();
    }

    /**
     * Get the items that are present in both lists
     */
    public List<String> getRetainedItems() {
        return newValues.stream()
                .filter(currentValues::contains)
                .toList();
    }

    /**
     * Return true if any items were added.
     */
    public boolean hasAdditions() {
        return !getAddedItems().isEmpty();
    }

    /**
     * Return true if any items were removed.
     */
    public boolean hasRemovals() {
        return !getRemovedItems().isEmpty();
    }
}
