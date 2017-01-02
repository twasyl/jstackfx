package io.twasyl.jstackfx.controllers;

import io.twasyl.jstackfx.beans.Dump;
import io.twasyl.jstackfx.beans.InMemoryDump;
import io.twasyl.jstackfx.beans.ThreadElement;
import io.twasyl.jstackfx.exceptions.DumpException;
import io.twasyl.jstackfx.factory.DumpFactory;
import io.twasyl.jstackfx.ui.SearchField;
import io.twasyl.jstackfx.ui.charts.LockedSynchronizersRepartitionChart;
import io.twasyl.jstackfx.ui.charts.StateRepartitionChart;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
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
    private TextArea threadElementSource;
    @FXML
    private StateRepartitionChart threadsRepartition;
    @FXML
    private LockedSynchronizersRepartitionChart mostLockedSynchronizers;
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
            this.clearUI();
            this.updateSearchField(dump);
            this.updateThreadInformationsTable(dump);
            this.updateDumpInformations(dump);
            this.threadsRepartition.setDump(dump);
            this.mostLockedSynchronizers.setDump(dump);
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

    protected void clearUI() {
        this.searchField.clear();
        this.threadElementDetails.getChildren().clear();
        this.threadElementSource.setText("");
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
                this.threadElementSource.setText(newItem.getSource());
            }
        });

        this.threadElements.itemsProperty().bind(this.searchField.resultsProperty());

        this.dump.addListener((value, oldDump, newDump) -> {
            this.saveDumpButton.setDisable(newDump == null || !(newDump instanceof InMemoryDump));
        });
    }
}
