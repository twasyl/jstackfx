package io.twasyl.jstackfx.ui.charts;

import io.twasyl.jstackfx.factory.DumpFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX @@NEXT-VERSION@@
 */
public class StateRepartitionChartTest extends Application {
    private static File DUMP_FILE = new File("src/test/resources/intellij.txt");

    @Override
    public void start(Stage primaryStage) throws Exception {
        final StateRepartitionChart chart = new StateRepartitionChart();
        chart.setDump(DumpFactory.read(DUMP_FILE));

        final Scene scene = new Scene(chart);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
