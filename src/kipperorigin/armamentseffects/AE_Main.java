package kipperorigin.armamentseffects;

import kipperorigin.armamentseffects.effects.AE_EffectDisarm;
import kipperorigin.armamentseffects.effects.AE_EffectDrain;
import kipperorigin.armamentseffects.effects.AE_EffectExplode;
import kipperorigin.armamentseffects.effects.AE_EffectInstakill;
import kipperorigin.armamentseffects.effects.AE_EffectParticle;
import kipperorigin.armamentseffects.effects.AE_EffectPotions;
import kipperorigin.armamentseffects.effects.AE_EffectSound;
import kipperorigin.armamentseffects.effects.AE_EffectSpawn;
import kipperorigin.armamentseffects.effects.AE_EffectStun;
import kipperorigin.armamentseffects.effects.AE_EffectTeleport;
import kipperorigin.armamentseffects.effects.AE_EffectVelocity;
import kipperorigin.armamentseffects.effects.AE_EffectWeb;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AE_Main extends JavaPlugin {

	private final AE_EffectManager listener = new AE_EffectManager();
	private final PluginManager pm = Bukkit.getPluginManager();

	@Override
	public void onEnable() {
		pm.registerEvents(listener, this);

		// drain
		AE_EffectDrain drain = new AE_EffectDrain(this);
		listener.registerEffect("drain", drain);
		listener.registerEffect("leech", drain);

		// disarm
		AE_EffectDisarm disarm = new AE_EffectDisarm();
		listener.registerEffect("disarm", disarm);
		listener.registerEffect("unarm", disarm);

		// explode
		AE_EffectExplode explode = new AE_EffectExplode();
		listener.registerEffect("explode", explode);
		listener.registerEffect("boom", explode);

		// instakill
		AE_EffectInstakill instakill = new AE_EffectInstakill();
		listener.registerEffect("instakill", instakill);
		listener.registerEffect("slay", instakill);

		// potions
		AE_EffectPotions potions = new AE_EffectPotions();
		listener.registerEffect("inflict", potions);
		listener.registerEffect("apply", potions);

		// sound
		AE_EffectSound sound = new AE_EffectSound(this);
		listener.registerEffect("playsound", sound);
		listener.registerEffect("play", sound);

		// spawnmob
		AE_EffectSpawn spawn = new AE_EffectSpawn();
		listener.registerEffect("spawn", spawn);
		listener.registerEffect("summon", spawn);

		// velocity
		AE_EffectVelocity velocity = new AE_EffectVelocity();
		listener.registerEffect("velocity", velocity);
		listener.registerEffect("speed", velocity);

		// web
		AE_EffectWeb web = new AE_EffectWeb(this);
		listener.registerEffect("web", web);
		listener.registerEffect("trap", web);

		// particle
		AE_EffectParticle particle = new AE_EffectParticle(this);
		listener.registerEffect("particle", particle);
		listener.registerEffect("display", particle);

		// disarm
		AE_EffectTeleport teleport = new AE_EffectTeleport();
		listener.registerEffect("teleport", teleport);
		listener.registerEffect("move", teleport);

		// stun
		AE_EffectStun stun = new AE_EffectStun();
		listener.registerEffect("stun", stun);
		listener.registerEffect("freeze", stun);
	}

}
