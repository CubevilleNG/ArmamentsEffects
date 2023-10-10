package org.cubeville.effects.managers.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.cubeville.effects.managers.sources.value.ValueSource;

@SerializableAs("CoordinateModifierScale")
public class CoordinateModifierScale implements CoordinateModifier
{
    ValueSource source;
    
    public CoordinateModifierScale(ValueSource source) {
	this.source = source;
    }

    public CoordinateModifierScale(Map<String, Object> config) {
	source = (ValueSource) config.get("source");
    }

    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("source", source);
	return ret;
    }
    
    public List<Vector> modify(List<Vector> coordinates, int step) {
	double mult = source.getValue(step);
	List<Vector> ret = new ArrayList<>();
	for(Vector v: coordinates) {
	    ret.add(new Vector(v.getX() * mult,
			       v.getY() * mult,
			       v.getZ()));
	}
	return ret;
    }

    public String getInfo(boolean detailed) {
	return "Scale " + source.getInfo(detailed);
    }
}
