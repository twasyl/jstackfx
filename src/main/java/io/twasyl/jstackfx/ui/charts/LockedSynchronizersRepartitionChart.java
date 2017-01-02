package io.twasyl.jstackfx.ui.charts;

import io.twasyl.jstackfx.beans.Dump;
import io.twasyl.jstackfx.beans.ThreadElement;
import io.twasyl.jstackfx.beans.ThreadReference;
import io.twasyl.jstackfx.ui.TooltipUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.PieChart;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link PieChart} showing the most locked synchronizers in a {@link Dump}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX @@NEXT-VERSION@@
 */
public class LockedSynchronizersRepartitionChart extends PieChart {
    private final ObjectProperty<Dump> dump = new SimpleObjectProperty<>(null);

    public LockedSynchronizersRepartitionChart() {
        this.initializeDumpProperty();
        this.setTitle("Locked synchronizers repartition");
        this.setLabelsVisible(false);
    }

    /**
     * Initialize the {@link #dumpProperty()} to react to changes.
     */
    private void initializeDumpProperty() {
        this.dump.addListener((value, oldDump, newDump) -> {
            this.clearSeries();

            if (newDump != null) {
                this.populateChart(newDump);
            }
        });
    }

    private void populateChart(final Dump dump) {
        final Map<String, Data> data = new HashMap<>();

        for (final ThreadElement thread : dump.getElements()) {
            for (final ThreadReference synchronizer : thread.getLockedSynchronizers()) {
                if (!data.containsKey(synchronizer.getClassName())) {
                    final Data synchronizerData = new PieChart.Data(synchronizer.getClassName(), 0);
                    TooltipUtils.addTooltipToData(synchronizerData, synchronizerData.nameProperty());
                    data.put(synchronizer.getClassName(), synchronizerData);
                }

                final PieChart.Data synchronizerData = data.get(synchronizer.getClassName());
                synchronizerData.setPieValue(synchronizerData.getPieValue() + 1);
            }
        }
        this.getData().clear();
        this.getData().addAll(data.values());
    }

    /**
     * Removes all series of the chart.
     */
    private void clearSeries() {
        this.getData().clear();
    }

    public ObjectProperty<Dump> dumpProperty() { return this.dump; }
    public Dump getDump() { return dump.get(); }
    public void setDump(Dump dump) { this.dump.set(dump); }
}
