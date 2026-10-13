package dev.saynt.tweaks.sort;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;

import dev.saynt.tweaks.config.SayntTweaksConfig;

/** Sends planned clicks, either instantly or a few per tick in safe mode. */
public final class ClickQueue {
	public record Click(int slot, int button, ContainerInput input) {
		public static Click pickup(int slot) {
			return new Click(slot, 0, ContainerInput.PICKUP);
		}

		public static Click quickMove(int slot) {
			return new Click(slot, 0, ContainerInput.QUICK_MOVE);
		}
	}

	private static final Queue<Click> pending = new ArrayDeque<>();
	private static AbstractContainerMenu menu;

	private ClickQueue() {
	}

	public static boolean isBusy() {
		return !pending.isEmpty();
	}

	public static void start(AbstractContainerMenu target, List<Click> clicks) {
		menu = target;
		pending.addAll(clicks);
		SayntTweaksConfig config = SayntTweaksConfig.get();
		send(config.safeMode ? config.safeModeClicksPerTick : Integer.MAX_VALUE);
	}

	/** Called every client tick. */
	public static void tick() {
		if (isBusy()) {
			send(SayntTweaksConfig.get().safeModeClicksPerTick);
		}
	}

	private static void send(int count) {
		Minecraft client = Minecraft.getInstance();
		// Stop if the player closed the chest in the middle of a slow sort
		if (client.player == null || client.gameMode == null || client.player.containerMenu != menu) {
			pending.clear();
			return;
		}
		for (int i = 0; i < count && !pending.isEmpty(); i++) {
			Click click = pending.poll();
			client.gameMode.handleContainerInput(menu.containerId, click.slot(), click.button(), click.input(), client.player);
		}
	}
}
