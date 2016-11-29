package kipperorigin.armamentseffects.managers;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("RemoveLivingEntityEffect")
public class RemoveLivingEntityEffect extends EffectWithLivingEntity
{
    public RemoveLivingEntityEffect(String name) {
        setName(name);
    }

    public RemoveLivingEntityEffect(Map<String, Object> config) {
        setName((String) config.get("name"));
    }
    
    public Map<String, Object> serialize() {
        return getSerializationBase();
    }

    public void play(LivingEntity entity) {
        if(!(entity instanceof Player))
            entity.remove();
    }

    public List<String> getInfo() {
        return getInfoBase();
    }

    public String getType() {
        return "RemoveLivingEntity";
    }
}
