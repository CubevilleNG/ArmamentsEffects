package kipperorigin.armamentseffects.effects;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;

import kipperorigin.armamentseffects.AE_Main;
import kipperorigin.armamentseffects.event.AE_ProjectileEvent;
import kipperorigin.armamentseffects.resources.AE_EntityGetter;

public class AE_EffectPacketParticles extends AE_EffectParent implements Listener {

    int running;

    private AE_Main plugin;

    public AE_EffectPacketParticles(AE_Main plugin) {

        this.plugin = plugin;
    }
    
    private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private AE_EntityGetter eg = new AE_EntityGetter();

	@SuppressWarnings("deprecation")
	@Override
    public void run(final AE_ProjectileEvent event) {
		
		final Player player = event.getPlayer();
		final Projectile projectile = event.getProjectile();
		String[] args = event.getArgs();
		int timer = 1;
		PacketContainer particlePacket = protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
		int[] il = {0,0};
		
		if (args.length == 0 || args.length > 11)
			return;
		
		if (args.length >= 1)
			try {
				particlePacket.getParticles().write(0, Particle.valueOf(args[0].toUpperCase()));
			} catch (IllegalArgumentException e) {
				player.sendMessage("ARGUMENT 1 INVALID PARTICLE");
			}
		if (args.length >= 2)
			try {
				particlePacket.getIntegers().write(0, Particle.valueOf(args[1].toUpperCase()).getId());
			} catch (IllegalArgumentException e) {
				player.sendMessage("ARGUMENT 2 IS AN INVALID PARTICLE SPREAD");
			}
		if (args.length >= 6 && !args[5].equalsIgnoreCase("x"))
			try {
				particlePacket.getFloat().write(3, Float.valueOf(args[5]));
			} catch (NumberFormatException e) {
				player.sendMessage("ARGUMENT 6 MUST BE X OR A NUMBER");
			}
		if (args.length >= 7 && !args[6].equalsIgnoreCase("x"))
			try {
				particlePacket.getFloat().write(4, Float.valueOf(args[6]));
			} catch (NumberFormatException e) {
				player.sendMessage("ARGUMENT 7 MUST BE X OR A NUMBER");
			}
		if (args.length >= 8 && !args[7].equalsIgnoreCase("x"))
			try {
				particlePacket.getFloat().write(5, Float.valueOf(args[7]));
			} catch (NumberFormatException e) {
				player.sendMessage("ARGUMENT 8 MUST BE X OR A NUMBER");
			}
		if (args.length >= 9 && !args[8].equalsIgnoreCase("x")) {
			try {
				il[0] = Material.valueOf(args[8].toUpperCase()).getId();
			} catch (IllegalArgumentException e) {
				player.sendMessage("ARGUMENT 9 MUST BE A MATERIAL");
			}
			try {
				particlePacket.getIntegerArrays().write(0, il);
			} catch (IllegalArgumentException e) {
				player.sendMessage("UNKOWN ERROR PLEASE REPORT");
			}
		}
		if (args.length >= 10)
			if (args[8].equalsIgnoreCase("x"))
				timer = 1;
			else
				try {
					timer = Integer.valueOf(args[8]);
				} catch (NumberFormatException e) {
					player.sendMessage("MUST BE AN INTEGER");
				}
				
		try {
    		protocolManager.sendServerPacket(player, particlePacket);
    	} catch (InvocationTargetException e) {
    		throw new RuntimeException("Cannot send packet " + particlePacket, e);
    	}
		
        final int taskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
        	float xF = 0;
        	float yF = 0;
        	float zF = 0;
            @Override
            public void run() {
            	
            	int i = 0;
            	List<Player> players = eg.getPlayersInRadius(50, projectile);
            	
        		if (args.length >= 3 && !args[2].equalsIgnoreCase("x"))
        			try {
        				xF = Float.valueOf(args[2]);
        			} catch (NumberFormatException e) {
        				player.sendMessage("ARGUMENT 3 MUST BE X OR A NUMBER");
        			}
        		if (args.length >= 4 && !args[3].equalsIgnoreCase("x"))
        			try {
        				yF = Float.valueOf(args[3]);
        			} catch (NumberFormatException e) {
        				player.sendMessage("ARGUMENT 4 MUST BE X OR A NUMBER");
        			}
        		if (args.length >= 5 && !args[4].equalsIgnoreCase("x"))
        			try {
        				zF = Float.valueOf(args[4]);
        			} catch (NumberFormatException e) {
        				player.sendMessage("ARGUMENT 5 MUST BE X OR A NUMBER");
        			}
        		particlePacket.getFloat()
        			.write(0, (float) projectile.getLocation().getX() + xF)
        			.write(1, (float) projectile.getLocation().getY() + yF)
        			.write(2, (float) projectile.getLocation().getZ() + zF)
        			.write(6, 1F);
            	
            	if (players != null) {
            		if (players.size() != 0 && !players.isEmpty()) {
            			try {
                    		for (int x = 0; x < players.size(); x++)
                    			protocolManager.sendServerPacket(players.get(x), particlePacket);
                    	} catch (InvocationTargetException e) {
                    		throw new RuntimeException("Cannot send packet " + particlePacket, e);
                    	}
            		}
            	}
            	
                if (projectile.getLocation().getY() <= 0) {
                	while(projectile.hasMetadata("Data " + String.valueOf(i))) {
                		Bukkit.getScheduler().cancelTask(projectile.getMetadata("Data " + String.valueOf(i)).get(0).asInt());
                		i++;
                	}
                	projectile.eject();
                	projectile.remove();
        		}
            }
        }, 0L, timer).getTaskId();
        MetadataValue x = new FixedMetadataValue(plugin, taskId);

        if (args.length == 11 && args[10].equalsIgnoreCase("permanent")) {
    		event.getPlayer().sendMessage("taskId = " + String.valueOf(taskId));
        } else {
			int i = 0;
			
			while(projectile.hasMetadata("Data " + String.valueOf(i))) 
				i++;
			
			projectile.setMetadata("Data " + String.valueOf(i), x);
            if (event.getRawEvent().isCancelled() && projectile.hasMetadata("Data"))
                Bukkit.getScheduler().cancelTask(projectile.getMetadata("Data").get(0).asInt());
        }
	}
}
