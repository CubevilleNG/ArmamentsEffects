package org.cubeville.effects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterDouble;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.effects.hooks.InteractHookTargetLocationParticlePlayer;
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.ParticleEffect;
import org.cubeville.effects.registry.Registry;
import org.cubeville.effects.util.ItemUtil;

public class HookCreateInteractTargetLocationParticlePlayerCommand extends Command
{
    public HookCreateInteractTargetLocationParticlePlayerCommand() {
        super("hook create interact targetlocationparticleplayer");
        addBaseParameter(new CommandParameterEffect(ParticleEffect.class));
        addParameter("fixedpitch", true, new CommandParameterDouble());
        addParameter("speed", true, new CommandParameterDouble());
        addParameter("step", true, new CommandParameterDouble());
        addParameter("yoffset", true, new CommandParameterDouble());
        addFlag("origindir");
        addFlag("condstop");
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {
        String itemName = ItemUtil.getItemInMainHandName(player);
        if(itemName == null) throw new CommandExecutionException("No named item in hand!");

        Effect effect = (Effect) baseParameters.get(0);
        double speed = 1.0;
        if(parameters.get("speed") != null) speed = (double) parameters.get("speed");
        double step = 1.0;
        if(parameters.get("step") != null) step = (double) parameters.get("step");
        double yoffset = 0.0;
        if(parameters.get("yoffset") != null) yoffset = (double) parameters.get("yoffset");
        boolean fixedpitch = parameters.containsKey("fixedpitch");
        double fixedpitchval = 0.0;
        if(fixedpitch) fixedpitchval = (double) parameters.get("fixedpitch");
        boolean origindir = flags.contains("origindir");
        boolean condstop = flags.contains("condstop");
        Registry.getInstance().registerEvent(itemName, new InteractHookTargetLocationParticlePlayer(effect.getName(), yoffset, step, speed, fixedpitch, fixedpitchval, origindir, condstop));
        CommandUtil.saveConfig();

        return new CommandResponse("Hook created.");
    }
}
