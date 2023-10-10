package org.cubeville.effects.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import org.cubeville.effects.managers.sources.value.ConstantValueSource;
import org.cubeville.effects.managers.sources.value.ValueSource;

@SerializableAs("ArmorStandProperties")
public class ArmorStandProperties implements ConfigurationSerializable
{
        
    public boolean visible = true;
    public boolean hasArms = false;
    public boolean small = false;
    
    public ValueSource rotation = new ConstantValueSource(0);
    public ValueSource headPoseX = new ConstantValueSource(0);
    public ValueSource headPoseY = new ConstantValueSource(0);
    public ValueSource headPoseZ = new ConstantValueSource(0);
    public ValueSource bodyPoseX = new ConstantValueSource(0);
    public ValueSource bodyPoseY = new ConstantValueSource(0);
    public ValueSource bodyPoseZ = new ConstantValueSource(0);
    public ValueSource leftArmPoseX = new ConstantValueSource(0);
    public ValueSource leftArmPoseY = new ConstantValueSource(0);
    public ValueSource leftArmPoseZ = new ConstantValueSource(0);
    public ValueSource rightArmPoseX = new ConstantValueSource(0);
    public ValueSource rightArmPoseY = new ConstantValueSource(0);
    public ValueSource rightArmPoseZ = new ConstantValueSource(0);
    public ValueSource leftLegPoseX = new ConstantValueSource(0);
    public ValueSource leftLegPoseY = new ConstantValueSource(0);
    public ValueSource leftLegPoseZ = new ConstantValueSource(0);
    public ValueSource rightLegPoseX = new ConstantValueSource(0);
    public ValueSource rightLegPoseY = new ConstantValueSource(0);
    public ValueSource rightLegPoseZ = new ConstantValueSource(0);
    
    public ItemStack headItem = null;
    public ItemStack leftHandItem = null;
    public ItemStack rightHandItem = null;
    public ItemStack bodyItem = null;
    public ItemStack feetItem = null;

    public ArmorStandProperties() {
    }
    
    public ArmorStandProperties(Map<String, Object> config)
    {
        visible = (boolean) config.get("visible");
        hasArms = (boolean) config.get("hasArms");
        small = (boolean) config.get("small");
        
        rotation = (ValueSource) config.get("rotation");
        headPoseX = (ValueSource) config.get("headPoseX");
        headPoseY = (ValueSource) config.get("headPoseY");
        headPoseZ = (ValueSource) config.get("headPoseZ");
        bodyPoseX = (ValueSource) config.get("bodyPoseX");
        bodyPoseY = (ValueSource) config.get("bodyPoseY");
        bodyPoseZ = (ValueSource) config.get("bodyPoseZ");
        leftArmPoseX = (ValueSource) config.get("leftArmPoseX");
        leftArmPoseY = (ValueSource) config.get("leftArmPoseY");
        leftArmPoseZ = (ValueSource) config.get("leftArmPoseZ");
        rightArmPoseX = (ValueSource) config.get("rightArmPoseX");
        rightArmPoseY = (ValueSource) config.get("rightArmPoseY");
        rightArmPoseZ = (ValueSource) config.get("rightArmPoseZ");
        leftLegPoseX = (ValueSource) config.get("leftLegPoseX");
        leftLegPoseY = (ValueSource) config.get("leftLegPoseY");
        leftLegPoseZ = (ValueSource) config.get("leftLegPoseZ");
        rightLegPoseX = (ValueSource) config.get("rightLegPoseX");
        rightLegPoseY = (ValueSource) config.get("rightLegPoseY");
        rightLegPoseZ = (ValueSource) config.get("rightLegPoseZ");
        
        headItem = (ItemStack) config.get("headItem");
        leftHandItem = (ItemStack) config.get("leftHandItem");
        rightHandItem = (ItemStack) config.get("rightHandItem");
        bodyItem = (ItemStack) config.get("bodyItem");
        feetItem = (ItemStack) config.get("feetItem");
    }


    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("visible", visible);
        ret.put("hasArms", hasArms);
        ret.put("small", small);
        ret.put("rotation", rotation);
        ret.put("headPoseX", headPoseX);
        ret.put("headPoseY", headPoseY);
        ret.put("headPoseZ", headPoseZ);
        ret.put("bodyPoseX", bodyPoseX);
        ret.put("bodyPoseY", bodyPoseY);
        ret.put("bodyPoseZ", bodyPoseZ);
        ret.put("leftArmPoseX", leftArmPoseX);
        ret.put("leftArmPoseY", leftArmPoseY);
        ret.put("leftArmPoseZ", leftArmPoseZ);
        ret.put("rightArmPoseX", rightArmPoseX);
        ret.put("rightArmPoseY", rightArmPoseY);
        ret.put("rightArmPoseZ", rightArmPoseZ);
        ret.put("leftLegPoseX", leftLegPoseX);
        ret.put("leftLegPoseY", leftLegPoseY);
        ret.put("leftLegPoseZ", leftLegPoseZ);
        ret.put("rightLegPoseX", rightLegPoseX);
        ret.put("rightLegPoseY", rightLegPoseY);
        ret.put("rightLegPoseZ", rightLegPoseZ);
        ret.put("headItem", headItem);
        ret.put("leftHandItem", leftHandItem);
        ret.put("rightHandItem", rightHandItem);
        ret.put("bodyItem", bodyItem);
        ret.put("feetItem", feetItem);
        return ret;
    }
    
    
}

