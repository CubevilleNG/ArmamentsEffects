package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.effects.managers.EffectManager;

public class InfoCommand extends Command
{
    public InfoCommand() {
	super("effect info");
	addBaseParameter(new CommandParameterString());
        addFlag("detailed");
        addParameter("limit", true, new CommandParameterString());
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {
	String name =(String) baseParameters.get(0);
        String limit = "";
        if(parameters.containsKey("limit"))
            limit = (String) parameters.get("limit");
	List<String> info = EffectManager.getInstance().getEffectInfo(name, flags.contains("detailed"), limit);
	if(info == null) throw new CommandExecutionException("Effect " + name + " not found!");
	player.sendMessage("--------------- Effect " + name + " ---------------");
	for(String i: info) {
	    player.sendMessage(i);
	}
        return null;
    }
}
