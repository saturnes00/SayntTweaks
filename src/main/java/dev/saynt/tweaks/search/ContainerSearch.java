package dev.saynt.tweaks.search;

import java.text.Normalizer;
import java.util.Locale;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import dev.saynt.tweaks.mixin.AbstractContainerScreenAccessor;

/** A search box above containers: slots that don't match the query are dimmed. */
public final class ContainerSearch {
	private static final int WIDTH = 90;
	private static final int HEIGHT = 12;
	private static final int DIM_COLOR = 0xC0000000;

	private ContainerSearch() {
	}

	public static void attach(AbstractContainerScreen<?> screen) {
		AbstractContainerScreenAccessor acc = (AbstractContainerScreenAccessor) screen;
		int left = acc.sayntstweaks$getLeftPos();
		int top = acc.sayntstweaks$getTopPos();

		EditBox box = new EditBox(Minecraft.getInstance().font,
				left + acc.sayntstweaks$getImageWidth() - WIDTH, top - HEIGHT - 2, WIDTH, HEIGHT,
				Component.translatable("sayntstweaks.search"));
		box.setHint(Component.translatable("sayntstweaks.search.hint"));
		box.setMaxLength(50);
		Screens.getWidgets(screen).add(box);

		// While typing, keys go to the box only: "E" must not close the chest,
		// and number keys must not swap items with the hotbar
		ScreenKeyboardEvents.allowKeyPress(screen).register((s, event) -> {
			if (!box.isFocused() || event.isEscape()) {
				return true;
			}
			box.keyPressed(event);
			return false;
		});

		ScreenEvents.afterExtract(screen).register((s, graphics, mouseX, mouseY, delta) -> {
			String query = normalize(box.getValue().trim());
			if (query.isEmpty()) {
				return;
			}
			for (Slot slot : screen.getMenu().slots) {
				if (!matches(slot.getItem(), query)) {
					int x = left + slot.x;
					int y = top + slot.y;
					graphics.fill(x, y, x + 16, y + 16, DIM_COLOR);
				}
			}
		});
	}

	/** Matches the displayed name or the item id, ignoring case and accents ("epee" finds "Épée"). */
	private static boolean matches(ItemStack stack, String query) {
		if (stack.isEmpty()) {
			return false;
		}
		String name = normalize(stack.getHoverName().getString());
		String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().replace('_', ' ');
		return name.contains(query) || id.contains(query);
	}

	private static String normalize(String text) {
		return Normalizer.normalize(text, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.toLowerCase(Locale.ROOT);
	}
}
