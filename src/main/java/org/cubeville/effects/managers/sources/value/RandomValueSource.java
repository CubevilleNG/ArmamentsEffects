package org.cubeville.effects.managers.sources.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("RandomValueSource")
public class RandomValueSource implements ValueSource
{
    double min;
    double max;

    Random random = new Random();
    
    public RandomValueSource(double min, double max) {
	this.min = min;
	this.max = max;
    }

    public RandomValueSource(Map<String, Object> config) {
	min = (Double)config.get("min");
	max = (Double)config.get("max");
    }

    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("min", min);
	ret.put("max", max);
	return ret;
    }

    public double getValue(int step) {
	return random.nextDouble() * (max - min) + min;
    }

    public String getInfo(boolean detailed) {
	return "Random " + min + " - " + max;
    }
    
}
