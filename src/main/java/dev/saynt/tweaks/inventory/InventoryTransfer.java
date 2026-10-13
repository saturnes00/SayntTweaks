package dev.saynt.tweaks.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import dev.saynt.tweaks.sort.ClickQueue;
import dev.saynt.tweaks.sort.ClickQueue.Click;
import dev.saynt.tweaks.sort.MenuSlots;
import dev.saynt.tweaks.sort.SlotSimulation;

/**
 * Moves items between the open container and the player's main inventory.
 * The hotbar is never touched, so you keep what you are holding.
 */
public final class InventoryTransfer {
	private InventoryTransfer() {
	}

	/** Shift-clicks every inventory stack whose item is already in the container. */
	public static void depositMatching(AbstractContainerMenu menu) {
		if (!canStart(menu)) {
			return;
		}
		List<ItemStack> inContainer = MenuSlots.container(menu).stream().map(Slot::getItem).filter(s -> !s.isEmpty()).toList();
		List<Click> clicks = new ArrayList<>();
		for (Slot slot : MenuSlots.mainInventory(menu)) {
			ItemStack stack = slot.getItem();
			if (!stack.isEmpty() && inContainer.stream().anyMatch(other -> ItemStack.isSameItemSameComponents(stack, other))) {
				clicks.add(Click.quickMove(slot.index));
			}
		}
		ClickQueue.start(menu, clicks);
	}

	/** Shift-clicks every main inventory stack into the container. */
	public static void depositAll(AbstractContainerMenu menu) {
		if (!canStart(menu)) {
			return;
		}
		List<Click> clicks = new ArrayList<>();
		for (Slot slot : MenuSlots.mainInventory(menu)) {
			if (slot.hasItem()) {
				clicks.add(Click.quickMove(slot.index));
			}
		}
		ClickQueue.start(menu, clicks);
	}

	/**
	 * Moves everything from the container into the main inventory. Shift-click
	 * would fill the hotbar first, so stacks are placed by hand instead: topping
	 * up matching stacks, then filling empty slots.
	 */
	public static void takeAll(AbstractContainerMenu menu) {
		if (!canStart(menu)) {
			return;
		}
		List<Slot> container = MenuSlots.container(menu);
		List<Slot> slots = new ArrayList<>(container);
		slots.addAll(MenuSlots.mainInventory(menu));
		SlotSimulation sim = new SlotSimulation(slots);
		int first = container.size();

		for (int source = 0; source < first; source++) {
			if (sim.stack(source).isEmpty()) {
				continue;
			}
			sim.click(source);
			for (int target = first; target < sim.size() && !sim.carried().isEmpty(); target++) {
				if (!sim.isFull(target) && ItemStack.isSameItemSameComponents(sim.carried(), sim.stack(target))) {
					sim.click(target);
				}
			}
			for (int target = first; target < sim.size() && !sim.carried().isEmpty(); target++) {
				if (sim.stack(target).isEmpty()) {
					sim.click(target);
				}
			}
			if (!sim.carried().isEmpty()) {
				// Inventory full: put the rest back and stop
				sim.click(source);
				break;
			}
		}
		ClickQueue.start(menu, sim.clicks());
	}

	private static boolean canStart(AbstractContainerMenu menu) {
		return Minecraft.getInstance().player != null && menu.getCarried().isEmpty() && !ClickQueue.isBusy();
	}
}
