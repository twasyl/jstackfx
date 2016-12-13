package io.twasyl.jstackfx.beans;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.*;

/**
 * A thread element is a part in the thread dump that gives information about a thread. It typically has a number,
 * various IDs and priorities, a name, a state among others.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class ThreadElement {
    final static Font bold = Font.font("Helvetica", FontWeight.BOLD, 12);
    final static Font normal = Font.font("Helvetica", FontWeight.NORMAL, 12);
    final static Font code = Font.font("Courier New", FontWeight.NORMAL, 12);

    protected final ObjectProperty<Dump> dump = new SimpleObjectProperty<>();
    protected final StringProperty name = new SimpleStringProperty();
    protected final LongProperty number = new SimpleLongProperty();
    protected final ObjectProperty<Thread.State> state = new SimpleObjectProperty<>();
    protected final StringProperty callingStack = new SimpleStringProperty();
    protected final IntegerProperty priority = new SimpleIntegerProperty();
    protected final IntegerProperty osPriority = new SimpleIntegerProperty();
    protected final StringProperty threadId = new SimpleStringProperty();
    protected final SetProperty<ThreadReference> lockedSynchronizers = new SimpleSetProperty<>(FXCollections.observableSet());
    protected final SetProperty<ThreadReference> holdingLocks = new SimpleSetProperty<>(FXCollections.observableSet());
    protected final SetProperty<ThreadReference> waitingToLock = new SimpleSetProperty<>(FXCollections.observableSet());
    protected final SetProperty<ThreadReference> parkingReasons = new SimpleSetProperty<>(FXCollections.observableSet());
    protected final StringProperty source = new SimpleStringProperty();

    public ObjectProperty<Dump> dumpProperty() {
        return dump;
    }

    public Dump getDump() {
        return dump.get();
    }

    public void setDump(Dump dump) {
        this.dump.set(dump);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public LongProperty numberProperty() {
        return number;
    }

    public long getNumber() {
        return number.get();
    }

    public void setNumber(long number) {
        this.number.set(number);
    }

    public ObjectProperty<Thread.State> stateProperty() {
        return state;
    }

    public Thread.State getState() {
        return state.get();
    }

    public void setState(Thread.State state) {
        this.state.set(state);
    }

    public StringProperty callingStackProperty() {
        return callingStack;
    }

    public String getCallingStack() {
        return callingStack.get();
    }

    public void setCallingStack(String callingStack) {
        this.callingStack.set(callingStack);
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public int getPriority() {
        return priority.get();
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    public IntegerProperty osPriorityProperty() {
        return osPriority;
    }

    public int getOsPriority() {
        return osPriority.get();
    }

    public void setOsPriority(int osPriority) {
        this.osPriority.set(osPriority);
    }

    public StringProperty threadIdProperty() {
        return threadId;
    }

    public String getThreadId() {
        return threadId.get();
    }

    public void setThreadId(String threadId) {
        this.threadId.set(threadId);
    }

    public SetProperty<ThreadReference> lockedSynchronizersProperty() {
        return lockedSynchronizers;
    }

    public ObservableSet<ThreadReference> getLockedSynchronizers() {
        return lockedSynchronizers.get();
    }

    public void setLockedSynchronizers(ObservableSet<ThreadReference> lockedSynchronizers) {
        this.lockedSynchronizers.set(lockedSynchronizers);
    }

    public SetProperty<ThreadReference> holdingLocksProperty() {
        return holdingLocks;
    }

    public ObservableSet<ThreadReference> getHoldingLocks() {
        return holdingLocks.get();
    }

    public void setHoldingLocks(ObservableSet<ThreadReference> holdingLocks) {
        this.holdingLocks.set(holdingLocks);
    }

    public SetProperty<ThreadReference> waitingToLockProperty() {
        return waitingToLock;
    }

    public ObservableSet<ThreadReference> getWaitingToLock() {
        return waitingToLock.get();
    }

    public void setWaitingToLock(ObservableSet<ThreadReference> waitingToLock) {
        this.waitingToLock.set(waitingToLock);
    }

    public SetProperty<ThreadReference> parkingReasonsProperty() {
        return parkingReasons;
    }

    public ObservableSet<ThreadReference> getParkingReasons() {
        return parkingReasons.get();
    }

    public void setParkingReasons(ObservableSet<ThreadReference> parkingReasons) {
        this.parkingReasons.set(parkingReasons);
    }

    public String getSource() {
        return source.get();
    }

    public StringProperty sourceProperty() {
        return source;
    }

    public void setSource(String source) {
        this.source.set(source);
    }

    /**
     * Get threads in the given {@link Dump dump} that are blocking this thread.
     *
     * @return A never {@code null} collection of threads blocking this instance.
     */
    public Set<ThreadElement> getBlockingThreads() {
        final Set<ThreadElement> blockingThreads = new HashSet<>();

        if (!getDump().getElements().isEmpty()) {
            for (final ThreadElement thread : getDump().getElements()) {
                if (!Objects.equals(thread, this)) {
                    final long numberOfLocks = thread.getHoldingLocks()
                            .stream()
                            .filter(this.getWaitingToLock()::contains)
                            .count();

                    if (numberOfLocks > 0) {
                        blockingThreads.add(thread);
                    }
                }
            }
        }

        return blockingThreads;
    }

    /**
     * Get threads in the given {@link Dump dump} that are blocked by this thread.
     *
     * @return A never {@code null} collection of threads blocked by this instance.
     */
    public Set<ThreadElement> getBlockedThreads() {
        final Set<ThreadElement> blockedThreads = new HashSet<>();

        if (!getDump().getElements().isEmpty()) {
            for (final ThreadElement thread : getDump().getElements()) {
                if (!Objects.equals(thread, this)) {
                    final long numberOfLocks = thread.getWaitingToLock()
                            .stream()
                            .filter(this.getHoldingLocks()::contains)
                            .count();

                    if (numberOfLocks > 0) {
                        blockedThreads.add(thread);
                    }
                }
            }
        }

        return blockedThreads;
    }

    public List<Text> asText() {
        final List<Text> texts = new ArrayList<>();

        appendThreadNumberText(texts);
        appendThreadName(texts);
        appendThreadId(texts);

        Text text;

        text = new Text("Priority:");
        text.setFont(bold);
        texts.add(text);

        text = new Text(" " + this.getPriority() + " ");
        text.setFont(normal);
        texts.add(text);

        text = new Text("OS priority:");
        text.setFont(bold);
        texts.add(text);

        text = new Text(" " + this.getOsPriority() + "\n\n");
        text.setFont(normal);
        texts.add(text);

        this.appendThreadReferences(texts, "Parking to wait for:", this.parkingReasons);
        this.appendThreadReferences(texts, "Locked synchronizers:", this.lockedSynchronizers);
        this.appendThreadReferences(texts, "Wait to acquire locks:", this.waitingToLock);
        this.appendThreadReferences(texts, "Holding locks on:", this.holdingLocks);

        if (this.callingStack.isNotNull().and(this.callingStack.isNotEmpty()).get()) {
            text = new Text("Calling stack:\n\n");
            text.setFont(bold);
            texts.add(text);

            text = new Text(this.getCallingStack());
            text.setFont(code);
            defineContextMenu(text);
            texts.add(text);
        }
        return texts;
    }

    protected void appendThreadNumberText(List<Text> texts) {
        Text text = new Text("#");
        text.setFont(bold);
        texts.add(text);

        text = new Text("" + this.getNumber());
        text.setFont(bold);
        defineContextMenu(text);
        texts.add(text);
    }

    protected void appendThreadName(List<Text> texts) {
        Text text = new Text(" ");
        text.setFont(normal);
        texts.add(text);

        text = new Text(this.getName());
        text.setFont(normal);
        defineContextMenu(text);
        texts.add(text);

        text = new Text(" ");
        text.setFont(normal);
        texts.add(text);
    }

    protected void appendThreadId(List<Text> texts) {
        Text text = new Text("(");
        text.setFont(normal);
        texts.add(text);

        text = new Text(this.getThreadId());
        text.setFont(normal);
        defineContextMenu(text);
        texts.add(text);

        text = new Text(")\n\n");
        text.setFont(normal);
        texts.add(text);
    }

    protected void appendThreadReferences(final List<Text> texts, final String label, final Collection<ThreadReference> references) {
        if (!references.isEmpty()) {
            Text text = new Text(label + "\n\n");
            text.setFont(bold);
            texts.add(text);

            for (final ThreadReference reference : references) {
                text = new Text(reference.getThreadId());
                text.setFont(normal);
                defineContextMenu(text);
                texts.add(text);

                text = new Text(" (");
                text.setFont(normal);
                texts.add(text);

                text = new Text(reference.getClassName());
                text.setFont(code);
                defineContextMenu(text);
                texts.add(text);

                text = new Text(")\n");
                text.setFont(normal);
                texts.add(text);
            }

            text = new Text("\n");
            text.setFont(normal);
            texts.add(text);
        }
    }

    protected void defineContextMenu(final Text text) {
        final MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(event -> {
            Clipboard.getSystemClipboard().setContent(Collections.singletonMap(DataFormat.PLAIN_TEXT, text.getText()));
        });
        final ContextMenu menu = new ContextMenu(copy);

        text.setOnContextMenuRequested(event -> {
            menu.show(text, event.getScreenX(), event.getScreenY());
        });
    }
}
