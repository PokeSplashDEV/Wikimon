package org.pokesplash.wikimon;

import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.pokemon.Species;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pokesplash.wikimon.command.CommandHandler;
import org.pokesplash.wikimon.config.Config;
import org.pokesplash.wikimon.config.Lang;
import org.pokesplash.wikimon.util.SpawnDetails;

import java.util.ArrayList;
import java.util.HashMap;

public class Wikimon implements ModInitializer {
	public static final String MOD_ID = "Wikimon";
	public static final String BASE_PATH = "/config/" + MOD_ID + "/";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Config config = new Config();
	public static final Lang lang = new Lang();
	public static final HashMap<Species, ArrayList<SpawnDetail>> spawnDetails = new HashMap<>();

	/**
	 * Runs the mod initializer.
	 */
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(CommandHandler::registerCommands);
		load();
	}

	public static void load() {
		config.init();
		lang.init();
		SpawnDetails.init();
	}
}
