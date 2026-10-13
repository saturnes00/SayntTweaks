package dev.saynt.tweaks.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import net.fabricmc.loader.api.FabricLoader;

import dev.saynt.tweaks.SayntTweaks;
import dev.saynt.tweaks.sort.SortMode;

/** Settings stored in config/sayntstweaks.json. */
public final class SayntTweaksConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("sayntstweaks.json");

	private static SayntTweaksConfig instance = new SayntTweaksConfig();

	/** How items are ordered. Shift-click a sort button to cycle it. */
	public SortMode sortMode = SortMode.MINECRAFT;

	/** Refills the held stack from the inventory when it runs out. */
	public boolean hotbarRefill = true;

	/** Shows a search box above chests and shulker boxes. */
	public boolean searchBar = true;

	/** Spreads the sorting clicks over several ticks, for servers with an anti-cheat. */
	public boolean safeMode = false;

	/** In safe mode, how many clicks are sent per tick (20 ticks = 1 second). */
	public int safeModeClicksPerTick = 2;

	public static SayntTweaksConfig get() {
		return instance;
	}

	public static void load() {
		if (Files.exists(FILE)) {
			try (Reader reader = Files.newBufferedReader(FILE)) {
				SayntTweaksConfig loaded = GSON.fromJson(reader, SayntTweaksConfig.class);
				if (loaded != null) {
					instance = loaded;
				}
			} catch (IOException | JsonParseException e) {
				SayntTweaks.LOGGER.warn("Could not read {}, using defaults", FILE, e);
			}
		}
		if (instance.sortMode == null) {
			instance.sortMode = SortMode.MINECRAFT;
		}
		instance.safeModeClicksPerTick = Math.max(1, instance.safeModeClicksPerTick);
		save();
	}

	public static void save() {
		try (Writer writer = Files.newBufferedWriter(FILE)) {
			GSON.toJson(instance, writer);
		} catch (IOException e) {
			SayntTweaks.LOGGER.warn("Could not write {}", FILE, e);
		}
	}
}
