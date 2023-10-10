package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandParameterInteger;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.effects.managers.CommandWithLocationEffect;
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectManager;

public class EffectLoad extends Command
{
    private String folder;

    public EffectLoad(String folder) {
        super("effect load");
        addBaseParameter(new CommandParameterString());
        this.folder = folder;
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        String name = (String) baseParameters.get(0);
        if(EffectManager.getInstance().getEffectByName(name) != null) {
            throw new CommandExecutionException("Effect " + name + " already exists.");
        };

        YamlConfiguration file = new YamlConfiguration();
        try {
            file.load(folder + player.getUniqueId().toString());
        }
        catch(Exception e) {
            throw new CommandExecutionException("Could not load effect.");
        }

        if(file.get("effect") == null || (!(file.get("effect") instanceof Effect))) {
            throw new CommandExecutionException("Could not load effect.");
        }

        Effect effect = (Effect) file.get("effect");
        effect.setName(name);
        EffectManager.getInstance().addEffect(effect);

        CommandUtil.saveConfig();
        
        return new CommandResponse("Effect loaded.");
    }   
}
