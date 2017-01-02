package io.twasyl.jstackfx.ui;

import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;

/**
 * Utility class providing methods for working with {@link Tooltip}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX @@NEXT-VERSION@@
 */
public class TooltipUtils {

    public static void addTooltipToData(final PieChart.Data blockedThreads, final String label) {
        addTooltipToData(blockedThreads, new SimpleStringProperty(label));
    }

    public static void addTooltipToData(final PieChart.Data data, final StringProperty label) {
        data.nodeProperty().addListener((value, oldNode, newNode) -> {
            if (newNode != null) {
                Tooltip.install(newNode, createNumberedTooltip(label, data.pieValueProperty()));
            }
        });
    }

    public static Tooltip createNumberedTooltip(final StringProperty label, final DoubleProperty value) {
        final StringExpression text = label.concat(": ").concat(value.asString("%.0f"));
        final Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(text);
        return tooltip;
    }
}
