package org.cubeville.effects.util;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldEditUtils {
    public static List<Vector> getSelectionCoordinates(Player player) {
        return getSelectionCoordinates(player, null);
    }
    public static List<Vector> getSelectionCoordinates(Player player, Material material) {
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        Region selection = null;
        try {
            assert worldEdit != null;
            selection = worldEdit.getSession(player).getSelection(worldEdit.getSession(player).getSelectionWorld());
        } catch(IncompleteRegionException e){
            throw new IllegalArgumentException("Incomplete region selection.");
        }
        if (selection == null) throw new IllegalArgumentException("No region selected.");
        BlockVector3 bvMin = selection.getMinimumPoint();
        BlockVector3 bvMax = selection.getMaximumPoint();
        World world = BukkitAdapter.adapt(Objects.requireNonNull(selection.getWorld()));
        Location min = new Location(world, bvMin.getX(), bvMin.getY(), bvMin.getZ());
        Location max = new Location(world, bvMax.getX(), bvMax.getY(), bvMax.getZ());
        Block minBlock = min.getBlock();
        Block maxBlock = max.getBlock();
        Location mid = max.subtract(min).multiply(0.5).add(min);
        mid.setY(min.getY());
        List<Vector> coordList = new ArrayList<>();
        for(int x = minBlock.getX(); x <= maxBlock.getX(); x++) {
            for(int y = minBlock.getY(); y <= maxBlock.getY(); y++) {
                for(int z = minBlock.getZ(); z <= maxBlock.getZ(); z++) {
                    if (material == null) {
                        if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
                            coordList.add(new Vector((x - mid.getX()), (y - mid.getY()), (z - mid.getZ())));
                        }
                    } else if (world.getBlockAt(x, y, z).getType() == material) {
                        coordList.add(new Vector((x - mid.getX()), (y - mid.getY()), (z - mid.getZ())));
                    }
                }
            }
        }
        return coordList;
    }
}
