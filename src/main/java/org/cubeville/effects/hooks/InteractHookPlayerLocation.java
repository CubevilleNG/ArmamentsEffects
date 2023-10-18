package org.cubeville.effects.hooks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectManager;
import org.cubeville.effects.managers.EffectWithLocation;

@SerializableAs("InteractHookPlayerLocation")
public class InteractHookPlayerLocation implements InteractHook
{
    EffectWithLocation effect;
    boolean fixedPitch = false;
    double yoffset = 0.0;
    double offset = 0.0;

    public InteractHookPlayerLocation(Effect effect, boolean fixedPitch, double yoffset, double offset) {
	this.effect = (EffectWithLocation) effect;
        this.fixedPitch = fixedPitch;
        this.yoffset = yoffset;
        this.offset = offset;
    }

    public InteractHookPlayerLocation(Map<String, Object> config) {
        effect = (EffectWithLocation) EffectManager.getInstance().getEffectByName((String) config.get("effect"));
        if(config.containsKey("fixedPitch"))
            fixedPitch = (boolean)config.get("fixedPitch");
        if(config.containsKey("yoffset")) 
            yoffset = (double) config.get("yoffset");
        if(config.containsKey("offset"))
            offset = (double) config.get("offset");
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("effect", effect.getName());
        ret.put("fixedPitch", fixedPitch);
        ret.put("offset", offset);
        ret.put("yoffset", yoffset);
        return ret;
    }

    public String getInfo() {
        String info = "PlayerLocation: " + effect.getName();
        if(fixedPitch) info += ", fp";
        if(yoffset != 0.0) info += ", yo = " + yoffset;
        if(offset != 0.0) info += ", o = " + offset;
        return info;
    }

    public boolean process(PlayerInteractEvent event) {
        Location loc = event.getPlayer().getLocation();
        playAt(loc);
        return true;
    }

    public void playAt(Location location) {
        Location loc = location.clone();
        if(fixedPitch) loc.setPitch(0);
        loc.setY(loc.getY() + yoffset);
        loc.add(loc.getDirection().multiply(offset));
	effect.play(loc);
    }

    public boolean usesEffect(Effect effect) {
        return effect == this.effect;
    }

    public boolean alwaysActive() {
        return false;
    }
}
