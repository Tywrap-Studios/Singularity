package redstonedev.singularity;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detector {
    public static List<BlockPos> detect(List<BlockPos> positions) {
        Map<BlockPos, Integer> items = new HashMap<>();

        root:
        for (BlockPos pos : positions) {
            if (items.containsKey(pos)) continue;

            for (BlockPos p : items.keySet()) {
                if (isInRange(pos, p, Singularity.CONFIG.generalOptions.singularityRadius)) {
                    items.put(p, items.get(p) + 1);
                    continue root;
                }
            }

            items.put(pos, 1);
        }

        return items.keySet().stream().filter(val -> items.get(val) >= Singularity.CONFIG.generalOptions.minimumSize).toList();
    }

    private static boolean isInRange(BlockPos root, BlockPos point, double range) {
        double distSqr = Math.pow(point.getX() - root.getX(), 2) +
                Math.pow(point.getY() - root.getY(), 2) +
                Math.pow(point.getZ() - root.getZ(), 2);

        double dist = Math.sqrt(distSqr);

        return dist <= range;
    }
}
