package dev.saynt.tweaks;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;

import dev.saynt.tweaks.config.SayntTweaksConfig;
import dev.saynt.tweaks.inventory.InventoryTransfer;
import dev.saynt.tweaks.mixin.AbstractContainerScreenAccessor;
import dev.saynt.tweaks.sort.ContainerSorter;

/** The small buttons added to container screens. */
final class ContainerButtons {
	private static final int SIZE = 10;
	private static final int MARGIN_RIGHT = 7;
	private static final int SPACING = 2;

	private static final WidgetSprites SORT = sprites("sort_button");
	private static final WidgetSprites DEPOSIT = sprites("deposit_button");
	private static final WidgetSprites DEPOSIT_ALL = sprites("deposit_all_button");
	private static final WidgetSprites TAKE_ALL = sprites("take_all_button");

	private ContainerButtons() {
	}

	/** Sort button on the title row, for the chest or shulker box. */
	static void addContainerSort(AbstractContainerScreen<?> screen) {
		addSort(screen, "sayntstweaks.sort.container", ContainerSorter::sortContainer, 0, ContainerButtons::titleRow);
	}

	/** Sort button on the "Inventory" label row, for the player's main inventory. */
	static void addInventorySort(AbstractContainerScreen<?> screen) {
		addSort(screen, "sayntstweaks.sort.inventory", ContainerSorter::sortPlayerInventory, 0, ContainerButtons::inventoryRow);
	}

	/** Transfer buttons: "take all" next to the container sort, the deposits next to the inventory sort. */
	static void addTransfers(AbstractContainerScreen<?> screen) {
		add(screen, TAKE_ALL, tooltip("sayntstweaks.take_all"),
				b -> InventoryTransfer.takeAll(screen.getMenu()), 1, ContainerButtons::titleRow);
		add(screen, DEPOSIT, tooltip("sayntstweaks.deposit"),
				b -> InventoryTransfer.depositMatching(screen.getMenu()), 1, ContainerButtons::inventoryRow);
		add(screen, DEPOSIT_ALL, tooltip("sayntstweaks.deposit_all"),
				b -> InventoryTransfer.depositAll(screen.getMenu()), 2, ContainerButtons::inventoryRow);
	}

	/** A title line plus a gray explanation line. */
	private static Supplier<Component> tooltip(String key) {
		Component text = Component.translatable(key).append("\n").append(Component.translatable(key + ".hint"));
		return () -> text;
	}

	private static void addSort(AbstractContainerScreen<?> screen, String tooltipKey,
			Consumer<AbstractContainerMenu> sort, int column, ToIntFunction<AbstractContainerScreenAccessor> y) {
		Supplier<Component> tooltip = () -> Component.translatable(tooltipKey)
				.append("\n")
				.append(Component.translatable("sayntstweaks.sort_mode",
						Component.translatable(SayntTweaksConfig.get().sortMode.translationKey())))
				.append("\n")
				.append(Component.translatable("sayntstweaks.sort_mode.hint"));
		add(screen, SORT, tooltip, b -> {
			if (Minecraft.getInstance().hasShiftDown()) {
				SayntTweaksConfig config = SayntTweaksConfig.get();
				config.sortMode = config.sortMode.next();
				SayntTweaksConfig.save();
			} else {
				sort.accept(screen.getMenu());
			}
		}, column, y);
	}

	/**
	 * Adds a button aligned to the right edge of the panel; {@code column} counts
	 * buttons from the right. The tooltip is rebuilt when its text changes.
	 */
	private static void add(AbstractContainerScreen<?> screen, WidgetSprites sprites, Supplier<Component> tooltip,
			Consumer<ImageButton> onPress, int column, ToIntFunction<AbstractContainerScreenAccessor> y) {
		AbstractContainerScreenAccessor acc = (AbstractContainerScreenAccessor) screen;
		ImageButton button = new ImageButton(0, 0, SIZE, SIZE, sprites, b -> onPress.accept((ImageButton) b));
		Screens.getWidgets(screen).add(button);

		Component[] shown = {null};
		Runnable update = () -> {
			// The recipe book can slide the inventory sideways, so follow it every frame
			int right = acc.sayntstweaks$getLeftPos() + acc.sayntstweaks$getImageWidth() - MARGIN_RIGHT;
			button.setX(right - SIZE - column * (SIZE + SPACING));
			button.setY(y.applyAsInt(acc));

			Component text = tooltip.get();
			if (!text.equals(shown[0])) {
				shown[0] = text;
				button.setTooltip(Tooltip.create(text));
			}
		};
		update.run();
		ScreenEvents.beforeExtract(screen).register((s, graphics, mouseX, mouseY, delta) -> update.run());
	}

	private static int titleRow(AbstractContainerScreenAccessor acc) {
		return acc.sayntstweaks$getTopPos() + 5;
	}

	private static int inventoryRow(AbstractContainerScreenAccessor acc) {
		return acc.sayntstweaks$getTopPos() + acc.sayntstweaks$getInventoryLabelY() - 1;
	}

	private static WidgetSprites sprites(String name) {
		return new WidgetSprites(
				Identifier.fromNamespaceAndPath(SayntTweaks.MOD_ID, name),
				Identifier.fromNamespaceAndPath(SayntTweaks.MOD_ID, name + "_highlighted"));
	}
}
