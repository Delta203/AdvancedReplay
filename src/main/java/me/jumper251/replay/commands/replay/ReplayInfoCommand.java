package me.jumper251.replay.commands.replay;




import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.commands.AbstractCommand;
import me.jumper251.replay.commands.SubCommand;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.data.ReplayInfo;
import me.jumper251.replay.replaysystem.recording.optimization.ReplayOptimizer;
import me.jumper251.replay.replaysystem.recording.optimization.ReplayStats;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;

public class ReplayInfoCommand extends SubCommand {

	public ReplayInfoCommand(AbstractCommand parent) {
		super(parent, "info", "Informationen über ein Replay", "info <§8Name§b>", false);
	}

	@Override
	public boolean execute(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length != 2) return false;
		
		String name = args[1];
		

		if (ReplaySaver.exists(name)) {
			cs.sendMessage(ReplaySystem.PREFIX + "Lade Replay §e" + name + "§7...");


			ReplaySaver.load(name, replay -> {
				ReplayInfo info = replay.getReplayInfo() != null ? replay.getReplayInfo() : new ReplayInfo(replay.getId(), replay.getData().getCreator(), null, replay.getData().getDuration());
				ReplayStats stats = ReplayOptimizer.analyzeReplay(replay);				
				
				cs.sendMessage(ReplaySystem.PREFIX + "§8» §6Replayinfos über §e§l" + replay.getId() + " §8«");
				if (info.getCreator() != null) {
					cs.sendMessage(ReplaySystem.PREFIX + "§7§oErstellt von: §9" + info.getCreator());
				}

				cs.sendMessage(ReplaySystem.PREFIX + "§7§oDauer: §6" + (info.getDuration() / 20) + " §7§oSekunden");
				cs.sendMessage(ReplaySystem.PREFIX + "§7§oSpieler: §6" + stats.getPlayers().stream().collect(Collectors.joining("§7, §6")));
				//cs.sendMessage(ReplaySystem.PREFIX + "§7§oQuality: " + (replay.getData().getQuality() != null ? replay.getData().getQuality().getQualityName() : ReplayQuality.HIGH.getQualityName()));

				if (cs instanceof Player) {
					((Player)cs).spigot().sendMessage(buildMessage(stats));
				}
				if (stats.getEntityCount() > 0) {
					cs.sendMessage(ReplaySystem.PREFIX + "§7§oEntities: §6" + stats.getEntityCount());
				}

				
				
			});
			
		} else {
			cs.sendMessage(ReplaySystem.PREFIX + "§cReplay wurde nicht gefunden.");
		}
		
		return true;
	}
	
	public BaseComponent[] buildMessage(ReplayStats stats) {
		return new ComponentBuilder(ReplaySystem.PREFIX + "§7§oAufgenommene Daten: ")
				.append("§6§n" + stats.getActionCount() + "§r")
				.event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(stats.getSortedActions().entrySet().stream().map(e -> "§7" + e.getKey() + ": §b" + e.getValue()).collect(Collectors.joining("\n"))).create()))
				.append(" §7§oactions")
				.reset()
				.create();
 
	}
	
	@Override
	public List<String> onTab(CommandSender cs, Command cmd, String label, String[] args) {
		return ReplaySaver.getReplays().stream()
				.filter(name -> StringUtil.startsWithIgnoreCase(name, args.length > 1 ? args[1] : null))
				.collect(Collectors.toList());
	}

	
}
