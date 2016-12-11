package io.twasyl.jstackfx.ui.cells;

import io.twasyl.jstackfx.beans.ThreadElement;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.util.Iterator;
import java.util.Set;

/**
 * Class responsible of creating cells that lists a collection of {@link ThreadElement}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class ThreadListCellFactory implements Callback<TableColumn<ThreadElement, Set<ThreadElement>>, TableCell<ThreadElement, Set<ThreadElement>>> {
    @Override
    public TableCell<ThreadElement, Set<ThreadElement>> call(TableColumn<ThreadElement, Set<ThreadElement>> param) {
        final TableCell<ThreadElement, Set<ThreadElement>> cell = new TableCell<ThreadElement, Set<ThreadElement>>() {
            @Override
            protected void updateItem(Set<ThreadElement> item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && !item.isEmpty() && !empty) {
                    final TextFlow threadList = buildThreadListGraphic(this, item);

                    if (this.prefHeightProperty().isBound()) {
                        this.prefHeightProperty().unbind();
                    }

                    this.prefHeightProperty().bind(threadList.heightProperty());
                    threadList.prefWidthProperty().bind(this.widthProperty().subtract(-5));

                    this.setGraphic(threadList);
                    this.layout();
                } else {
                    this.setGraphic(null);
                }
            }
        };

        return cell;
    }

    protected TextFlow buildThreadListGraphic(final TableCell<ThreadElement, Set<ThreadElement>> cell, final Set<ThreadElement> threads) {
        final TextFlow threadsGraphic = new TextFlow();
        threadsGraphic.setPrefHeight(20);

        final Iterator<ThreadElement> threadIterator = threads.iterator();
        while (threadIterator.hasNext()) {
            final ThreadElement thread = threadIterator.next();

            threadsGraphic.getChildren().add(buildThreadLink(cell, thread));

            if (threadIterator.hasNext()) {
                threadsGraphic.getChildren().add(buildThreadSeparator());
            }
        }
        return threadsGraphic;
    }

    protected Text buildThreadLink(final TableCell<ThreadElement, Set<ThreadElement>> cell, final ThreadElement thread) {
        final Text threadText = new Text(thread.getName());
        threadText.getStyleClass().add("text");

        threadText.setOnMouseClicked(ThreadListCellFactory.this.buildMouseClickedEventHandler(cell, thread));
        threadText.setOnMouseEntered(event -> threadText.setCursor(Cursor.HAND));

        return threadText;
    }

    protected Text buildThreadSeparator() {
        final Text separator = new Text(", ");
        separator.getStyleClass().add("text");
        return separator;
    }

    protected EventHandler<MouseEvent> buildMouseClickedEventHandler(final TableCell<ThreadElement, Set<ThreadElement>> cell, final ThreadElement thread) {
        final EventHandler<MouseEvent> handler = event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1) {
                cell.getTableView().getSelectionModel().select(thread);
            }
        };

        return handler;
    }
}
