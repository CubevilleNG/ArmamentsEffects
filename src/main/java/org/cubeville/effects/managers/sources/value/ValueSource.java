package org.cubeville.effects.managers.sources.value;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface ValueSource extends ConfigurationSerializable
{
    public double getValue(int step);
    public String getInfo(boolean detailed);
}
