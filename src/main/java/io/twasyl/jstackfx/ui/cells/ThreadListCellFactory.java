package io.twasyl.jstackfx.ui.cells;

import io.twasyl.jstackfx.beans.ThreadInformation;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.util.Set;
import java.util.StringJoiner;

/**
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
 */
public class ThreadListCellFactory implements Callback<TableColumn<ThreadInformation, Set<ThreadInformation>>, TableCell<ThreadInformation, Set<ThreadInformation>>> {
    @Override
    public TableCell<ThreadInformation, Set<ThreadInformation>> call(TableColumn<ThreadInformation, Set<ThreadInformation>> param) {
        final TableCell<ThreadInformation, Set<ThreadInformation>> cell = new TableCell<ThreadInformation, Set<ThreadInformation>>() {
            @Override
            protected void updateItem(Set<ThreadInformation> item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && !item.isEmpty() && !empty) {
                    final TextFlow threads = new TextFlow();
                    threads.setPrefHeight(20);

                    for (final ThreadInformation thread : item) {
                        final Text threadText = new Text(thread.getName());
                        threadText.getStyleClass().add("text");

                        threadText.setOnMouseClicked(event -> {
                            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1) {
                                this.getTableView().getSelectionModel().select(thread);
                            }
                        });
                        threads.getChildren().add(threadText);
                    }
                    this.setGraphic(threads);
                } else {
                    this.setGraphic(null);
                }
            }
        };

        return cell;
    }
}
