package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import org.cubeville.commons.commands.CommandParameterWorld;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandParameterVector;
import org.cubeville.commons.commands.CommandParameterFloat;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.effects.hooks.InteractHook;
import org.cubeville.effects.registry.Registry;

public class InteractHookPlayerCommand extends BaseCommand
{
    public InteractHookPlayerCommand() {
        super("interacthookplayer");
        addBaseParameter(new CommandParameterString());
        addBaseParameter(new CommandParameterWorld());
        addBaseParameter(new CommandParameterVector());
        addBaseParameter(new CommandParameterFloat());
        addBaseParameter(new CommandParameterFloat());        
        addFlag("silent");
    }

    public CommandResponse execute(CommandSender player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        Vector v = (Vector) baseParameters.get(2);
        Location location = new Location((World) baseParameters.get(1), v.getX(), v.getY(), v.getZ(), (float) baseParameters.get(3), (float) baseParameters.get(4));

        String itemname = (String) baseParameters.get(0);
        String citemname = itemname.replace('&', '§');
        List<InteractHook> hooks = Registry.getInstance().getInteractHooksOfItem(citemname);

        for(InteractHook hook: hooks) {
            hook.playAt(location);
        }

        if(flags.contains("silent")) {
            return new CommandResponse("");
        }
        else {
            return null;
        }
    }        
    
}
