package io.twasyl.jstackfx.search;

import io.twasyl.jstackfx.beans.ThreadElement;
import io.twasyl.jstackfx.search.exceptions.EvaluateException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testing class {@link FieldExpression}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public class FieldExpressionTest {

    @Test(expected = NullPointerException.class)
    public void tryToBuildWithNullClass() {
        FieldExpression.Builder.create(null);
    }

    @Test(expected = NullPointerException.class)
    public void tryToBuildWithNullField() {
        FieldExpression.Builder.create(ThreadElement.class).onField(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToBuildWithEmptyField() {
        FieldExpression.Builder.create(ThreadElement.class).onField("   ");
    }

    @Test(expected = NullPointerException.class)
    public void tryToBuildWithNullComparator() {
        FieldExpression.Builder.create(ThreadElement.class).using(null);
    }

    @Test
    public void testEqualOnState() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.NEW);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .withWalue(Thread.State.NEW)
                .using(Comparator.EQUAL)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void testEqualOnStateWhenDifferent() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .withWalue(Thread.State.NEW)
                .using(Comparator.EQUAL)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void testEqualOnStateWhenNull() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(null);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .withWalue(Thread.State.NEW)
                .using(Comparator.EQUAL)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void testEqualOnStateWhenExpectedIsNull() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.NEW);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .withWalue(null)
                .using(Comparator.EQUAL)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void differentComparatorWithDifferentStates() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.NEW);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .withWalue(Thread.State.RUNNABLE)
                .using(Comparator.DIFFERENT)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void differentComparatorWithEqualStates() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .withWalue(Thread.State.RUNNABLE)
                .using(Comparator.DIFFERENT)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void differentComparatorWithNullState() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(null);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .withWalue(Thread.State.NEW)
                .using(Comparator.DIFFERENT)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void differentComparatorWithExpectedNullState() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.NEW);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .withWalue(null)
                .using(Comparator.DIFFERENT)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void lowerOnNumber() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(2l)
                .using(Comparator.LESS)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void lowerOnNumberWhenGreater() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(0l)
                .using(Comparator.LESS)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void lowerOnStateWhenExpectedIsNull() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(null)
                .using(Comparator.LESS)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void lowerOrEqualOnNumber() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(2l)
                .using(Comparator.LESS_OR_EQUAL)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void lowerOrEqualOnNumberWhenEqual() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(2);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(2l)
                .using(Comparator.LESS_OR_EQUAL)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void lowerEqualOnNumberWhenGreater() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(0l)
                .using(Comparator.LESS_OR_EQUAL)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void lowerOrEqualOnStateWhenExpectedIsNull() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(null)
                .using(Comparator.LESS_OR_EQUAL)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void greaterOnNumber() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(2);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(1l)
                .using(Comparator.GREATER)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void greaterOnNumberWhenLower() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(2l)
                .using(Comparator.GREATER)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void greaterOnStateWhenExpectedIsNull() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(null)
                .using(Comparator.GREATER)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void greaterOrEqualOnNumber() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(2);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(1l)
                .using(Comparator.GREATER_OR_EQUAL)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void greaterOrEqualOnNumberWhenEqual() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(1l)
                .using(Comparator.GREATER_OR_EQUAL)
                .build();
        assertTrue(fieldExpression.match(element));
    }

    @Test
    public void greaterOrEqualOnNumberWhenLower() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(2l)
                .using(Comparator.GREATER_OR_EQUAL)
                .build();
        assertFalse(fieldExpression.match(element));
    }

    @Test
    public void greaterOrEqualOnStateWhenExpectedIsNull() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setNumber(1);

        final FieldExpression<ThreadElement> fieldExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .withWalue(null)
                .using(Comparator.GREATER_OR_EQUAL)
                .build();
        assertFalse(fieldExpression.match(element));
    }
}
