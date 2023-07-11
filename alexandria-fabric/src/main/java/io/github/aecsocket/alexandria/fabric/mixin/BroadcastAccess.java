package io.github.aecsocket.alexandria.fabric.mixin;

import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerEntity.class)
public interface BroadcastAccess {
  @Accessor("broadcast")
  Consumer<Packet<?>> getBroadcast();
}
