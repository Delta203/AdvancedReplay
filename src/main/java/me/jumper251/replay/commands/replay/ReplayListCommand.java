package me.jumper251.replay.commands.replay;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.commands.AbstractCommand;
import me.jumper251.replay.commands.CommandPagination;
import me.jumper251.replay.commands.IPaginationExecutor;
import me.jumper251.replay.commands.SubCommand;
import me.jumper251.replay.filesystem.saving.DatabaseReplaySaver;
import me.jumper251.replay.filesystem.saving.DefaultReplaySaver;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.data.ReplayInfo;
import me.jumper251.replay.utils.MathUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;

public class ReplayListCommand extends SubCommand {

	public ReplayListCommand(AbstractCommand parent) {
		super(parent, "list", "Zeigt alle Replays", "list [§8Seite§b]", false);
	}

	@Override
	public boolean execute(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length > 2) return false;

		
		if (ReplaySaver.getReplays().size() > 0) {
			int page = 1;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));

			if (args.length == 2 && MathUtils.isInt(args[1])) page = Integer.valueOf(args[1]);
			

			List<String> replays = ReplaySaver.getReplays();
			replays.sort(dateComparator());
			
			CommandPagination<String> pagination = new CommandPagination<>(replays, 9);
			cs.sendMessage(ReplaySystem.PREFIX + "Verfügbare Replays: §8(§6" + ReplaySaver.getReplays().size() + "§8) §7| Seite: §e" + page + "§7/§e" + pagination.getPages());

			pagination.printPage(page, new IPaginationExecutor<String>() {

				@Override
				public void print(String element) {
					String message = String.format(" §6§o%s    §e%s", (getCreationDate(element) != null ? format.format(getCreationDate(element)) : ""), element);
					if (cs instanceof Player) {
						BaseComponent[] comps;
						if (DatabaseReplaySaver.getInfo(element) != null && DatabaseReplaySaver.getInfo(element).getCreator() != null) {
							ReplayInfo info = DatabaseReplaySaver.getInfo(element);

							comps = new ComponentBuilder(message)
									.event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("§7Replay §e§l" + info.getID() + "\n\n§7Erstellt von: §6" + info.getCreator() + "\n§7Dauer: §6" + (info.getDuration() / 20) + " §7Sekunden" + "\n\n§aJetzt abspielen!").create()))
									.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/replay play " + info.getID()))
									.create();
						} else {
							comps = new ComponentBuilder(message)
									.event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("§7Replay §e§l" + element + "\n\n§aJetzt abspielen!").create()))
									.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/replay play " + element))
									.create();
						}
						((Player) cs).spigot().sendMessage(comps);
					} else {
						cs.sendMessage(message);
					}
					
				}
			});
						
			
		} else {
			cs.sendMessage(ReplaySystem.PREFIX + "§cEs wurden keine Replays gefunden.");
		}
		return true;
	}
	
	private Date getCreationDate(String replay) {
		if (ReplaySaver.replaySaver instanceof DefaultReplaySaver) {
			return new Date(new File(DefaultReplaySaver.DIR, replay + ".replay").lastModified());
		}
		
		if (ReplaySaver.replaySaver instanceof DatabaseReplaySaver) {
			return new Date(DatabaseReplaySaver.replayCache.get(replay).getTime());
		}
		
		return null;
	}
	
	private Comparator<String> dateComparator() {
		return (s1, s2) -> {
			if (getCreationDate(s1) != null && getCreationDate(s2) != null) {
				return getCreationDate(s1).compareTo(getCreationDate(s2));
			} else {
				return 0;
			}
			
		};
	}
	
	@Override
	public List<String> onTab(CommandSender cs, Command cmd, String label, String[] args) {
		return IntStream.range(1, new CommandPagination<>(ReplaySaver.getReplays(), 9).getPages() + 1)
				.boxed()
				.map(String::valueOf)
				.collect(Collectors.toList());
	}

	
}
