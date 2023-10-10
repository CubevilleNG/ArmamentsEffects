package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.effects.managers.EffectManager;
import org.cubeville.effects.managers.ParticleEffect;
import org.cubeville.effects.managers.ParticleEffectComponent;

public class EffectCreateParticleCommand extends Command
{
    public EffectCreateParticleCommand() {
        super("effect create particle");
        addBaseParameter(new CommandParameterString());
        ParticleCommandHelper.addCommandParameters(this);
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) {
        String name = (String) baseParameters.get(0);
        ParticleEffect effect = new ParticleEffect(name);
        ParticleCommandHelper.setEffectValues(effect, parameters);
        ParticleEffectComponent component = new ParticleEffectComponent();
        effect.addComponent(component);
        ParticleCommandHelper.setComponentValues(component, parameters, flags, player);
        EffectManager.getInstance().addEffect(effect);
        CommandUtil.saveConfig();
        return null;
    }
}
