package io.twasyl.jstackfx.controllers;

import io.twasyl.jstackfx.beans.Dump;
import io.twasyl.jstackfx.beans.InMemoryDump;
import io.twasyl.jstackfx.beans.ThreadElement;
import io.twasyl.jstackfx.beans.ThreadReference;
import io.twasyl.jstackfx.exceptions.DumpException;
import io.twasyl.jstackfx.factory.DumpFactory;
import io.twasyl.jstackfx.ui.SearchField;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller of the {@code jstack.fxml} file.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class JStackFXController implements Initializable {

    private static Logger LOGGER = Logger.getLogger(JStackFXController.class.getName());

    @FXML
    private TableView<ThreadElement> threadElements;
    @FXML
    private TextFlow dumpInformations;
    @FXML
    private TextFlow threadElementDetails;
    @FXML
    private PieChart threadsRepartition;
    @FXML
    private PieChart mostLockedSynchronizers;
    @FXML
    private Button saveDumpButton;
    @FXML
    private SearchField searchField;

    private final ObjectProperty<Dump> dump = new SimpleObjectProperty<>(null);

    @FXML
    private void chooseDumpToOpen(final ActionEvent event) {
        try {
            this.chooseDumpToOpen();
        } catch (Exception e) {
            showError(e);
        }
    }

    @FXML
    private void saveThreadDump(final ActionEvent event) {
        final FileChooser fileSaver = new FileChooser();
        fileSaver.setTitle("Save thread dump");
        final File dumpFile = fileSaver.showSaveDialog(null);

        if (dumpFile != null) {
            try {
                this.writeToDumpFile(dumpFile, this.dump.get());
                this.loadDumpFile(dumpFile);
            } catch (Exception e) {
                showError(e);
            }
        }
    }

    @FXML
    private void makeThreadDump(final ActionEvent event) {
        final TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter the process ID to make the thread dump for:");
        final String userAnswer = dialog.showAndWait().orElse(null);

        if (userAnswer != null && !userAnswer.isEmpty()) {
            final long pid = Long.parseLong(userAnswer);
            try {
                this.dumpPID(pid);
            } catch (DumpException e) {
                showError(e);
            }
        }
    }

    /**
     * Opens a file chooser and open the dump file in the UI.
     */
    protected void chooseDumpToOpen() throws IOException, InstantiationException, IllegalAccessException {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Open dump file");
        final File fileToOpen = chooser.showOpenDialog(null);

        if (fileToOpen != null) {
            this.loadDumpFile(fileToOpen);
        }
    }

    /**
     * Loads the dump file and update the UI.
     *
     * @param file The dump file to open.
     */
    public void loadDumpFile(final File file) throws IOException, IllegalAccessException, InstantiationException {
        if (file == null) throw new NullPointerException("The file to open is null");
        if (!file.exists()) throw new FileNotFoundException("The file doesn't exist");

        final Dump dump = DumpFactory.read(file);

        this.updateUI(dump);
        this.dump.set(dump);
    }

    /**
     * Create a thread dump for the given PID.
     *
     * @param pid The PID of the process to dump.
     * @throws DumpException
     */
    public void dumpPID(final long pid) throws DumpException {
        final Dump dump = DumpFactory.make(pid);
        this.updateUI(dump);
        this.dump.set(dump);
    }

    public void updateUI(final Dump dump) {
        if (dump != null) {
            this.updateSearchField(dump);
            this.updateThreadInformationsTable(dump);
            this.updateDumpInformations(dump);
            this.updateThreadRepartionChart(dump);
            this.updateMostLockedSynchronizesChart(dump);
        }
    }

    protected void writeToDumpFile(final File dumpFile, final Dump dump) throws IOException {
        if (dumpFile == null) throw new NullPointerException("The dump file can not be null");
        if (dump == null) throw new NullPointerException("The dump to save can not be null");
        if (!(dump instanceof InMemoryDump)) throw new IllegalArgumentException("The dump is not an in-memory dump");

        try (final PrintWriter output = new PrintWriter(dumpFile)) {
            ((InMemoryDump) dump).getLines().forEach(output::println);
            output.flush();
        }
    }

    protected void updateSearchField(final Dump dump) {
        this.searchField.setDataSet(dump.getElements());
    }

    protected void updateThreadInformationsTable(final Dump dump) {
        this.threadElements.getSelectionModel().clearSelection();
    }

    protected void updateDumpInformations(final Dump dump) {
        this.dumpInformations.getChildren().clear();
        this.dumpInformations.getChildren().addAll(dump.asText());
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

        for (ThreadElement information : dump.getElements()) {
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

        for (final ThreadElement thread : dump.getElements()) {
            for (final ThreadReference synchronizer : thread.getLockedSynchronizers()) {
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

    /**
     * Show an error dialog to the end user for the provided exception.
     *
     * @param exception The exception that was thrown.
     */
    protected void showError(Exception exception) {
        final Alert error = new Alert(Alert.AlertType.ERROR, exception.getMessage(), ButtonType.OK);
        error.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.threadElements.getSelectionModel().selectedItemProperty().addListener((value, oldItem, newItem) -> {
            this.threadElementDetails.getChildren().clear();
            if (newItem != null) {
                this.threadElementDetails.getChildren().addAll(newItem.asText());
            }
        });

        this.threadElements.itemsProperty().bind(this.searchField.resultsProperty());

        this.dump.addListener((value, oldDump, newDump) -> {
            this.saveDumpButton.setDisable(newDump == null || !(newDump instanceof InMemoryDump));
        });
    }
}
