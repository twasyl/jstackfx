package io.twasyl.jstackfx.ui.charts;

import io.twasyl.jstackfx.beans.Dump;
import io.twasyl.jstackfx.ui.TooltipUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.chart.PieChart;

import java.util.Map;

/**
 * An implementation of {@link PieChart} that shows the by state of all {@link Dump#getElements() elements} of a
 * {@link Dump dump}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX @@NEXT-VERSION@@
 */
public class StateRepartitionChart extends PieChart {
    private final ObjectProperty<Dump> dump = new SimpleObjectProperty<>(null);

    private final Data newThreads = new Data(Thread.State.NEW.name(), 0);
    private final Data runnableThreads = new Data(Thread.State.RUNNABLE.name(), 0);
    private final Data waitingThreads = new Data(Thread.State.WAITING.name() + "/" + Thread.State.TIMED_WAITING.name(), 0);
    private final Data blockedThreads = new Data(Thread.State.BLOCKED.name(), 0);
    private final Data terminatedThreads = new Data(Thread.State.TERMINATED.name(), 0);

    public StateRepartitionChart() {
        this.initializeSeries();
        this.initializeDumpProperty();
        this.setTitle("Thread repartition by state");
        this.setLabelsVisible(false);
    }

    /**
     * Initialize the series used in the chart.
     */
    private void initializeSeries() {
        TooltipUtils.addTooltipToData(newThreads, "New threads");
        TooltipUtils.addTooltipToData(runnableThreads, "Runnable threads");
        TooltipUtils.addTooltipToData(waitingThreads, "Waiting threads");
        TooltipUtils.addTooltipToData(blockedThreads, "Blocked threads");
        TooltipUtils.addTooltipToData(terminatedThreads, "Terminated threads");

        this.setData(FXCollections.observableArrayList(newThreads, runnableThreads, waitingThreads, blockedThreads, terminatedThreads));
    }

    /**
     * Initialize the {@link #dumpProperty()} to react to changes.
     */
    private void initializeDumpProperty() {
        this.dump.addListener((value, oldDump, newDump) -> {
            this.clearSeries();

            if (newDump != null) {
                this.populateSeries(newDump);
            }
        });
    }

    /**
     * Set the value of all series to 0.
     */
    private void clearSeries() {
        this.newThreads.setPieValue(0);
        this.runnableThreads.setPieValue(0);
        this.waitingThreads.setPieValue(0);
        this.blockedThreads.setPieValue(0);
        this.terminatedThreads.setPieValue(0);
    }

    /**
     * Set the value of each serie of the chart according the given {@link Dump}
     *
     * @param dump The dump used to populate the series.
     */
    private void populateSeries(final Dump dump) {
        final Map<Thread.State, Long> statesCounter = dump.countNumberOfThreadsByState();
        if (statesCounter.containsKey(Thread.State.NEW)) {
            newThreads.setPieValue(statesCounter.get(Thread.State.NEW));
        }

        if (statesCounter.containsKey(Thread.State.RUNNABLE)) {
            runnableThreads.setPieValue(statesCounter.get(Thread.State.RUNNABLE));
        }

        if (statesCounter.containsKey(Thread.State.TIMED_WAITING)) {
            waitingThreads.setPieValue(statesCounter.get(Thread.State.TIMED_WAITING));
        }

        if (statesCounter.containsKey(Thread.State.WAITING)) {
            waitingThreads.setPieValue(waitingThreads.getPieValue() + statesCounter.get(Thread.State.WAITING));
        }

        if (statesCounter.containsKey(Thread.State.BLOCKED)) {
            blockedThreads.setPieValue(statesCounter.get(Thread.State.BLOCKED));
        }

        if (statesCounter.containsKey(Thread.State.TERMINATED)) {
            terminatedThreads.setPieValue(statesCounter.get(Thread.State.TERMINATED));
        }
    }

    public ObjectProperty<Dump> dumpProperty() { return this.dump; }
    public Dump getDump() { return dump.get(); }
    public void setDump(Dump dump) { this.dump.set(dump); }
}
