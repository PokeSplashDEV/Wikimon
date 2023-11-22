package org.pokesplash.wikimon.config;

import com.google.gson.Gson;
import org.pokesplash.wikimon.Wikimon;
import org.pokesplash.wikimon.util.Utils;

import java.util.concurrent.CompletableFuture;

public class Lang {
	private String title;
	private String fillerMaterial;

	public Lang() {
		title = Wikimon.MOD_ID;
		fillerMaterial = "minecraft:white_stained_glass_pane";
	}

	public String getTitle() {
		return title;
	}

	public String getFillerMaterial() {
		return fillerMaterial;
	}

	/**
	 * Method to initialize the config.
	 */
	public void init() {
		CompletableFuture<Boolean> futureRead = Utils.readFileAsync(Wikimon.BASE_PATH, "lang.json",
				el -> {
					Gson gson = Utils.newGson();
					Lang lang = gson.fromJson(el, Lang.class);
					title = lang.getTitle();
					fillerMaterial = lang.getFillerMaterial();
				});

		if (!futureRead.join()) {
			Wikimon.LOGGER.info("No lang.json file found for " + Wikimon.MOD_ID + ". Attempting to " +
					"generate " +
					"one.");
			Gson gson = Utils.newGson();
			String data = gson.toJson(this);
			CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Wikimon.BASE_PATH, "lang.json", data);

			if (!futureWrite.join()) {
				Wikimon.LOGGER.fatal("Could not write lang.json for " + Wikimon.MOD_ID + ".");
			}
			return;
		}
		Wikimon.LOGGER.info(Wikimon.MOD_ID + " lang file read successfully.");
	}
}
