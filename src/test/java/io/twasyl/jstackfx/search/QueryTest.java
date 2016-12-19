package io.twasyl.jstackfx.search;

import io.twasyl.jstackfx.beans.ThreadElement;
import io.twasyl.jstackfx.search.exceptions.UnparsableQueryException;
import org.junit.Test;

import java.util.regex.Matcher;

import static io.twasyl.jstackfx.search.Query.*;
import static org.junit.Assert.*;

/**
 * Testing class {@link Query}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public class QueryTest {

    @Test
    public void simpleExpressionWithEqualParsing() {
        final String expression = "state = new";
        final Matcher matcher = Query.EXPRESSION_PATTERN.matcher(expression);

        assertTrue(matcher.find());
        assertEquals("state", matcher.group(FIELD_NAME_GROUP));
        assertEquals("=", matcher.group(COMPARATOR_GROUP));
        assertEquals("new", matcher.group(FIELD_VALUE_GROUP));
    }

    @Test
    public void simpleExpressionWithoutSpace() {
        final String expression = "state=new";
        final Matcher matcher = Query.EXPRESSION_PATTERN.matcher(expression);

        assertTrue(matcher.find());
        assertEquals("state", matcher.group(FIELD_NAME_GROUP));
        assertEquals("=", matcher.group(COMPARATOR_GROUP));
        assertEquals("new", matcher.group(FIELD_VALUE_GROUP));
    }

    @Test
    public void simpleExpressionWithDifferentParsing() {
        final String expression = "state != new";
        final Matcher matcher = Query.EXPRESSION_PATTERN.matcher(expression);

        assertTrue(matcher.find());
        assertEquals("state", matcher.group(FIELD_NAME_GROUP));
        assertEquals("!=", matcher.group(COMPARATOR_GROUP));
        assertEquals("new", matcher.group(FIELD_VALUE_GROUP));
    }

    @Test
    public void findOrOperand() {
        final String expression = "state != new or";
        final Matcher matcher = Query.EXPRESSION_PATTERN.matcher(expression);

        assertTrue(matcher.find());
        assertEquals("state", matcher.group(FIELD_NAME_GROUP));
        assertEquals("!=", matcher.group(COMPARATOR_GROUP));
        assertEquals("new", matcher.group(FIELD_VALUE_GROUP));
        assertEquals(Operand.OR.getRegexExpression(), matcher.group(OPERAND_GROUP));
    }

    @Test
    public void findAndOperand() {
        final String expression = "state != new and";
        final Matcher matcher = Query.EXPRESSION_PATTERN.matcher(expression);

        assertTrue(matcher.find());
        assertEquals("state", matcher.group(FIELD_NAME_GROUP));
        assertEquals("!=", matcher.group(COMPARATOR_GROUP));
        assertEquals("new", matcher.group(FIELD_VALUE_GROUP));
        assertEquals(Operand.AND.getRegexExpression(), matcher.group(OPERAND_GROUP));
    }

    @Test
    public void matchQuery() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = RUNNABLE");

        assertTrue(query.match(element));
    }

    @Test
    public void matchQueryWithOperandAndNotOtherExpression() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = RUNNABLE and ");

        assertTrue(query.match(element));
    }

    @Test
    public void matchQueryWithOperandAndOtherExpression() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = RUNNABLE and number = 20");

        assertTrue(query.match(element));
    }

    @Test
    public void matchQueryWithOperandAndOtherExpressionUsingLessOrEqual() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = RUNNABLE and number <= 20");

        assertTrue(query.match(element));
    }

    @Test
    public void matchQueryWithOperandAndOtherExpressionUsingLess() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = RUNNABLE and number < 21");

        assertTrue(query.match(element));
    }

    @Test
    public void matchQueryWithOperandAndOtherExpressionUsingGreaterOrEqual() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = RUNNABLE and number >= 20");

        assertTrue(query.match(element));
    }

    @Test
    public void matchQueryWithOperandAndOtherExpressionUsingGreater() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = RUNNABLE and number > 19");

        assertTrue(query.match(element));
    }

    @Test
    public void doesntMatchQuery() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = NEW");

        assertFalse(query.match(element));
    }

    @Test
    public void doesntMatchQueryWithOperandAndNotOtherExpression() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = NEW and ");

        assertFalse(query.match(element));
    }

    @Test
    public void doesntMatchQueryWithOperandAndOtherExpression() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = NEW and number = 20");

        assertFalse(query.match(element));
    }

    @Test
    public void doesntMatchQueryWithOperandAndOtherExpressionUsingLess() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = NEW and number < 20");

        assertFalse(query.match(element));
    }

    @Test
    public void doesntMatchQueryWithOperandAndOtherExpressionUsingLessOrEqual() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = NEW and number <= 19");

        assertFalse(query.match(element));
    }

    @Test
    public void doesntMatchQueryWithOperandAndOtherExpressionUsingGreater() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = NEW and number > 20");

        assertFalse(query.match(element));
    }

    @Test
    public void doesntMatchQueryWithOperandAndOtherExpressionUsingGreaterOrEqual() throws UnparsableQueryException {
        final ThreadElement element = new ThreadElement();
        element.setState(Thread.State.RUNNABLE);
        element.setNumber(20);

        final Query<ThreadElement> query = Query.create(ThreadElement.class);
        query.parse("state = NEW and number > 21");

        assertFalse(query.match(element));
    }
}
