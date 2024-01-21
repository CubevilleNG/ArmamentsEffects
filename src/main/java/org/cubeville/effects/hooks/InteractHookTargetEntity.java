package org.cubeville.effects.hooks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.managers.EffectManager;
import org.cubeville.effects.managers.EffectWithLivingEntity;
import org.cubeville.effects.util.PlayerUtil;

public class InteractHookTargetEntity implements InteractHook
{
    EffectWithLivingEntity effect;
    double maxDist;
    
    public InteractHookTargetEntity(Effect effect, double maxDist) {
        this.effect = (EffectWithLivingEntity) effect;
        this.maxDist = maxDist;
    }

    public InteractHookTargetEntity(Map<String, Object> config) {
        effect = (EffectWithLivingEntity) EffectManager.getInstance().getEffectByName((String) config.get("effect"));
        if(config.get("maxDist") != null) {
            maxDist = (double) config.get("maxDist");
        }
        else {
            maxDist = 10.0;
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("effect", effect.getName());
        ret.put("maxDist", maxDist);
        return ret;
    }

    public String getInfo() {
        return "TargetEntity: " + effect.getName();
    }

    public boolean process(PlayerInteractEvent event) {
	Player player = event.getPlayer();
        LivingEntity target = PlayerUtil.findTargetEntity(player, player.getNearbyEntities(maxDist, maxDist, maxDist), 1000, maxDist);

        if(target == null) return true;

        effect.play(target, event);

        return true;
    }

    public boolean usesEffect(Effect effect) {
        return(this.effect == effect);
    }

    public boolean alwaysActive() {
        return false;
    }

    public void playAt(Location location) {}
}
