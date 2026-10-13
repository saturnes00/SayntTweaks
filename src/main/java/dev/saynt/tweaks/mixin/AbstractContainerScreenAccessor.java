package dev.saynt.tweaks.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
	@Accessor("leftPos")
	int sayntstweaks$getLeftPos();

	@Accessor("topPos")
	int sayntstweaks$getTopPos();

	@Accessor("imageWidth")
	int sayntstweaks$getImageWidth();

	@Accessor("inventoryLabelY")
	int sayntstweaks$getInventoryLabelY();
}
