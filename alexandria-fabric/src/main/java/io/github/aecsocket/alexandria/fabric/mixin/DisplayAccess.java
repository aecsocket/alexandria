package io.github.aecsocket.alexandria.fabric.mixin;

import com.mojang.math.Transformation;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DisplayAccess {
    @Accessor("DATA_TRANSLATION_ID")
    static EntityDataAccessor<Vector3f> getTranslation() {
        throw new AssertionError();
    }

    @Accessor("DATA_SCALE_ID")
    static EntityDataAccessor<Vector3f> getScale() {
        throw new AssertionError();
    }

    @Accessor("DATA_LEFT_ROTATION_ID")
    static EntityDataAccessor<Quaternionf> getLeftRotation() {
        throw new AssertionError();
    }

    @Accessor("DATA_RIGHT_ROTATION_ID")
    static EntityDataAccessor<Quaternionf> getRightRotation() {
        throw new AssertionError();
    }

    @Invoker("getInterpolationDelay")
    int invokeGetInterpolationDelay();

    @Invoker("setInterpolationDelay")
    void invokeSetInterpolationDelay(int i);

    @Invoker("getInterpolationDuration")
    int invokeGetInterpolationDuration();

    @Invoker("setInterpolationDuration")
    void invokeSetInterpolationDuration(int i);

    @Invoker("setTransformation")
    void invokeSetTransformation(Transformation transformation);

    @Invoker("setBillboardConstraints")
    void invokeSetBillboardConstraints(Display.BillboardConstraints billboardConstraints);

    @Invoker("getViewRange")
    float invokeGetViewRange();

    @Invoker("setViewRange")
    void invokeSetViewRange(float f);

    @Invoker("getGlowColorOverride")
    int invokeGetGlowColorOverride();

    @Invoker("setGlowColorOverride")
    void invokeSetGlowColorOverride(int i);
}
