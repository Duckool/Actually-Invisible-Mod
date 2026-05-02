package duckool.actuallyinvisible.mixin;

import static duckool.actuallyinvisible.ActuallyInvisible.isActuallyInvisible;
import duckool.actuallyinvisible.GameRules;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


//No Mob Detection
@Mixin(TargetingConditions.class)
public class TargetingConditionsMixin {

	@Inject(at = @At("HEAD"), method = "test", cancellable = true)
	private void onTest(ServerLevel level, LivingEntity targeter, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {

		boolean mobDetection = level.getGameRules().get(GameRules.MOB_CAN_DETECT_BOOLEAN_GAMERULE);

		if (mobDetection) { //if mob can detect
			return;
		}

		if (target instanceof Player player) {

			boolean isActuallyInvisible = isActuallyInvisible(player);
			boolean attackedByThisPlayer = (targeter != null) && (targeter.getLastHurtByMob() == player);
			boolean mobReaction = level.getGameRules().get(GameRules.MOB_CAN_REACT_BOOLEAN_GAMERULE);

			if (!mobReaction) { //if mob can't react
				return;
			}

			if (isActuallyInvisible && !attackedByThisPlayer) {
				cir.setReturnValue(false);
			}
		}
	}
}
