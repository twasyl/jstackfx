package io.twasyl.jstackfx;

import io.twasyl.jstackfx.controllers.JStackFXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

/**
 * Application class of JStackFX.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class JStackFX extends Application {
    protected static final String FILE_PARAMETER = "file";
    protected static final String PID_PARAMETER = "pid";

    private File fileToOpenAtStartup = null;
    private String pidToDumpAtStartup = null;

    @Override
    public void init() throws Exception {
        final Map<String, String> parameters = getParameters().getNamed();

        if (parameters.containsKey(PID_PARAMETER)) {
            this.pidToDumpAtStartup = parameters.get(PID_PARAMETER);
        } else if (parameters.containsKey(FILE_PARAMETER)) {
            this.fileToOpenAtStartup = new File(parameters.get(FILE_PARAMETER));
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(JStackFX.class.getResource("/io/twasyl/jstackfx/fxml/jstackfx.fxml"));
        final Parent root = loader.load();

        if (this.pidToDumpAtStartup != null) {
            final JStackFXController controller = loader.getController();
            try {
                final long pid = Long.parseLong(this.pidToDumpAtStartup);
                controller.dumpPID(pid);
            } catch (Exception e) {
                final Alert errorDialog = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                errorDialog.setTitle("Can create thread dump for process " + this.pidToDumpAtStartup);
                errorDialog.showAndWait();
            }
        } else if (this.fileToOpenAtStartup != null) {
            final JStackFXController controller = loader.getController();
            try {
                controller.loadDumpFile(this.fileToOpenAtStartup);
            } catch (Exception e) {
                final Alert errorDialog = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                errorDialog.setTitle("Can not open dump file");
                errorDialog.showAndWait();
            }
        }

        final Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("JStackFX");
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
