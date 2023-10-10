package org.cubeville.effects.managers.sources.value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("ListValueSource")
public class ListValueSource implements ValueSource
{
    List<Double> values;

    public ListValueSource(List<Double> values) {
        this.values = values;
    }

    public ListValueSource(Map<String, Object> config) {
        values = (List<Double>)config.get("values");
    }

    public double getValue(int step) {
        if(values.size() == 0) return 0;
        if(step < 0) step = 0;
        if(step >= values.size()) step = values.size() - 1;
        return values.get(step);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("values", values);
        return ret;
    }

    public String getInfo(boolean detailed) {
        String ret = "List ";
        for(int i = 0; i < values.size(); i++) {
            if(i == 10 && detailed == false) {
                ret += "...";
                break;
            }
            if(i > 0) ret += ", ";
            ret += values.get(i);
        }
        return ret;
    }
}
