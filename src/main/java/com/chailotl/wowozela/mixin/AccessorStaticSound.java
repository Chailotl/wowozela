package com.chailotl.wowozela.mixin;

import net.minecraft.client.sound.StaticSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.OptionalInt;

@Mixin(StaticSound.class)
public interface AccessorStaticSound
{
	@Invoker
	OptionalInt callGetStreamBufferPointer();
}
