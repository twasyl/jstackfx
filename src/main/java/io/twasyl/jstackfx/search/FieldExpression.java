package io.twasyl.jstackfx.search;

import io.twasyl.jstackfx.search.exceptions.EvaluateException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public class FieldExpression<T> {

    public static class Builder<B> {
        private final FieldExpression<B> fieldExpression = new FieldExpression<>();

        private Builder() {
        }

        public static <B> Builder<B> create(Class<B> clazz) {
            if (clazz == null) throw new NullPointerException("The class can not be null");

            final Builder<B> builder = new Builder<B>();
            builder.fieldExpression.clazz = clazz;
            return builder;
        }

        public FieldExpression build() {
            try {
                final BeanInfo bean = Introspector.getBeanInfo(this.fieldExpression.clazz);
                final PropertyDescriptor[] descriptors = bean.getPropertyDescriptors();
                boolean found = false;
                int index = 0;

                while (!found && index < descriptors.length) {
                    final PropertyDescriptor descriptor = descriptors[index];

                    if (this.fieldExpression.fieldName.equals(descriptor.getName())) {
                        found = true;
                        this.fieldExpression.fieldReadMethod = descriptor.getReadMethod();
                    }

                    index++;
                }

                if (this.fieldExpression.fieldReadMethod == null) {
                    throw new NullPointerException("Getter for the field " + this.fieldExpression.fieldName + " in given class " + this.fieldExpression.clazz.getName());
                }
            } catch (IntrospectionException e) {
                throw new IllegalArgumentException("Can not determine BeanInfo for instance object", e);
            }

            return this.fieldExpression;
        }

        public Builder onField(final String field) {
            if (field == null) throw new NullPointerException("The field can not be null");
            if (field.trim().isEmpty()) throw new IllegalArgumentException("The field can not be empty");

            this.fieldExpression.fieldName = field.trim();
            return this;
        }

        public Builder withWalue(final Comparable value) {
            this.fieldExpression.value = value;
            return this;
        }

        public Builder using(final Comparator comparator) {
            if (comparator == null) throw new NullPointerException("The comparator can not be null");

            this.fieldExpression.comparator = comparator;
            return this;
        }
    }

    protected Class<T> clazz = null;
    protected Method fieldReadMethod = null;
    protected Comparator comparator = null;
    protected String fieldName = null;
    protected Comparable value = null;

    private FieldExpression() {
    }

    public boolean match(final T instance) throws EvaluateException {
        try {
            final Comparable instanceValue = Comparable.class.cast(fieldReadMethod.invoke(instance));
            final boolean evaluation = this.comparator.evaluate(instanceValue, this.value);
            return evaluation;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new EvaluateException("Can not determine value for field " + this.fieldName + " for given instance", e);
        }
    }
}
