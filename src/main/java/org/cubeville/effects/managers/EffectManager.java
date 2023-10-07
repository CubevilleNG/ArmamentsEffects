package org.cubeville.effects.managers;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("EffectManager")
public class EffectManager implements ConfigurationSerializable
{
    List<Effect> effects;
    static EffectManager instance;

    public static EffectManager getInstance() {
        return instance;
    }

    public EffectManager(Map<String, Object> config) {
        effects = (List<Effect>) config.get("effects");
        instance = this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("effects", effects);
        return ret;
    }
    
    public EffectManager() {
        effects = new ArrayList<>();
        instance = this;
    }

    public void addEffect(Effect effect) {
        if(getEffectIdByName(effect.getName()) != -1) throw new IllegalArgumentException("Effect with that name already exists!");
        effects.add(effect);
    }

    public void removeEffect(Effect effect) {
        effects.remove(effect);
    }
    
    public int getEffectIdByName(String name) {
        for(int i = 0; i < effects.size(); i++) {
            if(effects.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    public List<String> getEffectList() {
        List<String> ret = new ArrayList<>();
        for(Effect effect: effects) {
            ret.add(effect.getName() + ": " + effect.getType());
        }
        return ret;
    }

    public Effect getEffectById(int id) {
        return effects.get(id);
    }

    public Effect getEffectByName(String name) {
        int id = getEffectIdByName(name);
        if(id == -1) return null;
        return effects.get(id);
    }

    public List<String> getEffectInfo(String name, boolean detailed) {
        Effect effect = getEffectByName(name);
        if(effect == null) return null;
        return effect.getInfo(detailed);
    }

    public void updateExternalEffectHookReferences() {
        for(Effect effect: effects) {
            if(effect instanceof EffectWithHook) {
                EffectWithHook e = (EffectWithHook) effect;
                Set<String> names = e.getExternalEffectNames();
                for(String name: names) {
                    Effect externalEffect = getEffectByName(name);
                    e.setExternalEffectReference(name, externalEffect);
                }
            }
        }
    }
}
