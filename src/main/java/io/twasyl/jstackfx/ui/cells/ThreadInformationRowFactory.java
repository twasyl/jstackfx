package io.twasyl.jstackfx.ui.cells;

import io.twasyl.jstackfx.beans.ThreadInformation;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
 */
public class ThreadInformationRowFactory implements Callback<TableView<ThreadInformation>, TableRow<ThreadInformation>> {

    @Override
    public TableRow<ThreadInformation> call(TableView<ThreadInformation> param) {
        final ThreadInformationRow row = new ThreadInformationRow();

        return row;
    }
}
