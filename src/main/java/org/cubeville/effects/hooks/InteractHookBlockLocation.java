package org.cubeville.effects.hooks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectWithLocation;

public class InteractHookBlockLocation implements InteractHook
{
    EffectWithLocation effect;

    public InteractHookBlockLocation(Effect effect) {
        this.effect = (EffectWithLocation) effect;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        return ret;
    }

    public String getInfo() {
        return "Interact -> BlockLocation: " + effect.getName();
    }
    
    public void process(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            effect.play(event.getClickedBlock().getLocation());
        }
    }

    public boolean usesEffect(Effect effect) {
        return effect == this.effect;
    }

    public boolean alwaysActive() {
        return false;
    }

    public void playAt(Location location) {}
}
