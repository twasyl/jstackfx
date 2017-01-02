package io.twasyl.jstackfx.ui.charts;

import io.twasyl.jstackfx.beans.Dump;
import io.twasyl.jstackfx.beans.DumpTimeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.chart.*;
import javafx.util.StringConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX @@NEXT-VERSION@@
 */
public class StateRepartitionTimelineChart extends LineChart<Number, Number> {
    protected static final DateTimeFormatter DATE_TIME_FORMATTER_OUTPUT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    private final ObjectProperty<DumpTimeline> dumpTimeline = new SimpleObjectProperty<>(null);

    private final Series newThreads = new Series();
    private final Series runnableThreads = new Series();
    private final Series waitingThreads = new Series();
    private final Series blockedThreads = new Series();
    private final Series terminatedThreads = new Series();

    private final NumberAxis xAxis;
    private final NumberAxis yAxis;

    public StateRepartitionTimelineChart() {
        super(new NumberAxis(), new NumberAxis());

        this.xAxis = (NumberAxis) getXAxis();
        this.yAxis = (NumberAxis) getYAxis();

        this.xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if(object == null) return null;
                else {
                    return DATE_TIME_FORMATTER_OUTPUT.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(object.longValue()), ZoneId.systemDefault()));
                }
            }

            @Override
            public Number fromString(String string) {
                if(string == null || string.isEmpty()) return null;
                else {
                    return LocalDateTime.parse(string, DATE_TIME_FORMATTER_OUTPUT).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
                }
            }
        });

        this.initializeDumpTimelineProperty();
        this.initializeSeries();
    }

    /**
     * Initialize the series used in the chart.
     */
    private void initializeSeries() {
        newThreads.setName("New threads");
        runnableThreads.setName("Runnable threads");
        waitingThreads.setName("Waiting threads");
        blockedThreads.setName("Blocked threads");
        terminatedThreads.setName("Terminated threads");

        this.setData(FXCollections.observableArrayList(newThreads, runnableThreads, waitingThreads, blockedThreads, terminatedThreads));
    }

    /**
     * Initialize the {@link #dumpTimelineProperty()} to react to changes.
     */
    private void initializeDumpTimelineProperty() {
        this.dumpTimeline.addListener((value, oldTimeline, newTimeline) -> {
            this.clearSeries();

            if (newTimeline != null) {
                this.populateSeries(newTimeline);
            }
        });
    }

    /**
     * Set the value of all series to 0.
     */
    private void clearSeries() {
        this.newThreads.getData().clear();
        this.runnableThreads.getData().clear();
        this.waitingThreads.getData().clear();
        this.blockedThreads.getData().clear();
        this.terminatedThreads.getData().clear();
    }

    /**
     * Set the value of each serie of the chart according the given {@link Dump}
     *
     * @param dumpTimeline The timeline used to populate the series.
     */
    private void populateSeries(final DumpTimeline dumpTimeline) {
        dumpTimeline.getDumps().forEach(dump -> {
            final Map<Thread.State, Long> counters = dump.countNumberOfThreadsByState();
            final ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(dump.getGenerationDateTime());
            final long generationTimestamp = dump.getGenerationDateTime().toInstant(zoneOffset).toEpochMilli();

            if(counters.containsKey(Thread.State.NEW)) {
                this.newThreads.getData().add(new Data<>(generationTimestamp, counters.get(Thread.State.NEW)));
            }

            if(counters.containsKey(Thread.State.RUNNABLE)) {
                this.runnableThreads.getData().add(new Data<>(generationTimestamp, counters.get(Thread.State.RUNNABLE)));
            }

            Data<Number, Number> waitingData = new Data<>(generationTimestamp, 0l);

            if(counters.containsKey(Thread.State.WAITING)) {
                waitingData.setYValue(counters.get(Thread.State.WAITING));
            }

            if(counters.containsKey(Thread.State.TIMED_WAITING)) {
                waitingData.setYValue(waitingData.getYValue().longValue() + counters.get(Thread.State.TIMED_WAITING));
            }

            if(waitingData.getXValue().longValue() > 0) {
                this.waitingThreads.getData().add(waitingData);
            }

            if(counters.containsKey(Thread.State.BLOCKED)) {
                this.blockedThreads.getData().add(new Data<>(generationTimestamp, counters.get(Thread.State.BLOCKED)));
            }

            if(counters.containsKey(Thread.State.TERMINATED)) {
                this.terminatedThreads.getData().add(new Data<>(generationTimestamp, counters.get(Thread.State.TERMINATED)));
            }
        });
    }

    public ObjectProperty<DumpTimeline> dumpTimelineProperty() { return this.dumpTimeline; }
    public DumpTimeline getDumpTimeline() { return dumpTimeline.get(); }
    public void setDumpTimeline(DumpTimeline dumpTimeline) { this.dumpTimeline.set(dumpTimeline); }
}
