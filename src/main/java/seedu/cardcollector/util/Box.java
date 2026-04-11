package seedu.cardcollector.util;

/**
 * A container that holds a value or null.
 * Instances are created via the {@link #of(T value)} factory method.
 */
public class Box<T> {
    private final T value;

    private Box(T value) {
        this.value = value;
    }

    /**
     * Creates a new {@code Box} containing the given value.
     * @value Value to store.
     */
    public static <T> Box<T> of(T value) {
        return new Box<>(value);
    }

    /**
     * Returns the contained value, or {@code null}.
     */
    public T get() {
        return value;
    }
}
