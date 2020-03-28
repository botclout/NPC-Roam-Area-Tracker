import org.osbot.rs07.canvas.paint.Painter;
import org.osbot.rs07.input.mouse.BotMouseListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.event.MouseEvent;
import java.util.function.Consumer;

@ScriptManifest(logo = "", name = "Monster Aggro Areas", version = 0, author = "camaro", info = "")
public class Main extends Script {

    private Runnable positionRetriever;

    private Painter areaPainter;

    private Consumer<MouseEvent> mouseEventConsumer;

    BotMouseListener listener = new BotMouseListener() {
        @Override
        public void checkMouseEvent(MouseEvent mouseEvent) {
            mouseEventConsumer.accept(mouseEvent);
        }
    };

    @Override
    public void onStart() {
        NPCPositionTracker positionRetriever = new NPCPositionTracker(getBot());
        MouseEventConsumer mouseEventConsumer = new MouseEventConsumer(getBot());
        AreaPainter areaPainter = new AreaPainter(getBot(), positionRetriever);
        this.positionRetriever = positionRetriever;
        this.mouseEventConsumer = mouseEventConsumer;
        this.areaPainter = areaPainter;
        positionRetriever.addObserver(areaPainter);
        mouseEventConsumer.addObserver(areaPainter);
        getBot().addMouseListener(listener);
        getBot().addPainter(areaPainter);
    }

    @Override
    public int onLoop() {
        positionRetriever.run();
        return 1000;
    }

    @Override
    public void onExit() {
        getBot().removeMouseListener(listener);
        getBot().removePainter(areaPainter);
    }

}
