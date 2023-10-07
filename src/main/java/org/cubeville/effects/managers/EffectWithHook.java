package org.cubeville.effects.managers;

import java.util.Set;

public interface EffectWithHook
{
    public Set<String> getExternalEffectNames();
    public void setExternalEffectReference(String externalEffectName, Effect externalEffect);
};
