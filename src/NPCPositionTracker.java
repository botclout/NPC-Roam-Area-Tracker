import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.MethodProvider;

import java.util.*;
import java.util.function.Function;

public class NPCPositionTracker extends Observable
        implements Function<Integer, Set<Position>>, Runnable {

    private final Map<Integer, Set<Position>> monsterPositionMap = new HashMap<>();

    private final MethodProvider api;

    public NPCPositionTracker(Bot bot) {
        api = bot.getMethods();
    }

    @Override
    public void run() {
        for (NPC n : api.getNpcs().getAll()) {
            int i = n.getIndex();
            Position pos = n.getPosition();
            if (!monsterPositionMap.containsKey(i)) {
                Set<Position> positionList = new HashSet<Position>() {
                    {
                        add(pos);
                    }
                };
                monsterPositionMap.put(i, positionList);
                setChanged();
                notifyObservers(i);
            } else {
                Set<Position> positionList = monsterPositionMap.get(i);
                if (positionList.add(pos)) {
                    setChanged();
                    notifyObservers(i);
                }
            }
        }
    }

    @Override
    public Set<Position> apply(Integer integer) {
        return monsterPositionMap.get(integer);
    }
}
