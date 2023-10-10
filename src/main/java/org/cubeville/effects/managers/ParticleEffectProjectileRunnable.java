package org.cubeville.effects.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.cubeville.effects.registry.ProjectileTrackerAction;
import org.cubeville.effects.registry.Registry;

public class ParticleEffectProjectileRunnable extends BukkitRunnable implements ProjectileTrackerAction
{
    private final Projectile projectile;
    private final ParticleEffect effect;
    private final Player player;
    private int step;
    private final int runningEffectId;
    
    public ParticleEffectProjectileRunnable(ParticleEffect effect, Projectile projectile) {
        this.projectile = projectile;
        this.effect = effect;
        if(projectile.getShooter() instanceof Player)
            player = (Player) projectile.getShooter();
        else
            player = null;
        step = 0;
        runningEffectId = EffectManager.getNewRunningEffectId();
        Registry.getInstance().addProjectileHitAction(projectile, this);
    }

    private void abort() {
        this.cancel();
        Registry.getInstance().removeProjectileHitAction(projectile, this);
    }

    public void projectileHitEvent() {
        abort();
    }
    
    @Override
    public void run() {
        if(projectile.isDead()) {
            abort();
            return;
        }
        step++;
        if(step == 100) {
            abort();
            return;
        }
        Location loc = projectile.getLocation();
        loc.setYaw(360 - loc.getYaw());
        loc.setPitch(-loc.getPitch());        
        if(!effect.play(step, new StaticParticleEffectLocationCalculator(loc), player, runningEffectId)) {
            abort();
            return;
        }
    }
    
}
