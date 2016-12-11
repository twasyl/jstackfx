package io.twasyl.jstackfx.search;

/**
 * Enumeration defining which comparators are allowed in a {@link Query}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public enum Comparator {
    EQUAL("="),
    DIFFERENT("!=");

    private final String regexExpression;

    Comparator(final String regexExpression) {
        this.regexExpression = regexExpression;
    }

    public String getRegexExpression() {
        return regexExpression;
    }

    public static Comparator fromRegex(final String regex) {
        for (final Comparator comparator : values()) {
            if (comparator.regexExpression.equals(regex)) {
                return comparator;
            }
        }
        return null;
    }

    public boolean evaluate(final Comparable obj1, final Comparable obj2) {
        if (obj1 == null && obj2 == null) {
            return this == EQUAL;
        } else if ((obj1 != null && obj2 == null) || (obj1 == null && obj2 != null)) {
            return this == DIFFERENT;
        } else {
            final int compareTo = obj1.compareTo(obj2);

            if (this == EQUAL) {
                return compareTo == 0;
            } else if (this == DIFFERENT) {
                return compareTo != 0;
            } else {
                return true;
            }
        }
    }
}
