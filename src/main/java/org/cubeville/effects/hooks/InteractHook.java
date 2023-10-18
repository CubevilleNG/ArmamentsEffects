package org.cubeville.effects.hooks;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

public interface InteractHook extends Hook
{
    public boolean process(PlayerInteractEvent event);
    public void playAt(Location location);
}
