package org.cubeville.effects.managers.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.cubeville.effects.managers.sources.value.ValueSource;

@SerializableAs("CoordinateModifierScale2d")
public class CoordinateModifierScale2d implements CoordinateModifier
{
    ValueSource sourceX;
    ValueSource sourceY;
    
    public CoordinateModifierScale2d(ValueSource sourceX, ValueSource sourceY)
    {
	this.sourceX = sourceX;
	this.sourceY = sourceY;
    }

    public CoordinateModifierScale2d(Map<String, Object> config) {
	sourceX = (ValueSource) config.get("sourceX");
	sourceY = (ValueSource) config.get("sourceY");
    }

    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("sourceX", sourceX);
	ret.put("sourceY", sourceY);
	return ret;
    }
    
    public List<Vector> modify(List<Vector> coordinates, int step)
    {
	double multX = sourceX.getValue(step);
	double multY = sourceY.getValue(step);
	List<Vector> ret = new ArrayList<>();
	for(Vector v: coordinates) {
	    ret.add(new Vector(v.getX() * multX,
			       v.getY() * multY,
			       v.getZ()));
	}
	return ret;
    }

    public String getInfo(boolean detailed) {
	return "Scale2d " + sourceX.getInfo(detailed) + ", " + sourceY.getInfo(detailed);
    }
}
