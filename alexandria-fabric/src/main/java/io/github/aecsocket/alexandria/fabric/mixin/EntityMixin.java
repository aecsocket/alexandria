package io.github.aecsocket.alexandria.fabric.mixin;

import io.github.aecsocket.alexandria.fabric.Persistable;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements Persistable {
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  @Unique
  private Optional<Boolean> isPersistent = Optional.empty();

  @Override
  public Optional<Boolean> isPersistent() {
    return isPersistent;
  }

  @Override
  public void setPersistent(boolean value) {
    isPersistent = Optional.of(value);
  }

  @Override
  public void clearPersistent() {
    isPersistent = Optional.empty();
  }

  @Inject(method = "shouldBeSaved", at = @At("HEAD"), cancellable = true)
  private void shouldBeSaved(CallbackInfoReturnable<Boolean> cir) {
    isPersistent.ifPresent(cir::setReturnValue);
  }
}
