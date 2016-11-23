package io.twasyl.jstackfx.ui.cells;

import com.sun.javafx.css.PseudoClassState;
import io.twasyl.jstackfx.beans.ThreadInformation;
import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thierry Wasylczenko
 * @since SlideshowFX @@NEXT-VERSION@@
 */
public class ThreadInformationRow extends TableRow<ThreadInformation> {

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
    protected void updateItem(ThreadInformation item, boolean empty) {
        PSEUDO_CLASS_STATES.forEach((sate, pseudoClass) -> {
            this.pseudoClassStateChanged(pseudoClass, false);
        });

        if(!empty && item != null) {
            this.pseudoClassStateChanged(PSEUDO_CLASS_STATES.get(item.getState()), true);
        }
    }
}
