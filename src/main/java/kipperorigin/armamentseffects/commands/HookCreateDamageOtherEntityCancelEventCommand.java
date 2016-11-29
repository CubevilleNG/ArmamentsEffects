package kipperorigin.armamentseffects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;

import kipperorigin.armamentseffects.registry.Registry;
import kipperorigin.armamentseffects.util.ItemUtil;
import kipperorigin.armamentseffects.hooks.DamageOtherEntityHookCancelEvent;

public class HookCreateDamageOtherEntityCancelEventCommand extends Command
{
    public HookCreateDamageOtherEntityCancelEventCommand() {
        super("hook create damageotherentity cancelevent");
    }

    public void execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {
        String itemName = ItemUtil.safeGetItemInMainHandName(player);
        Registry.getInstance().registerDamageOtherEntityEvent(itemName, new DamageOtherEntityHookCancelEvent());
    }

}
