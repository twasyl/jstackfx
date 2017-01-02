package io.twasyl.jstackfx.ui.charts;

import io.twasyl.jstackfx.beans.DumpTimeline;
import io.twasyl.jstackfx.factory.DumpFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX @@NEXT-VERSION@@
 */
public class StateRepartitionTimelineChartTest extends Application {
    private static File TIMELINE_01 = new File("src/test/resources/timeline_01.txt");
    private static File TIMELINE_02 = new File("src/test/resources/timeline_02.txt");

    @Override
    public void start(Stage primaryStage) throws Exception {
        final DumpTimeline timeline = new DumpTimeline();
        timeline.getDumps().addAll(
                DumpFactory.read(TIMELINE_01),
                DumpFactory.read(TIMELINE_02));

        final StateRepartitionTimelineChart chart = new StateRepartitionTimelineChart();
        chart.setDumpTimeline(timeline);

        final Scene scene = new Scene(chart);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
