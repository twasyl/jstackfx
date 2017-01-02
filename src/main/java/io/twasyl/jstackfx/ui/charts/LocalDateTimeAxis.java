package io.twasyl.jstackfx.ui.charts;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.List;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX @@NEXT-VERSION@@
 */
public class LocalDateTimeAxis extends Axis<LocalDateTime> {

    protected final LongProperty lowerBound = new SimpleLongProperty();
    protected final LongProperty upperBound = new SimpleLongProperty();

    protected LocalDateTime minDate;
    protected LocalDateTime maxDate;

    @Override
    protected Object autoRange(double length) {
        if(isAutoRanging()) {
            return new LocalDateTime[] { minDate, maxDate };
        } else {
            return getRange();
        }
    }

    @Override
    protected void setRange(Object range, boolean animate) {
        LocalDateTime[] dateTimesRange = (LocalDateTime[]) range;

        this.minDate = dateTimesRange[0];
        this.maxDate = dateTimesRange[1];
    }

    @Override
    protected Object getRange() {
        return new LocalDateTime[] { minDate, maxDate };
    }

    @Override
    public double getZeroPosition() {
        return 0;
    }

    @Override
    public double getDisplayPosition(LocalDateTime value) {
        final double axisLength = getSide().isHorizontal() ? getWidth() : getHeight();
        final double datesDiff = toNumericValue(this.maxDate) - toNumericValue(this.minDate);

        return 0;
    }

    @Override
    public LocalDateTime getValueForDisplay(double displayPosition) {

        return null;
    }

    @Override
    public boolean isValueOnAxis(LocalDateTime value) {
        if(value == null) return false;
        else {
            final double valueX = toNumericValue(value);
            final double minX = toNumericValue(this.minDate);
            final double maxX = toNumericValue(this.maxDate);

            return valueX >= minX && valueX < maxX;
        }
    }

    @Override
    public double toNumericValue(LocalDateTime value) {
        return value.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
    }

    @Override
    public LocalDateTime toRealValue(double value) {
        final long timestamp = new Double(value).longValue();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    @Override
    protected List<LocalDateTime> calculateTickValues(double length, Object range) {
        return null;
    }

    @Override
    protected String getTickMarkLabel(LocalDateTime value) {
        return null;
    }
}
