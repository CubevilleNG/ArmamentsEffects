package org.cubeville.effects.hooks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.cubeville.effects.Effects;
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectManager;
import org.cubeville.effects.managers.ParticleEffect;
import org.cubeville.effects.managers.ParticleEffectTimedRunnable;
import org.cubeville.effects.util.PlayerUtil;

@SerializableAs("InteractHookTargetLocationParticlePlayer")
public class InteractHookTargetLocationParticlePlayer implements InteractHook
{
    private ParticleEffect effect;
    private double yOffset;
    private double stepsPerTick;
    private double speed;
    private boolean fixedPitch;
    private double pitch;
    private boolean originDir;
    private boolean targetFoundStopsProcessing;
    
    public InteractHookTargetLocationParticlePlayer(String effectName, double yOffset, double stepsPerTick, double speed, boolean fixedPitch, double pitch, boolean originDir, boolean targetFoundStopsProcessing) {
	this.effect = (ParticleEffect) EffectManager.getInstance().getEffectByName(effectName);
	this.yOffset = yOffset;
	this.stepsPerTick = stepsPerTick;
	this.speed = speed;
	this.fixedPitch = fixedPitch;
	this.pitch = pitch;
        this.originDir = originDir;
        this.targetFoundStopsProcessing = targetFoundStopsProcessing;
    }

    public InteractHookTargetLocationParticlePlayer(Map<String, Object> config) {
        String effectName = (String) config.get("effect");
        this.effect = (ParticleEffect) EffectManager.getInstance().getEffectByName(effectName);
        this.yOffset = (double) config.get("yOffset");
        this.stepsPerTick = (double) config.get("stepsPerTick");
        this.speed = (double) config.get("speed");
        this.fixedPitch = (boolean) config.get("fixedPitch");
        this.pitch = (double) config.get("pitch");
        if(config.get("originDir") != null) {
            this.originDir = (boolean) config.get("originDir");
        }
        else {
            this.originDir = false;
        }
        if(config.get("targetFoundStopsProcessing") != null) {
            this.targetFoundStopsProcessing = (boolean) config.get("targetFoundStopsProcessing");
        }
        else {
            this.targetFoundStopsProcessing = false;
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("effect", effect.getName());
        ret.put("yOffset", yOffset);
        ret.put("stepsPerTick", stepsPerTick);
        ret.put("speed", speed);
        ret.put("pitch", pitch);
        ret.put("fixedPitch", fixedPitch);
        ret.put("originDir", originDir);
        ret.put("targetFoundStopsProcessing", targetFoundStopsProcessing);
        return ret;
    }

    public String getInfo() {
        String info = "TargetLocationParticlePlayer: " + effect.getName();
        if(fixedPitch) info += ", fp = " + pitch;
        if(yOffset != 0.0) info += ", yo = " + yOffset;
        if(stepsPerTick != 1.0) info += ", step = " + stepsPerTick;
        if(speed != 1.0) info += ", speed = " + speed;
        if(originDir) info += ", od";
        if(targetFoundStopsProcessing) info += ", cs";
        return info;
    }

    public boolean process(PlayerInteractEvent event) {
	Player player = event.getPlayer();
        Entity target = PlayerUtil.findTargetEntity(player, player.getNearbyEntities(10, 10, 10), 1000);
        if(target == null) {
            return true;
        }
        Location loc = target.getLocation().clone();
        loc.setY(loc.getY() + yOffset);
        if(originDir) {
            Vector dir = player.getLocation().toVector().subtract(loc.toVector());
            loc.setDirection(dir);
        }
        if(fixedPitch) loc.setPitch((float)pitch);
        new ParticleEffectTimedRunnable(Effects.getInstance(), player, effect, stepsPerTick, speed, loc, false, false, false).runTaskTimer(Effects.getInstance(), 1, 1);
        return !targetFoundStopsProcessing;
    }

    public boolean usesEffect(Effect effect) {
        return (effect == this.effect);
    }

    public boolean alwaysActive() {
        return false;
    }

    public void playAt(Location location) {}
    
}
