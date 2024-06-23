package com.chailotl.wowozela;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements ModInitializer
{
	public static final String MOD_ID = "wowozela";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier START_WOWOZELA = Identifier.of(MOD_ID, "start");
	public static final Identifier STOP_WOWOZELA = Identifier.of(MOD_ID, "stop");
	public static final Identifier CHANGE_WOWOZELA = Identifier.of(MOD_ID, "change");
	public static final Item WOWOZELA = new WowozelaItem(new Item.Settings().maxCount(1));
	public static final SimpleParticleType DOT = FabricParticleTypes.simple();

	public static Map<UUID, Identifier> instrumentIndices = new HashMap<>();

	@Override
	public void onInitialize()
	{
		//LOGGER.info("Hello Fabric world!");

		Sounds.register();

		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "wowozela"), WOWOZELA);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(WOWOZELA);
		});

		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Main.MOD_ID, "dot"), DOT);

		PayloadTypeRegistry.playS2C().register(StartWowozelaPayload.ID, StartWowozelaPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(StopWowozelaPayload.ID, StopWowozelaPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(ChangeWowozelaPayload.ID, ChangeWowozelaPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ChangeWowozelaPayload.ID, (payload, context) ->
			context.server().execute(() -> instrumentIndices.put(context.player().getUuid(), payload.id()))
		);
	}

	public record StartWowozelaPayload(UUID uuid, Identifier id) implements CustomPayload
	{
		public static final CustomPayload.Id<StartWowozelaPayload> ID = new CustomPayload.Id<>(Main.START_WOWOZELA);
		public static final PacketCodec<RegistryByteBuf, StartWowozelaPayload> CODEC = PacketCodec.tuple(
			Uuids.PACKET_CODEC, StartWowozelaPayload::uuid,
			Identifier.PACKET_CODEC, StartWowozelaPayload::id,
			StartWowozelaPayload::new);

		@Override
		public CustomPayload.Id<? extends CustomPayload> getId()
		{
			return ID;
		}
	}

	public record StopWowozelaPayload(UUID uuid) implements CustomPayload
	{
		public static final CustomPayload.Id<StopWowozelaPayload> ID = new CustomPayload.Id<>(Main.STOP_WOWOZELA);
		public static final PacketCodec<RegistryByteBuf, StopWowozelaPayload> CODEC = PacketCodec.tuple(
			Uuids.PACKET_CODEC, StopWowozelaPayload::uuid,
			StopWowozelaPayload::new);

		@Override
		public CustomPayload.Id<? extends CustomPayload> getId()
		{
			return ID;
		}
	}

	public record ChangeWowozelaPayload(Identifier id) implements CustomPayload
	{
		public static final CustomPayload.Id<ChangeWowozelaPayload> ID = new CustomPayload.Id<>(Main.CHANGE_WOWOZELA);
		public static final PacketCodec<RegistryByteBuf, ChangeWowozelaPayload> CODEC = PacketCodec.tuple(
			Identifier.PACKET_CODEC, ChangeWowozelaPayload::id,
			ChangeWowozelaPayload::new);

		@Override
		public CustomPayload.Id<? extends CustomPayload> getId()
		{
			return ID;
		}
	}
}