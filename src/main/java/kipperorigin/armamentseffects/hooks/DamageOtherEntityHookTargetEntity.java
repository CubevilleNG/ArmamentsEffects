package kipperorigin.armamentseffects.hooks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.LivingEntity;

import kipperorigin.armamentseffects.managers.Effect;
import kipperorigin.armamentseffects.managers.EffectManager;
import kipperorigin.armamentseffects.managers.EffectWithLivingEntity;

public class DamageOtherEntityHookTargetEntity implements DamageOtherEntityHook
{
    EffectWithLivingEntity effect;
    
    public DamageOtherEntityHookTargetEntity(Effect effect) {
        this.effect = (EffectWithLivingEntity) effect;
    }

    public DamageOtherEntityHookTargetEntity(Map<String, Object> config) {
        this.effect = (EffectWithLivingEntity) EffectManager.getInstance().getEffectByName((String) config.get("effect"));
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("effect", effect);
        return ret;
    }

    public String getInfo() {
        return "DamageOtherEntity -> LivingEntity: " + effect.getName();
    }
    
    public void process(EntityDamageByEntityEvent event) {
        effect.play((LivingEntity) event.getEntity());
    }

    public boolean usesEffect(Effect effect) {
        return effect == this.effect;
    }

    public boolean alwaysActive() {
        return false;
    }
}
