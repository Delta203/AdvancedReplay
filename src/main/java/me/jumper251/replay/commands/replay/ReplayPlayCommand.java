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
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import me.jumper251.replay.utils.fetcher.Consumer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ReplayPlayCommand extends SubCommand {

	public ReplayPlayCommand(AbstractCommand parent) {
		super(parent, "play", "Spiele ein Replay ab", "play <§8Name§b>", true);
	}

	@Override
	public boolean execute(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length != 2) return false;
		
		String name = args[1];
		
		final Player p = (Player)cs;	
		
		if (ReplaySaver.exists(name) && !ReplayHelper.replaySessions.containsKey(p.getName())) {
			p.sendMessage("");
			p.sendMessage(ReplaySystem.PREFIX + "Die Replay-Daten von §e" + name + "§7 werden geladen...");
			try {
				ReplaySaver.load(args[1], new Consumer<Replay>() {
					
					@Override
					public void accept(Replay replay) {
						p.sendMessage(ReplaySystem.PREFIX + "Das Replay wurde geladen. Dauer: §e" + (replay.getData().getDuration() / 20) + "§7 Sekunden");
						p.sendMessage("");
						
						BaseComponent[] info, leave, delete;
						info = new ComponentBuilder("§8[§b§lInfos§8] §7Infos über das Replay")
								.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/replay info " + name))
								.create();
						leave = new ComponentBuilder("§8[§c§lVerlassen§8] §7Verlasse das Replay")
								.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/replay leave"))
								.create();
						delete = new ComponentBuilder("§8[§4§lLöschen§8] §7Lösche das jeweilige Replay")
								.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/replay delete " + name))
								.create();
						
						((Player) cs).spigot().sendMessage(info);
						((Player) cs).spigot().sendMessage(leave);
						((Player) cs).spigot().sendMessage(delete);
						p.sendMessage("");
						replay.play(p);
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
				
				p.sendMessage(ReplaySystem.PREFIX + "§cError while loading §o" + name + ".replay. §r§cCheck console for more details.");
			}
		} else {
			p.sendMessage(ReplaySystem.PREFIX + "§cReplay wurde nicht gefunden.");
		}
		
		return true;
	}
	
	@Override
	public List<String> onTab(CommandSender cs, Command cmd, String label, String[] args) {
		return ReplaySaver.getReplays().stream()
				.filter(name -> StringUtil.startsWithIgnoreCase(name, args.length > 1 ? args[1] : null))
				.collect(Collectors.toList());
	}
	

	
}
