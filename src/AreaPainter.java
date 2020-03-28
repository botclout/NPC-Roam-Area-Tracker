import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.canvas.paint.Painter;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class AreaPainter implements Observer, Painter {

    private final Bot bot;

    private final Function<Integer, Set<Position>> positionSupplier;

    private volatile int index = -1;

    private volatile Area confirmedAreaToPaint;

    public AreaPainter(Bot bot, Function<Integer, Set<Position>> positionSupplier) {
        this.bot = bot;
        this.positionSupplier = positionSupplier;
    }

    @Override
    public synchronized void onPaint(Graphics2D g) {
        if (index == -1) return;
        if (confirmedAreaToPaint != null) {
            g.setColor(Color.GREEN);
            for (Position p : confirmedAreaToPaint.getPositions()) {
                Polygon polygon = p.getPolygon(bot);
                g.drawPolygon(polygon);
            }
        }
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        if (o instanceof NPCPositionTracker) {
            if ((Integer) arg == index) {
                Set<Position> positions = positionSupplier.apply(index);
                generateAreas(positions);
            }
        } else if (o instanceof MouseEventConsumer) {
            if ((Integer) arg != this.index) {
                index = (Integer) arg;
                if (index != -1) {
                    Set<Position> positions = positionSupplier.apply(index);
                    generateAreas(positions);
                }
            }
        }
    }

    private synchronized void generateAreas(Set<Position> positions) {
        Optional<Integer> maxX = positions.stream().map(Position::getX).max(Comparator.naturalOrder());
        Optional<Integer> minX = positions.stream().map(Position::getX).min(Comparator.naturalOrder());
        Optional<Integer> maxY = positions.stream().map(Position::getY).max(Comparator.naturalOrder());
        Optional<Integer> minY = positions.stream().map(Position::getY).min(Comparator.naturalOrder());
        Optional<Integer> z = positions.stream().findAny().map(Position::getZ);
        if (maxX.isPresent() && minX.isPresent() && maxY.isPresent() && minY.isPresent() && z.isPresent()) {
            confirmedAreaToPaint = new Area(minX.get(), maxY.get(), maxX.get(), minY.get()).setPlane(z.get());
        } else {
            confirmedAreaToPaint = null;
        }
    }

}
