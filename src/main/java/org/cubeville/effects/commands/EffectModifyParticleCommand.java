package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterInteger;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.effects.managers.ParticleEffect;
import org.cubeville.effects.managers.ParticleEffectComponent;

public class EffectModifyParticleCommand extends Command {

    public EffectModifyParticleCommand() {
        super("effect modify");
        addBaseParameter(new CommandParameterEffect(ParticleEffect.class));
        ParticleCommandHelper.addCommandParameters(this);
        addParameter("component", true, new CommandParameterInteger(1, 25));
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException {
        if(parameters.size() == 0 && flags.size() == 0) throw new CommandExecutionException("No modification parameters.");
        ParticleEffect effect = (ParticleEffect) baseParameters.get(0);
        ParticleCommandHelper.setEffectValues(effect, parameters);
        int componentIdx = 0;
        boolean create = false;
        if(parameters.get("component") != null) {
            componentIdx = (int) parameters.get("component") - 1;
            int currentSize = effect.getComponents().size();
            if(componentIdx > currentSize) throw new CommandExecutionException("Effect currently only has " + currentSize + " components!");
            if(componentIdx == currentSize) {
                effect.addComponent(new ParticleEffectComponent());
                create = true;
            }
        }
        ParticleEffectComponent component = effect.getComponents().get(componentIdx);
        ParticleCommandHelper.setComponentValues(component, parameters, flags, player);
        CommandUtil.saveConfig();
        return new CommandResponse("Effect component successfully " + (create ? "created." : "modified."));
    }
}
