package io.twasyl.jstackfx.ui.cells;

import io.twasyl.jstackfx.beans.ThreadElement;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class ThreadElementRowFactory implements Callback<TableView<ThreadElement>, TableRow<ThreadElement>> {

    @Override
    public TableRow<ThreadElement> call(TableView<ThreadElement> param) {
        final ThreadElementRow row = new ThreadElementRow();

        return row;
    }
}
