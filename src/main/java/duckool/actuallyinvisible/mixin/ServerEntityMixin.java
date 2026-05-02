package duckool.actuallyinvisible.mixin;

import static duckool.actuallyinvisible.ActuallyInvisible.isActuallyInvisible;

import duckool.actuallyinvisible.GameRules;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//No Visible armor
@Mixin(ServerEntity.class)
public class ServerEntityMixin {

	@Shadow
	@Final
	private ServerLevel level;

	@Redirect(
			method = { "sendPairingData"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
			)
	)

	//Send Empty equipment slots for server if player is invisible
	private ItemStack hideArmorPackets(LivingEntity entity, EquipmentSlot slot) {

		boolean gearVisibility = level.getGameRules().get(GameRules.GEAR_VISIBILITY_BOOLEAN_GAMERULE);

		if (!gearVisibility){
			return entity.getItemBySlot(slot);
		}

		if (entity instanceof Player player) {

			boolean isActuallyInvisible = isActuallyInvisible(player);

			if (isActuallyInvisible) {
				return ItemStack.EMPTY;
			}
		}
		return entity.getItemBySlot(slot);
	}
}