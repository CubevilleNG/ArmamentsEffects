package org.cubeville.effects.managers.sources.value;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("LinearValueSource")
public class LinearValueSource implements ValueSource
{
    double start;
    double increment;

    public LinearValueSource(double start, double increment) {
	this.start = start;
	this.increment = increment;
    }

    public LinearValueSource(Map<String, Object> config) {
	start = (Double)config.get("start");
	increment = (Double)config.get("increment");
    }
    
    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("start", start);
	ret.put("increment", increment);
	return ret;
    }

    public double getValue(int step) {
	return start + step * increment;
    }
    
    public String getInfo(boolean detailed) {
	return "Linear " + start + " " + increment;
    }

}
