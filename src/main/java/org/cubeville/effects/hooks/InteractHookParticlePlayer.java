package org.cubeville.effects.hooks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cubeville.effects.Effects;
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectManager;
import org.cubeville.effects.managers.ParticleEffect;
import org.cubeville.effects.managers.ParticleEffectTimedRunnable;

@SerializableAs("InteractHookParticlePlayer")
public class InteractHookParticlePlayer implements InteractHook
{
    private ParticleEffect effect;
    private double yOffset;
    private double ySneakShift;
    private double stepsPerTick;
    private double speed;
    private boolean fixedPitch;
    private double pitch;
    private boolean followPlayerLocation;
    private boolean followPlayerYaw;
    private boolean followPlayerPitch;
    
    public InteractHookParticlePlayer(String effectName, double yOffset, double stepsPerTick, double speed, boolean fixedPitch, double pitch, double ySneakShift, boolean followPlayerLocation, boolean followPlayerYaw, boolean followPlayerPitch) {
	this.effect = (ParticleEffect) EffectManager.getInstance().getEffectByName(effectName);
	this.yOffset = yOffset;
	this.stepsPerTick = stepsPerTick;
	this.speed = speed;
	this.fixedPitch = fixedPitch;
	this.pitch = pitch;
        this.ySneakShift = ySneakShift;
        this.followPlayerLocation = followPlayerLocation;
        this.followPlayerYaw = followPlayerYaw;
        this.followPlayerPitch = followPlayerPitch;
    }

    public InteractHookParticlePlayer(Map<String, Object> config) {
        String effectName = (String) config.get("effect");
        this.effect = (ParticleEffect) EffectManager.getInstance().getEffectByName(effectName);
        this.yOffset = (double) config.get("yOffset");
        this.stepsPerTick = (double) config.get("stepsPerTick");
        this.speed = (double) config.get("speed");
        this.fixedPitch = (boolean) config.get("fixedPitch");
        this.pitch = (double) config.get("pitch");
        if(config.get("ySneakShift") != null)
            ySneakShift = (double) config.get("ySneakShift");
        else
            ySneakShift = 0.0;
        followPlayerLocation = config.containsKey("followPlayerLocation") ? (boolean) config.get("followPlayerLocation") : false;
        followPlayerYaw = config.containsKey("followPlayerYaw") ? (boolean) config.get("followPlayerYaw") : false;
        followPlayerPitch = config.containsKey("followPlayerPitch") ? (boolean) config.get("followPlayerPitch") : false;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("effect", effect.getName());
        ret.put("yOffset", yOffset);
        ret.put("stepsPerTick", stepsPerTick);
        ret.put("speed", speed);
        ret.put("pitch", pitch);
        ret.put("fixedPitch", fixedPitch);
        ret.put("ySneakShift", ySneakShift);
        ret.put("followPlayerLocation", followPlayerLocation);
        ret.put("followPlayerYaw", followPlayerYaw);
        ret.put("followPlayerPitch", followPlayerPitch);
        return ret;
    }

    public String getInfo() {
        String info = "ParticlePlayer: " + effect.getName();
        if(fixedPitch) info += ", fp = " + pitch;
        if(yOffset != 0.0) info += ", yo = " + yOffset;
        if(ySneakShift != 0.0) info += ", sys = " + ySneakShift;
        if(stepsPerTick != 1.0) info += ", steps = " + stepsPerTick;
        if(speed != 1.0) info += ", speed = " + speed;
        if(followPlayerLocation) info += ", fpl";
        if(followPlayerYaw) info += ", fpy";
        if(followPlayerPitch) info += ", fpp";
        return info;
    }
    
    public void process(PlayerInteractEvent rawEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) rawEvent;
        Player player = event.getPlayer();
	Location loc = player.getLocation().clone();
	loc.setY(loc.getY() + yOffset);
        if(player.isSneaking()) loc.setY(loc.getY() + ySneakShift);
	if(fixedPitch) loc.setPitch((float)pitch);
	new ParticleEffectTimedRunnable(Effects.getInstance(), player, effect, stepsPerTick, speed, loc, followPlayerLocation, followPlayerYaw, followPlayerPitch).runTaskTimer(Effects.getInstance(), 1, 1);
    }

    public void playAt(Location location) {
        Location loc = location.clone();
        loc.setY(loc.getY() + yOffset);
        if(fixedPitch) loc.setPitch((float)pitch);
	new ParticleEffectTimedRunnable(Effects.getInstance(), null, effect, stepsPerTick, speed, loc, false, false, false).runTaskTimer(Effects.getInstance(), 1, 1);        
    }
    
    public boolean usesEffect(Effect effect) {
        return effect == this.effect; // TODO, this needs an overhaul anyways
    }

    public boolean alwaysActive() {
        return false;
    }
}
