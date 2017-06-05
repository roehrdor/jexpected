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

package test.util.function;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;

import util.function.Expected;

/**
 * Tests for the {@link Expected} class
 */
public class ExpectedTest {

	/**
	 * Static helper function to create an {@link Expected}
	 * 
	 * @param <T>
	 *            the type of the value
	 * @param t
	 *            the value to provide
	 * @return an {@link Expected} for the given value
	 */
	private static <T> Expected<T, ?> createExpected(T t) {
		return Expected.expected(t);
	}

	/**
	 * Static helper function to create an unexpected {@link Expected}
	 * 
	 * @param <T>
	 *            the type of the error value
	 * @param t
	 *            the error value to provide
	 * @return an {@link Expected} for the unexpected given error value
	 */
	private static <T> Expected<?, T> createUnExpected(T t) {
		return Expected.unexpected(t);
	}

	@Test
	public void testExpectedHasValue() {
		assertTrue(createExpected(1337).hasValue());
		assertTrue(createExpected(null).hasValue());
	}

	@Test
	public void testUnExpectedHasErrorValue() {
		assertTrue(createUnExpected(1337).hasErrorValue());
		assertTrue(createUnExpected(null).hasErrorValue());
	}

	@Test
	public void testExpectedHasNoErrorValue() {
		assertTrue(!createExpected(1337).hasErrorValue());
		assertTrue(!createExpected(null).hasErrorValue());
	}

	@Test
	public void testExpectedHasNoValue() {
		assertTrue(!createUnExpected(1337).hasValue());
		assertTrue(!createUnExpected(null).hasValue());
	}

	@Test
	public void testExpectedHasCorrectValue() {
		assertEquals(Integer.class, createExpected(1337).getValue().getClass());
		assertEquals((int) 1337, (int) createExpected(1337).getValue());
		assertNull(createExpected(null).getValue());
	}

	@Test
	public void testUnExpectedHasCorrectErrorValue() {
		assertEquals(Integer.class, createUnExpected(1337).getErrorValue().getClass());
		assertEquals((int) 1337, (int) createUnExpected(1337).getErrorValue());
		assertNull(createUnExpected(null).getErrorValue());
	}

	@Test(expected = NoSuchElementException.class)
	public void testExpectedThrowErrorValueNonNull() {
		createExpected(1337).getErrorValue();
	}

	@Test(expected = NoSuchElementException.class)
	public void testUnExpectedThrowErrorValueNonNull() {
		createUnExpected(1337).getValue();
	}

	@Test(expected = NoSuchElementException.class)
	public void testExpectedThrowErrorValueNull() {
		createExpected(null).getErrorValue();
	}

	@Test(expected = NoSuchElementException.class)
	public void testUnExpectedThrowErrorValueNull() {
		createUnExpected(null).getValue();
	}

	@Test
	public void testIfValue() {
		createExpected(1337).ifValue(v -> {
			assertEquals((int) 1337, (int) v);
		});
		createExpected(null).ifValue(v -> {
			assertNull(v);
		});
		createUnExpected(null).ifValue(v -> {
			fail("Unexpected has no value");
		});
	}

	@Test
	public void testFilter() {
		assertEquals(1337, (int) createExpected(1337).filer(v -> {
			assertEquals(1337, (int) v);
			return v == 1337;
		}).get());
		assertFalse(createExpected(1337).filer(v -> {
			assertEquals(1337, (int) v);
			return v != 1337;
		}).isPresent());
		assertEquals(Optional.empty(), createExpected(null).filer(v -> {
			assertNull(v);
			return v == null;
		}));
		assertEquals(Optional.empty(), createUnExpected(null).filer(v -> {
			fail("Unexpected cannot be filtered");
			return false;
		}));
	}

	@Test
	public void testMap() {
		assertEquals(1338, (int) createExpected(1337).map(v -> v + 1).getValue());
		assertEquals(1338, (int) createExpected(null).map(v -> 1338).getValue());
		assertEquals(404, (int) createUnExpected(404).map(v -> 1).getErrorValue());
	}

	@Test
	public void testBind() {
		final Object ret = new Object();
		assertEquals(ret, createExpected(1337).bind(v -> {
			assertEquals(1337, (int) v);
			return ret;
		}));
		assertEquals(ret, createExpected(null).bind(v -> {
			assertNull(v);
			return ret;
		}));
		assertNull(createUnExpected(404).bind(v -> {
			return ret;
		}));
	}

	@Test
	public void testBindExpected() {
		final Object ret = new Object();
		assertEquals(ret, createExpected(1337).bindExpected(v -> {
			assertEquals(1337, (int) v);
			return Expected.expected(ret);
		}).getValue());
		assertEquals(ret, createExpected(null).bindExpected(v -> {
			assertNull(v);
			return Expected.expected(ret);
		}).getValue());
		assertEquals(404, (int) createUnExpected(404).bindExpected(v -> {
			return Expected.expected(ret);
		}).getErrorValue());
	}
}
