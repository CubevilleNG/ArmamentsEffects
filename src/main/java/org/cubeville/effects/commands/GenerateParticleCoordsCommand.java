package org.cubeville.effects.commands;

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
import org.bukkit.util.BlockVector;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvtools.CVTools;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GenerateParticleCoordsCommand extends Command {


    public GenerateParticleCoordsCommand() {
        super("generate coords");
    }

    @Override
    public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> list) throws CommandExecutionException {
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
        String coordString = "";
        for(int x = minBlock.getX(); x <= maxBlock.getX(); x++) {
            for(int y = minBlock.getY(); y <= maxBlock.getY(); y++) {
                for(int z = minBlock.getZ(); z <= maxBlock.getZ(); z++) {
                    if(world.getBlockAt(x, y, z).getType() != Material.AIR) {
                        String colon = "";
                        if (!coordString.equals("")) {
                            colon = ";";
                        }
                            coordString = coordString + colon + (x - mid.getX()) + "," + (y - mid.getY()) + "," + (z - mid.getZ());
                    }
                }
            }
        }
        player.sendMessage(coordString);
        return null;
    }
}
