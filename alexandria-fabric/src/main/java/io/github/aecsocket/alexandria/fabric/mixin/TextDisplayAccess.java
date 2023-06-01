package io.github.aecsocket.alexandria.fabric.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.TextDisplay.class)
public interface TextDisplayAccess {
    @Invoker("setText")
    void invokeSetText(Component component);

    @Invoker("setLineWidth")
    void invokeSetLineWidth(int i);

    @Invoker("setTextOpacity")
    void invokeSetTextOpacity(byte b);

    @Invoker("getBackgroundColor")
    int invokeGetBackgroundColor();

    @Invoker("setBackgroundColor")
    void invokeSetBackgroundColor(int i);

    @Invoker("setFlags")
    void invokeSetFlags(byte b);
}
