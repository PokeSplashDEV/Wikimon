package org.pokesplash.wikimon.util;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.pokemon.Species;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class SpawnDetails {
	private static HashMap<Species, HashMap<String, ArrayList<SpawnDetail>>> spawnDetails = new HashMap<>();

	public static HashMap<String, ArrayList<SpawnDetail>> getSpawnDetails(Species pokemon) {
		return spawnDetails.get(pokemon);
	}

	public static void init() {

		for (SpawnDetail detail : CobblemonSpawnPools.WORLD_SPAWN_POOL.getDetails()) {

			String[] pokemon = detail.getId().replaceAll("[0-9]", "").split("-");

			Species species = PokemonSpecies.INSTANCE.getByName(pokemon[0]);

			// If Pokemon doesn't exist, add it.
			if (!spawnDetails.containsKey(species)) {
				spawnDetails.put(species, new HashMap<>());
			}

			// Add the detail to the exist details.
			HashMap<String, ArrayList<SpawnDetail>> currentDetails = spawnDetails.get(species);

			if (pokemon.length == 1) {
				ArrayList<SpawnDetail> formDetails = currentDetails.get("Default");
				if (formDetails == null) {
					formDetails = new ArrayList<>();
				}
				formDetails.add(detail);
				currentDetails.put("Default", formDetails);
			} else {
				ArrayList<SpawnDetail> formDetails = currentDetails.get(pokemon[1]);
				if (formDetails == null) {
					formDetails = new ArrayList<>();
				}
				formDetails.add(detail);
				currentDetails.put(pokemon[1], formDetails);
			}

			spawnDetails.put(species, currentDetails);

		}
	}
}
