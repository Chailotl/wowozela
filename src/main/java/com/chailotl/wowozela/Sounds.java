package com.chailotl.wowozela;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sounds
{
	public static final SoundEvent SINE = registerSoundEvent("sine");
	public static final SoundEvent TRIANGLE = registerSoundEvent("triangle");
	public static final SoundEvent SQUARE = registerSoundEvent("square");
	public static final SoundEvent PULSE_25 = registerSoundEvent("pulse_25");
	public static final SoundEvent PULSE_12_5 = registerSoundEvent("pulse_12_5");
	public static final SoundEvent SAW = registerSoundEvent("saw");
	public static final SoundEvent NES_NOISE = registerSoundEvent("nes_noise");
	public static final SoundEvent MEOW = registerSoundEvent("meow");
	public static final SoundEvent WAWA = registerSoundEvent("wawa");
	public static final SoundEvent DOOOOOOOOH = registerSoundEvent("dooooooooh");
	public static final SoundEvent DOOT = registerSoundEvent("doot");
	public static final SoundEvent MIKU = registerSoundEvent("miku");
	public static final SoundEvent PRIMA = registerSoundEvent("prima");

	public static List<Instrument> instruments = Arrays.asList(
		new Instrument("Sine wave", SINE).volume(0.25f).interruptible(),
		new Instrument("Triangle wave", TRIANGLE).volume(0.25f).interruptible(),
		new Instrument("Saw wave", SAW).volume(0.125f).interruptible(),
		new Instrument("Square wave", SQUARE).volume(0.09375f).interruptible(),
		new Instrument("Pulse 25% wave", PULSE_25).volume(0.09375f).interruptible(),
		new Instrument("Pulse 12.5% wave", PULSE_12_5).volume(0.09375f).interruptible(),
		new Instrument("NES noise", NES_NOISE).volume(0.15f).interruptible(),
		new Instrument("Meow", MEOW).volume(0.375f),
		new Instrument("Wawa", WAWA).interruptible().looping(75610, 333043),
		new Instrument("Dooooooooh", DOOOOOOOOH).volume(0.5f).interruptible().looping(22472, 40723),
		new Instrument("Doot", DOOT).volume(0.375f),
		new Instrument("Miku", MIKU).volume(0.375f).interruptible().looping(58202, 88193),
		new Instrument("Prima", PRIMA).volume(0.5f).interruptible().looping(62587, 83063),
		new Instrument("Bass", SoundEvents.BLOCK_NOTE_BLOCK_BASS.value()),
		new Instrument("Snare Drum", SoundEvents.BLOCK_NOTE_BLOCK_SNARE.value()),
		new Instrument("Hi-hat", SoundEvents.BLOCK_NOTE_BLOCK_HAT.value()),
		new Instrument("Kick Drum", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM.value()),
		new Instrument("Glockenspiel", SoundEvents.BLOCK_NOTE_BLOCK_BELL.value()),
		new Instrument("Flute", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value()),
		new Instrument("Chimes", SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value()),
		new Instrument("Guitar", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value()),
		new Instrument("Xylophone", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value()),
		new Instrument("Vibraphone", SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE.value()),
		new Instrument("Cow Bell", SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value()),
		new Instrument("Didgeridoo", SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value()),
		new Instrument("Synthesizer", SoundEvents.BLOCK_NOTE_BLOCK_BIT.value()),
		new Instrument("Banjo", SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value()),
		new Instrument("Electric Piano", SoundEvents.BLOCK_NOTE_BLOCK_PLING.value()),
		new Instrument("Harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP.value())
	);

	public static Map<Identifier, Instrument> instrumentMap = new HashMap<>();

	public static void register()
	{
		instruments.forEach(instrument -> instrumentMap.put(instrument.id, instrument));
	}

	public static SoundEvent registerSoundEvent(String id)
	{
		Identifier soundId = Identifier.of(Main.MOD_ID, id);
		return Registry.register(Registries.SOUND_EVENT, soundId, SoundEvent.of(soundId));
	}

	public static class Instrument
	{
		public final String name;
		public final SoundEvent sound;
		public final Identifier id;
		public float volume = 1;
		public boolean interruptible = false;
		public boolean looping = false;
		public int start = 0;
		public int end = 0;

		public Instrument(String name, SoundEvent sound)
		{
			this.name = name;
			this.sound = sound;
			id = sound.getId();
		}

		public Instrument(String name, RegistryEntry.Reference<SoundEvent> reference)
		{
			this(name, reference.value());
		}

		public Instrument volume(float volume)
		{
			this.volume = volume;
			return this;
		}

		public Instrument interruptible()
		{
			interruptible = true;
			return this;
		}

		public Instrument looping(int start, int end)
		{
			looping = true;
			this.start = start;
			this.end = end;
			return this;
		}
	}
}