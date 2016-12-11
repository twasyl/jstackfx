package io.twasyl.jstackfx.search;

import io.twasyl.jstackfx.beans.ThreadElement;
import io.twasyl.jstackfx.search.exceptions.EvaluateException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing class {@link FieldExpressionQueue}.
 *
 * @author Thierry Wasylczenko
 * @since JStack 1.1
 */
public class FieldExpressionQueueTest {

    @Test
    public void addAndOnEmptyQueue() {
        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.and();

        assertTrue(queue.expressions.isEmpty());
    }

    @Test
    public void addOrOnEmptyQueue() {
        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.or();

        assertTrue(queue.expressions.isEmpty());
    }

    @Test
    public void addExpressionWithoutOperand() {
        final FieldExpression expression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(null)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(expression);

        assertEquals(1, queue.expressions.size());
        assertNotNull(queue.expressions.get(0));
        assertNotNull(queue.expressions.get(0).getValue1());
        assertNull(queue.expressions.get(0).getValue2());
    }

    @Test
    public void addExpressionWithAndOperand() {
        final FieldExpression expression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(null)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(expression).and();

        assertEquals(1, queue.expressions.size());
        assertNotNull(queue.expressions.get(0));
        assertNotNull(queue.expressions.get(0).getValue1());
        assertNotNull(queue.expressions.get(0).getValue2());
        assertEquals(expression, queue.expressions.get(0).getValue1());
        assertEquals(Operand.AND, queue.expressions.get(0).getValue2());
    }

    @Test
    public void addExpressionWithOrOperand() {
        final FieldExpression expression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(null)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(expression).or();

        assertEquals(1, queue.expressions.size());
        assertNotNull(queue.expressions.get(0));
        assertNotNull(queue.expressions.get(0).getValue1());
        assertNotNull(queue.expressions.get(0).getValue2());
        assertEquals(expression, queue.expressions.get(0).getValue1());
        assertEquals(Operand.OR, queue.expressions.get(0).getValue2());
    }

    @Test
    public void addExpressionWithOrPlusAndOperand() {
        final FieldExpression expression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(null)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(expression).or().and();

        assertEquals(1, queue.expressions.size());
        assertNotNull(queue.expressions.get(0));
        assertNotNull(queue.expressions.get(0).getValue1());
        assertNotNull(queue.expressions.get(0).getValue2());
        assertEquals(expression, queue.expressions.get(0).getValue1());
        assertEquals(Operand.AND, queue.expressions.get(0).getValue2());
    }

    @Test
    public void buildScriptExpressionWithoutOperand() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);

        final FieldExpression expression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(Thread.State.RUNNABLE)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(expression);

        assertEquals("true", queue.buildScriptExpression(element));
    }

    @Test
    public void buildScriptExpressionWithOperandAndNoOtherExpression() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);

        final FieldExpression expression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(Thread.State.RUNNABLE)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(expression).or();

        assertEquals("true", queue.buildScriptExpression(element));
    }

    @Test
    public void buildScriptExpressionWithOperandAndOtherExpression() throws EvaluateException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(10);

        final FieldExpression stateExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(Thread.State.RUNNABLE)
                .build();

        final FieldExpression numberExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .using(Comparator.EQUAL)
                .withWalue(20l)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(stateExpression).and().put(numberExpression);

        assertEquals("true && false", queue.buildScriptExpression(element));
    }

    @Test
    public void match() {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);

        final FieldExpression expression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(Thread.State.RUNNABLE)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(expression);

        assertTrue(queue.match(element));
    }

    @Test
    public void matchMultipleExpression() {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(10);

        final FieldExpression stateExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(Thread.State.RUNNABLE)
                .build();

        final FieldExpression numberExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .using(Comparator.EQUAL)
                .withWalue(10l)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(stateExpression).and().put(numberExpression);

        assertTrue(queue.match(element));
    }

    @Test
    public void doesntMatch() {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.NEW);

        final FieldExpression expression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(Thread.State.RUNNABLE)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(expression);

        assertFalse(queue.match(element));
    }

    @Test
    public void doesntMatchMultipleExpression() {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final FieldExpression stateExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("state")
                .using(Comparator.EQUAL)
                .withWalue(Thread.State.RUNNABLE)
                .build();

        final FieldExpression numberExpression = FieldExpression.Builder.create(ThreadElement.class)
                .onField("number")
                .using(Comparator.EQUAL)
                .withWalue(10l)
                .build();

        final FieldExpressionQueue<ThreadElement> queue = new FieldExpressionQueue<>();
        queue.put(stateExpression).and().put(numberExpression);

        assertFalse(queue.match(element));
    }
}
