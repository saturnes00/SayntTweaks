package dev.saynt.tweaks.sort;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

/** Picks out groups of slots from a menu. */
public final class MenuSlots {
	private MenuSlots() {
	}

	/** The chest/shulker part of the menu. */
	public static List<Slot> container(AbstractContainerMenu menu) {
		List<Slot> slots = new ArrayList<>();
		for (Slot slot : menu.slots) {
			if (!(slot.container instanceof Inventory)) {
				slots.add(slot);
			}
		}
		return slots;
	}

	/** The player's 27 main inventory slots: no hotbar, armor or offhand. */
	public static List<Slot> mainInventory(AbstractContainerMenu menu) {
		List<Slot> slots = new ArrayList<>();
		for (Slot slot : menu.slots) {
			int index = slot.getContainerSlot();
			if (slot.container instanceof Inventory && index >= 9 && index < 36) {
				slots.add(slot);
			}
		}
		return slots;
	}
}
