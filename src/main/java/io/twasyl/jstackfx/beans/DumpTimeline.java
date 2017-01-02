package io.twasyl.jstackfx.beans;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX @@NEXT-VERSION@@
 */
public class DumpTimeline {

    private final ListProperty<Dump> dumps = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ObservableList<Dump> getDumps() {
        return dumps.get();
    }

    public ListProperty<Dump> dumpsProperty() {
        return dumps;
    }

    public void setDumps(ObservableList<Dump> dumps) {
        this.dumps.set(dumps);
    }

    public List<ThreadElement> findThreads(final String threadId) {
        final List<ThreadElement> threads = new ArrayList<>();

        this.dumps.forEach(dump -> {
            dump.getElements().forEach(element -> {
                if (threadId.equals(element.getThreadId())) {
                    threads.add(element);
                }
            });
        });

        return threads;
    }
}
