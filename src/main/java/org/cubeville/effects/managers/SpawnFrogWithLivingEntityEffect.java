package org.cubeville.effects.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Map;

@SerializableAs("SpawnFrogWithLivingEntityEffect")
public class SpawnFrogWithLivingEntityEffect extends EffectWithLivingEntity {

    private Frog.Variant variant;

    public SpawnFrogWithLivingEntityEffect(String name, Frog.Variant variant) {
        setName(name);
        this.variant = variant;
    }

    public SpawnFrogWithLivingEntityEffect(Map<String, Object> config) {
        String var = (String) config.get("variant");
        if(var.equalsIgnoreCase("cold")) {
            variant = Frog.Variant.COLD;
        } else if(var.equalsIgnoreCase("temperate")) {
            variant = Frog.Variant.TEMPERATE;
        } else if(var.equalsIgnoreCase("warm")) {
            variant = Frog.Variant.WARM;
        }
        setName((String) config.get("name"));
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = getSerializationBase();
        if(variant.equals(Frog.Variant.COLD)) {
            ret.put("variant", "cold");
        } else if(variant.equals(Frog.Variant.TEMPERATE)) {
            ret.put("variant", "temperate");
        } else if(variant.equals(Frog.Variant.WARM)) {
            ret.put("variant", "warm");
        }
        return ret;
    }

    public void play(LivingEntity entity, Event event) {
        if(event instanceof PlayerInteractEvent) {
            if(entity instanceof Player) {
                Location location = entity.getTargetBlock(null, 5).getLocation().add(0.5, 1, 0.5);
                Frog frog = (Frog) entity.getWorld().spawnEntity(location, EntityType.FROG);
                frog.setVariant(variant);
                if(location.getWorld().getLivingEntities().contains(frog)) {
                    ((PlayerInteractEvent)event).getItem().setAmount(((PlayerInteractEvent)event).getItem().getAmount() - 1);
                } else {
                    entity.sendMessage(ChatColor.RED + "Unable to spawn frog here! Are you allowed spawn things here?");
                }
            }
        }
    }

    public List<String> getInfo(boolean detailed, String limit) {
        List<String> ret = getInfoBase();
        ret.add("Variant: " + variant);
        return ret;
    }

    public String getType() {
        return "Variant";
    }
}
