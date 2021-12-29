package hu.webarticum.miniconnect.rdmsframework.storage;

/**
 * Marker interface for selection entry order keys.
 * 
 * <p>
 * If the order keys of two result entries are equal,
 * the entries can be merged by their order index.
 * Order indices are valid for the duration of the current transaction
 * or non-transactional query execution (until the underlying table changes),
 * but order keys can be reusable between transactions
 * (if not, it should be documented).
 * </p>
 * 
 * @see TableSelection
 * @see TableSelectionEntry
 */
public interface OrderKey {

    public static OrderKey adHoc() {
        return new OrderKey() {};
    }
    
}
