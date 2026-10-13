package dev.saynt.tweaks.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.saynt.tweaks.theme.Theme;

/** Makes the container titles readable on the dark panels. */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenLabelsMixin {
	@Redirect(method = "extractLabels", require = 0, at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"))
	private void sayntstweaks$themedLabel(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y,
			int color, boolean shadow) {
		graphics.text(font, text, x, y, Theme.labelColor(color), shadow);
	}
}
