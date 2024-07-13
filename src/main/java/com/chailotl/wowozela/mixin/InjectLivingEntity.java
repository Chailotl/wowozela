package com.chailotl.wowozela.mixin;

import com.chailotl.wowozela.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class InjectLivingEntity
{
	@Shadow
	protected ItemStack activeItemStack;
	@Shadow
	public abstract boolean isUsingItem();

	@Inject(
		method = "isBlocking",
		at = @At("HEAD"),
		cancellable = true
	)
	private void undoSlowdown(CallbackInfoReturnable<Boolean> cir)
	{
		if (isUsingItem() && activeItemStack.isOf(Main.WOWOZELA))
		{
			cir.setReturnValue(false);
		}
	}
}
