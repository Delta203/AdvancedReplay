package me.jumper251.replay.commands.replay;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.commands.AbstractCommand;
import me.jumper251.replay.commands.SubCommand;
import me.jumper251.replay.filesystem.saving.DefaultReplaySaver;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.utils.ReplayManager;

public class ReplayStopCommand extends SubCommand {

	public ReplayStopCommand(AbstractCommand parent) {
		super(parent, "stop", "§cStoppe und speichere ein Replay", "stop <§8Name§b> [§8Options§b]", false);
	}

	@Override
	public boolean execute(CommandSender cs, Command cmd, String label, String[] args) {
		/* console only */
		if(!cs.equals(Bukkit.getConsoleSender())) {
			cs.sendMessage(ReplaySystem.PREFIX + "§cNur die Console darf den Befehl ausführen.");
			return false;
		}
		
		if (args.length > 3 || args.length < 2) return false;
		
		String name = args[1];
		boolean isForce = args.length == 3 && args[2].equals("-force");
		boolean isNoSave = args.length == 3 && args[2].equals("-nosave");

		if (ReplayManager.activeReplays.containsKey(name) && ReplayManager.activeReplays.get(name).isRecording()) {
			Replay replay = ReplayManager.activeReplays.get(name);

			if (isNoSave || replay.getRecorder().getData().getActions().size() == 0) {
				replay.getRecorder().stop(false);

				cs.sendMessage(ReplaySystem.PREFIX + "§7Replay §e" + name + " §7erfolgreich gespeichert.");
			} else {
				if (ReplaySaver.exists(name) && !isForce) {
					cs.sendMessage(ReplaySystem.PREFIX + "§cDas Replay existiert bereits. Benutze §o-force §r§cum das alte Replay zu überschreiben oder §o-nosave §r§cum das Replay zu verwerfen.");
					return true;
				}
				
				cs.sendMessage(ReplaySystem.PREFIX + "Speichere das Replay §e" + name + "§7...");
				replay.getRecorder().stop(true);
			
				String path = ReplaySaver.replaySaver instanceof DefaultReplaySaver ? ReplaySystem.getInstance().getDataFolder() + "/replays/" + name + ".replay" : null;
				cs.sendMessage(ReplaySystem.PREFIX + "§7Das Replay wurde erfolgreich gespeichert. " + (path != null ? " to §o" + path : ""));
			}
			
		} else {
			cs.sendMessage(ReplaySystem.PREFIX + "§cReplay wurde nicht gefunden.");
		}
		
		return true;
	}
	
	@Override
	public List<String> onTab(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length == 3) return Arrays.asList("-nosave", "-force");
		
		return ReplayManager.activeReplays.keySet().stream()
				.filter(name -> StringUtil.startsWithIgnoreCase(name, args.length > 1 ? args[1] : null))
				.collect(Collectors.toList());
	}

	
}
