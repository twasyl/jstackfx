package io.twasyl.jstackfx;

import io.twasyl.jstackfx.controllers.JStackFXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
 */
public class JStackFX extends Application {
    protected static final String FILE_PARAMETER = "file";

    private File fileToOpenAtStartup = null;

    @Override
    public void init() throws Exception {
        final Map<String, String> parameters = getParameters().getNamed();

        if (parameters.containsKey(FILE_PARAMETER)) {
            this.fileToOpenAtStartup = new File(parameters.get(FILE_PARAMETER));
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(JStackFX.class.getResource("/io/twasyl/jstackfx/fxml/jstackfx.fxml"));
        final Parent root = loader.load();

        if (this.fileToOpenAtStartup != null) {
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
