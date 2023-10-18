package org.cubeville.effects.hooks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cubeville.effects.managers.Effect;

@SerializableAs("InteractionHookCancelEvent")
public class InteractHookCancelEvent implements InteractHook
{
    public InteractHookCancelEvent() {}
    
    public InteractHookCancelEvent(Map<String, Object> config) {
    }

    public Map<String, Object> serialize() {
        return new HashMap<>();
    }

    public String getInfo() {
        return "CancelEvent";
    }

    public boolean process(PlayerInteractEvent event) {
	event.setCancelled(true);
        return true;
    }

    public boolean usesEffect(Effect effect) {
        return false;
    }

    public boolean alwaysActive() {
        return true;
    }

    public void playAt(Location location) {}
}
