package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterFloat;
import org.cubeville.commons.commands.CommandParameterEnum;
import org.cubeville.commons.commands.CommandParameterInteger;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.effects.managers.EffectManager;
import org.cubeville.effects.managers.SoundEffect;

public class EffectCreateSoundCommand extends Command
{
    public EffectCreateSoundCommand() {
        super("effect create sound");
        addBaseParameter(new CommandParameterString());
        addBaseParameter(new CommandParameterEnum(Sound.class));
        addOptionalBaseParameter(new CommandParameterFloat());
        addParameter("delay", true, new CommandParameterInteger());
        addParameter("volume", true, new CommandParameterFloat());
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {
        String name = (String) baseParameters.get(0);
        if(EffectManager.getInstance().getEffectByName(name) != null) {
            throw new CommandExecutionException("Effect with name " + name + " already exists!");
        };
        
        float pitch = 1.0F;
        if(baseParameters.size() == 3) pitch = (Float) baseParameters.get(2);

        int delay = 0;
        if(parameters.containsKey("delay")) {
            delay = (Integer) parameters.get("delay");
        }

        float volume = 1.0F;
        if(parameters.containsKey("volume")) {
            volume = (float) parameters.get("volume");
        }
        
        SoundEffect effect = new SoundEffect(name, (Sound) baseParameters.get(1), pitch, delay, volume);
        EffectManager.getInstance().addEffect(effect);
        CommandUtil.saveConfig();

        return null;
    }
}
