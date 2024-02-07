package mixins.translationsapi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements LocalizationTarget {
    @Shadow public ServerGamePacketListenerImpl connection;

    @Override
    public String getLanguageCode() {
        return this.connection != null ? ((LocalizationTarget) this.connection).getLanguageCode() : null;
    }
}
