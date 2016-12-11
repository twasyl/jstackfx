package io.twasyl.jstackfx.search;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public enum Operand {
    AND("and", "&&"),
    OR("or", "||");

    private final String regexExpression;
    private final String programmingOperator;

    Operand(final String regexExpression, final String programmingOperator) {
        this.regexExpression = regexExpression;
        this.programmingOperator = programmingOperator;
    }

    public String getRegexExpression() {
        return regexExpression;
    }

    public String getProgrammingOperator() {
        return programmingOperator;
    }

    public static Operand fromRegex(final String regex) {
        for (final Operand operand : values()) {
            if (operand.regexExpression.equals(regex)) {
                return operand;
            }
        }
        return null;
    }
}
