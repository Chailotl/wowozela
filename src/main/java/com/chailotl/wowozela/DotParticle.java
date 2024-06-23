package com.chailotl.wowozela;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class DotParticle extends SpriteBillboardParticle
{
	protected DotParticle(ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider provider)
	{
		super(clientWorld, x, y, z);
		setSprite(provider);

		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;

		gravityStrength = 0;
		velocityMultiplier = 0.5f;
		maxAge = 20 * 5;
		scale = 1/32f;
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	protected int getBrightness(float tint)
	{
		return 15728880;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider provider;

		public Factory(SpriteProvider provider)
		{
			this.provider = provider;
		}

		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velX, double velY, double velZ)
		{
			return new DotParticle(world, x, y, z, velX, velY, velZ, provider);
		}
	}
}
