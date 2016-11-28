package io.twasyl.jstackfx.beans;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * An implementation of {@link Dump} for thread dumps realized in memory and which results haven't been stored within
 * a file.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class InMemoryDump extends Dump {
    private final ListProperty<String> lines = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ListProperty<String> linesProperty() { return lines; }
    public ObservableList<String> getLines() { return lines.get(); }
    public void setLines(ObservableList<String> lines) { this.lines.set(lines); }
}
