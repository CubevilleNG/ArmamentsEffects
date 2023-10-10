package org.cubeville.effects.managers;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

@SerializableAs("SpawnEntityEffect")
public class SpawnEntityEffect extends EffectWithLocation
{
    private EntityType type;

    public SpawnEntityEffect(String name, EntityType type) {
        setName(name);
        this.type = type;
    }

    public SpawnEntityEffect(Map<String, Object> config) {
        setName((String) config.get("name"));
        type = EntityType.valueOf((String) config.get("type"));
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = getSerializationBase();
        ret.put("type", type.toString());
        return ret;
    }

    public void play(Location location) {
        Entity entity = location.getWorld().spawnEntity(location, type);
        if(entity instanceof Horse) {
            ((Horse) entity).setVariant(Horse.Variant.SKELETON_HORSE);
        }
    }

    public List<String> getInfo(boolean detailed, String limit) {
        List<String> ret = getInfoBase();
        ret.add("Entity type: " + type.toString());
        return ret;
    }

    public String getType() {
        return "SpawnEntity";
    }
}
