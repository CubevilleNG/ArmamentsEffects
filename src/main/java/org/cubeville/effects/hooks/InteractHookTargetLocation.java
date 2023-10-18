package org.cubeville.effects.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectManager;
import org.cubeville.effects.managers.EffectWithLocation;
import org.cubeville.effects.util.Conversions;
import org.cubeville.effects.util.PlayerUtil;

@SerializableAs("InteractHookTargetLocation")
public class InteractHookTargetLocation implements InteractHook
{
    List<Effect> effects;
    List<Effect> noTargetEffects;
    boolean fixedPitch = false;
    double zoffset = 0.0;
    boolean origindir = false;
    
    public InteractHookTargetLocation(List<Effect> effects, List<Effect> noTargetEffects, boolean fixedPitch, double zoffset, boolean origindir) {
	this.effects = new ArrayList<>();
	for(Effect effect: effects) {
	    if(effect instanceof EffectWithLocation) this.effects.add((EffectWithLocation) effect);
	}
	this.noTargetEffects = new ArrayList<>();
	for(Effect effect: noTargetEffects) {
	    if(effect instanceof EffectWithLocation) this.noTargetEffects.add((EffectWithLocation) effect);
	}
        this.fixedPitch = fixedPitch;
        this.zoffset = zoffset;
        this.origindir = origindir;
    }

    public InteractHookTargetLocation(Map<String, Object> config) {
        effects = new ArrayList<>();
        for(String effect: (List<String>) config.get("effects")) {
            effects.add(EffectManager.getInstance().getEffectByName(effect));
        }
        noTargetEffects = new ArrayList<>();
        for(String effect: (List<String>) config.get("noTargetEffects")) {
            noTargetEffects.add(EffectManager.getInstance().getEffectByName(effect));
        }
        if(config.containsKey("fixedPitch")) {
            fixedPitch = (boolean)config.get("fixedPitch");
        }
        if(config.containsKey("zoffset")) {
            zoffset = (Double) config.get("zoffset");
        }
        if(config.containsKey("origindir")) {
            origindir = (Boolean) config.get("origindir");
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("effects", Conversions.getListOfEffectNames(effects));
        ret.put("noTargetEffects", Conversions.getListOfEffectNames(noTargetEffects));
        ret.put("fixedPitch", fixedPitch);
        ret.put("zoffset", zoffset);
        ret.put("origindir", origindir);
        return ret;
    }

    public String getInfo() {
        return "TargetLocation: ["
            + Conversions.joinStringList(Conversions.getListOfEffectNames(effects), ",") + "] / ["
            + Conversions.joinStringList(Conversions.getListOfEffectNames(noTargetEffects), ",") + "]"
            + (fixedPitch ? " (fixed pitch)" : "");
    }

    public boolean process(PlayerInteractEvent event) {
	Player player = event.getPlayer();
        Entity target = PlayerUtil.findTargetEntity(player, player.getNearbyEntities(10, 10, 10), 1000);

        if(target != null) {
            Location loc = target.getLocation().clone();
            if(fixedPitch) loc.setPitch(0);
            if(origindir) {
                Vector dir = player.getLocation().toVector().subtract(loc.toVector());
                loc.setDirection(dir);
            }
            Vector dir = loc.getDirection().multiply(zoffset);
            loc.add(dir);
	    for(Effect effect: effects) {
		((EffectWithLocation) effect).play(loc);
	    }
	}
	else {
            Location loc = player.getLocation();
            if(fixedPitch) loc.setPitch(0);
	    for(Effect effect: noTargetEffects) {
		((EffectWithLocation) effect).play(loc);
	    }
	}
        
        return true;
    }

    public boolean usesEffect(Effect effect) {
        for(int i = 0; i < effects.size(); i++) if(effects.get(i) == effect) return true;
        for(int i = 0; i < noTargetEffects.size(); i++) if(noTargetEffects.get(i) == effect) return true;
        return false;
    }

    public boolean alwaysActive() {
        return false;
    }

    public void playAt(Location location) {}    
}
