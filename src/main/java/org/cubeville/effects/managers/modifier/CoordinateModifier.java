package org.cubeville.effects.managers.modifier;

import java.util.List;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

public interface CoordinateModifier extends ConfigurationSerializable
{
    public List<Vector> modify(List<Vector> coordinates, int step);
    public String getInfo(boolean detailed);
}
