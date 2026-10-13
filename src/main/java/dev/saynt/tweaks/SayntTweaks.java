package dev.saynt.tweaks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.saynt.tweaks.config.SayntTweaksConfig;
import dev.saynt.tweaks.inventory.HotbarRefill;
import dev.saynt.tweaks.search.ContainerSearch;
import dev.saynt.tweaks.sort.ClickQueue;

public class SayntTweaks implements ClientModInitializer {
	public static final String MOD_ID = "sayntstweaks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		SayntTweaksConfig.load();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ClickQueue.tick();
			HotbarRefill.tick(client);
		});

		// Darker menus, on by default and toggleable in the resource pack screen
		ResourceLoader.registerBuiltinPack(
				Identifier.fromNamespaceAndPath(MOD_ID, "dark_ui"),
				FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(),
				Component.translatable("sayntstweaks.pack.dark_ui"),
				PackActivationType.DEFAULT_ENABLED);

		ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
			if (screen instanceof ContainerScreen || screen instanceof ShulkerBoxScreen) {
				AbstractContainerScreen<?> container = (AbstractContainerScreen<?>) screen;
				ContainerButtons.addContainerSort(container);
				ContainerButtons.addInventorySort(container);
				ContainerButtons.addTransfers(container);
				if (SayntTweaksConfig.get().searchBar) {
					ContainerSearch.attach(container);
				}
			} else if (screen instanceof InventoryScreen inventory) {
				ContainerButtons.addInventorySort(inventory);
			}
		});
		LOGGER.info("Saynt's Tweaks loaded");
	}
}
