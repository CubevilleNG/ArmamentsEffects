package kipperorigin.armamentseffects.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandParameterUUID;
import org.cubeville.commons.commands.CommandResponse;

import kipperorigin.armamentseffects.registry.Registry;

public class PermissionAddCommand extends Command
{
    public PermissionAddCommand() {
        super("permission add");
        addBaseParameter(new CommandParameterString());
        addBaseParameter(new CommandParameterUUID());
    }

    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) {
        Registry.getInstance().getPermissionList().addPermission((String) baseParameters.get(0), (UUID) baseParameters.get(1));
        CommandUtil.saveConfig();
        CommandUtil.clearPermissionCache();
        return null;
    }
}
