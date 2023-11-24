package org.pokesplash.wikimon.util;

import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class SpawnDetails {
	private static HashMap<String, ArrayList<SpawnDetail>> spawnDetails = new HashMap<>();

	public static ArrayList<SpawnDetail> getSpawnDetails(String pokemon) {
		return spawnDetails.get(pokemon);
	}

	public static void init() {
		for (SpawnDetail detail : CobblemonSpawnPools.WORLD_SPAWN_POOL.getDetails()) {
			String pokemon = detail.getName().getString();

			// If Pokemon doesn't exist, add it.
			if (!spawnDetails.containsKey(pokemon)) {
				spawnDetails.put(pokemon, new ArrayList<>());
			}

			// Add the detail to the exist details.
			ArrayList<SpawnDetail> currentDetails = spawnDetails.get(pokemon);
			currentDetails.add(detail);

			spawnDetails.put(pokemon, currentDetails);

		}
	}
}
