package org.pokesplash.wikimon.util;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.ArrayList;

public abstract class CobblemonUtils {
	public static boolean isHA(Pokemon pokemon) {
		if (pokemon.getForm().getAbilities().getMapping().get(Priority.LOW) == null ||
				pokemon.getForm().getAbilities().getMapping().get(Priority.LOW).size() != 1) {
			return false;
		}
		String ability =
				pokemon.getForm().getAbilities().getMapping().get(Priority.LOW).get(0).getTemplate().getName();

		return pokemon.getAbility().getName().equalsIgnoreCase(ability);
	}

	public static ArrayList<AbilityTemplate> getNormalAbilities(Pokemon pokemon) {

		ArrayList<AbilityTemplate> abilities = new ArrayList<>();

		for (PotentialAbility ability : pokemon.getForm().getAbilities().getMapping().get(Priority.LOWEST)) {
			abilities.add(ability.getTemplate());
		}

		return abilities;
	}

	public static ArrayList<AbilityTemplate> getNormalAbilities(FormData pokemon) {

		ArrayList<AbilityTemplate> abilities = new ArrayList<>();

		for (PotentialAbility ability : pokemon.getAbilities().getMapping().get(Priority.LOWEST)) {
			abilities.add(ability.getTemplate());
		}

		return abilities;
	}

	public static AbilityTemplate getHA(Pokemon pokemon) {
		if (pokemon.getForm().getAbilities().getMapping().get(Priority.LOW) == null ||
				pokemon.getForm().getAbilities().getMapping().get(Priority.LOW).size() != 1) {
			return null;
		}

		return pokemon.getForm().getAbilities().getMapping().get(Priority.LOW).get(0).getTemplate();
	}

	public static AbilityTemplate getHA(FormData pokemon) {
		if (pokemon.getAbilities().getMapping().get(Priority.LOW) == null ||
				pokemon.getAbilities().getMapping().get(Priority.LOW).size() != 1) {
			return null;
		}

		return pokemon.getAbilities().getMapping().get(Priority.LOW).get(0).getTemplate();
	}
}
