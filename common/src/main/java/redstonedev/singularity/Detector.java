package redstonedev.singularity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Detector {
    public static List<GlobalPos> getAllItems(MinecraftServer server) {
        List<GlobalPos> positions = new ArrayList<>();

        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemEntity) {
                    positions.add(GlobalPos.of(entity.getLevel().dimension(), entity.blockPosition()));
                }
            }
        }

        return positions;
    }

    public static List<ItemEntity> detectSingularities(MinecraftServer server) {
        Util.chat(server, Component.translatable("singularity.chat.detection.start"),
                false);

        Map<GlobalPos, List<ItemEntity>> map = new HashMap<>();
        List<Integer> found = new ArrayList<>();

        for (ServerLevel level : server.getAllLevels()) {
            Iterable<Entity> entities = level.getAllEntities();

            for (Entity entity : entities) {
                if (found.contains(entity.getId()))
                    continue;

                if (entity instanceof ItemEntity) {
                    GlobalPos pos = GlobalPos.of(entity.getLevel().dimension(), entity.blockPosition());
                    List<ItemEntity> tmp = new ArrayList<>();

                    tmp.add((ItemEntity) entity);

                    if (!map.containsKey(pos))
                        map.put(pos, tmp);

                    for (Entity e : entities) {
                        if (e instanceof ItemEntity) {
                            GlobalPos epos = GlobalPos.of(e.getLevel().dimension(), e.blockPosition());

                            if (isInRange(pos, epos,
                                    Singularity.CONFIG.generalOptions.singularityRadius)) {
                                List<ItemEntity> list = map.get(pos);

                                list.add((ItemEntity) e);
                                map.replace(pos, list);
                                found.add(e.getId());
                            }
                        }
                    }
                }
            }
        }

        List<ItemEntity> entities = new ArrayList<>();

        for (Map.Entry<GlobalPos, List<ItemEntity>> entry : map.entrySet()) {
            int items = 0;

            for (ItemEntity e : entry.getValue()) {
                items += e.getItem().getCount();
            }

            if (items >= Singularity.CONFIG.clearOptions.minimumItems) {
                Component x = Component.literal(Integer.toString(entry.getKey().pos().getX()))
                        .withStyle(ChatFormatting.AQUA);

                Component y = Component.literal(Integer.toString(entry.getKey().pos().getY()))
                        .withStyle(ChatFormatting.AQUA);
                        
                Component z = Component.literal(Integer.toString(entry.getKey().pos().getZ()))
                        .withStyle(ChatFormatting.AQUA);

                Util.chat(server, Component.translatable("singularity.chat.detection.find", x, y, z, items),
                        Singularity.CONFIG.generalOptions.displayPotentialsPublicly);

                for (ItemEntity val : entry.getValue()) {
                    entities.add(val);
                }
            }
        }

        Util.chat(server, Component.translatable("singularity.chat.detection.end"),
                false);

        return entities;
    }

    public static boolean isInRange(GlobalPos root, GlobalPos point, double range) {
        double x = point.pos().getX() - root.pos().getX();
        double y = point.pos().getY() - root.pos().getY();
        double z = point.pos().getZ() - root.pos().getZ();

        double distSqr = x * x + y * y + z * z;
        double dist = Math.sqrt(distSqr);

        return (distSqr == 0 || dist <= range) && root.dimension() == point.dimension();
    }
}
