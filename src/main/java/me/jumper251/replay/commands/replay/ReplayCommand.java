package me.jumper251.replay.commands.replay;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.commands.AbstractCommand;
import me.jumper251.replay.commands.MessageFormat;
import me.jumper251.replay.commands.SubCommand;

public class ReplayCommand extends AbstractCommand {

	public ReplayCommand() {
		super("replay", ReplaySystem.PREFIX + "AdvancedReplay §ev" + ReplaySystem.getInstance().getDescription().getVersion(), "replay.command");
	}

	@Override
	protected MessageFormat setupFormat() {
		return new MessageFormat()
				.overview("§b/{command} {args} §7{desc}")
				.syntax(ReplaySystem.PREFIX + "§c/{command} §b{args}")
				.permission("§cI''m sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.")
				.notFound(ReplaySystem.PREFIX + "§cDer Befehl wurde nicht gefunden.");
	}

	@Override
	protected SubCommand[] setupCommands() {
		
		return new SubCommand[] { new ReplayStartCommand(this), 
				new ReplayStopCommand(this).addAlias("save"), 
				new ReplayPlayCommand(this), 
				new ReplayDeleteCommand(this).addAlias("remove"),
				new ReplayLeaveCommand(this),
				new ReplayInfoCommand(this),
				new ReplayListCommand(this), 
				new ReplayReloadCommand(this),
				};
	}

}
