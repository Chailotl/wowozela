package com.chailotl.wowozela;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.StreamSupport;

@Environment(EnvType.CLIENT)
public class ClientMain implements ClientModInitializer
{
	public static Map<UUID, Wowozela> wowozelas = new HashMap<>();

	public static List<String> keys = Arrays.asList(
		"F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F",
		"F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#"
	);

	public static List<Boolean> notFlats = Arrays.asList(
		false, true, false, true, false, true, true, false, true, false, true, true,
		false, true, false, true, false, true, true, false, true, false, true, true, false
	);

	public static Identifier localInstrument = Sounds.SINE.getId();

	private static KeyBinding changeInstrumentKeyBind;

	@Override
	public void onInitializeClient()
	{
		ParticleFactoryRegistry.getInstance().register(Main.DOT, DotParticle.Factory::new);

		changeInstrumentKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.wowozela.changeInstrument",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_R,
			"category.wowozela.wowozela"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (changeInstrumentKeyBind.wasPressed())
			{
				if (client.player == null) { return; }

				if (StreamSupport.stream(client.player.getHandItems().spliterator(), false).anyMatch(item -> item.isOf(Main.WOWOZELA)))
				{
					openWowozelaScreen();
				}
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Main.StartWowozelaPayload.ID, (payload, context) -> {
			ClientWorld world = context.client().world;
			if (world == null) { return; }
			PlayerEntity player = world.getPlayerByUuid(payload.uuid());
			if (player == null) { return; }
			addWowozela(player, payload.id());
		});

		ClientPlayNetworking.registerGlobalReceiver(Main.StopWowozelaPayload.ID, (payload, context) -> {
			ClientWorld world = context.client().world;
			if (world == null) { return; }
			PlayerEntity player = world.getPlayerByUuid(payload.uuid());
			if (player == null) { return; }
			removeWowozela(player);
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			ParticleManager particleManager = MinecraftClient.getInstance().particleManager;
			var toRemove = new LinkedList<PlayerEntity>();

			wowozelas.values().forEach(wowozela -> {
				PlayerEntity player = wowozela.player;

				for (int i = 0; i < 6; ++i)
				{
					float tickDelta = i / 6f;
					float pitch = wowozela.getPitch(player, tickDelta);
					float percent = getPitchPercent(pitch);
					Color color = Color.getHSBColor(percent + 1 / 3f, 1, 1);

					Vec3d pos = wowozela.getPos(player, tickDelta).add(0, player.getStandingEyeHeight(), 0);
					Vec3d dir = Vec3d.fromPolar(pitch, wowozela.getYaw(player, tickDelta)).multiply(0.8);

					Particle particle = particleManager.addParticle(Main.DOT, pos.x, pos.y, pos.z, dir.x, dir.y, dir.z);

					if (particle != null)
					{
						particle.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
					}
				}

				wowozela.updatePrevValues();

				// Catch if player doesn't exists (disconnected), died, or isn't using the wowozela
				if (client.world.getPlayerByUuid(player.getUuid()) == null ||
					!player.isAlive() ||
					!player.getStackInHand(player.getActiveHand()).isOf(Main.WOWOZELA))
				{
					toRemove.add(wowozela.player);
				}
			});

			toRemove.forEach(ClientMain::removeWowozela);
		});

		HudRenderCallback.EVENT.register((context, tickDelta) -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			float percent = (-player.getPitch() + 90f) / 180f;
			Color color = Color.getHSBColor(percent + 1 / 3f, 1, 1);

			if (StreamSupport.stream(player.getHandItems().spliterator(), false).anyMatch(item -> item.isOf(Main.WOWOZELA)))
			{
				Window window = MinecraftClient.getInstance().getWindow();
				TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
				int x = window.getScaledWidth() / 2;
				int y = window.getScaledHeight() / 2;

				float scale = percent * 24f;
				String key = keys.get(Math.round(scale));
				int offset = 16;

				context.drawTextWithShadow(renderer, key, x + 21, y - 3, color.getRGB());

				for (int i = 0; i < notFlats.size(); ++i)
				{
					boolean notFlat = notFlats.get(i);
					context.drawHorizontalLine(x + 8, x + (notFlat ? 16 : 12), y + (int) (scale * offset) - i * offset, notFlat ? Colors.WHITE : Colors.GRAY);
				}
			}
		});
	}

	public static void openWowozelaScreen()
	{
		MinecraftClient.getInstance().setScreen(new WowozelaScreen());
	}

	public static void addWowozela(PlayerEntity player, Identifier id)
	{
		if (wowozelas.containsKey(player.getUuid()))
		{
			removeWowozela(player);
		}
		wowozelas.put(player.getUuid(), new Wowozela(player, id));
	}

	public static void removeWowozela(PlayerEntity player)
	{
		Wowozela wowozela = wowozelas.getOrDefault(player.getUuid(), null);
		if (wowozela == null) { return; }
		wowozela.soundInstance.end();
		wowozelas.remove(player.getUuid());
	}

	public static class Wowozela
	{
		public final PlayerEntity player;
		public final WowozelaSoundInstance soundInstance;
		private float prevPitch;
		private float prevYaw;
		private Vec3d prevPos;

		public Wowozela(PlayerEntity player, Identifier id)
		{
			this.player = player;
			soundInstance = new WowozelaSoundInstance(player, id);
			updatePrevValues();

			MinecraftClient.getInstance().getSoundManager().play(soundInstance);
		}

		public void updatePrevValues()
		{
			prevPitch = player.getPitch();
			prevYaw = player.getYaw();
			prevPos = player.getPos();
		}

		public float getPitch(PlayerEntity player, float tickDelta)
		{
			return MathHelper.lerp(tickDelta, prevPitch, player.getPitch());
		}

		public float getYaw(PlayerEntity player, float tickDelta)
		{
			return MathHelper.lerp(tickDelta, prevYaw, player.getYaw());
		}

		public Vec3d getPos(PlayerEntity player, float tickDelta)
		{
			Vec3d pos = player.getPos();
			double x = MathHelper.lerp(tickDelta, prevPos.x, pos.getX());
			double y = MathHelper.lerp(tickDelta, prevPos.y, pos.getY());
			double z = MathHelper.lerp(tickDelta, prevPos.z, pos.getZ());
			return new Vec3d(x, y, z);
		}
	}

	public static float getPitchPercent(float pitch)
	{
		return (-pitch + 90f) / 180f;
	}

	public static float getPitchPercent(PlayerEntity player)
	{
		return getPitchPercent(player.getPitch());
	}

	public static float getSoundPitch(PlayerEntity player)
	{
		float percent = getPitchPercent(player);
		return (float)Math.pow(2.0, percent * 2 - 1);
	}

	public static boolean isLocalPlayer(PlayerEntity player)
	{
		PlayerEntity local = MinecraftClient.getInstance().player;
		return local != null && player.getUuid() == local.getUuid();
	}
}