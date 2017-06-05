//           DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
//                   Version 2, December 2004
//
// Copyright (C) 2017 roehrdor@outlook.com
//
// Everyone is permitted to copy and distribute verbatim or modified
// copies of this license document, and changing it is allowed as long
// as the name is changed.
//
//            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
//   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
//
//  0. You just DO WHAT THE FUCK YOU WANT TO.

package util.function;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The {@code Expected} class provides some other kind of error handling
 * compared to {@code Exception}. Instead of returning the result in the case of
 * success and throwing an exception in the case of failure, {@code Expected}
 * objects can be created of results by calling {@link #expected(Object)} and
 * error values by calling {@link #unexpected(Object)} function.
 * 
 * @author roehrdor
 *
 * @param <T>
 *            the type of the expected value
 * @param <E>
 *            the type of the unexpected error value
 */
public final class Expected<T, E> {

	/** Whether the {@code Expected} holds an expected value or not */
	private final boolean isExpected;

	/** The expected value */
	private final T value;

	/** The error value */
	private final E errorValue;

	/**
	 * Create a new Expected type. Null are valid parmeters
	 * 
	 * @param value
	 *            the value
	 * @param errorValue
	 *            the error value
	 * @param isExpected
	 *            whether value is expected
	 */
	private Expected(T value, E errorValue, boolean isExpected) {
		this.value = value;
		this.errorValue = errorValue;
		this.isExpected = isExpected;
	}

	/**
	 * Make an {@code Expected} of the given value
	 * 
	 * @param <T>
	 *            the type of the value
	 * @param <E>
	 *            the type of the error value
	 * @param value
	 *            the value, can be null
	 * @return an expected representing the value
	 */
	public static <T, E> Expected<T, E> expected(T value) {
		return new Expected<>(value, null, true);
	}

	/**
	 * Make an {@code Expected} of the given error value
	 *
	 * @param <T>
	 *            the type of the value
	 * @param <E>
	 *            the type of the error value
	 * @param errorValue
	 *            the error value, can be null
	 * @return an expected containing the not expected error value
	 */
	public static <T, E> Expected<T, E> unexpected(E errorValue) {
		return new Expected<>(null, errorValue, false);
	}

	/**
	 * @return true if the {@code Expected} contains the expected value
	 */
	public boolean hasValue() {
		return this.isExpected;
	}

	/**
	 * @return true if the {@code Expected} contains an unexpected error value
	 */
	public boolean hasErrorValue() {
		return !this.isExpected;
	}

	/**
	 * Get the value of the {@code Expected}. If the value does not exist the
	 * {@code NoSuchElementException} will be thrown
	 * 
	 * @return the value if existent
	 * @throws NoSuchElementException
	 *             if the {@code Expected} does not contain an expected value
	 */
	public T getValue() throws NoSuchElementException {
		if (!this.isExpected) {
			throw new NoSuchElementException("No value present");
		}
		return this.value;
	}

	/**
	 * Get the error value of the {@code Expected}. If the value does not exist
	 * the {@code NoSuchElementException} will be thrown
	 * 
	 * @return the error value if existent
	 * @throws NoSuchElementException
	 *             if the {@code Expected} does not contain an unexpected value
	 */
	public E getErrorValue() throws NoSuchElementException {
		if (this.isExpected) {
			throw new NoSuchElementException("No value present");
		}
		return this.errorValue;
	}

	/**
	 * If a value is present and consumer is not null, the consumer is invoked
	 * with the value. Otherwise nothing will be done
	 * 
	 * @param consumer
	 *            to be executed if not null and value present
	 */
	public void ifValue(Consumer<? super T> consumer) {
		if (this.isExpected && consumer != null) {
			consumer.accept(this.value);
		}
	}

	/**
	 * If a value is present, the provided predicate is not null and the value
	 * matches the given predicate an {@link Optional} describing the value will
	 * be returned. In every other case {@link Optional#empty()} will be
	 * returned.
	 * 
	 * @param predicate
	 *            the predicate to apply to the value if both exist
	 * @return an {@link Optional} describing the value if predicate is not
	 *         null, value exists and matches the predicate, otherwise empty
	 *         Optional
	 */
	public Optional<T> filer(Predicate<? super T> predicate) {
		if (this.isExpected && predicate != null) {
			return predicate.test(this.value) ? Optional.ofNullable(this.value) : Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	/**
	 * <p>
	 * If the expected value is present and the provided mapper is not null, the
	 * mapping function will be applied to the contained value. The return value
	 * of the mapper will be wrapped into an {@code Expected} by calling the
	 * {@link #expected(Object)} function.
	 * </p>
	 * <p>
	 * If the mapper is null a {@link NullPointerException} will be thrown. If
	 * the value does not exist an {@code Expected} with the unexpected value
	 * will be returned.
	 * </p>
	 * 
	 * @param <U>
	 *            the type of the result of the mapping function
	 * @param mapper
	 *            mapping function to be applied if not null and value exists
	 * @return an Expected containing the result of the mapper or if the value
	 *         does not exist an {@code Expected} of the contained error value
	 * @throws NullPointerException
	 *             if the mapper is null
	 */
	public <U> Expected<U, E> map(Function<? super T, ? extends U> mapper) throws NullPointerException {
		if (this.isExpected) {
			return expected(mapper.apply(this.value));
		} else {
			return unexpected(this.errorValue);
		}
	}

	/**
	 * <p>
	 * If the expected value is present and the provided binder is not null, the
	 * mapping function will be applied to the contained value. The return value
	 * of the binder will be returned without being wrapped into an
	 * {@code Expected}
	 * </p>
	 * <p>
	 * If the binder is null a {@link NullPointerException} will be thrown. If
	 * the value does not exist null will be returned
	 * </p>
	 * 
	 * @param <U>
	 *            the return type of the binding function
	 * @param binder
	 *            binding function to be applied if not null and value exists
	 * @return the result of the applied binding function or null if no value
	 *         does exist
	 * @throws NullPointerException
	 *             if the binder is null
	 */
	public <U> U bind(Function<? super T, ? extends U> binder) {
		if (this.isExpected) {
			return binder.apply(this.value);
		} else {
			return null;
		}
	}

	/**
	 * <p>
	 * If the expected value is present and the provided binder is not null, the
	 * mapping function will be applied to the contained value. The return value
	 * of the binder will be returned as {@code Expected}.
	 * </p>
	 * <p>
	 * If the binder is null a {@link NullPointerException} will be thrown. If
	 * the value does exist an {@code Expected} with the already contained error
	 * value will be returned.
	 * </p>
	 * 
	 * @param <U>
	 *            the value type of the binding function
	 * @param binder
	 *            binding function to be applied if not null and value exists
	 * @return the result of the applied binding function or an {@code Expected}
	 *         with the already contained unexpected value if no value is
	 *         present
	 * @throws NullPointerException
	 *             if the binder is null
	 */
	public <U> Expected<U, E> bindExpected(Function<? super T, ? extends Expected<U, E>> binder) {
		if (this.isExpected) {
			return binder.apply(this.value);
		} else {
			return unexpected(this.errorValue);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorValue == null) ? 0 : errorValue.hashCode());
		result = prime * result + (isExpected ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || this.getClass() != obj.getClass())
			return false;

		// obj is an instance of Expected
		Expected<?, ?> other = (Expected<?, ?>) obj;

		// Compare flag
		if (this.isExpected != other.isExpected)
			return false;

		// Compare error value
		if (this.errorValue == null && other.errorValue != null) {
			return false;
		} else if (!this.errorValue.equals(other.errorValue)) {
			return false;
		}

		// Compare value
		if (this.value == null && other.value != null) {
			return false;
		} else if (!value.equals(other.value)) {
			return false;
		}

		// Nothing differs
		return true;
	}
}
