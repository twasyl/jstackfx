package io.twasyl.jstackfx.search;

import io.twasyl.jstackfx.beans.Pair;
import io.twasyl.jstackfx.search.exceptions.EvaluateException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public class FieldExpressionQueue<T> {
    private static Logger LOGGER = Logger.getLogger(FieldExpressionQueue.class.getName());

    protected ScriptEngine engine;
    protected List<Pair<FieldExpression<T>, Operand>> expressions = new ArrayList<>();

    public FieldExpressionQueue() {
        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("nashorn");
    }

    public FieldExpressionQueue put(final FieldExpression<T> expression) {
        this.expressions.add(new Pair<>(expression, null));
        return this;
    }

    public FieldExpressionQueue and() {
        this.setOperandToLastElement(Operand.AND);
        return this;
    }

    public FieldExpressionQueue or() {
        this.setOperandToLastElement(Operand.OR);
        return this;
    }

    public FieldExpressionQueue clear() {
        this.expressions.clear();
        return this;
    }

    public boolean match(final T instance) {
        if (expressions.isEmpty()) {
            return false;
        } else {
            try {
                final Object result = this.engine.eval(this.buildScriptExpression(instance));
                return result != null && Objects.equals(true, result);
            } catch (ScriptException | EvaluateException e) {
                if(LOGGER.isLoggable(Level.FINE)){
                    LOGGER.log(Level.WARNING, "Can not evaluate matching", e);
                }
                return false;
            }
        }
    }

    protected String buildScriptExpression(final T instance) throws EvaluateException {
        final StringBuilder scriptExpression = new StringBuilder("");
        boolean continueBuilding = true;
        int index = 0;

        while (continueBuilding && index < this.expressions.size()) {
            final Pair<FieldExpression<T>, Operand> pair = this.expressions.get(index);
            scriptExpression.append(pair.getValue1().match(instance));

            if (pair.getValue2() != null) {
                boolean hasMorePair = (index + 1) < this.expressions.size();
                final Pair<FieldExpression<T>, Operand> nextPair;

                if (hasMorePair) {
                    nextPair = this.expressions.get(index + 1);
                } else {
                    nextPair = null;
                }

                continueBuilding = nextPair != null && nextPair.getValue1() != null;
            } else {
                continueBuilding = false;
            }

            index++;

            if(continueBuilding) {
                scriptExpression.append(" ").append(pair.getValue2().getProgrammingOperator()).append(" ");
            }
        }

        return scriptExpression.toString();
    }

    protected void setOperandToLastElement(final Operand operand) {
        final Pair<FieldExpression<T>, Operand> last = getLast();
        if (last != null) {
            last.setValue2(operand);
        }
    }

    protected Pair<FieldExpression<T>, Operand> getLast() {
        if (this.expressions.isEmpty()) return null;
        else return this.expressions.get(this.expressions.size() - 1);
    }
}
