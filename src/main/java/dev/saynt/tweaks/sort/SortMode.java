package dev.saynt.tweaks.sort;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public enum SortMode {
	/** Minecraft's own item order, close to the creative menu. */
	MINECRAFT,
	/** Alphabetical by displayed name. */
	NAME,
	/** Items you have the most of first. */
	QUANTITY,
	/** Blocks, tools, armor, food, potions, then everything else. */
	CATEGORY;

	private static final Comparator<ItemStack> BY_REGISTRY =
			Comparator.comparingInt(stack -> BuiltInRegistries.ITEM.getId(stack.getItem()));

	/** Tie-breakers shared by every mode, so identical items always end up side by side. */
	private static final Comparator<ItemStack> TIE_BREAK = BY_REGISTRY
			.thenComparing(stack -> stack.getHoverName().getString())
			.thenComparing(Comparator.comparingInt(ItemStack::getCount).reversed())
			.thenComparingInt(ItemStack::getDamageValue);

	public String translationKey() {
		return "sayntstweaks.sort_mode." + name().toLowerCase();
	}

	public SortMode next() {
		SortMode[] modes = values();
		return modes[(ordinal() + 1) % modes.length];
	}

	/** Builds the ordering for this mode. QUANTITY needs the stacks to count totals. */
	Comparator<ItemStack> comparator(List<ItemStack> stacks) {
		return switch (this) {
			case MINECRAFT -> TIE_BREAK;
			case NAME -> {
				Collator collator = Collator.getInstance();
				collator.setStrength(Collator.PRIMARY);
				yield Comparator.<ItemStack, String>comparing(stack -> stack.getHoverName().getString(), collator)
						.thenComparing(TIE_BREAK);
			}
			case QUANTITY -> {
				Map<Item, Integer> totals = new HashMap<>();
				for (ItemStack stack : stacks) {
					totals.merge(stack.getItem(), stack.getCount(), Integer::sum);
				}
				yield Comparator.<ItemStack>comparingInt(stack -> -totals.getOrDefault(stack.getItem(), 0))
						.thenComparing(TIE_BREAK);
			}
			case CATEGORY -> Comparator.comparingInt(SortMode::category).thenComparing(TIE_BREAK);
		};
	}

	private static int category(ItemStack stack) {
		if (stack.getItem() instanceof BlockItem) {
			return 0;
		}
		if (stack.has(DataComponents.EQUIPPABLE)) {
			return 2;
		}
		if (stack.has(DataComponents.TOOL) || stack.has(DataComponents.WEAPON) || stack.has(DataComponents.MAX_DAMAGE)) {
			return 1;
		}
		if (stack.has(DataComponents.FOOD)) {
			return 3;
		}
		if (stack.has(DataComponents.POTION_CONTENTS)) {
			return 4;
		}
		return 5;
	}
}
