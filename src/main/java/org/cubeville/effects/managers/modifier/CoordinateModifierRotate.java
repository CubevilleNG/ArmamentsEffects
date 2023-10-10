package org.cubeville.effects.managers.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.cubeville.effects.managers.sources.value.ValueSource;

@SerializableAs("CoordinateModifierRotate")
public class CoordinateModifierRotate implements CoordinateModifier
{
    ValueSource source;

    public CoordinateModifierRotate(ValueSource source) {
	this.source = source;
    }

    public CoordinateModifierRotate(Map<String, Object> config) {
	source = (ValueSource) config.get("source");
    }

    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("source", source);
	return ret;
    }
    
    public List<Vector> modify(List<Vector> coordinates, int step) {
	double rot = Math.toRadians(source.getValue(step));
	double cosRot = Math.cos(rot);
	double sinRot = Math.sin(rot);
	List<Vector> ret = new ArrayList<>();
	for(Vector v: coordinates) {
	    ret.add(new Vector(v.getX() * cosRot + v.getY() * sinRot, v.getY() * cosRot - v.getX() * sinRot , v.getZ()));
	}
	return ret;
    }

    public String getInfo(boolean detailed) {
	return "Rotate " + source.getInfo(detailed);
    }

}
