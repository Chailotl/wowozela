package com.chailotl.wowozela;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class WowozelaItem extends Item
{
	public WowozelaItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.BLOCK;
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return true;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{

		tooltip.add(Text.literal("Press [").formatted(Formatting.DARK_GRAY)
			.append(Text.keybind("key.wowozela.changeInstrument").formatted(Formatting.GRAY))
			.append(Text.literal("] to change instrument").formatted(Formatting.DARK_GRAY)));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		if (world.isClient)
		{
			if (ClientMain.isLocalPlayer(user))
			{
				ClientMain.addWowozela(user, ClientMain.localInstrument);
			}
		}
		else
		{
			UUID uuid = user.getUuid();
			Identifier id = Main.instrumentIndices.getOrDefault(uuid, Sounds.SINE.getId());
			var payload = new Main.StartWowozelaPayload(uuid, id);

			PlayerLookup.all(world.getServer()).forEach(player ->
			{
				if (!player.equals(user))
				{
					ServerPlayNetworking.send(player, payload);
				}
			});
		}

		return ItemUsage.consumeHeldItem(world, user, hand);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{
		stopWowozela(world, user);
		return stack;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		stopWowozela(world, user);
	}

	private void stopWowozela(World world, LivingEntity user)
	{
		if (world.isClient)
		{
			if (user instanceof PlayerEntity player && ClientMain.isLocalPlayer(player))
			{
				ClientMain.removeWowozela(player);
			}
		}
		else
		{
			PlayerLookup.all(world.getServer()).forEach(player ->
			{
				if (!player.equals(user))
				{
					ServerPlayNetworking.send(player, new Main.StopWowozelaPayload(user.getUuid()));
				}
			});
		}
	}
}