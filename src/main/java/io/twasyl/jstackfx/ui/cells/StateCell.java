package io.twasyl.jstackfx.ui.cells;

import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import io.twasyl.jstackfx.beans.ThreadInformation;
import javafx.scene.control.TableCell;

import java.util.HashMap;
import java.util.Map;

/**
 * Cell displaying an {@link OctIcon} according a {@link java.lang.Thread.State}.
 *
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
 */
public class StateCell extends TableCell<ThreadInformation, Thread.State> {

    private static final Map<Thread.State, OctIcon> ICONS = new HashMap<>();

    static {
        ICONS.put(Thread.State.NEW, OctIcon.PLUS);
        ICONS.put(Thread.State.RUNNABLE, OctIcon.SYNC);
        ICONS.put(Thread.State.WAITING, OctIcon.CLOCK);
        ICONS.put(Thread.State.TIMED_WAITING, OctIcon.CLOCK);
        ICONS.put(Thread.State.BLOCKED, OctIcon.STOP);
        ICONS.put(Thread.State.TERMINATED, OctIcon.CHECK);
    }

    @Override
    protected void updateItem(Thread.State item, boolean empty) {
        if (!empty && item != null) {
            final OctIcon icon = ICONS.get(item);

            if(icon != null) {
                final OctIconView displayedIcon = new OctIconView(icon);
                displayedIcon.setGlyphSize(20);
                this.setGraphic(displayedIcon);
            } else {
                this.setGraphic(null);
            }
        } else {
            setGraphic(null);
        }
    }


}
