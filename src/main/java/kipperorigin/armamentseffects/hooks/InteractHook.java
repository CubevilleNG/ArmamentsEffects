package kipperorigin.armamentseffects.hooks;

import org.bukkit.event.player.PlayerInteractEvent;

public interface InteractHook extends Hook
{
    public void process(PlayerInteractEvent event);
}
