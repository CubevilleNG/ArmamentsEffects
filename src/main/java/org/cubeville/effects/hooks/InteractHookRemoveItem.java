package org.cubeville.effects.hooks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cubeville.effects.managers.Effect;

@SerializableAs("InteractHookRemoveItem")
public class InteractHookRemoveItem implements InteractHook
{
    public InteractHookRemoveItem() {}

    public InteractHookRemoveItem(Map<String, Object> config) {
    }

    public Map<String, Object> serialize() {
        return new HashMap<>();
    }

    public String getInfo() {
        return "RemoveItem";
    }

    public boolean process(PlayerInteractEvent event) {
	// Find item stack in player's hand and remove one
	event.getItem().setAmount(event.getItem().getAmount() - 1);
        return true;
    }

    public boolean usesEffect(Effect effect) {
        return false;
    }

    public boolean alwaysActive() {
        return false;
    }

    public void playAt(Location location) {}
}
