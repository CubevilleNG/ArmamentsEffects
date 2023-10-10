package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterEnum;
import org.cubeville.commons.commands.CommandParameterFloat;
import org.cubeville.commons.commands.CommandParameterInteger;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.effects.managers.SoundEffect;

public class EffectModifySoundCommand extends Command {

    public EffectModifySoundCommand() {
	super("effect modify");
	addBaseParameter(new CommandParameterEffect(SoundEffect.class));
	addBaseParameter(new CommandParameterEnum(Sound.class));
	addOptionalBaseParameter(new CommandParameterFloat());
        addParameter("delay", true, new CommandParameterInteger());
        addParameter("volume", true, new CommandParameterFloat());
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

	SoundEffect effect = (SoundEffect) baseParameters.get(0);

        float pitch = 1.0f;
	if(baseParameters.size() == 3) pitch = (Float) baseParameters.get(2);

        int delay = 0;
        if(parameters.containsKey("delay")) delay = (Integer) parameters.get("delay");

        float volume = 1.0F;
        if(parameters.containsKey("volume")) {
            volume = (float) parameters.get("volume");
        }

        effect.modify((Sound) baseParameters.get(1), pitch, delay, volume);

	CommandUtil.saveConfig();
	return new CommandResponse("Effect successfully modified.");
    }
    
}
