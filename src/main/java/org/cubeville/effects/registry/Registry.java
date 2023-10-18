package org.cubeville.effects.registry;

// TODO: Somehow remove projectiles from the projectiledamageactions with timeout

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.projectiles.ProjectileSource;

import org.cubeville.effects.Effects;
import org.cubeville.effects.hooks.BlockBreakHook;
import org.cubeville.effects.hooks.DamageOtherEntityHook;
import org.cubeville.effects.hooks.Hook;
import org.cubeville.effects.hooks.InteractHook;
import org.cubeville.effects.hooks.MoveHook;
import org.cubeville.effects.hooks.ProjectileHitHook;
import org.cubeville.effects.hooks.ProjectileLaunchHook;
import org.cubeville.effects.managers.Effect;
import org.cubeville.effects.util.ItemUtil;

@SerializableAs("Registry")
public class Registry implements ConfigurationSerializable
{
    private Map<String, RegistryHook<InteractHook>> interactEvents;
    private Map<String, RegistryHook<DamageOtherEntityHook>> damageOtherEntityEvents;
    private Map<String, RegistryHook<ProjectileLaunchHook>> projectileLaunchEvents;
    private Map<String, RegistryHook<ProjectileHitHook>> projectileHitEvents;
    private Map<String, RegistryHook<MoveHook>> moveEvents;
    private Map<String, RegistryHook<BlockBreakHook>> blockBreakEvents;
    
    private Map<String, Map<String, RegistryHook<Hook>>> eventMaps;

    static Registry instance;

    private PermissionList permissionList;

    Map<UUID, Set<ProjectileTrackerAction>> projectileHitActions;
    Map<UUID, ProjectileDamageTracker> projectileDamageActions;

    public Registry() {
	interactEvents = new HashMap<>();
	damageOtherEntityEvents = new HashMap<>();
	projectileLaunchEvents = new HashMap<>();
        projectileHitEvents = new HashMap<>();
        moveEvents = new HashMap<>();
        blockBreakEvents = new HashMap<>();

        initializeEventMaps();

        instance = this;
        permissionList = new PermissionList();

        projectileHitActions = new HashMap<>();
        projectileDamageActions = new HashMap<>();
    }

    public Registry(Map<String, Object> config) {
        // TODO!!! : wha?
        interactEvents = (Map<String, RegistryHook<InteractHook>>) config.get("interact");
        damageOtherEntityEvents = (Map<String, RegistryHook<DamageOtherEntityHook>>) config.get("damage");
        projectileLaunchEvents = (Map<String, RegistryHook<ProjectileLaunchHook>>) config.get("projectilelaunch");
        projectileHitEvents = (Map<String, RegistryHook<ProjectileHitHook>>) config.get("projectilehit");
        moveEvents = (Map<String, RegistryHook<MoveHook>>) config.get("move");
        if(moveEvents == null) moveEvents = new HashMap<>();
        blockBreakEvents = (Map<String, RegistryHook<BlockBreakHook>>) config.get("blockbreak");
        if(blockBreakEvents == null) blockBreakEvents = new HashMap<>();
        initializeEventMaps();
        permissionList = (PermissionList) config.get("permissionList");
        instance = this;
        projectileHitActions = new HashMap<>();
        projectileDamageActions = new HashMap<>();
    }

    private void initializeEventMaps() {
        eventMaps = new HashMap<>();
        eventMaps.put("interact", (Map) interactEvents);
        eventMaps.put("damage", (Map) damageOtherEntityEvents);
        eventMaps.put("projectilelaunch", (Map) projectileLaunchEvents);
        eventMaps.put("projectilehit", (Map) projectileHitEvents);
        eventMaps.put("move", (Map) moveEvents);
        eventMaps.put("blockbreak", (Map) blockBreakEvents);
    }
    
    public static Registry getInstance() {
        return instance;
    }

    public PermissionList getPermissionList() {
        return permissionList;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        for(String key: eventMaps.keySet()) ret.put(key, eventMaps.get(key));
        ret.put("permissionList", permissionList);
        return ret;
    }

    public List<String> getHookList() {
        List<String> ret = new ArrayList<>();
        for(String key: eventMaps.keySet()) {
            ret.add(key + ":");
            for(String hk: eventMaps.get(key).keySet()) {
                ret.add("  " + hk + ":");
                int cnt = 1;
                for(Hook h: eventMaps.get(key).get(hk).getHooks()) {
                    ret.add("    " + (cnt++) + ") " + ((Hook) h).getInfo());
                }
            }
        }
        return ret;
    }

    public List<String> getHookList(String itemName) {
        List<String> ret = new ArrayList<>();
        for(String key: eventMaps.keySet()) {
            if(eventMaps.get(key).containsKey(itemName)) {
                RegistryHook<Hook> rh = eventMaps.get(key).get(itemName);
                String i = "";
                if(rh.getPermission() != null) i += rh.getPermission();
                if(rh.getCooldown() != 0) {
                    if(i.length() > 0) i += ",";
                    i += (Double.valueOf(rh.getCooldown()) / 1000);
                }
                if(i.length() > 0) i = " (" + i + ")";
                ret.add(key + i + ":");
                int cnt = 1;
                for(Hook h: rh.getHooks()) {
                    ret.add("  " + (cnt++) + ") " + ((Hook) h).getInfo());
                }
            }
        }
        return ret;
    }

    private Map<String, RegistryHook<Hook>> getMapByHookType(Hook hook) {
        Map<String, RegistryHook<Hook>> ret = null;
        if(hook instanceof InteractHook) {
            ret = (Map)interactEvents;
        }
        else if(hook instanceof DamageOtherEntityHook) {
            ret = (Map)damageOtherEntityEvents;
        }
        else if(hook instanceof ProjectileLaunchHook) {
            ret = (Map)projectileLaunchEvents;
        }
        else if(hook instanceof MoveHook) {
            ret = (Map)moveEvents;
        }
        else if(hook instanceof BlockBreakHook) {
            ret = (Map)blockBreakEvents;
        }
        else if(hook instanceof ProjectileHitHook) {
            ret = (Map)projectileHitEvents;
        }
        return ret;
    }
    
    public void registerEvent(String name, Hook hook) {
        Map<String, RegistryHook<Hook>> map = getMapByHookType(hook);
        if(map.get(name) == null) map.put(name, new RegistryHook<>());
        map.get(name).getHooks().add(hook);
    }
    
    public void processInteractEvent(PlayerInteractEvent event) {
	if(event.getHand() != EquipmentSlot.HAND) return;

        String itemName = ItemUtil.getItemName(event.getItem());
        if(itemName == null) return;
        if(!interactEvents.containsKey(itemName)) return;

        RegistryHook<InteractHook> rh = interactEvents.get(itemName);
        boolean isPermitted = rh.isPermitted(event.getPlayer().getUniqueId());
        boolean processing = true;
        for(InteractHook hook: rh.getHooks()) {
            if((processing && isPermitted) || hook.alwaysActive()) {
                if(hook.process(event) == false) {
                    processing = false;
                }
            }
        }
    }

    public List<InteractHook> getInteractHooksOfItem(String ItemName) {
        if(!interactEvents.containsKey(ItemName)) {
            return null;
        }
        List<InteractHook> hooks = interactEvents.get(ItemName).getHooks();
        return hooks;
    }
    
    public void processMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String itemName = ItemUtil.getItemName(event.getPlayer().getInventory().getBoots());
        
        if(itemName == null) return;
        if(!moveEvents.containsKey(itemName)) return;

        RegistryHook<MoveHook> rh = moveEvents.get(itemName);
        boolean isPermitted = rh.isPermitted(event.getPlayer().getUniqueId());
        for(MoveHook hook: rh.getHooks()) {
            if(isPermitted || hook.alwaysActive()) {
                hook.process(event);
            }
        }
    }

    public void processBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String itemName = ItemUtil.getItemName(event.getPlayer().getInventory().getItemInMainHand());
        if(itemName != null && blockBreakEvents.containsKey(itemName)) {
            RegistryHook<BlockBreakHook> rh = blockBreakEvents.get(itemName);
            boolean isPermitted = rh.isPermitted(player.getUniqueId());
            for(BlockBreakHook hook: rh.getHooks()) {
                if(isPermitted || hook.alwaysActive()) {
                    hook.process(event);
                }
            }
        }
    }
    
    public void deregisterInteractEvent(String name, int index) {
        deregisterEvent(interactEvents, name, index);
    }

    public void deregisterProjectileLaunchEvent(String name, int index) {
        deregisterEvent(projectileLaunchEvents, name, index);
    }

    public void deregisterProjectileHitEvent(String name, int index) {
        deregisterEvent(projectileHitEvents, name, index);
    }

    public void deregisterMoveEvent(String name, int index) {
        deregisterEvent(moveEvents, name, index);
    }

    public void deregisterBlockBreakEvent(String name, int index) {
        deregisterEvent(blockBreakEvents, name, index);
    }
    
    public void processEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            String itemName = ItemUtil.getItemName(damager.getInventory().getItemInMainHand());
            if(itemName != null && damageOtherEntityEvents.containsKey(itemName)) {
                RegistryHook<DamageOtherEntityHook> rh = damageOtherEntityEvents.get(itemName);
                boolean isPermitted = rh.isPermitted(damager.getUniqueId());
                for(DamageOtherEntityHook hook: rh.getHooks()) {
                    if(isPermitted || hook.alwaysActive()) {
                        hook.process(event);
                    }
                }
            }
        }
        else if(event.getDamager() instanceof Projectile) {
            Projectile damager = (Projectile) event.getDamager();
            if(damager.getShooter() instanceof Player) {
                UUID uuid = damager.getUniqueId();
                if(projectileDamageActions.containsKey(uuid)) {
                    RegistryHook<DamageOtherEntityHook> rh = projectileDamageActions.get(uuid).getRegistryHook();
                    boolean isPermitted = rh.isPermitted(((Player) damager.getShooter()).getUniqueId());
                    for(DamageOtherEntityHook hook: rh.getHooks()) {
                        if(isPermitted || hook.alwaysActive()) {
                            hook.process(event);
                        }
                    }
                }
            }
        }
    }

    public void deregisterDamageOtherEntityEvent(String name, int index) {
        deregisterEvent(damageOtherEntityEvents, name, index);
    }

    public void processProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if(!(event.getEntity() instanceof Projectile)) return;
        ProjectileSource shooter = ((Projectile) event.getEntity()).getShooter();
        if(!(shooter instanceof Player)) return;
        Player player = (Player) shooter;

        // TODO: Will the last snowball also trigger?
        String itemName = ItemUtil.getItemName(player.getInventory().getItemInMainHand());
        if(itemName == null) return;

        if(projectileLaunchEvents.containsKey(itemName)) {
            RegistryHook<ProjectileLaunchHook> rh = projectileLaunchEvents.get(itemName);
            boolean isPermitted = rh.isPermitted(player.getUniqueId());
            for(ProjectileLaunchHook hook: rh.getHooks()) {
                if(isPermitted || hook.alwaysActive()) {
                    hook.process(event);
                }
            }
        }

        // TODO!
        // if(projectileHitEvents.containsKey(itemName)) {
        //     addProjectileHitAction((Projectile) event.getEntity(), new ProjectileTrackerHookProcessor(projectileHitEvents.get(itemName)));
        // }
        if(damageOtherEntityEvents.containsKey(itemName)) {
            addProjectileDamageAction((Projectile) event.getEntity(), damageOtherEntityEvents.get(itemName));
        }
    }

    private void deregisterEvent(Map<String, ?> map, String name, int index) {
        RegistryHook rh = (RegistryHook) map.get(name);
        List<?> list = rh.getHooks();
        if(list == null) throw new IllegalArgumentException("No hooks available for item " + name + ".");
        if(index < 1 || index > list.size()) throw new IllegalArgumentException("No hook nr " + index + " available.");
        list.remove(index - 1);
        if(list.size() == 0) map.remove(name);
    }

    public boolean isEffectInUse(Effect effect) {
        for(Map<String, RegistryHook<Hook>> eventMap: eventMaps.values()) {
            for(String s: eventMap.keySet()) {
                if(hookListUsesEffect(effect, eventMap.get(s).getHooks())) return true;
            }
        }
        return false;
    }

    private boolean hookListUsesEffect(Effect effect, List<?> list) {
        List<Hook> hooklist = (List<Hook>) list;
        for(Hook h: hooklist) {
            if(h.usesEffect(effect)) return true;
        }
        return false;
    }

    public void addProjectileHitAction(Projectile projectile, ProjectileTrackerAction action) {
        UUID uuid = projectile.getUniqueId();
        if(!projectileHitActions.containsKey(uuid)) {
            projectileHitActions.put(uuid, new HashSet<ProjectileTrackerAction>());
        }
        projectileHitActions.get(uuid).add(action);
    }

    public void processProjectileHitEvent(ProjectileHitEvent event) {
        /*Projectile projectile = event.getEntity();
        UUID uuid = projectile.getUniqueId();
        if(projectileHitActions.containsKey(uuid)) {
            for(ProjectileTrackerAction action: projectileHitActions.get(uuid)) {
                action.projectileHitEvent();
            }
        }
        if(projectileDamageActions.containsKey(uuid)) {
            Bukkit.getScheduler().runTask(Effects.getInstance(), () -> projectileDamageActions.remove(uuid));
        }*/
        if(!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();
        String itemName = ItemUtil.getItemName(player.getInventory().getItemInMainHand());
        if(itemName == null) return;

        if(projectileHitEvents.containsKey(itemName)) {
            RegistryHook<ProjectileHitHook> rh = projectileHitEvents.get(itemName);
            boolean isPermitted = rh.isPermitted(player.getUniqueId());
            for(ProjectileHitHook hook : rh.getHooks()) {
                if(isPermitted || hook.alwaysActive()) {
                    hook.process(event);
                }
            }
        }
    }

    public void removeProjectileHitAction(Projectile projectile, ProjectileTrackerAction action) {
        UUID uuid = projectile.getUniqueId();
        Set<ProjectileTrackerAction> set = projectileHitActions.get(uuid);
        if(set != null) {
            set.remove(action);
            if(set.size() == 0) projectileHitActions.remove(uuid);
        }
    }
    
    public void addProjectileDamageAction(Projectile projectile, RegistryHook<DamageOtherEntityHook> registryHook) {
        UUID uuid = projectile.getUniqueId();
        projectileDamageActions.put(uuid, new ProjectileDamageTracker(registryHook));
    }

    public void setPermission(String itemName, String eventClass, String permission) {
        Map<String, RegistryHook<Hook>> eventMap = (Map<String, RegistryHook<Hook>>) eventMaps.get(eventClass);
        if(!eventMap.containsKey(itemName)) throw new IllegalArgumentException("Item not defined in this event class.");
        eventMap.get(itemName).setPermission(permission);
    }

    public void setCooldown(String itemName, String eventClass, double cooldown) {
        Map<String, RegistryHook<Hook>> eventMap = (Map<String, RegistryHook<Hook>>) eventMaps.get(eventClass);
        if(!eventMap.containsKey(itemName)) throw new IllegalArgumentException("Item not defined in this event class.");
        eventMap.get(itemName).setCooldown(new Double(cooldown * 1000).intValue());
    }
    
    public Set<String> getEventClasses() {
        return eventMaps.keySet();
    }

    public void clearPermissionCache() {
        for(Map<String, RegistryHook<Hook>> eventMap: eventMaps.values()) {
            for(RegistryHook<Hook> hook: eventMap.values()) {
                hook.clearPermissionCache();
            }
        }
    }
}
