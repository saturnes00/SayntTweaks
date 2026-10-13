package dev.saynt.tweaks.sort;

import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import dev.saynt.tweaks.config.SayntTweaksConfig;

/**
 * Sorts a container using only regular slot clicks, exactly like a player
 * would by hand. The server never needs to know this mod exists, so it works
 * on vanilla servers.
 *
 * The whole sort is first played out on a copy of the slots to get the list
 * of clicks, then {@link ClickQueue} sends them, all at once or spread out
 * in safe mode.
 */
public final class ContainerSorter {
	private ContainerSorter() {
	}

	/** Sorts the chest/shulker part of the menu. */
	public static void sortContainer(AbstractContainerMenu menu) {
		sort(menu, MenuSlots.container(menu));
	}

	/** Sorts the player's 27 main inventory slots, leaving the hotbar, armor and offhand alone. */
	public static void sortPlayerInventory(AbstractContainerMenu menu) {
		sort(menu, MenuSlots.mainInventory(menu));
	}

	private static void sort(AbstractContainerMenu menu, List<Slot> slots) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.gameMode == null || !menu.getCarried().isEmpty() || ClickQueue.isBusy()) {
			return;
		}

		SlotSimulation sim = new SlotSimulation(slots);
		mergeStacks(sim);
		orderStacks(sim, SayntTweaksConfig.get().sortMode.comparator(sim.nonEmptyStacks()));
		ClickQueue.start(menu, sim.clicks());
	}

	/** Combines partial stacks of the same item so each item has at most one partial stack. */
	private static void mergeStacks(SlotSimulation sim) {
		for (int i = 0; i < sim.size(); i++) {
			for (int j = i + 1; j < sim.size(); j++) {
				ItemStack into = sim.stack(i);
				if (into.isEmpty() || into.getCount() >= sim.maxStackSize(i, into)) {
					break;
				}
				if (ItemStack.isSameItemSameComponents(into, sim.stack(j))) {
					sim.click(j);
					sim.click(i);
					// Whatever didn't fit goes back where it came from
					if (!sim.carried().isEmpty()) {
						sim.click(j);
					}
				}
			}
		}
	}

	/**
	 * Selection sort: for each position, bring the smallest remaining stack there.
	 * A swap is three clicks: pick up the source, drop it on the target (which picks
	 * the target's stack up), then drop that into the now-empty source.
	 */
	private static void orderStacks(SlotSimulation sim, Comparator<ItemStack> order) {
		for (int t = 0; t < sim.size(); t++) {
			int best = -1;
			for (int s = t; s < sim.size(); s++) {
				ItemStack stack = sim.stack(s);
				if (!stack.isEmpty() && (best < 0 || order.compare(stack, sim.stack(best)) < 0)) {
					best = s;
				}
			}
			if (best < 0) {
				return;
			}

			ItemStack current = sim.stack(t);
			if (best == t || (!current.isEmpty() && order.compare(current, sim.stack(best)) == 0)) {
				continue;
			}

			sim.click(best);
			sim.click(t);
			if (!sim.carried().isEmpty()) {
				sim.click(best);
			}
		}
	}
}
