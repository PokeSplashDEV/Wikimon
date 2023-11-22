package org.pokesplash.wikimon.config;

import com.google.gson.Gson;
import org.pokesplash.wikimon.Wikimon;
import org.pokesplash.wikimon.util.Utils;

import java.util.concurrent.CompletableFuture;

public class Config {
	private boolean isExample;

	public Config() {
		isExample = true;
	}

	public void init() {
		CompletableFuture<Boolean> futureRead = Utils.readFileAsync(Wikimon.BASE_PATH,
				"config.json", el -> {
					Gson gson = Utils.newGson();
					Config cfg = gson.fromJson(el, Config.class);
					isExample = cfg.isExample();
				});

		if (!futureRead.join()) {
			Wikimon.LOGGER.info("No config.json file found for " + Wikimon.MOD_ID + ". Attempting to generate" +
					" " +
					"one");
			Gson gson = Utils.newGson();
			String data = gson.toJson(this);
			CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Wikimon.BASE_PATH,
					"config.json", data);

			if (!futureWrite.join()) {
				Wikimon.LOGGER.fatal("Could not write config for " + Wikimon.MOD_ID + ".");
			}
			return;
		}
		Wikimon.LOGGER.info(Wikimon.MOD_ID + " config file read successfully");
	}

	public boolean isExample() {
		return isExample;
	}
}
