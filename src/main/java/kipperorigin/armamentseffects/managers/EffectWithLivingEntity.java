package kipperorigin.armamentseffects.managers;

import org.bukkit.entity.LivingEntity;

public abstract class EffectWithLivingEntity extends Effect
{
    public abstract void play(LivingEntity entity);
}
