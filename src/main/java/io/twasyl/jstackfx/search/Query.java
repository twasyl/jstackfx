package io.twasyl.jstackfx.search;

import io.twasyl.jstackfx.search.exceptions.ConversionException;
import io.twasyl.jstackfx.search.exceptions.UnparsableQueryException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class defining a query that can be made using the search bar.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public class Query<T> {

    private static Logger LOGGER = Logger.getLogger(Query.class.getName());

    protected static Pattern EXPRESSION_PATTERN;
    protected static String FIELD_NAME_GROUP = "fieldName";
    protected static String FIELD_VALUE_GROUP = "fieldValue";
    protected static String COMPARATOR_GROUP = "comparator";
    protected static String OPERAND_GROUP = "operand";

    protected static Map<String, Class> FIELD_TYPE_CACHE = new HashMap<>();

    static {
        final StringJoiner comparatorsRegex = new StringJoiner("|", "(?<" + COMPARATOR_GROUP + ">", ")");
        Arrays.stream(Comparator.values()).forEach(comparator -> comparatorsRegex.add(comparator.getRegexExpression()));

        final StringBuilder expression = new StringBuilder("(?<").append(FIELD_NAME_GROUP).append(">[^\\s]+)")
                .append("\\s*").append(comparatorsRegex.toString())
                .append("\\s*(?<").append(FIELD_VALUE_GROUP).append(">[^\\s]+)")
                .append("\\s*(?<").append(OPERAND_GROUP).append(">or|and)?");

        EXPRESSION_PATTERN = Pattern.compile(expression.toString());
    }

    protected Class<T> clazz;
    protected FieldExpressionQueue<T> expressions;
    protected String rawQuery;

    private Query(final Class<T> clazz) {
        this.clazz = clazz;
        this.expressions = new FieldExpressionQueue<>();
    }

    public static <T> Query<T> create(final Class<T> clazz) {
        if (clazz == null) throw new NullPointerException("The class can not be null");
        final Query<T> query = new Query(clazz);
        return query;
    }

    public String getRawQuery() {
        return rawQuery;
    }

    public boolean parse(final String query) throws UnparsableQueryException {
        this.rawQuery = query;
        final Matcher matcher = EXPRESSION_PATTERN.matcher(query);
        this.expressions.clear();

        boolean parsingSuccessful = true;

        while (matcher.find() && parsingSuccessful) {
            final String field = matcher.group(FIELD_NAME_GROUP);
            final Comparator comparator = Comparator.fromRegex(matcher.group(COMPARATOR_GROUP));
            final Comparable value;

            try {
                value = convertFieldValue(field, matcher.group(FIELD_VALUE_GROUP));
                this.expressions.put(FieldExpression.Builder.create(this.clazz)
                        .onField(field)
                        .using(comparator)
                        .withWalue(value)
                        .build());

                final String operandString = matcher.group(OPERAND_GROUP);
                if (operandString != null) {
                    final Operand operand = Operand.fromRegex(operandString);
                    if (operand == Operand.AND) {
                        this.expressions.and();
                    } else if (operand == Operand.OR) {
                        this.expressions.or();
                    }
                }
            } catch (ConversionException e) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.WARNING, "Error parsing the query", e);
                }
                parsingSuccessful = false;
            }
        }
        return parsingSuccessful;
    }

    public boolean match(final T object) {
        return this.expressions.match(object);
    }

    protected Comparable convertFieldValue(final String fieldName, final String fieldValue) throws ConversionException {
        if (!FIELD_TYPE_CACHE.containsKey(fieldName)) {
            FIELD_TYPE_CACHE.put(fieldName, determineFieldClass(fieldName));
        }

        final Class aClass = FIELD_TYPE_CACHE.get(fieldName);

        try {
            if (aClass.isEnum()) {
                return Enum.valueOf(aClass, fieldValue.toUpperCase());
            } else if (aClass.equals(byte.class) || aClass.equals(Byte.class)) {
                return Byte.parseByte(fieldValue);
            } else if (aClass.equals(int.class) || aClass.equals(Integer.class)) {
                return Integer.parseInt(fieldValue);
            } else if (aClass.equals(long.class) || aClass.equals(Long.class)) {
                return Long.parseLong(fieldValue);
            } else if (aClass.equals(short.class) || aClass.equals(Short.class)) {
                return Short.parseShort(fieldValue);
            } else if (aClass.equals(float.class) || aClass.equals(Float.class)) {
                return Float.parseFloat(fieldValue);
            } else if (aClass.equals(double.class) || aClass.equals(Double.class)) {
                return Double.parseDouble(fieldValue);
            } else if (aClass.equals(boolean.class) || aClass.equals(Boolean.class)) {
                return Boolean.parseBoolean(fieldValue);
            } else if (aClass.equals(char.class) || aClass.equals(Character.class)) {
                if (fieldValue.length() == 1) {
                    return fieldValue.charAt(0);
                } else {
                    throw new ConversionException("Field value [" + fieldValue + "] is not a character");
                }
            } else if (aClass.equals(String.class)) {
                return fieldValue;
            } else {
                throw new ConversionException("Type [" + aClass.getName() + "] is not supported in search");
            }
        } catch (ConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new ConversionException("Can not convert value [" + fieldValue + "] to class [" + aClass.getName() + "]", e);
        }
    }

    protected Class determineFieldClass(final String fieldName) {
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(this.clazz);
            final PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            final PropertyDescriptor fieldDescriptor = Arrays.stream(descriptors).filter(descriptor -> descriptor.getName().equals(fieldName)).findFirst().orElse(null);

            return fieldDescriptor.getReadMethod().getReturnType();

        } catch (IntrospectionException e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.WARNING, "Error determining the field [" + fieldName + "] class", e);
            }
            return null;
        }
    }
}
