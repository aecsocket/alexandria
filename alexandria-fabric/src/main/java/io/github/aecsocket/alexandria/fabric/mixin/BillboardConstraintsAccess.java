package io.github.aecsocket.alexandria.fabric.mixin;

import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Display.BillboardConstraints.class)
public interface BillboardConstraintsAccess {
    @Accessor
    byte getId();
}
