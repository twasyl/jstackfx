package io.twasyl.jstackfx.search;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testing class {@link Comparator}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public class ComparatorTest {

    @Test
    public void testEquals() {
        assertTrue(Comparator.EQUAL.evaluate(1l, 1l));
    }

    @Test
    public void testEqualsWhenDifferent() {
        assertFalse(Comparator.EQUAL.evaluate(1l, 2l));
    }

    @Test
    public void testEqualWithStates() {
        assertTrue(Comparator.EQUAL.evaluate(Thread.State.NEW, Thread.State.NEW));
    }

    @Test
    public void testEqualWithDifferentStates() {
        assertFalse(Comparator.EQUAL.evaluate(Thread.State.NEW, Thread.State.RUNNABLE));
    }

    @Test
    public void testDifferent() {
        assertTrue(Comparator.DIFFERENT.evaluate(1l, 2l));
    }

    @Test
    public void testDifferentWhenEqual() {
        assertFalse(Comparator.DIFFERENT.evaluate(1l, 1l));
    }

    @Test
    public void testDifferentWithStates() {
        assertTrue(Comparator.DIFFERENT.evaluate(Thread.State.NEW, Thread.State.RUNNABLE));
    }

    @Test
    public void testDifferentWithEqualStates() {
        assertFalse(Comparator.DIFFERENT.evaluate(Thread.State.NEW, Thread.State.NEW));
    }

    @Test
    public void testLower() {
        assertTrue(Comparator.LESS.evaluate(1, 2));
    }

    @Test
    public void testLowerWhenNot() {
        assertFalse(Comparator.LESS.evaluate(2, 1));
    }

    @Test
    public void testLowerWhenEqual() {
        assertFalse(Comparator.LESS.evaluate(1, 1));
    }

    @Test
    public void testLowerOrEqual() {
        assertTrue(Comparator.LESS_OR_EQUAL.evaluate(1, 2));
    }

    @Test
    public void testLowerOrEqualWhenNot() {
        assertFalse(Comparator.LESS_OR_EQUAL.evaluate(2, 1));
    }

    @Test
    public void testLowerOrEqualWhenEqual() {
        assertTrue(Comparator.LESS_OR_EQUAL.evaluate(1, 1));
    }

    @Test
    public void testGreater() {
        assertTrue(Comparator.GREATER.evaluate(2, 1));
    }

    @Test
    public void testGreaterWhenNot() {
        assertFalse(Comparator.GREATER.evaluate(1, 2));
    }

    @Test
    public void testGreaterWhenEqual() {
        assertFalse(Comparator.GREATER.evaluate(1, 1));
    }

    @Test
    public void testGreaterOrEqual() {
        assertTrue(Comparator.GREATER_OR_EQUAL.evaluate(2, 1));
    }

    @Test
    public void testGreaterOrEqualWhenNot() {
        assertFalse(Comparator.GREATER_OR_EQUAL.evaluate(1, 2));
    }

    @Test
    public void testGreaterOrEqualWhenEqual() {
        assertTrue(Comparator.GREATER_OR_EQUAL.evaluate(1, 1));
    }
}
