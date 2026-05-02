	package duckool.actuallyinvisible.mixin;

	import com.mojang.datafixers.util.Pair;
	import duckool.actuallyinvisible.GameRules;
	import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
	import net.minecraft.server.level.ServerPlayer;
	import net.minecraft.core.particles.ParticleOptions;
	import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
	import net.minecraft.world.effect.MobEffectInstance;
	import net.minecraft.world.effect.MobEffects;
	import net.minecraft.world.entity.Entity;
	import net.minecraft.world.entity.EquipmentSlot;
	import net.minecraft.world.entity.LivingEntity;
	import net.minecraft.world.entity.player.Player;
	import net.minecraft.world.item.ItemStack;
	import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
	import org.spongepowered.asm.mixin.Mixin;
	import org.spongepowered.asm.mixin.Unique;
	import org.spongepowered.asm.mixin.injection.At;
	import org.spongepowered.asm.mixin.injection.Inject;
	import org.spongepowered.asm.mixin.injection.ModifyArg;
	import org.spongepowered.asm.mixin.injection.ModifyVariable;
	import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

	import java.util.ArrayList;
	import java.util.Collection;
	import java.util.List;

	import static duckool.actuallyinvisible.ActuallyInvisible.isActuallyInvisible;
	import static duckool.actuallyinvisible.ActuallyInvisible.getInvisibilityParticleOptions;

	@Mixin(LivingEntity.class)
	public class LivingEntityMixin {

		@ModifyArg(
				method = "handleEquipmentChanges",
				at = @At(
						value = "INVOKE",
						target = "Lnet/minecraft/network/protocol/game/ClientboundSetEquipmentPacket;<init>(ILjava/util/List;)V"
				),
				index = 1
		)
		private List<Pair<EquipmentSlot, ItemStack>> spoofLiveArmorPacket(List<Pair<EquipmentSlot, ItemStack>> originalList) {
			LivingEntity self = (LivingEntity) (Object) this;

			if (!(self instanceof ServerPlayer player) || !isActuallyInvisible(player)) {
				return originalList;
			}

			boolean isGearVisible = player.level().getGameRules().get(GameRules.GEAR_VISIBILITY_BOOLEAN_GAMERULE);

			if (isGearVisible) {
				return originalList;
			}

			List<Pair<EquipmentSlot, ItemStack>> fakeList = new ArrayList<>(originalList.size());

			for (Pair<EquipmentSlot, ItemStack> pair : originalList) {
				fakeList.add(Pair.of(pair.getFirst(), ItemStack.EMPTY));
			}

			return fakeList;
		}

		@Inject(method = "onEffectAdded", at = @At("TAIL"))
		private void onPotionAdded(MobEffectInstance effect, Entity source, CallbackInfo ci) {
			LivingEntity self = (LivingEntity) (Object) this;

			if (effect.getEffect() == MobEffects.INVISIBILITY) {
				if (self instanceof Player player && !player.level().isClientSide()) {
					forceArmorPacketUpdate(player);
				}
			}
		}

		@Inject(method = "onEffectsRemoved", at = @At("TAIL"))
		private void onAllPotionsRemoved(Collection<MobEffectInstance> effects, CallbackInfo ci) {
			LivingEntity self = (LivingEntity) (Object) this;

			boolean lostInvisibility = effects.stream().anyMatch(e -> e.getEffect() == MobEffects.INVISIBILITY);

			if (lostInvisibility) {
				if (self instanceof Player player && !player.level().isClientSide()) {
					forceArmorPacketUpdate(player);
				}
			}
		}

		@Unique
		private void forceArmorPacketUpdate(Player player) {
			List<Pair<EquipmentSlot, ItemStack>> equipmentList = new ArrayList<>();

			boolean invisible = isActuallyInvisible(player);

			for (EquipmentSlot slot : EquipmentSlot.VALUES) {
				ItemStack item = player.getItemBySlot(slot);
				equipmentList.add(Pair.of(slot, invisible ? ItemStack.EMPTY : item.copy()));
			}

			ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(player.getId(), equipmentList);

			if (player instanceof ServerPlayer serverPlayer) {
				for (ServerPlayer observer : PlayerLookup.tracking(serverPlayer)) {
					observer.connection.send(packet);
				}
			}
		}

		//PARTICLES RANGE
		@ModifyVariable(
				method = "updateSynchronizedMobEffectParticles",
				at = @At("STORE"),
				ordinal = 0
		)
		private List<ParticleOptions> muteGlobalInvisibilityParticles(List<ParticleOptions> visibleEffectParticles) {
			LivingEntity self = (LivingEntity) (Object) this;

			if (self instanceof Player player && isActuallyInvisible(player)) {

				return new ArrayList<>();
			}

			return visibleEffectParticles;
		}

		@Inject(method = "tick", at = @At("TAIL"))
		private void bubbleVisibility(CallbackInfo ci) {

			LivingEntity self = (LivingEntity) (Object) this;

			if (!self.level().isClientSide() && self instanceof ServerPlayer invisiblePlayer) {

				int block_range = invisiblePlayer.level().getGameRules().get(GameRules.BUBBLE_VISIBILITY_RANGE_INT_GAMERULE);

				if (block_range <= 0) {
					return;
				}

				final double blockRangeSqr = block_range * block_range;

				if (isActuallyInvisible(invisiblePlayer)) {
					if (invisiblePlayer.tickCount % 10 == 0) {

						for (ServerPlayer observer : PlayerLookup.tracking(invisiblePlayer)) {

							if (invisiblePlayer.distanceToSqr(observer) <= blockRangeSqr) {

								ParticleOptions defaultParticle = getInvisibilityParticleOptions(invisiblePlayer);

								if (defaultParticle != null) {

									int bubbleCount = 1;

									ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
											defaultParticle, //particle type
											false, //overrideLimiter
											false, //alwaysShow
											invisiblePlayer.getX(), //Pos X
											invisiblePlayer.getY() + 1.0, //Pos Y
											invisiblePlayer.getZ(), //Pos Z
											0.3F, 0.5F, 0.3F, //Bounding Box X Y Z
											0.0F, //Speed
											bubbleCount //Count
									);
									observer.connection.send(packet);
								}
							}
						}
					}
				}
			}
		}
	}