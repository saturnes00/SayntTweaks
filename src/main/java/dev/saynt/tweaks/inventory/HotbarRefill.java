package dev.saynt.tweaks.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import dev.saynt.tweaks.config.SayntTweaksConfig;
import dev.saynt.tweaks.sort.ClickQueue;
import dev.saynt.tweaks.sort.MenuSlots;

/**
 * When the stack in your hand runs out (last block placed, last food eaten,
 * tool broken), swaps in a matching stack from the main inventory.
 */
public final class HotbarRefill {
	private static ItemStack lastHeld = ItemStack.EMPTY;
	private static int lastSlot = -1;

	private HotbarRefill() {
	}

	/** Called every client tick. */
	public static void tick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null || client.gameMode == null) {
			lastHeld = ItemStack.EMPTY;
			return;
		}

		Inventory inventory = player.getInventory();
		int selected = inventory.getSelectedSlot();
		ItemStack held = inventory.getItem(selected);

		boolean ranOut = selected == lastSlot && !lastHeld.isEmpty() && held.isEmpty();
		// Only while playing: in a menu the player is moving items on purpose
		if (ranOut && client.gui.screen() == null && !player.isCreative() && !player.isSpectator()
				&& !ClickQueue.isBusy() && SayntTweaksConfig.get().hotbarRefill) {
			Slot source = findReplacement(player, lastHeld);
			if (source != null) {
				// SWAP exchanges the clicked slot with the hotbar slot given as the button
				client.gameMode.handleContainerInput(player.inventoryMenu.containerId, source.index, selected,
						ContainerInput.SWAP, player);
			}
		}

		lastHeld = inventory.getItem(selected).copy();
		lastSlot = selected;
	}

	/** Prefers an identical stack, then any stack of the same item (e.g. a less worn tool). */
	private static Slot findReplacement(LocalPlayer player, ItemStack wanted) {
		Slot sameItem = null;
		for (Slot slot : MenuSlots.mainInventory(player.inventoryMenu)) {
			if (!slot.hasItem()) {
				continue;
			}
			ItemStack stack = slot.getItem();
			if (ItemStack.isSameItemSameComponents(stack, wanted)) {
				return slot;
			}
			if (sameItem == null && stack.getItem() == wanted.getItem()) {
				sameItem = slot;
			}
		}
		return sameItem;
	}
}
