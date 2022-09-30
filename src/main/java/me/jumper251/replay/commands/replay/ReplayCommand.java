package me.jumper251.replay.commands.replay;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

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
		List<SubCommand> scmd = new ArrayList<>();
		if(serverisGameserver()) scmd.add(new ReplayStartCommand(this));
		if(serverisGameserver()) scmd.add(new ReplayStopCommand(this).addAlias("save")); 
		if(!serverisGameserver()) scmd.add(new ReplayPlayCommand(this));
		if(!serverisGameserver()) scmd.add(new ReplayDeleteCommand(this).addAlias("remove"));
		if(!serverisGameserver()) scmd.add(new ReplayLeaveCommand(this));
		if(!serverisGameserver()) scmd.add(new ReplayInfoCommand(this));
		if(!serverisGameserver()) scmd.add(new ReplayListCommand(this));
		scmd.add(new ReplayReloadCommand(this));
		
		SubCommand[] scmdlist = new SubCommand[scmd.size()];
		scmdlist = scmd.toArray(scmdlist);
		
		return scmdlist;
	}

	private boolean serverisGameserver() {
		return !Bukkit.getServer().getMotd().equalsIgnoreCase("Replay Server");
	}
}
