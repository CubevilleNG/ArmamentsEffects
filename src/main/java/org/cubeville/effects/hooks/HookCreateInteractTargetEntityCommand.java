package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.commons.commands.CommandParameterDouble;
import org.cubeville.effects.hooks.InteractHookTargetEntity;
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectWithLivingEntity;
import org.cubeville.effects.registry.Registry;
import org.cubeville.effects.util.ItemUtil;

public class HookCreateInteractTargetEntityCommand extends Command
{
    public HookCreateInteractTargetEntityCommand() {
        super("hook create interact targetentity");
        addBaseParameter(new CommandParameterEffect(EffectWithLivingEntity.class));
        addParameter("maxdist", true, new CommandParameterDouble());
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {
        EffectWithLivingEntity effect = (EffectWithLivingEntity) baseParameters.get(0);
        String itemName = ItemUtil.safeGetItemInMainHandName(player);
        double maxDist = 10.0;
        if(parameters.get("maxdist") != null)
            maxDist = (double) parameters.get("maxdist");
        Registry.getInstance().registerEvent(itemName, new InteractHookTargetEntity(effect, maxDist));
        CommandUtil.saveConfig();
        return null;
    }
}
