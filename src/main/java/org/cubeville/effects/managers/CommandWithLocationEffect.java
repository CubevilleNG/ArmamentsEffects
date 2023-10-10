package org.cubeville.effects.managers;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("CommandWithLocationEffect")
public class CommandWithLocationEffect extends EffectWithLocation
{
    private String command;
    
    public CommandWithLocationEffect(String name, String command) {
        setName(name);
        this.command = command;
    }

    public CommandWithLocationEffect(Map<String, Object> config) {
        command = (String) config.get("command");
        setName((String) config.get("name"));
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = getSerializationBase();
        ret.put("command", command);
        return ret;
    }

    public void play(Location location) {
        String c = command.replace("%world%", location.getWorld().getName());
        c = c.replace("%location%", location.getX() + "," + location.getY() + "," + location.getZ());
	c = c.replace("%locationyp%", location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
    }

    public List<String> getInfo(boolean detailed, String limit) {
        List<String> ret = getInfoBase();
        ret.add("Command: " + command);
        return ret;
    }

    public String getType() {
        return "Command";
    }

    public void modify(String command) {
        this.command = command;
    }
}
