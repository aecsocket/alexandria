package com.github.aecsocket.minecommons.paper.effect;

import org.bukkit.World;

import com.github.aecsocket.minecommons.core.effect.Effector;
import com.github.aecsocket.minecommons.core.effect.ForwardingEffector;

/**
 * A wrapper of an effector around a world.
 * @param manager The underlying effectors manager.
 * @param world The underlying world.
 */
public record WorldEffector(
    PaperEffectors manager,
    World world
) implements ForwardingEffector {
    @Override
    public Iterable<? extends Effector> effectors() {
        return world.getPlayers().stream().map(manager::ofPlayer).toList();
    }
}
