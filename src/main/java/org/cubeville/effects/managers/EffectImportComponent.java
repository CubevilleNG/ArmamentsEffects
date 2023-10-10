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
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectManager;
import org.cubeville.effects.managers.ParticleEffect;

public class EffectImportComponent extends Command
{
    private String folder;

    public EffectImportComponent(String folder) {
        super("effect import component");
        addBaseParameter(new CommandParameterInteger());
        addBaseParameter(new CommandParameterString());
        this.folder = folder;
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        int componentNr = (Integer) baseParameters.get(0);
        
        String name = (String) baseParameters.get(1);
        if(EffectManager.getInstance().getEffectByName(name) == null) {
            throw new CommandExecutionException("Effect " + name + " not found.");
        };
        if(!(EffectManager.getInstance().getEffectByName(name) instanceof ParticleEffect)) {
            throw new CommandExecutionException("Effect " + name + " is not a particle effect.");
        }
        
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
        if(!(effect instanceof ParticleEffect)) {
            throw new CommandExecutionException("Saved effect is no particle effect.");
        }

        ParticleEffect particleEffect = (ParticleEffect) effect;
        if(componentNr < 1 || componentNr > particleEffect.getComponents().size()) {
            throw new CommandExecutionException("Invalid component number.");
        }
        componentNr--;

        ((ParticleEffect)EffectManager.getInstance().getEffectByName(name)).addComponent(particleEffect.getComponents().get(componentNr));

        CommandUtil.saveConfig();
        
        return new CommandResponse("Component added.");
    }
}
