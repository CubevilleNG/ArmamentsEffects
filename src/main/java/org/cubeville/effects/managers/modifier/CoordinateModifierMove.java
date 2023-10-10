package org.cubeville.effects.managers.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.cubeville.effects.managers.sources.value.ValueSource;

@SerializableAs("CoordinateModifierMove")
public class CoordinateModifierMove implements CoordinateModifier
{
    ValueSource source;
    boolean x;
    boolean y;
    boolean z;
    
    public CoordinateModifierMove(ValueSource source, boolean x, boolean y, boolean z) {
	this.source = source;
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public CoordinateModifierMove(Map<String, Object> config) {
	source = (ValueSource) config.get("source");
	x = false;
	y = false;
	z = false;
	String dir = (String) config.get("directions");
	x = dir.contains("x");
	y = dir.contains("y");
	z = dir.contains("z");
    }
    
    public List<Vector> modify(List<Vector> coordinates, int step) {
	List<Vector> ret = new ArrayList<>();
	for(Vector v: coordinates) {
	    Vector nv = v.clone();
	    double m = source.getValue(step);
	    if(x) nv.setX(nv.getX() + m);
	    if(y) nv.setY(nv.getY() + m);
	    if(z) nv.setZ(nv.getZ() + m);
	    ret.add(nv);
	}
	return ret;
    }

    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("source", source);
	ret.put("directions", (x ? "x" : "") + (y ? "y" : "") + (z ? "z" : ""));
	return ret;
    }

    public String getInfo(boolean detailed) {
	return "Move " + (x ? "x" : "") + (y ? "y" : "") + (z ? "z" : "") + ": " + source.getInfo(detailed);
    }

}
