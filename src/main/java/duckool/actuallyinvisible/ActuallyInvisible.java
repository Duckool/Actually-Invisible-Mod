package duckool.actuallyinvisible;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;

import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActuallyInvisible implements ModInitializer {
	public static final String MOD_ID = "actually-invisible";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		var forceLoad = GameRules.ACTUALLY_INVISIBLE_CATEGORY;
		LOGGER.info("You're actually invisible now!");
	}

	//Stupid name just for identifying reasons
	public static boolean isActuallyInvisible(Player player) {
		return player.hasEffect(MobEffects.INVISIBILITY);
	}

	//Return ParticleOptions of Invisibility effect
	public static ParticleOptions getInvisibilityParticleOptions(Player player) {
		MobEffectInstance effect = player.getEffect(MobEffects.INVISIBILITY);

		if (effect != null) {
			return effect.getParticleOptions();
		}
		return null;
	}
}