package com.gitlab.aecsocket.minecommons.paper.effect;

import com.gitlab.aecsocket.minecommons.core.effect.Effector;
import com.gitlab.aecsocket.minecommons.core.effect.ForwardingEffector;
import org.bukkit.World;

import java.util.stream.Collectors;

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
        return world.getPlayers().stream().map(manager::ofPlayer).collect(Collectors.toList());
    }
}
