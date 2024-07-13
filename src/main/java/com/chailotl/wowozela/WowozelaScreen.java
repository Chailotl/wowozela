package com.chailotl.wowozela;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class WowozelaScreen extends Screen
{
	protected WowozelaScreen()
	{
		super(Text.literal("Wowozela instrument selection"));
	}

	@Override
	protected void init()
	{
		addDrawableChild(new InstrumentListWidget());
	}

	@Override
	public boolean shouldPause()
	{
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
	{
		return hoveredElement(mouseX, mouseY).filter(element -> element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)).isPresent();
	}

	private class InstrumentListWidget extends AlwaysSelectedEntryListWidget<InstrumentItem>
	{
		public InstrumentListWidget()
		{
			super(
				WowozelaScreen.this.client,
				WowozelaScreen.this.width,
				WowozelaScreen.this.height - 77,
				40,
				16
			);

			Sounds.instruments.forEach(instrument -> addEntry(new InstrumentItem(instrument)));

			children().stream().filter(entry -> entry.instrument.id == ClientMain.localInstrument).findFirst().ifPresent(entry -> {
				setSelected(entry);
				centerScrollOn(entry);
			});
		}
	}

	private class InstrumentItem extends AlwaysSelectedEntryListWidget.Entry<InstrumentItem>
	{
		private final Sounds.Instrument instrument;

		public InstrumentItem(Sounds.Instrument instrument)
		{
			this.instrument = instrument;
		}

		@Override
		public Text getNarration()
		{
			return Text.translatable("narrator.select", instrument.name);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta)
		{
			context.drawTextWithShadow(WowozelaScreen.this.textRenderer, instrument.name, x + 5, y + 2, 16777215);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0)
			{
				ClientMain.localInstrument = instrument.id;
				ClientPlayNetworking.send(new Main.ChangeWowozelaPayload(instrument.id));
				close();
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
