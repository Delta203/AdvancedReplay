package me.jumper251.replay.commands.replay;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.commands.AbstractCommand;
import me.jumper251.replay.commands.SubCommand;
import me.jumper251.replay.utils.MathUtils;
import me.jumper251.replay.utils.ReplayManager;
import me.jumper251.replay.utils.StringUtils;

public class ReplayStartCommand extends SubCommand {

	public ReplayStartCommand(AbstractCommand parent) {
		super(parent, "start", "Nehme ein neues Replay auf", "start [§8Name§b][§8:Dauer§b] [§8<Spieler...>§b]", false); 
	}

	@Override
	public boolean execute(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length < 1) return false;
		
		String name = parseName(args);
		int duration = parseDuration(args);
		
		if (name == null || duration < 0) {
			return false;
		}
		if (name.length() > 40) {
			cs.sendMessage(ReplaySystem.PREFIX + "§cDer Replayname ist zu lang.");
			return true;
		}
		if (ReplayManager.activeReplays.containsKey(name)) {
			cs.sendMessage(ReplaySystem.PREFIX + "§cDas Replay existiert bereits.");
			return true;
		}
		
		List<Player> toRecord = new ArrayList<>();

		if (args.length <= 2) {
			toRecord.addAll(Bukkit.getOnlinePlayers());

		} else {
			for (int i = 2; i < args.length; i++) {
				if (Bukkit.getPlayer(args[i]) != null) {
					toRecord.add(Bukkit.getPlayer(args[i]));
				}
			}

		}
		
		ReplayAPI.getInstance().recordReplay(name, cs, toRecord);

		if (duration <= 0) {
			cs.sendMessage(ReplaySystem.PREFIX + "§aDas Replay §e" + name + " §awird nun aufgenommen.\n§7Benutze §6/Replay stop " + name + "§7 um es zu speichern.");
		} else {
			cs.sendMessage(ReplaySystem.PREFIX + "§aDas Replay §e" + name + " §awird nun aufgenommen.\n§7Das Replay wird in §6" + duration + "§7 Sekunden gespeichert.");
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					ReplayAPI.getInstance().stopReplay(name, true, true);
				}
			}.runTaskLater(ReplaySystem.getInstance(), duration * 20);
		}
		
		if (args.length <= 2) {
			cs.sendMessage("§7INFO: Du nimmst alle online Spieler auf.");
		}
		
		return true;
	}
	
	
	private String parseName(String[] args) {
		if (args.length >= 2) {			
			String[] split = args[1].split(":");	
			
			if (args[1].contains(":")) {
				if (split.length == 2 && split[0].length() > 0) return split[0];
			} else {
				return args[1];
			}
		}

		
		return StringUtils.getRandomString(6);
	}
	
	private int parseDuration(String[] args) {
		if (args.length < 2 || !args[1].contains(":")) return 0;
		String[] split = args[1].split(":");

		if (split.length == 2 && MathUtils.isInt(split[1])) {
			return Integer.parseInt(split[1]);
		}
		
		if (split.length == 1) {
			if (!split[0].startsWith(":") || !MathUtils.isInt(split[0])) return -1;
			
			return Integer.parseInt(split[0]);
		}
		
		return 0;
	}

	
}
