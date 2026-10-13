package dev.saynt.tweaks.sort;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import dev.saynt.tweaks.sort.ClickQueue.Click;

/**
 * A copy of some slots that reproduces what vanilla left clicks do, so a
 * whole sequence of clicks can be planned before any is sent.
 */
public final class SlotSimulation {
	private final List<Slot> slots;
	private final List<ItemStack> stacks = new ArrayList<>();
	private final List<Click> clicks = new ArrayList<>();
	private ItemStack carried = ItemStack.EMPTY;

	public SlotSimulation(List<Slot> slots) {
		this.slots = slots;
		for (Slot slot : slots) {
			stacks.add(slot.getItem().copy());
		}
	}

	public int size() {
		return stacks.size();
	}

	public ItemStack stack(int i) {
		return stacks.get(i);
	}

	public ItemStack carried() {
		return carried;
	}

	public List<Click> clicks() {
		return clicks;
	}

	public int maxStackSize(int i, ItemStack stack) {
		return slots.get(i).getMaxStackSize(stack);
	}

	public boolean isFull(int i) {
		ItemStack stack = stacks.get(i);
		return !stack.isEmpty() && stack.getCount() >= maxStackSize(i, stack);
	}

	public List<ItemStack> nonEmptyStacks() {
		return stacks.stream().filter(stack -> !stack.isEmpty()).toList();
	}

	public void click(int i) {
		clicks.add(Click.pickup(slots.get(i).index));
		ItemStack inSlot = stacks.get(i);
		if (carried.isEmpty()) {
			carried = inSlot;
			stacks.set(i, ItemStack.EMPTY);
		} else if (inSlot.isEmpty()) {
			int moved = Math.min(carried.getCount(), maxStackSize(i, carried));
			ItemStack placed = carried.copy();
			placed.setCount(moved);
			stacks.set(i, placed);
			shrinkCarried(moved);
		} else if (ItemStack.isSameItemSameComponents(carried, inSlot)) {
			int moved = Math.min(carried.getCount(), maxStackSize(i, inSlot) - inSlot.getCount());
			inSlot.grow(moved);
			shrinkCarried(moved);
		} else if (carried.getCount() <= maxStackSize(i, carried)) {
			stacks.set(i, carried);
			carried = inSlot;
		}
	}

	private void shrinkCarried(int amount) {
		carried.shrink(amount);
		if (carried.isEmpty()) {
			carried = ItemStack.EMPTY;
		}
	}
}
