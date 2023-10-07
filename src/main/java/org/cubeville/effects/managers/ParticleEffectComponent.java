package org.cubeville.effects.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.cubeville.effects.managers.modifier.CoordinateModifier;
import org.cubeville.effects.managers.sources.coordinate.ConstantCoordinateSource;
import org.cubeville.effects.managers.sources.coordinate.CoordinateSource;
import org.cubeville.effects.managers.sources.value.ConstantValueSource;
import org.cubeville.effects.managers.sources.value.ValueSource;

@SerializableAs("ParticleEffectComponent")
public class ParticleEffectComponent implements ConfigurationSerializable
{
    private CoordinateSource coordinates;

    private Particle particle;

    private String externalEffectName;
    private EffectWithLocation externalEffect;
    
    private ValueSource count;
    private ValueSource spreadX;
    private ValueSource spreadY;
    private ValueSource spreadZ;
    private ValueSource size;
    private ValueSource colourRed;
    private ValueSource colourGreen;
    private ValueSource colourBlue;
    private ValueSource colourToRed;
    private ValueSource colourToGreen;
    private ValueSource colourToBlue;
    private Material material;
    private ValueSource speed;
    private boolean directionalCoordinates;
    private List<CoordinateModifier> modifiers;
    private List<ParticleEffectTimelineEntry> timeline;
    private boolean blockCollisionCheck;
    private boolean entityCollisionCheck;
    
    public ParticleEffectComponent() {
	coordinates = new ConstantCoordinateSource();
	particle = Particle.VILLAGER_HAPPY;
	count = new ConstantValueSource(0);
	spreadX = new ConstantValueSource(0);
        spreadY = new ConstantValueSource(0);
        spreadZ = new ConstantValueSource(0);
        speed = new ConstantValueSource(0);
	material = Material.AIR;
	directionalCoordinates = true;
	modifiers = new ArrayList<>();
	timeline = new ArrayList<>();
        blockCollisionCheck = false;
        entityCollisionCheck = false;
    }

    public ParticleEffectComponent(Map<String, Object> config) {
	coordinates = (CoordinateSource) config.get("coordinates");
        {
            String particleName = (String) config.get("particle");
            if(particleName.equals("none"))
                particle = null;
            else
                particle = Particle.valueOf(particleName);
        }
	count = (ValueSource) config.get("count");
        spreadX = (ValueSource) config.get("spreadX");
        spreadY = (ValueSource) config.get("spreadY");
        spreadZ = (ValueSource) config.get("spreadZ");
	material = Material.valueOf((String) config.get("material"));
	directionalCoordinates = (boolean) config.get("directionalCoordinates");
	modifiers = (List<CoordinateModifier>) config.get("modifiers");
	timeline = (List<ParticleEffectTimelineEntry>) config.get("timeline");

        if(config.get("colourRed") != null)
            colourRed = (ValueSource) config.get("colourRed");
        else
            colourRed = new ConstantValueSource(0);
        if(config.get("colourGreen") != null)
            colourGreen = (ValueSource) config.get("colourGreen");
        else
            colourGreen = new ConstantValueSource(0);
        if(config.get("colourBlue") != null)
            colourBlue = (ValueSource) config.get("colourBlue");
        else
            colourBlue = new ConstantValueSource(0);

        if(config.get("colourToRed") != null)
            colourToRed = (ValueSource) config.get("colourToRed");
        else
            colourToRed = new ConstantValueSource(0);
        if(config.get("colourToGreen") != null)
            colourToGreen = (ValueSource) config.get("colourToGreen");
        else
            colourToGreen = new ConstantValueSource(0);
        if(config.get("colourToBlue") != null)
            colourToBlue = (ValueSource) config.get("colourToBlue");
        else
            colourToBlue = new ConstantValueSource(0);

        if(config.get("size") != null)
            size = (ValueSource) config.get("size");
        else
            size = new ConstantValueSource(1);

        if(config.get("speed") != null)
            speed = (ValueSource) config.get("speed");
        else
            speed = new ConstantValueSource(0);

        if(config.get("blockCollisionCheck") != null)
            blockCollisionCheck = (boolean) config.get("blockCollisionCheck");
        else
            blockCollisionCheck = false;

        if(config.get("entityCollisionCheck") != null)
            entityCollisionCheck = (boolean) config.get("entityCollisionCheck");
        else
            entityCollisionCheck = false;

        if(config.get("externalEffectName") != null)
            externalEffectName = (String) config.get("externalEffectName");
    }

    public Map<String, Object> serialize() {
	Map<String, Object> ret = new HashMap<>();
	ret.put("coordinates", coordinates);
	ret.put("particle", particle == null ? "none" : particle.toString());
	ret.put("count", count);
        ret.put("spreadX", spreadX);
        ret.put("spreadY", spreadY);
        ret.put("spreadZ", spreadZ);
	ret.put("material", material.toString());
	ret.put("directionalCoordinates", directionalCoordinates);
	ret.put("modifiers", modifiers);
	ret.put("timeline", timeline);
        ret.put("colourRed", colourRed);
        ret.put("colourGreen", colourGreen);
        ret.put("colourBlue", colourBlue);
        ret.put("colourToRed", colourToRed);
        ret.put("colourToGreen", colourToGreen);
        ret.put("colourToBlue", colourToBlue);
	ret.put("size", size);
	ret.put("speed", speed);
        ret.put("blockCollisionCheck", blockCollisionCheck);
        ret.put("entityCollisionCheck", entityCollisionCheck);
        if(externalEffectName != null)
            ret.put("externalEffectName", externalEffectName);
	return ret;
    }

    public void addModifier(CoordinateModifier coordinateModifier) {
        modifiers.add(coordinateModifier);
    }

    public void deleteModifiers() {
        modifiers = new ArrayList<>();
    }
    
    public boolean isActive(int step) {
        if(timeline.size() == 0) return true;
	for(ParticleEffectTimelineEntry tle: timeline) {
	    if(step >= tle.getStepStart() && step < tle.getStepStart() + tle.getStepCount()) {
		return true;
	    }
	}
	return false;
    }

    public int getActiveTimelineCount(int step) {
        if(timeline.size() == 0) return 1;
        int ret = 0;
	for(ParticleEffectTimelineEntry tle: timeline) {
	    if(step >= tle.getStepStart() && step < tle.getStepStart() + tle.getStepCount()) {
		ret++;
	    }
	}
	return ret;
    }
    
    public int getLocationOffset(int step, int activeTimeline) {
        if(timeline.size() == 0) return 0;
        int tlnum = 0;
        for(ParticleEffectTimelineEntry tle: timeline) {
            if(step >= tle.getStepStart() && step < tle.getStepStart() + tle.getStepCount()) {
                if(activeTimeline == tlnum) {
                    return tle.getLocationOffset();
                }
                tlnum++;
            }
        }
        return 0;
    }
    
    public List<String> getInfo(boolean detailed) {
	List<String> ret = new ArrayList<>();
	ret.add("  Source: " + coordinates.getInfo(detailed));
	ret.add("  Particle: " + (particle == null ? "none" : particle));
        ret.add("  External effect: " + (externalEffectName == null ? "none" : externalEffectName));
	ret.add("  Count: " + count.getInfo());
	ret.add("  SpreadX: " + spreadX.getInfo());
	ret.add("  SpreadY: " + spreadY.getInfo());
	ret.add("  SpreadZ: " + spreadZ.getInfo());
        if(particle == Particle.ITEM_CRACK || particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_DUST || particle == Particle.FALLING_DUST) {
            ret.add("  Material: " + material);
        }
        ret.add("  Speed: " + speed.getInfo());
        if(particle != null && (particle == Particle.REDSTONE || particle == Particle.DUST_COLOR_TRANSITION)) {
            ret.add("  Red: " + colourRed.getInfo());
            ret.add("  Green: " + colourGreen.getInfo());
            ret.add("  Blue: " + colourBlue.getInfo());
            if(particle == Particle.DUST_COLOR_TRANSITION) {
                ret.add("  To Red: " + colourToRed.getInfo());
                ret.add("  To Green: " + colourToGreen.getInfo());
                ret.add("  To Blue: " + colourToBlue.getInfo());
            }
            ret.add("  Size: " + size.getInfo());
        }
	if(modifiers.size() > 0) {
	    ret.add("  Modifiers:");
	    for(CoordinateModifier modifier: modifiers) {
		ret.add("    " + modifier.getInfo());
	    }
	}
	if(timeline.size() > 0) {
	    ret.add("  Timeline:");
	    for(ParticleEffectTimelineEntry e: timeline) {
		ret.add("    " + e.getStepStart() + "-" + (e.getStepStart() + e.getStepCount()) + "x" + e.getStepRepeat());
	    }
	}
        if(blockCollisionCheck || entityCollisionCheck) {
            String l = "  Collision checks: ";
            if(blockCollisionCheck) l += "Blocks";
            if(blockCollisionCheck && entityCollisionCheck) l += " and ";
            if(entityCollisionCheck) l += "Entities";
            ret.add(l);
        }
	return ret;
    }

    public final List<CoordinateModifier> getModifiers() {
	return modifiers;
    }
    
    public final CoordinateSource getCoordinates() {
	return coordinates;
    }

    public final void setCoordinates(CoordinateSource coordinates) {
	this.coordinates = coordinates;
    }
    
    public final List<Vector> getModifiedCoordinates(int step, boolean useTimelineOffset) { // TODO the second parameter is new, still needs to be added here and in calls
	List<Vector> ret = coordinates.getVertices(step, 0);
	for(CoordinateModifier modifier: getModifiers()) {
	    ret = modifier.modify(ret, step);
	}
	return ret;
    }
    
    public final Particle getParticle() {
	return this.particle;
    }

    public final void setParticle(final Particle argParticle) {
	this.particle = argParticle;
    }

    public final ValueSource getCount() {
	return this.count;
    }

    public final void setCount(final ValueSource argCount) {
	this.count = argCount;
    }

    public final ValueSource getSpreadX() {
	return this.spreadX;
    }

    public final void setSpreadX(ValueSource spreadX) {
	this.spreadX = spreadX;
    }
    
    public final ValueSource getSpreadY() {
	return this.spreadY;
    }

    public final void setSpreadY(ValueSource spreadY) {
	this.spreadY = spreadY;
    }
    
    public final ValueSource getSpreadZ() {
	return this.spreadZ;
    }

    public final void setSpreadZ(ValueSource spreadZ) {
	this.spreadZ = spreadZ;
    }
    
    public final Material getMaterial() {
	return this.material;
    }

    public final void setMaterial(final Material argMaterial) {
	this.material = argMaterial;
    }

    public final boolean isDirectionalCoordinates() {
	return this.directionalCoordinates;
    }

    public final void setDirectionalCoordinates(final boolean argDirectionalCoordinates) {
	this.directionalCoordinates = argDirectionalCoordinates;
    }

    public final boolean getDirectionalCoordinates() {
	return directionalCoordinates;
    }

    public final List<ParticleEffectTimelineEntry> getTimeline() {
	return timeline;
    }

    public final ValueSource getColourRed() {
        return colourRed;
    }

    public final void setColourRed(ValueSource colourRed) {
        this.colourRed = colourRed;
    }
    
    public final ValueSource getColourGreen() {
        return colourGreen;
    }

    public final void setColourGreen(ValueSource colourGreen) {
        this.colourGreen = colourGreen;
    }
    
    public final ValueSource getColourBlue() {
        return colourBlue;
    }

    public final void setColourBlue(ValueSource colourBlue) {
        this.colourBlue = colourBlue;
    }
    
    public final ValueSource getColourToRed() {
        return colourToRed;
    }

    public final void setColourToRed(ValueSource colourToRed) {
        this.colourToRed = colourToRed;
    }
    
    public final ValueSource getColourToGreen() {
        return colourToGreen;
    }

    public final void setColourToGreen(ValueSource colourToGreen) {
        this.colourToGreen = colourToGreen;
    }
    
    public final ValueSource getColourToBlue() {
        return colourToBlue;
    }

    public final void setColourToBlue(ValueSource colourToBlue) {
        this.colourToBlue = colourToBlue;
    }
    
    public final ValueSource getSize() {
        return size;
    }

    public final void setSize(ValueSource size) {
        this.size = size;
    }

    public final void setSpeed(ValueSource speed) {
        this.speed = speed;
    }

    public final ValueSource getSpeed() {
        return speed;
    }

    public final void setBlockCollisionCheck(boolean blockCollisionCheck) {
        this.blockCollisionCheck = blockCollisionCheck;
    }
    
    public final boolean getBlockCollisionCheck() {
        return blockCollisionCheck;
    }

    public final void setEntityCollisionCheck(boolean entityCollisionCheck) {
        this.entityCollisionCheck = entityCollisionCheck;
    }
    
    public final boolean getEntityCollisionCheck() {
        return entityCollisionCheck;
    }

    public final String getExternalEffectName() {
        return externalEffectName;
    }

    public final EffectWithLocation getExternalEffect() {
        return externalEffect;
    }
    
    public final void setExternalEffect(String externalEffectName, Effect externalEffect) {
        if(externalEffect instanceof EffectWithLocation) {
            this.externalEffectName = externalEffectName;
            this.externalEffect = (EffectWithLocation) externalEffect;
        }
        else {
            this.externalEffectName = null;
            this.externalEffect = null;
        }
    }

    public final void setExternalEffect(Effect externalEffect) {
        if(externalEffect instanceof EffectWithLocation) {
            this.externalEffect = (EffectWithLocation) externalEffect;
        }
        else {
            this.externalEffectName = null;
            this.externalEffect = null;
        }
    }
}
