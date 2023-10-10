package org.cubeville.effects.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class Effect implements ConfigurationSerializable
{
    private String name;

    public Effect() {}
    
    public Effect(String name) {
	this.name = name;
    }

    public Effect(Map<String, Object> config) {
    }

    public abstract Map<String, Object> serialize();
    
    protected Map<String, Object> getSerializationBase() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("name", name);
        return ret;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
    
    public abstract List<String> getInfo(boolean detailed, String limit);
    
    public List<String> getInfoBase() {
        List<String> ret = new ArrayList<>();
        ret.add("Type: " + getType());
        return ret;
    }
    
    public abstract String getType();
}
