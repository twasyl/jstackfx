package io.twasyl.jstackfx.ui.cells;

import io.twasyl.jstackfx.beans.ThreadElement;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Factory used to create {@link StateCell}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class StateCellFactory implements Callback<TableColumn<ThreadElement, Thread.State>, TableCell<ThreadElement, Thread.State>> {

    @Override
    public TableCell<ThreadElement, Thread.State> call(TableColumn<ThreadElement, Thread.State> column) {
        final StateCell cell = new StateCell();

        return cell;
    }
}
