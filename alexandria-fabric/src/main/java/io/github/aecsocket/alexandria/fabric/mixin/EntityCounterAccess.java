package io.github.aecsocket.alexandria.fabric.mixin;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityCounterAccess {
  @Accessor("ENTITY_COUNTER")
  static AtomicInteger getEntityCounter() {
    throw new AssertionError();
  }
}
