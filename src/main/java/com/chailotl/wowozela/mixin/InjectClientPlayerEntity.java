package com.chailotl.wowozela.mixin;

import com.chailotl.wowozela.Main;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class InjectClientPlayerEntity extends LivingEntity
{
	protected InjectClientPlayerEntity(EntityType<? extends LivingEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Shadow
	public Input input;
	@Shadow
	private Hand activeHand;
	@Shadow
	public abstract boolean isUsingItem();

	@Inject(
		method = "tickMovement",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tickMovement()V"
		)
	)
	private void undoSlowdown(CallbackInfo ci)
	{
		if (isUsingItem() && !hasVehicle() && getStackInHand(activeHand).isOf(Main.WOWOZELA))
		{
			input.movementSideways *= 5F;
			input.movementForward *= 5F;
		}
	}
}