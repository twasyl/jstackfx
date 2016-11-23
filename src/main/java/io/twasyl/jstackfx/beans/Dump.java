package io.twasyl.jstackfx.beans;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
 */
public class Dump {
    protected final ObjectProperty<LocalDateTime> generationDateTime = new SimpleObjectProperty<>();
    protected final StringProperty description = new SimpleStringProperty();
    protected final ListProperty<ThreadInformation> elements = new SimpleListProperty<>(FXCollections.observableArrayList());
    protected final IntegerProperty numberOfJNIRefs = new SimpleIntegerProperty(0);

    public ObjectProperty<LocalDateTime> generationDateTimeProperty() { return generationDateTime; }
    public LocalDateTime getGenerationDateTime() { return generationDateTime.get(); }
    public void setGenerationDateTime(LocalDateTime generationDateTime) { this.generationDateTime.set(generationDateTime); }

    public StringProperty descriptionProperty() { return description; }
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

    public ListProperty<ThreadInformation> elementsProperty() { return elements; }
    public ObservableList<ThreadInformation> getElements() { return elements.get(); }
    public void setElements(ObservableList<ThreadInformation> elements) { this.elements.set(elements); }

    public IntegerProperty numberOfJNIRefsProperty() { return numberOfJNIRefs; }
    public int getNumberOfJNIRefs() { return numberOfJNIRefs.get(); }
    public void setNumberOfJNIRefs(int numberOfJNIRefs) { this.numberOfJNIRefs.set(numberOfJNIRefs); }

    /**
     * Count the number of threads that haven't a stack.
     * @return The number of threads without a stack.
     */
    public long countThreadsWithoutStack() {
        return this.getElements().stream().filter(thread -> {
            final String callingStack = thread.getCallingStack();
            return callingStack == null || callingStack.isEmpty();
        }).count();
    }

    /**
     * Counts the number of threads this dump contains which are in the given state.
     * @param state The state to match.
     * @return The number of threads in the provided state this dump contains.
     */
    public long countNumberOfThreads(final Thread.State state) {
        return this.getElements().stream()
                .filter(thread -> thread.getState() == state)
                .count();
    }

    public List<Text> asText() {
        final List<Text> texts = new ArrayList<>();

        final Font bold = Font.font("Helvetica", FontWeight.BOLD, 12);
        final Font normal = Font.font("Helvetica", FontWeight.NORMAL, 12);
        final Font code = Font.font("Courier New", FontWeight.NORMAL, 12);

        Text text = new Text("Generated at " + this.getGenerationDateTime().toString());
        text.setFont(bold);
        texts.add(text);

        text = new Text("\n" + this.getDescription() + "\n\n");
        text.setFont(normal);
        texts.add(text);

        text = new Text("# of threads:");
        text.setFont(bold);
        texts.add(text);

        text = new Text(" " + this.getElements().size() + "\n");
        text.setFont(normal);
        texts.add(text);

        text = new Text("# of threads w/o stack:");
        text.setFont(bold);
        texts.add(text);

        text = new Text(" " + this.countThreadsWithoutStack() + "\n");
        text.setFont(normal);
        texts.add(text);

        text = new Text("# of JNI references:");
        text.setFont(bold);
        texts.add(text);

        text = new Text(" " + this.getNumberOfJNIRefs() + "\n");
        text.setFont(normal);
        texts.add(text);

        return texts;
    }
}
