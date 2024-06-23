package com.chailotl.wowozela.mixin;

import com.chailotl.wowozela.Sounds;
import net.minecraft.client.sound.*;
import net.minecraft.util.Identifier;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.SOFTLoopPoints;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class InjectSoundSystem
{
	@Unique
	private static Identifier id;

	@Inject(
		method = "play(Lnet/minecraft/client/sound/SoundInstance;)V",
		at = @At("HEAD")
	)
	private void getId(SoundInstance sound, CallbackInfo ci)
	{
		id = sound.getId();
	}

	@Inject(
		method = "method_19752",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/sound/Source;setBuffer(Lnet/minecraft/client/sound/StaticSound;)V"
		)
	)
	private static void setSoftLoopPoints(StaticSound soundx, Source source, CallbackInfo ci)
	{
		Sounds.Instrument instrument = Sounds.instrumentMap.getOrDefault(id, null);

		if (instrument != null && instrument.looping)
		{
			((AccessorStaticSound) soundx).callGetStreamBufferPointer().ifPresent(pointer ->
			{
				AL11.alBufferiv(pointer, SOFTLoopPoints.AL_LOOP_POINTS_SOFT, new int[]{instrument.start, instrument.end});
			});
		}
	}
}
