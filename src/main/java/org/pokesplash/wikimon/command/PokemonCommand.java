package org.pokesplash.wikimon.command;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.spawning.*;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.command.SpawnPokemon;
import com.cobblemon.mod.common.data.CobblemonDataProvider;
import com.cobblemon.mod.common.item.CobblemonItem;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.pokesplash.wikimon.Wikimon;
import org.pokesplash.wikimon.util.LuckPermsUtils;
import org.pokesplash.wikimon.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonCommand {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("pokemon")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission + ".base");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("pokemon", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (Species species : PokemonSpecies.INSTANCE.getImplemented()) {
								builder.suggest(species.getName());
							}
							return builder.buildFuture();
						})
						.executes(this::stats)
						.then(CommandManager.literal("stats")
								.executes(this::stats)
						)
						.then(CommandManager.literal("gender")
								.executes(this::gender)
						)
						.then(CommandManager.literal("ability")
								.executes(this::stats)
						)
						.then(CommandManager.literal("catchrate")
								.executes(this::stats)
						)
						.then(CommandManager.literal("drops")
								.executes(this::stats)
						)
						.then(CommandManager.literal("type")
								.executes(this::stats)
						)
						.then(CommandManager.literal("tm")
								.executes(this::stats)
						)
						.then(CommandManager.literal("egg")
								.executes(this::stats)
						)
						.then(CommandManager.literal("moves")
								.executes(this::stats)
						)
						.then(CommandManager.literal("tutor")
								.executes(this::stats)
						)
						.then(CommandManager.literal("evo")
								.executes(this::stats)
						)
						.then(CommandManager.literal("evs")
								.executes(this::stats)
						)
						.then(CommandManager.literal("weight")
								.executes(this::stats)
						)
						.then(CommandManager.literal("spawn")
								.executes(this::stats)
						)
				).build();
	}


	public int stats(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			return 1;
		}

		String pokemonString = StringArgumentType.getString(context, "pokemon");
		Species pokemon = PokemonSpecies.INSTANCE.getByName(pokemonString.toLowerCase());

		List<FormData> forms = pokemon.getForms();

		if (forms.isEmpty()) {
			forms.add(pokemon.getStandardForm());
		}

		ArrayList<String> stats = new ArrayList<>();
		for (FormData form : forms) {

			Map<Stat, Integer> formStats = form.getBaseStats();

			String message = form.getName() +
					"\n§2HP: " + formStats.get(Stats.HP) +
					"\n§6Atk: " + formStats.get(Stats.ATTACK) +
					"\n§6Def: " + formStats.get(Stats.DEFENCE) +
					"\n§dSpA: " + formStats.get(Stats.SPECIAL_ATTACK) +
					"\n§eSpD: " + formStats.get(Stats.SPECIAL_DEFENCE) +
					"\n§bSpe: " + formStats.get(Stats.SPEED);

			stats.add(message);
		}

		context.getSource().sendMessage(Text.literal(
				format(pokemon, "Stats", stats)
		));

		return 1;
	}

	public int gender(CommandContext<ServerCommandSource> context) {
		if (!context.getSource().isExecutedByPlayer()) {
			return 1;
		}

		String pokemonString = StringArgumentType.getString(context, "pokemon");
		Species pokemon = PokemonSpecies.INSTANCE.getByName(pokemonString.toLowerCase());

		List<FormData> forms = pokemon.getForms();

		if (forms.isEmpty()) {
			forms.add(pokemon.getStandardForm());
		}

		ArrayList<String> stats = new ArrayList<>();
		for (FormData form : forms) {

			form.getMaleRatio();

			String message = form.getName() +  "\n§9Male §e" + (form.getMaleRatio() * 100) +
					"%\n§dFemale §e" + ((1-form.getMaleRatio()) * 100) + "%";

			stats.add(message);
		}

		context.getSource().sendMessage(Text.literal(
				format(pokemon, "Gender Ratio", stats)
		));

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(Text.literal(
				Utils.formatMessage(
						"§3§lUsage:\n" +
								"§b- /wiki pokemon <pokemon> <field>",
						context.getSource().isExecutedByPlayer()
				)));

		return 1;
	}

	public String format(Species pokemon, String header, ArrayList<String> data) {
		StringBuilder base = new StringBuilder("§3§l" + pokemon.getName() + " - " + header);

		for (String text : data) {
			base.append("\n§7§o").append(text);
		}

		return base.toString();
	}

}
