package org.cubeville.effects.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.World;
import org.bukkit.Chunk;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.block.data.type.Slab;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;

import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;

import org.cubeville.effects.pluginhook.PluginHookManager;

@SerializableAs("ParticleEffect")
public class ParticleEffect extends EffectWithLocation implements EffectWithHook
{
    private static List<Material> noCollisionBlocks = Arrays.asList(Material.CHAIN, Material.IRON_BARS, Material.LIGHT_BLUE_CARPET, Material.LIGHT_GRAY_CARPET, Material.LIME_CARPET, Material.MAGENTA_CARPET, Material.ORANGE_CARPET, Material.PINK_CARPET, Material.PURPLE_CARPET, Material.RED_CARPET, Material.WHITE_CARPET, Material.YELLOW_CARPET, Material.GREEN_CARPET, Material.GRAY_CARPET, Material.BROWN_CARPET, Material.BLACK_CARPET, Material.CYAN_CARPET , Material.TWISTING_VINES);
    
    private List<ParticleEffectComponent> components;

    // 1st int: runningEffectId, 2nd int: Component, 3rd int: Timeline-No
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, ArmorStand>>> armorstands = new HashMap<>();
    private int stepsLoop;
    private int repeatCount; // 0 = indefinitely, not recommended though
    private int repeatOffset;

    public ParticleEffect(String name) {
        setName(name);
        stepsLoop = 1;
        repeatCount = 1;
    }

    public ParticleEffect(Map<String, Object> config) {
        setName((String) config.get("name"));
        stepsLoop = (int) config.get("stepsLoop");
        repeatCount = (int) config.get("repeatCount");
        repeatOffset = (int) config.get("repeatOffset");
        components = (List<ParticleEffectComponent>) config.get("components");
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = getSerializationBase();
        ret.put("stepsLoop", stepsLoop);
        ret.put("repeatCount", repeatCount);
        ret.put("repeatOffset", repeatOffset);
        ret.put("components", components);
        return ret;
    }

    public int getStepsTotal() {
        return repeatCount * repeatOffset + stepsLoop;
    }

    public boolean hasStep(int Step) {
        if(repeatCount == 0) return true;
        return Step < getStepsTotal();
    }

    public void play(Location location) {
        play(0, new StaticParticleEffectLocationCalculator(location), null, EffectManager.getNewRunningEffectId());
    }

    private void removeArmorStand(ArmorStand as) {
        as.remove();
    }

    private void removeArmorStandForTimeline(int id, int componentNo, int timelineNo) {
        HashMap<Integer, HashMap<Integer, ArmorStand>> m = armorstands.get(id);
        if(m == null) return;

        HashMap<Integer, ArmorStand> am = m.get(componentNo);
        if(am == null) return;

        ArmorStand as = am.remove(timelineNo);
        removeArmorStand(as);
    }
    
    private void removeArmorStandsForId(int id) {
        HashMap<Integer, HashMap<Integer, ArmorStand>> m = armorstands.remove(id);
        if(m == null) return;

        for(int componentNo: m.keySet()) {
            HashMap<Integer, ArmorStand> am = m.get(componentNo);
            if(am == null) continue;
            for(ArmorStand as: am.values()) {
                removeArmorStand(as);
            }
        }
    }

    public boolean play(int step, ParticleEffectLocationCalculator locationCalculator, Player player, int id) {
        if(!hasStep(step)) {
            removeArmorStandsForId(id);
            return false;
        }

        int localStep = step % getStepsLoop();

        boolean blockCollisionDetected = false;
        boolean entityCollisionDetected = false;

        int componentNo = 0;
        
        for(ParticleEffectComponent component: components) {
            if(component.isActive(localStep)) {
                boolean blockCollisionCheck = component.getBlockCollisionCheck();
                boolean entityCollisionCheck = component.getEntityCollisionCheck();

                for(int timelineNo = 0; timelineNo < component.getTimelineCount(); timelineNo++) {
                    if(component.isTimelineActive(timelineNo, localStep) == false) continue;

                    int effectStep = localStep - component.getEffectOffset(timelineNo);
                                            
                    Location location = locationCalculator.getLocationForStep(step - component.getLocationOffset(timelineNo));
                    List<Vector> particleLocations = component.getModifiedCoordinates(effectStep, false);

                    if(component.isArmorStandActive() && particleLocations.size() > 0) {
                        if(component.getRemainingStepsOfTimeline(timelineNo, localStep) <= 1) {
                            removeArmorStandForTimeline(id, componentNo, timelineNo);
                        }
                        else {
                            ArmorStandProperties properties = component.getArmorStandProperties();
                            
                            Location armorStandLocation = location.clone();
                            {
                                Vector nvec = particleLocations.get(0);
                                if(component.getDirectionalCoordinates()) {
                                    nvec = transform(location.getYaw(), location.getPitch(), nvec);
                                }
                                armorStandLocation.add(nvec);
                            }
                            
                            armorStandLocation.setYaw(armorStandLocation.getYaw() + (float) properties.rotation.getValue(effectStep));
                            
                            // id, componentNo, timelineNo
                            HashMap<Integer, HashMap<Integer, ArmorStand>> m = armorstands.get(id);
                            if(m == null) {
                                m = new HashMap<Integer, HashMap<Integer, ArmorStand>>();
                                armorstands.put(id, m);
                            }
                            
                            HashMap<Integer, ArmorStand> tlm = m.get(componentNo);
                            if(tlm == null) {
                                tlm = new HashMap<Integer, ArmorStand>();
                                m.put(componentNo, tlm);
                            }
                            
                            ArmorStand as = tlm.get(timelineNo);

                            boolean standIsNew = false;
                            if(as == null) {
                                standIsNew = true;
                                NBTTagCompound nbt = new NBTTagCompound();
                                nbt.a("id", "minecraft:armor_stand");
                                nbt.a("Small", (byte) (properties.small ? 1 : 0));
                                nbt.a("NoGravity", (byte) 1);
                                nbt.a("Invisible", (byte) 1); // (properties.visible ? 0 : 1));
                                nbt.a("Invulnerable", (byte) 1);
                                nbt.a("Marker", (byte) 1);
                                WorldServer ws = ((CraftWorld)armorStandLocation.getWorld()).getHandle();
                                Optional<Entity> entity = EntityTypes.a(nbt, ws);
                                entity.get().b(armorStandLocation.getX(), armorStandLocation.getY(), armorStandLocation.getZ(), armorStandLocation.getYaw(), armorStandLocation.getPitch());
                                ws.tryAddFreshEntityWithPassengers(entity.get(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                                as = (ArmorStand)(entity.get().getBukkitEntity());
                                as.setPersistent(false);
                                
                                // TODO: Would be nice if we could move these two to nbt too:
                                as.setBasePlate(false);
                                as.setArms(properties.hasArms);
                                
                                for(EquipmentSlot slot: Arrays.asList(EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HAND, EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.OFF_HAND)) {
                                    as.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
                                    as.addEquipmentLock(slot, ArmorStand.LockType.ADDING);
                                }
                                tlm.put(timelineNo, as);
                            }
                            else {
                                as.teleport(armorStandLocation);
                            }
                            
                            as.setHeadPose(new EulerAngle(Math.toRadians(properties.headPoseX.getValue(effectStep)),
                                                          Math.toRadians(properties.headPoseY.getValue(effectStep)),
                                                          Math.toRadians(properties.headPoseZ.getValue(effectStep))));
                            as.setBodyPose(new EulerAngle(Math.toRadians(properties.bodyPoseX.getValue(effectStep)),
                                                          Math.toRadians(properties.bodyPoseY.getValue(effectStep)),
                                                          Math.toRadians(properties.bodyPoseZ.getValue(effectStep))));
                            as.setLeftLegPose(new EulerAngle(Math.toRadians(properties.leftLegPoseX.getValue(effectStep)),
                                                             Math.toRadians(properties.leftLegPoseY.getValue(effectStep)),
                                                             Math.toRadians(properties.leftLegPoseZ.getValue(effectStep))));
                            as.setRightLegPose(new EulerAngle(Math.toRadians(properties.rightLegPoseX.getValue(effectStep)),
                                                              Math.toRadians(properties.rightLegPoseY.getValue(effectStep)),
                                                              Math.toRadians(properties.rightLegPoseZ.getValue(effectStep))));
                            if(properties.hasArms) {
                                as.setLeftArmPose(new EulerAngle(Math.toRadians(properties.leftArmPoseX.getValue(effectStep)),
                                                                 Math.toRadians(properties.leftArmPoseY.getValue(effectStep)),
                                                                 Math.toRadians(properties.leftArmPoseZ.getValue(effectStep))));
                                as.setRightArmPose(new EulerAngle(Math.toRadians(properties.rightArmPoseX.getValue(effectStep)),
                                                                  Math.toRadians(properties.rightArmPoseY.getValue(effectStep)),
                                                                  Math.toRadians(properties.rightArmPoseZ.getValue(effectStep))));
                            }
                            
                            if(standIsNew) {
                                if(properties.headItem != null) as.getEquipment().setHelmet(properties.headItem, true);
                                if(properties.bodyItem != null) as.getEquipment().setChestplate(properties.bodyItem, true);
                                if(properties.rightHandItem != null) as.getEquipment().setItemInMainHand(properties.rightHandItem, true);
                                if(properties.leftHandItem != null) as.getEquipment().setItemInOffHand(properties.leftHandItem, true);
                                if(properties.feetItem != null) as.getEquipment().setBoots(properties.feetItem, true);
                                if(properties.visible) as.setVisible(true);
                            }
                        }
                    }

                    Vector spread = new Vector(component.getSpreadX().getValue(effectStep), component.getSpreadY().getValue(effectStep), component.getSpreadZ().getValue(effectStep));
                    spread = transform(location.getYaw(), location.getPitch(), spread);

                    for(Vector vec: particleLocations) {
                        Location nloc = location.clone();
                        Vector nvec = vec.clone();
                        if(component.getDirectionalCoordinates()) {
                            nvec = transform(location.getYaw(), location.getPitch(), nvec);
                        }
                        nloc.add(nvec);

                        if(blockCollisionCheck) {
                            Block block = location.getWorld().getBlockAt(nloc);
                            if(block.getType() != Material.AIR) {
                                if(!noCollisionBlocks.contains(block.getType())) {
                                    boolean collision = false;
                                    if(block.getBlockData() instanceof Slab) {
                                        Slab slab = (Slab) block.getBlockData();
                                        if(slab.getType() == Slab.Type.DOUBLE) collision = true;
                                        else {
                                            double f = nloc.getY() - Math.floor(nloc.getY());
                                            if(slab.getType() == Slab.Type.BOTTOM) {
                                                if(f < 0.5) collision = true;
                                            }
                                            else {
                                                if(f >= 0.5) collision = true;
                                            }
                                        }
                                    }
                                    else if(block.getBlockData() instanceof GlassPane) {
                                        if(((GlassPane) block.getBlockData()).getFaces().size() > 0)
                                            collision = true;
                                    }
                                    else {
                                        collision = true;
                                    }
                                    if(collision) {
                                        if(player != null && blockCollisionDetected == false) PluginHookManager.onBlockCollisionEvent(player, block);
                                        blockCollisionDetected = true;
                                        continue;
                                    }
                                }
                            }
                        }
                        
                        if(entityCollisionCheck) {
                            List<Player> entities = nloc.getWorld().getPlayers(); // TODO: Not going to deal with bats' hitboxes for now, can be added later
                            // TODO: Also not going to deal with flying / swimming / ..., for now only standing and sneaking is supported, until I find manage to get hitboxes from minecraft itself
                            for(Player entity: entities) {
                                //((CraftLivingEntity) entity).getHandle().getBoundingBoxForCulling();
                                if(entity == player) continue;
                                if(entity.hasMetadata("vanished")) continue;
                                Vector emin = entity.getLocation().toVector();
                                emin.subtract(new Vector(0.5f, 0.0f, 0.5f));
                                Vector emax = entity.getLocation().toVector();
                                emax.add(new Vector(0.5f, entity.isSneaking() ? 1.5f : 1.9f, 0.5f));
                                if(nloc.toVector().isInAABB(emin, emax)) {
                                    if(player != null && entityCollisionDetected == false) PluginHookManager.onEntityCollisionEvent(player, entity);
                                    entityCollisionDetected = true;
                                    break;
                                }
                            }
                            if(entityCollisionDetected) continue;
                        }

                        if(component.getExternalEffect() != null) {
                            component.getExternalEffect().play(nloc);
                        }
                        
                        if(component.getParticle() != null) {

                            double speed = (double) component.getSpeed().getValue(effectStep);
                            
                            if(component.getParticle() == Particle.REDSTONE || component.getParticle() == Particle.DUST_COLOR_TRANSITION) {
                                int red = (int) (Math.round(component.getColourRed().getValue(effectStep) * 255));
                                if(red < 0) red = 0;
                                if(red > 255) red = 255;
                                int green = (int) (Math.round(component.getColourGreen().getValue(effectStep) * 255));
                                if(green < 0) green = 0;
                                if(green > 255) green = 255;
                                int blue = (int) (Math.round(component.getColourBlue().getValue(effectStep) * 255));
                                if(blue < 0) blue = 0;
                                if(blue > 255) blue = 255;
                                float size = (float) component.getSize().getValue(effectStep);
                                
                                Particle.DustOptions dustoptions;
                                if(component.getParticle() == Particle.DUST_COLOR_TRANSITION) {
                                    int tored = (int) (Math.round(component.getColourToRed().getValue(effectStep) * 255));
                                    if(tored < 0) tored = 0;
                                    if(tored > 255) tored = 255;
                                    int togreen = (int) (Math.round(component.getColourToGreen().getValue(effectStep) * 255));
                                    if(togreen < 0) togreen = 0;
                                    if(togreen > 255) togreen = 255;
                                    int toblue = (int) (Math.round(component.getColourToBlue().getValue(effectStep) * 255));
                                    if(toblue < 0) toblue = 0;
                                    if(toblue > 255) toblue = 255;
                                    dustoptions = new Particle.DustTransition(Color.fromRGB(red, green, blue), Color.fromRGB(tored, togreen, toblue), size);
                                }
                                else 
                                    dustoptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), size);
                                
                                nloc.getWorld().spawnParticle(component.getParticle(), nloc.getX(), nloc.getY(), nloc.getZ(), (int)(component.getCount().getValue(effectStep)),
                                                              spread.getX(), spread.getY(), spread.getZ(), speed, dustoptions);
                            }
                            else if(component.getParticle() == Particle.ITEM_CRACK) {
                                nloc.getWorld().spawnParticle(component.getParticle(), nloc.getX(), nloc.getY(), nloc.getZ(), (int)(component.getCount().getValue(effectStep)),
                                                              spread.getX(), spread.getY(), spread.getZ(), speed, new ItemStack(component.getMaterial()));
                            }
                            else if(component.getParticle() == Particle.BLOCK_DUST || component.getParticle() == Particle.BLOCK_CRACK || component.getParticle() == Particle.FALLING_DUST) {
                                nloc.getWorld().spawnParticle(component.getParticle(), nloc.getX(), nloc.getY(), nloc.getZ(), (int)(component.getCount().getValue(effectStep)),
                                                              spread.getX(), spread.getY(), spread.getZ(), speed, component.getMaterial().createBlockData());
                            }
                            else {
                                nloc.getWorld().spawnParticle(component.getParticle(), nloc.getX(), nloc.getY(), nloc.getZ(), (int)(component.getCount().getValue(effectStep)),
                                                              spread.getX(), spread.getY(), spread.getZ(), speed);
                            }
                        }
                    }
                }
            }

            componentNo++;
        }
        
        boolean hasNextStep = true;
        if(blockCollisionDetected || entityCollisionDetected)
            hasNextStep = false;
        else {
            if(hasStep(step + 1) == false) hasNextStep = false;
        }

        if(hasNextStep == false) removeArmorStandsForId(id);

        return hasNextStep;
    }

    private List<Player> getNearbyPlayers(Location location)
    {
        List<Player> ret = new ArrayList<>();
        List<Player> playersInWorld = location.getWorld().getPlayers();
        for(Player player: playersInWorld) {
            if(player.getLocation().distance(location) < 50) { // TODO: Does that value make sense? Should it be configurable?
                ret.add(player);
            }
        }
        return ret;
    }

    private static Vector transform(double yaw, double pitch, Vector vec) {
        final double ryaw = Math.toRadians(yaw);
        final double rpitch = -Math.toRadians(pitch);
        
        final double cosYaw = Math.cos(ryaw);
        final double sinYaw = Math.sin(ryaw);
        final double cosPitch = Math.cos(rpitch);
        final double sinPitch = Math.sin(rpitch);
        
        final Vector left = new Vector(cosYaw, 0, sinYaw);
        final Vector up = new Vector(-sinYaw * -sinPitch, cosPitch, cosYaw * -sinPitch);
        final Vector forward = new Vector(-sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
        
        return left.multiply(vec.getX())
            .add(up.multiply(vec.getY()))
            .add(forward.multiply(vec.getZ()));
    }

    public List<String> getInfo(boolean detailed, String limit) {
        List<String> ret = getInfoBase();
        ret.add("Length: " + stepsLoop);
        ret.add("Repeat: " + repeatCount);

        int count = 0;
        for(ParticleEffectComponent component: components) {
            count++;
            if(limit.length() > 0 && Integer.toString(count).equals(limit) == false) continue;
            ret.add("§6* Component: " + count + "§r");
            ret.addAll(component.getInfo(detailed));
        }
        return ret;
    }

    public String getType() {
        return "Particle";
    }
    
    public final int getStepsLoop() {
        return this.stepsLoop;
    }

    public final void setStepsLoop(final int argStepsLoop) {
        this.stepsLoop = argStepsLoop;
    }

    public final int getRepeatCount() {
        return this.repeatCount;
    }

    public final void setRepeatCount(final int argRepeatCount) {
        this.repeatCount = argRepeatCount;
    }

    public final List<ParticleEffectComponent> getComponents() {
        if(components == null) components = new ArrayList<>();
        return components;
    }

    public final void addComponent(ParticleEffectComponent component) {
        if(components == null) components = new ArrayList<>();
        components.add(component);
    }

    public final void removeComponent(int index) {
        index--;
        if(index >= components.size() || index < 0) {
            throw new RuntimeException("No component " + index + "!");
        }
        components.remove(index);
    }
    
    public final void setRepeatOffset(final int argRepeatOffset) {
        repeatOffset = argRepeatOffset;
    }

    public final int getRepeatOffset() {
        return repeatOffset;
    }

    public Set<String> getExternalEffectNames() {
        Set<String> ret = new HashSet<>();
        for(ParticleEffectComponent component: components) {
            if(component.getExternalEffectName() != null)
                ret.add(component.getExternalEffectName());
        }
        return ret;
    }

    public void setExternalEffectReference(String externalEffectName, Effect externalEffect) {
        for(ParticleEffectComponent component: components) {
            if(component.getExternalEffectName() != null) {
                if(component.getExternalEffectName().equals(externalEffectName)) {
                    component.setExternalEffect(externalEffect);
                }
            }
        }
    }
}
