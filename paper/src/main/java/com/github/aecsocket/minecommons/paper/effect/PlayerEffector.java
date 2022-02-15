package com.github.aecsocket.minecommons.paper.effect;

import com.github.aecsocket.minecommons.core.Numbers;
import com.github.aecsocket.minecommons.core.Ticks;
import com.github.aecsocket.minecommons.core.effect.Effector;
import com.github.aecsocket.minecommons.core.effect.ParticleEffect;
import com.github.aecsocket.minecommons.core.effect.SoundEffect;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;
import com.github.aecsocket.minecommons.paper.PaperUtils;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * A wrapper of an effector around a player.
 * @param manager The underlying effector manager.
 * @param player The underlying player.
 */
public record PlayerEffector(
    PaperEffectors manager,
    Player player
) implements Effector {
    private void forcePlay(SoundEffect effect, Vector3 origin, double dist, Vector3 end) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(manager.plugin(), () -> {
            Vector3 delta = origin.subtract(end);
            Vector3 pos = (Double.compare(delta.manhattanLength(), 0) == 0
                ? delta
                : delta.normalize().multiply(2))
                .add(end);
            float volume = effect.sound().volume() * (float) (1 -
                Numbers.clamp01((dist - effect.dropoff()) / (effect.range() - effect.dropoff())));
            player.playSound(Sound.sound(
                effect.sound().name(), effect.sound().source(),
                volume,
                effect.sound().pitch()
            ), pos.x(), pos.y(), pos.z());
        }, (long) ((dist / effect.speed()) * Ticks.TPS));
    }

    @Override
    public void play(SoundEffect effect, Vector3 origin, double dist) {
        Vector3 end = PaperUtils.toCommons(player.getLocation());
        if (dist > effect.range())
            return;
        forcePlay(effect, origin, dist, end);
    }

    @Override
    public void play(SoundEffect effect, Vector3 origin) {
        Vector3 end = PaperUtils.toCommons(player.getLocation());
        double distSqr = end.sqrDistance(origin);
        if (distSqr > effect.sqrRange())
            return;
        forcePlay(effect, origin, Math.sqrt(distSqr), end);
    }

    @Override
    public void spawn(ParticleEffect effect, Vector3 origin) {
        if (!(effect.name() instanceof Particle particle))
            return;
        Vector3 size = effect.size();
        player.spawnParticle(particle, PaperUtils.toPaper(origin, player.getWorld()), (int) effect.count(),
            size.x(), size.y(), size.z(), effect.speed(), effect.data());
    }
}
