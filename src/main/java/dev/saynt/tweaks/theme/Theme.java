package dev.saynt.tweaks.theme;

import net.minecraft.client.Minecraft;

/** Tells whether the Dark UI resource pack is currently enabled. */
public final class Theme {
	public static final String PACK_ID = "sayntstweaks:dark_ui";

	/** The dark gray Minecraft uses for container titles. */
	public static final int VANILLA_LABEL_COLOR = 0xFF404040;
	public static final int LABEL_COLOR = 0xFFE0E0E0;

	private Theme() {
	}

	public static boolean isActive() {
		return Minecraft.getInstance().getResourcePackRepository().getSelectedIds().contains(PACK_ID);
	}

	/** Light titles on the dark panels, vanilla otherwise. */
	public static int labelColor(int color) {
		return color == VANILLA_LABEL_COLOR && isActive() ? LABEL_COLOR : color;
	}
}
