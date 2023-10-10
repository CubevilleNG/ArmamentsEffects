package org.cubeville.effects.managers;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.cubeville.commons.utils.ColorUtils;

@SerializableAs("PiercingEffect")
public class PiercingEffect extends Effect
{
    PiercingType type;
    double factor;
    boolean stats;
    
    public PiercingEffect(String name, PiercingType type, double factor, boolean stats) {
        super(name);
        this.type = type;
        this.factor = factor;
        this.stats = stats;
    }

    public PiercingEffect(Map<String, Object> config) {
        factor = (double) config.get("factor");
        type = PiercingType.valueOf((String) config.get("type"));
        stats = (boolean) config.get("stats");
        setName((String) config.get("name"));
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = getSerializationBase();
        ret.put("factor", factor);
        ret.put("type", type.toString());
        ret.put("stats", stats);
        return ret;
    }
    
    public void play(EntityDamageByEntityEvent event, Player damager) {
        if(!(event.getEntity() instanceof LivingEntity)) return;
        
        if(stats) {
            damager.sendMessage(ColorUtils.addColor("&6-----Stats Start-----"));
            damager.sendMessage(ColorUtils.addColor("&eOriginal Damage = " + event.getDamage()));
            damager.sendMessage(ColorUtils.addColor("&ePiercing Type = " + type));
            damager.sendMessage(ColorUtils.addColor("&ePiercing Ammount = " + factor));
            damager.sendMessage(ColorUtils.addColor("&cPre-Piercing Protection"));
            damager.sendMessage(ColorUtils.addColor("&cAbsorbtion&f = " + event.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)));
            damager.sendMessage(ColorUtils.addColor("&cArmor&f = " + event.getDamage(EntityDamageEvent.DamageModifier.ARMOR)));
            damager.sendMessage(ColorUtils.addColor("&cMagic&f = " + event.getDamage(EntityDamageEvent.DamageModifier.MAGIC)));
            damager.sendMessage(ColorUtils.addColor("&cResistance&f = " + event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE)));
            damager.sendMessage(ColorUtils.addColor("&c__________"));
            damager.sendMessage(ColorUtils.addColor("&cFinal Damage = " + event.getFinalDamage()));
        }

        double rfactor = 1 - (factor / 100);
        if(type == PiercingType.TRUE || type == PiercingType.PHYSICAL || type == PiercingType.ABSORPTION)
            changeDamage(EntityDamageEvent.DamageModifier.ABSORPTION, event, rfactor);
        if(type == PiercingType.TRUE || type == PiercingType.PHYSICAL || type == PiercingType.ARMOR)
            changeDamage(EntityDamageEvent.DamageModifier.ARMOR, event, rfactor);
        if(type == PiercingType.TRUE || type == PiercingType.MAGICAL || type == PiercingType.MAGIC)
            changeDamage(EntityDamageEvent.DamageModifier.MAGIC, event, rfactor);
        if(type == PiercingType.TRUE || type == PiercingType.MAGICAL || type == PiercingType.RESISTANCE)
            changeDamage(EntityDamageEvent.DamageModifier.RESISTANCE, event, rfactor);

        if(stats) {
            damager.sendMessage(ColorUtils.addColor("&aPost-Piercing Protection:"));
            damager.sendMessage(ColorUtils.addColor("&aAbsorbtion&f = " + event.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)));
            damager.sendMessage(ColorUtils.addColor("&aArmor&f = " + event.getDamage(EntityDamageEvent.DamageModifier.ARMOR)));
            damager.sendMessage(ColorUtils.addColor("&aMagic&f = " + event.getDamage(EntityDamageEvent.DamageModifier.MAGIC)));
            damager.sendMessage(ColorUtils.addColor("&aResistance&f = " + event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE)));
            damager.sendMessage(ColorUtils.addColor("&a__________"));
            damager.sendMessage(ColorUtils.addColor("&aFinal Damage = " + event.getFinalDamage()));
            damager.sendMessage(ColorUtils.addColor("&6-----Stats End-----"));
        }
    }

    private void changeDamage(EntityDamageEvent.DamageModifier modifier, EntityDamageByEntityEvent event, double factor) {
        event.setDamage(modifier, event.getDamage(modifier) * factor);
    }
    
    public static enum PiercingType {
        TRUE, MAGICAL, PHYSICAL, ABSORPTION, ARMOR, MAGIC, RESISTANCE;
    }

    public List<String> getInfo(boolean detailed, String limit) {
        List<String> ret = getInfoBase();
        ret.add("Piercing: " + type + " (" + factor + ")");
        return ret;
    }
    
    public String getType() {
        return "Piercing";
    }
}
