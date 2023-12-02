package org.pokesplash.wikimon.command;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.spawning.*;
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.command.SpawnPokemon;
import com.cobblemon.mod.common.data.CobblemonDataProvider;
import com.cobblemon.mod.common.item.CobblemonItem;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.registry.BiomeTagCondition;
import com.google.gson.Gson;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import kotlin.ranges.IntRange;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.pokesplash.wikimon.Wikimon;
import org.pokesplash.wikimon.util.CobblemonUtils;
import org.pokesplash.wikimon.util.LuckPermsUtils;
import org.pokesplash.wikimon.util.SpawnDetails;
import org.pokesplash.wikimon.util.Utils;

import java.util.*;

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
								.executes(this::ability)
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
								.executes(this::spawn)
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

			String message = form.getName() +  "\n§9Male §e" + (form.getMaleRatio() * 100) +
					"%\n§dFemale §e" + ((1-form.getMaleRatio()) * 100) + "%";

			stats.add(message);
		}

		context.getSource().sendMessage(Text.literal(
				format(pokemon, "Gender Ratio", stats)
		));

		return 1;
	}

	public int ability(CommandContext<ServerCommandSource> context) {
		if (!context.getSource().isExecutedByPlayer()) {
			return 1;
		}

		String pokemonString = StringArgumentType.getString(context, "pokemon");
		Species pokemon = PokemonSpecies.INSTANCE.getByName(pokemonString.toLowerCase());

		List<FormData> forms = pokemon.getForms();

		if (forms.isEmpty()) {
			forms.add(pokemon.getStandardForm());
		}

		ArrayList<Text> stats = new ArrayList<>();
		for (FormData form : forms) {

			ArrayList<AbilityTemplate> abilities = CobblemonUtils.getNormalAbilities(form);
			AbilityTemplate ha = CobblemonUtils.getHA(form);

			Text message = Text.literal("§7§o" + form.getName() +  "\n§3Regular: ");

			for (AbilityTemplate template : abilities) {
				message = Text.empty().append(message).append(
						Text.translatable(template.getDisplayName()).setStyle(Style.EMPTY.withColor(
								TextColor.parse("aqua")
						))).append(Text.literal("§b, "));
			}

			message = Text.empty().append(message).append(Text.literal("\n§6HA: "))
					.append(ha == null ? Text.literal("§8None") :
							Text.translatable(ha.getDisplayName()).setStyle(Style.EMPTY.withColor(
									TextColor.parse("yellow")
							)));

			stats.add(message);
		}

		context.getSource().sendMessage(
				formatText(pokemon, "Abilities", stats));

		return 1;
	}

	public int spawn(CommandContext<ServerCommandSource> context) {
		if (!context.getSource().isExecutedByPlayer()) {
			return 1;
		}

		String pokemonString = StringArgumentType.getString(context, "pokemon");
		Species pokemon = PokemonSpecies.INSTANCE.getByName(pokemonString.toLowerCase());

		if (pokemon == null) {
			context.getSource().sendMessage(Text.literal("§cCould not find Pokemon " + pokemonString));
			return 1;
		}

		HashMap<String, ArrayList<SpawnDetail>> spawnDetails = SpawnDetails.getSpawnDetails(pokemon);

		if (spawnDetails.isEmpty()) {
			context.getSource().sendMessage(Text.literal("§cCould not find spawn info for " + pokemonString));
			return 1;
		}

		ArrayList<Text> stats = new ArrayList<>();

		for (String form : spawnDetails.keySet()) {

			Text message = Text.literal("§7§o" + form);

			int x = 1;

			for (SpawnDetail detail : spawnDetails.get(form)) {

				message = Text.empty().append(message).append("\n\n§8§oCondition " + x + "\n");

				message = Text.empty().append(message)
						.append(Text.literal("§5Bucket: §d")).append(Text.literal(detail.getBucket().getName()))
						.append(Text.literal("\n§9Weight: §1")).append(Text.literal("" + detail.getWeight()))
						.append(Text.literal("\n§6Level: §e")).append(Text.literal("" + detail.getHeight()))
						.append(Text.literal("\n§2Context: §a")).append(Text.literal(detail.getContext().getName()));

				if (!detail.getConditions().isEmpty()) {
					for (SpawningCondition<?> condition : detail.getConditions()) {
						// Can See Sky
						if (condition.getCanSeeSky() != null) {
							message = Text.empty().append(message)
									.append(Text.literal("\n§3Can See Sky: §b" + condition.getCanSeeSky()));
						}

						// Raining
						if (condition.isRaining() != null) {
							message = Text.empty().append(message)
									.append("\n§3Should be raining: §b" + condition.isRaining());
						}

						// Thundering
						if (condition.isThundering() != null) {
							message = Text.empty().append(message)
									.append("\n§3Should be thunder: §b" + condition.isThundering());
						}

						// Max Light Level
						if (condition.getMaxLight() != null) {
							message = Text.empty().append(message)
									.append("\n§3Max Light Level: §b" + condition.getMaxLight());
						}

						// Min Light Level
						if (condition.getMinLight() != null) {
							message = Text.empty().append(message)
									.append("\n§3Min Light Level: §b" + condition.getMinLight());
						}

						// Max Y
						if (condition.getMaxY() != null) {
							message = Text.empty().append(message)
									.append("\n§3Max Y: §b" + condition.getMaxY());
						}

						// Min Y
						if (condition.getMinY() != null) {
							message = Text.empty().append(message)
									.append("\n§3Min Y: §b" + condition.getMinY());
						}

						// Time Range
						if (condition.getTimeRange() != null) {
							for (IntRange range : condition.getTimeRange().getRanges()) {
								message = Text.empty().append(message)
										.append("\n§3Time Range: §b" + range.getStart() + " - " + range.getEndInclusive());
							}
						}

						// Time Range
						if (condition.getMoonPhase() != null) {
							message = Text.empty().append(message)
									.append("\n§3Moon Phase: §b" + condition.getMoonPhase());
						}

						// TODO seperate biome command.
//						// Biomes
//						if (condition.getBiomes() != null) {
//							message = Text.empty().append(message).append("\n§8Biomes:");
//							for (RegistryLikeCondition<Biome> biome : condition.getBiomes()) {;
//								BiomeTagCondition biomeTagCondition = (BiomeTagCondition) biome;
//								message = Text.empty().append(message)
//										.append("\n§7- " + biomeTagCondition.getTag().id().getPath());
//							}
//						}
					}
				}

				if (!detail.getAnticonditions().isEmpty()) {
					for (SpawningCondition<?> condition : detail.getAnticonditions()) {
						// Can See Sky
						if (condition.getCanSeeSky() != null) {
							message = Text.empty().append(message)
									.append(Text.literal("\n§4Can See Sky: §c" + condition.getCanSeeSky()));
						}

						// Raining
						if (condition.isRaining() != null) {
							message = Text.empty().append(message)
									.append("\n§4Should be raining: §c" + condition.isRaining());
						}

						// Thundering
						if (condition.isThundering() != null) {
							message = Text.empty().append(message)
									.append("\n§4Should be thunder: §c" + condition.isThundering());
						}

						// Max Light Level
						if (condition.getMaxLight() != null) {
							message = Text.empty().append(message)
									.append("\n§4Max Light Level: §c" + condition.getMaxLight());
						}

						// Min Light Level
						if (condition.getMinLight() != null) {
							message = Text.empty().append(message)
									.append("\n§4Min Light Level: §c" + condition.getMinLight());
						}

						// Max Y
						if (condition.getMaxY() != null) {
							message = Text.empty().append(message)
									.append("\n§4Max Y: §c" + condition.getMaxY());
						}

						// Min Y
						if (condition.getMinY() != null) {
							message = Text.empty().append(message)
									.append("\n§4Min Y: §c" + condition.getMinY());
						}

						// Time Range
						if (condition.getTimeRange() != null) {
							for (IntRange range : condition.getTimeRange().getRanges()) {
								message = Text.empty().append(message)
										.append("\n§4Time Range: §c" + range.getStart() + " - " + range.getEndInclusive());
							}
						}

						// Time Range
						if (condition.getMoonPhase() != null) {
							message = Text.empty().append(message)
									.append("\n§4Moon Phase: §c" + condition.getMoonPhase());
						}
					}
				}

				x++;
			}

			stats.add(message);
		}

		context.getSource().sendMessage(
				formatText(pokemon, "Catch Rate", stats));

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

	public Text formatText(Species pokemon, String header, ArrayList<Text> data) {

		Text base = Text.literal("§3§l" + pokemon.getName() + " - " + header);

		for (Text text : data) {
			base = Text.empty().append(base).append(Text.literal("\n§7§o")).append(text);
		}

		return base;
	}

}
