package io.twasyl.jstackfx.controllers;

import io.twasyl.jstackfx.beans.Dump;
import io.twasyl.jstackfx.beans.LockedSynchronizer;
import io.twasyl.jstackfx.beans.ThreadInformation;
import io.twasyl.jstackfx.factory.DumpFactory;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller of the {@code jstack.fxml} file.
 *
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
 */
public class JStackFXController implements Initializable {

    @FXML
    private TableView<ThreadInformation> threadInformations;
    @FXML
    private TextFlow dumpInformations;
    @FXML
    private TextFlow threadInformationsDetails;
    @FXML
    private PieChart threadsRepartition;
    @FXML
    private PieChart mostLockedSynchronizers;

    @FXML
    private void chooseDumpToOpen(final ActionEvent event) {
        try {
            this.chooseDumpToOpen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a file chooser and open the dump file in the UI.
     */
    protected void chooseDumpToOpen() throws IOException {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Open dump file");
        final File fileToOpen = chooser.showOpenDialog(null);
        this.loadDumpFile(fileToOpen);
    }

    /**
     * Loads the dump file and update the UI.
     *
     * @param file The dump file to open.
     */
    public void loadDumpFile(final File file) throws IOException {
        if (file == null) throw new NullPointerException("The file to open is null");
        if (!file.exists()) throw new FileNotFoundException("The file doesn't exist");

        final Dump dump = DumpFactory.read(file);

        this.updateThreadInformationsTable(dump);
        this.updateDumpInformations(dump);
        this.updateThreadRepartionChart(dump);
        this.updateMostLockedSynchronizesChart(dump);
    }

    protected void updateThreadInformationsTable(final Dump dump) {
        this.threadInformations.getItems().clear();
        dump.getElements().forEach(this.threadInformations.getItems()::add);
        this.threadInformations.getSelectionModel().clearSelection();
    }

    protected void updateDumpInformations(final Dump dump) {
        this.dumpInformations.getChildren().clear();
        this.dumpInformations.getChildren().addAll(dump.asText());
        this.dumpInformations.setUserData(dump);
    }

    protected void updateThreadRepartionChart(final Dump dump) {
        final PieChart.Data newThreads = new PieChart.Data(Thread.State.NEW.name(), 0);
        this.addTooltipToData(newThreads, "New threads");

        final PieChart.Data runnableThreads = new PieChart.Data(Thread.State.RUNNABLE.name(), 0);
        this.addTooltipToData(runnableThreads, "Runnable threads");

        final PieChart.Data waitingThreads = new PieChart.Data(Thread.State.WAITING.name() + "/" + Thread.State.TIMED_WAITING.name(), 0);
        this.addTooltipToData(waitingThreads, "Waiting threads");

        final PieChart.Data blockedThreads = new PieChart.Data(Thread.State.BLOCKED.name(), 0);
        this.addTooltipToData(blockedThreads, "Blocked threads");

        final PieChart.Data terminatedThreads = new PieChart.Data(Thread.State.TERMINATED.name(), 0);
        this.addTooltipToData(terminatedThreads, "Terminated threads");

        for (ThreadInformation information : dump.getElements()) {
            if (information.getState() != null) {
                switch (information.getState()) {
                    case NEW:
                        newThreads.setPieValue(newThreads.getPieValue() + 1);
                        break;
                    case RUNNABLE:
                        runnableThreads.setPieValue(runnableThreads.getPieValue() + 1);
                        break;
                    case WAITING:
                    case TIMED_WAITING:
                        waitingThreads.setPieValue(waitingThreads.getPieValue() + 1);
                        break;
                    case BLOCKED:
                        blockedThreads.setPieValue(blockedThreads.getPieValue() + 1);
                        break;
                    case TERMINATED:
                        terminatedThreads.setPieValue(terminatedThreads.getPieValue() + 1);
                        break;
                }
            }
        }

        this.threadsRepartition.setData(FXCollections.observableArrayList(newThreads, runnableThreads, waitingThreads, blockedThreads, terminatedThreads));
    }

    protected void updateMostLockedSynchronizesChart(final Dump dump) {
        final Map<String, PieChart.Data> data = new HashMap<>();

        for (final ThreadInformation threadInformation : dump.getElements()) {
            for (final LockedSynchronizer synchronizer : threadInformation.getLockedSynchronizers()) {
                if (!data.containsKey(synchronizer.getClassName())) {
                    final PieChart.Data synchronizerData = new PieChart.Data(synchronizer.getClassName(), 0);
                    this.addTooltipToData(synchronizerData, synchronizerData.nameProperty());
                    data.put(synchronizer.getClassName(), synchronizerData);
                }

                final PieChart.Data synchronizerData = data.get(synchronizer.getClassName());
                synchronizerData.setPieValue(synchronizerData.getPieValue() + 1);
            }
        }
        this.mostLockedSynchronizers.getData().clear();
        this.mostLockedSynchronizers.getData().addAll(data.values());
    }

    protected void addTooltipToData(final PieChart.Data blockedThreads, final String label) {
        this.addTooltipToData(blockedThreads, new SimpleStringProperty(label));
    }

    protected void addTooltipToData(final PieChart.Data blockedThreads, final StringProperty label) {
        blockedThreads.nodeProperty().addListener((value, oldNode, newNode) -> {
            if (newNode != null) {
                Tooltip.install(newNode, this.createNumberedTooltip(label, blockedThreads.pieValueProperty()));
            }
        });
    }

    protected Tooltip createNumberedTooltip(final StringProperty label, final DoubleProperty value) {
        final StringExpression text = label.concat(": ").concat(value.asString("%.0f"));
        final Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(text);
        return tooltip;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.threadInformations.getSelectionModel().selectedItemProperty().addListener((value, oldItem, newItem) -> {
            this.threadInformationsDetails.getChildren().clear();
            if (newItem != null) {
                this.threadInformationsDetails.getChildren().addAll(newItem.asText());
            }
        });
    }
}
