package redstonedev.singularity;

import net.minecraft.core.GlobalPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detector {
    public static List<GlobalPos> detect(List<GlobalPos> positions) {
        Map<GlobalPos, Integer> items = new HashMap<>();

        root:
        for (GlobalPos pos : positions) {
            if (items.containsKey(pos)) continue;

            for (GlobalPos p : items.keySet()) {
                if (isInRange(pos, p, Singularity.CONFIG.generalOptions.singularityRadius)) {
                    items.put(p, items.get(p) + 1);
                    continue root;
                }
            }

            items.put(pos, 1);
        }

        return items.keySet().stream().filter(val -> items.get(val) >= Singularity.CONFIG.generalOptions.minimumSize).toList();
    }

    public static boolean isInRange(GlobalPos root, GlobalPos point, double range) {
        double distSqr = Math.pow(point.pos().getX() - root.pos().getX(), 2) +
                Math.pow(point.pos().getY() - root.pos().getY(), 2) +
                Math.pow(point.pos().getZ() - root.pos().getZ(), 2);

        double dist = Math.sqrt(distSqr);

        return dist <= range;
    }
}
