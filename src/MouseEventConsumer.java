import org.osbot.rs07.Bot;
import org.osbot.rs07.api.model.Model;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.MethodProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.function.Consumer;

public class MouseEventConsumer extends Observable implements Consumer<MouseEvent> {

    private final MethodProvider api;

    public MouseEventConsumer(Bot bot) {
        api = bot.getMethods();
    }

    @Override
    public void accept(MouseEvent mouseEvent) {
        if (mouseEvent != null && SwingUtilities.isRightMouseButton(mouseEvent) &&
                mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
            for (NPC n : api.getNpcs().getAll()) {
                Model model = n.getModel();
                if (model == null) continue;
                Rectangle area = model.getBoundingBox(n.getGridX(), n.getGridY(), n.getZ());
                if (area == null) continue;
                if (area.contains(mouseEvent.getPoint())) {
                    setChanged();
                    notifyObservers(n.getIndex());
                    return;
                }
            }
            setChanged();
            notifyObservers(-1);
        }
    }

}
