package com.chailotl.wowozela;

import com.chailotl.wowozela.mixin.AccessorSoundManager;
import com.chailotl.wowozela.mixin.AccessorSoundSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class WowozelaSoundInstance extends MovingSoundInstance
{
	private final PlayerEntity player;
	private final Sounds.Instrument instrument;
	private boolean active = true;

	protected WowozelaSoundInstance(PlayerEntity player, Identifier id)
	{
		super(Sounds.instrumentMap.get(id).sound, SoundCategory.PLAYERS, SoundInstance.createRandom());
		instrument = Sounds.instrumentMap.get(id);
		repeat = true;
		repeatDelay = 0;
		volume = instrument.volume;
		this.pitch = ClientMain.getSoundPitch(player);
		this.player = player;
	}

	@Override
	public void tick()
	{
		x = player.getX();
		y = player.getY();
		z = player.getZ();

		if (active)
		{
			setPitch(ClientMain.getSoundPitch(player));
		}
	}

	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}

	public void end()
	{
		active = false;

		if (instrument.interruptible)
		{
			setDone();
		}
		else
		{
			var sourceManager = ((AccessorSoundSystem) ((AccessorSoundManager) MinecraftClient.getInstance().getSoundManager()).getSoundSystem()).getSources().getOrDefault(this, null);

			if (sourceManager == null) { return; }

			sourceManager.run(source -> source.setLooping(false));
		}
	}
}