package io.twasyl.jstackfx.beans;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a thread reference present in a thread dump. A reference is known as a thread ID and a class name.
 * It typically appears as locked synchronizers, locks and so on in a thread dump.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class ThreadReference {
    protected final StringProperty threadId = new SimpleStringProperty();
    protected final StringProperty className = new SimpleStringProperty();

    public StringProperty threadIdProperty() { return threadId; }
    public String getThreadId() { return threadId.get(); }
    public void setThreadId(String threadId) { this.threadId.set(threadId); }

    public StringProperty classNameProperty() { return className; }
    public String getClassName() { return className.get(); }
    public void setClassName(String className) { this.className.set(className); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadReference that = (ThreadReference) o;

        return getThreadId() != null ? getThreadId().equals(that.getThreadId()) : that.getThreadId() == null;
    }

    @Override
    public int hashCode() {
        return getThreadId() != null ? getThreadId().hashCode() : 0;
    }
}
