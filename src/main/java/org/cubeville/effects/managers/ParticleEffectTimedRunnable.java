package org.cubeville.effects.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleEffectTimedRunnable extends BukkitRunnable
{
    private final JavaPlugin plugin;
    //private final Location location;
    private final ParticleEffect effect;
    private int step;
    private int ticks;
    private double stepsPerTick;
    //private double speed;
    private Player player;
    private ParticleEffectLocationCalculator locationCalculator;
    private final int runningEffectId;

    public ParticleEffectTimedRunnable(JavaPlugin plugin, Player player, ParticleEffect effect, double stepsPerTick, double speed, Location location, boolean followPlayerLocation, boolean followPlayerYaw, boolean followPlayerPitch)
    {
        runningEffectId = EffectManager.getNewRunningEffectId();
        locationCalculator = new StraightParticleEffectLocationCalculator(location, speed, player, followPlayerLocation, followPlayerYaw, followPlayerPitch);
	this.plugin = plugin;
	this.effect = effect;
	this.stepsPerTick = stepsPerTick;
        this.player = player;
	ticks = 0;
	step = 0;
    }

    @Override
    public void run() {
	ticks++;
	while(step + 1 < ticks * stepsPerTick) {
            if(!effect.play(step++, locationCalculator, player, runningEffectId)) {
                this.cancel();
                return;
            }
        }
    }
}
