package io.twasyl.jstackfx.ui.cells;

import io.twasyl.jstackfx.beans.ThreadInformation;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
 */
public class StateCellFactory implements Callback<TableColumn<ThreadInformation, Thread.State>, TableCell<ThreadInformation, Thread.State>> {

    @Override
    public TableCell<ThreadInformation, Thread.State> call(TableColumn<ThreadInformation, Thread.State> column) {
        final StateCell cell = new StateCell();
        cell.getStyleClass().add("state-cell");

        return cell;
    }
}
