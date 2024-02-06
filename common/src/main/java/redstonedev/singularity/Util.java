package redstonedev.singularity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

public class Util {
    public static void chat(MinecraftServer server, Component msg, boolean operatorOnly) {
        server.sendSystemMessage(msg);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (operatorOnly) {
                if (player.hasPermissions(2)) {
                    player.sendSystemMessage(msg);
                }
            } else {
                player.sendSystemMessage(msg);
            }
        }
    }

    public static <K, O, V> Map<O, V> mapKeys(Map<K, V> map, Function<K, O> processor) {
        Map<O, V> newMap = new HashMap<>();

        for (Map.Entry<K, V> item : map.entrySet()) {
            newMap.put(processor.apply(item.getKey()), item.getValue());
        }

        return newMap;
    }

    public static <K, V, O> Map<K, O> mapValues(Map<K, V> map, Function<V, O> processor) {
        Map<K, O> newMap = new HashMap<>();

        for (Map.Entry<K, V> item : map.entrySet()) {
            newMap.put(item.getKey(), processor.apply(item.getValue()));
        }

        return newMap;
    }

    public static <T> JsonElement toJson(T obj) {
        return new Gson().toJsonTree(obj);
    }

    public static <T> JsonElement toJson(List<T> list) {
        return new Gson().toJsonTree(list.stream().map(Util::toJson).toList());
    }

    public static <V> JsonElement toJson(Map<JsonPrimitive, V> map) {
        return new Gson().toJsonTree(mapValues(map, Util::toJson));
    }

    public static JsonObject toJson(GlobalPos pos) {
        JsonObject obj = new JsonObject();

        obj.add("level", new JsonPrimitive(pos.dimension().location().toString()));
        obj.add("x", new JsonPrimitive(pos.pos().getX()));
        obj.add("y", new JsonPrimitive(pos.pos().getY()));
        obj.add("z", new JsonPrimitive(pos.pos().getZ()));

        return obj;
    }

    public static JsonObject toJson(ItemEntity entity) {
        JsonObject obj = new JsonObject();

        obj.add("id", new JsonPrimitive(entity.getId()));
        obj.add("pos", toJson(GlobalPos.of(entity.getLevel().dimension(), entity.blockPosition())));

        return obj;
    }
}
