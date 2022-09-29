package me.jumper251.replay.commands.replay;




import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.commands.AbstractCommand;
import me.jumper251.replay.commands.SubCommand;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import me.jumper251.replay.replaysystem.replaying.Replayer;

public class ReplayLeaveCommand extends SubCommand {

	public ReplayLeaveCommand(AbstractCommand parent) {
		super(parent, "leave", "Verlasse das Replay", "leave", true);
	}

	@Override
	public boolean execute(CommandSender cs, Command cmd, String label, String[] args) {		
		Player p = (Player) cs;
		
		if (ReplayHelper.replaySessions.containsKey(p.getName())) {
			Replayer replayer = ReplayHelper.replaySessions.get(p.getName());
			
			replayer.stop();
			
		} else {
			p.sendMessage(ReplaySystem.PREFIX + "§cDu bist nicht in einem Replay.");
		}
		
		return true;
	}

	
}
