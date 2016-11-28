package io.twasyl.jstackfx.beans;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;

/**
 * An implementation of {@link Dump} for thread dumps stored within files.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class FileDump extends Dump {
    private ObjectProperty<File> file = new SimpleObjectProperty<>();

    public ObjectProperty<File> fileProperty() { return file; }
    public File getFile() { return file.get(); }
    public void setFile(File file) { this.file.set(file); }
}
