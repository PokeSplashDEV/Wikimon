package org.pokesplash.wikimon.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.wikimon.Wikimon;
import org.pokesplash.wikimon.util.LuckPermsUtils;

public class ReloadCommand {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("reload")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission + ".reload");
					} else {
						return true;
					}
				})
				.executes(this::run)
				.build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		Wikimon.load();

		context.getSource().sendMessage(Text.literal("Config reloaded."));

		return 1;
	}
}
