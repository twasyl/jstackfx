package io.twasyl.jstackfx.ui.cells;

import com.sun.javafx.css.PseudoClassState;
import io.twasyl.jstackfx.beans.ThreadElement;
import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a {@link ThreadElement} within a {@link javafx.scene.control.TableView} of elements.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class ThreadElementRow extends TableRow<ThreadElement> {

    private static final Map<Thread.State, PseudoClass> PSEUDO_CLASS_STATES = new HashMap<>();

    static {
        PSEUDO_CLASS_STATES.put(Thread.State.NEW, PseudoClassState.getPseudoClass("new"));
        PSEUDO_CLASS_STATES.put(Thread.State.RUNNABLE, PseudoClassState.getPseudoClass("runnable"));
        PSEUDO_CLASS_STATES.put(Thread.State.WAITING, PseudoClassState.getPseudoClass("waiting"));
        PSEUDO_CLASS_STATES.put(Thread.State.TIMED_WAITING, PseudoClassState.getPseudoClass("timed-waiting"));
        PSEUDO_CLASS_STATES.put(Thread.State.BLOCKED, PseudoClassState.getPseudoClass("blocked"));
        PSEUDO_CLASS_STATES.put(Thread.State.TERMINATED, PseudoClassState.getPseudoClass("terminated"));
    }

    @Override
    protected void updateItem(ThreadElement item, boolean empty) {
        super.updateItem(item, empty);

        PSEUDO_CLASS_STATES.forEach((sate, pseudoClass) -> {
            this.pseudoClassStateChanged(pseudoClass, false);
        });

        if(!empty && item != null) {
            this.pseudoClassStateChanged(PSEUDO_CLASS_STATES.get(item.getState()), true);
        }
    }
}
