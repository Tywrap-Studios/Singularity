package redstonedev.singularity.util;

import net.minecraft.core.GlobalPos;
import redstonedev.singularity.Singularity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class Dumps {
    public static String serialize(List<GlobalPos> positions) {
        StringBuilder data = new StringBuilder();

        for (GlobalPos pos : positions) {
            data.append("(").append(pos.pos().getX()).append(",").append(pos.pos().getY()).append(",").append(pos.pos().getZ()).append(");");
        }

        return data.toString();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void create(Path dir, List<GlobalPos> positions) {
        Path path = dir.resolve("singularity").resolve("dump-" + LocalDate.now() + "-" + LocalTime.now().toString().replaceAll(":", "-") + ".txt");

        if (Singularity.CONFIG.generalOptions.deleteOldDumps && path.getParent().toFile().exists()) {
            for (File file : Objects.requireNonNull(path.getParent().toFile().listFiles())) {
                file.delete();
            }
        }

        if (!path.getParent().toFile().exists()) {
            path.getParent().toFile().mkdirs();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));

            writer.write(serialize(positions));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
