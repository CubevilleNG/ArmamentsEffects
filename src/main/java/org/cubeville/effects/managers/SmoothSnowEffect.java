package org.cubeville.effects.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

public class SmoothSnowEffect extends EffectWithLocation
{
    public SmoothSnowEffect(String name) {
        setName(name);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>();
    }

    public void play(Location location) {
        // World world = location.getWorld();
        // int bx = (int) location.getX();
        // int by = (int) location.getY();
        // int bz = (int) location.getZ();
        // if(world.getBlockAt(bx, by, bz).getTypeId() == 78) { // Snow pads will be replaced
        //     by--;
        // }
        // double closestUp = -1;
        // double closestDown = -1;
        // for(int x = bx - 10; x <= bx + 10; x++) {
        //     for(int z = bz - 10; z <= bz + 10; z++) {
        //         if(world.getBlockAt(x, by, z).getTypeId() == 0) {
        //             double dist = location.distance(new Location(world, x, by, z));
        //             if(closestDown == -1 || closestDown > dist) closestDown = dist;
        //         }
        //         if(world.getBlockAt(x, by + 1, z).getTypeId() != 0 && world.getBlockAt(x, by + 1, z).getTypeId() != 78) {
        //             double dist = location.distance(new Location(world, x, by, z));
        //             if(closestUp == -1 || closestUp > dist) closestUp = dist;
        //         }
        //     }
        // }
        // if(closestUp != -1 && closestDown != -1 && closestUp != 0) {
        //     double ratio = closestDown / closestUp;
        //     ratio *= 4;
        //     if(ratio > 7) ratio = 7;
        //     int snowHeight = (int) ratio;
        //     if(snowHeight == 0) {
        //         world.getBlockAt(bx, by + 1, bz).setTypeIdAndData((byte)0, (byte)0, false);
        //     }
        //     else {
        //         world.getBlockAt(bx, by + 1, bz).setTypeIdAndData((byte)78, (byte)(snowHeight - 1), false);
        //     }
        // }
    }

    public List<String> getInfo(boolean detailed, String limit) {
        return getInfoBase();
    }

    public String getType() {
        return "SnoothSnow";
    }
}
