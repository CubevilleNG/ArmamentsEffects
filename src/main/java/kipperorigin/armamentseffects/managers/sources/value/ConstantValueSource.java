package kipperorigin.armamentseffects.managers.sources.value;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("ConstantValueSource")
public class ConstantValueSource implements ValueSource
{
    double value;

    public ConstantValueSource(double value) {
	this.value = value;
    }

    public ConstantValueSource(Map<String, Object> config) {
	this.value = (double) config.get("value");
    }

    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("value", value);
	return ret;
    }
    
    public double getValue(int step) {
	return value;
    }
    
    public String getInfo() {
	return "Constant " + value;
    }
}
