package org.cubeville.effects.managers.sources.value;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("SinewaveValueSource")
public class SinewaveValueSource implements ValueSource
{
    double degreesOffset;
    double degreesPerStep;
    double size;
    double sizeOffset;
    boolean abs;
    
    public SinewaveValueSource(double degreesOffset, double degreesPerStep, double size, double sizeOffset, boolean abs) {
	this.sizeOffset = sizeOffset;
	this.degreesPerStep = degreesPerStep;
	this.size = size;
	this.degreesOffset = degreesOffset;
	this.abs = abs;
    }

    public SinewaveValueSource(Map<String, Object> config) {
	degreesOffset = (double)config.get("degreesOffset");
	degreesPerStep = (double)config.get("degreesPerStep");
	size = (double)config.get("size");
	sizeOffset = (double)config.get("sizeOffset");
	abs = (boolean)config.get("abs");
    }
    
    public double getValue(int step) {
	double r = Math.toRadians(degreesPerStep * step + degreesOffset);
	if(abs)
	    return Math.abs(Math.sin(r) * size) + sizeOffset;
	else {
	    return Math.sin(r) * size + sizeOffset;
        }
    }

    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("degreesOffset", degreesOffset);
	ret.put("degreesPerStep", degreesPerStep);
	ret.put("size", size);
	ret.put("sizeOffset", sizeOffset);
	ret.put("abs", abs);
	return ret;
    }

    public String getInfo(boolean detailed) {
	return "Sinewave " + degreesOffset + "+" + degreesPerStep + ", " + sizeOffset + "+-" + size + (abs ? " abs" : "");
    }
}
