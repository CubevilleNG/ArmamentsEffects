package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectManager;
import org.cubeville.effects.registry.Registry;

public class EffectRemove extends Command
{
    public EffectRemove() {
        super("effect remove");
        addBaseParameter(new CommandParameterEffect(Effect.class));
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {
        Effect effect = (Effect) baseParameters.get(0);
        if(Registry.getInstance().isEffectInUse(effect)) throw new CommandExecutionException("Effect is in use by hook, removal not possible.");
        if(EffectManager.getInstance().isEffectInUseAsExternalEffect(effect.getName())) throw new CommandExecutionException("Effect is in use by other effect, removal not possible.");
        EffectManager.getInstance().removeEffect(effect);
        CommandUtil.saveConfig();
        return null;
    }
}
