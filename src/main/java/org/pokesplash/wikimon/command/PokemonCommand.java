package org.pokesplash.wikimon.command;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.spawning.BestSpawner;
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.wikimon.Wikimon;
import org.pokesplash.wikimon.util.LuckPermsUtils;
import org.pokesplash.wikimon.util.Utils;

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
				.executes(this::run)
				.then(CommandManager.argument("pokemon", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (Species species : PokemonSpecies.INSTANCE.getImplemented()) {
								builder.suggest(species.getName());
							}
							return builder.buildFuture();
						})
						.executes(this::run)
				).build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		String pokemonString = StringArgumentType.getString(context, "pokemon");

		Species pokemon = PokemonSpecies.INSTANCE.getByName(pokemonString);

		if (pokemon == null) {
			context.getSource().sendMessage(Text.literal(
					Utils.formatMessage("§cPokemon " + pokemonString + " could not be found",
							context.getSource().isExecutedByPlayer())
			));
		}

		CobblemonSpawnPools.WORLD_SPAWN_POOL.getDetails()

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(Text.literal(
				Utils.formatMessage(
						"§3§lUsage:\n" +
								"§b- /wiki pokemon <pokemon>",
						context.getSource().isExecutedByPlayer()
				)));
	}
}
