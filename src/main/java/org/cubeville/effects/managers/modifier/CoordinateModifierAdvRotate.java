package org.cubeville.effects.managers.modifier;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.cubeville.effects.managers.sources.value.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("CoordinateModifierAdvRotate")
public class CoordinateModifierAdvRotate implements CoordinateModifier
{
    ValueSource source;
	String direction;

    public CoordinateModifierAdvRotate(ValueSource source, String direction) {
	this.source = source;
	this.direction = direction;
    }

    public CoordinateModifierAdvRotate(Map<String, Object> config) {
	source = (ValueSource) config.get("source");
	direction = (String) config.get("direction");
    }

    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("source", source);
	ret.put("direction", direction);
	return ret;
    }
    
    public List<Vector> modify(List<Vector> coordinates, int step) {
	double rot = Math.toRadians(source.getValue(step));
	double cosRot = Math.cos(rot);
	double sinRot = Math.sin(rot);
	List<Vector> ret = new ArrayList<>();
		switch (direction) {
			case "xy":
				for (Vector v : coordinates) {
					ret.add(new Vector(v.getX() * cosRot + v.getY() * sinRot, v.getY() * cosRot - v.getX() * sinRot, v.getZ()));
				}
				break;
			case "xz":
				for (Vector v : coordinates) {
					ret.add(new Vector(v.getX() * cosRot + v.getZ() * sinRot, v.getY(), v.getZ() * cosRot - v.getX() * sinRot));
				}
				break;
			case "yz":
				for (Vector v : coordinates) {
					ret.add(new Vector(v.getX(), v.getY() * cosRot - v.getZ() * sinRot, v.getZ() * cosRot + v.getY() * sinRot));
				}
				break;
		}
//	for(Vector v: coordinates) {
//		ret.add(new Vector(v.getX() * cosRot + v.getY() * sinRot, v.getY() * cosRot - v.getX() * sinRot , v.getZ()));
//	}
	return ret;
    }

    public String getInfo(boolean detailed) {
	return "Advanced Rotate " + direction + ": " + source.getInfo(detailed);
    }

}
