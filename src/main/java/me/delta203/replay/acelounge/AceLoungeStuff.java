package me.delta203.replay.acelounge;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.servermanager.delta203.api.AceLoungeAPI;
import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.filesystem.saving.DatabaseReplaySaver;
import me.jumper251.replay.filesystem.saving.DefaultReplaySaver;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.data.ReplayInfo;

public class AceLoungeStuff implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByBlockEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onWeather(WeatherChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		e.setQuitMessage(null);
	}
	
	@EventHandler
	public void onLeave(PlayerKickEvent e) {
		e.setLeaveMessage(null);
	}
	
	@EventHandler
	public void onBuild(BlockPlaceEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		e.setCancelled(true);
	}
	
	private ArrayList<Player> wait = new ArrayList<>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			try {
				if(p.getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§cZurück zur Lobby")) {
					e.setCancelled(true);
					AceLoungeAPI.sendOtherServer(p, AceLoungeAPI.lobbies[new Random().nextInt(AceLoungeAPI.lobbies.length)]);
				}else if(p.getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§bReplays")) {
					e.setCancelled(true);
					if(wait.contains(p)) return;
					pages.put(p, 1);
					buildMenuGui(p);
					wait.add(p);
					Bukkit.getScheduler().scheduleSyncDelayedTask(ReplaySystem.instance, () -> wait.remove(p), 20 * 2);
				}else if(p.getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§4Bekannte Fehler")) {
					e.setCancelled(true);
					if(wait.contains(p)) return;
					p.sendMessage(ReplaySystem.PREFIX + "§4Bekannte Fehler§7:");
					p.sendMessage("§7- §cReplays auf selben Welten kann problematisch werden (Blockupdates)");
					p.sendMessage("§7- §cNach Tod eines Replayspielers wird dieser nicht immer neu angezeigt (Packet für höhere Versionen)");
					p.sendMessage("§7└ §aNeu zum Spieler teleportieren zeigt den Spieler wieder an");
					wait.add(p);
					Bukkit.getScheduler().scheduleSyncDelayedTask(ReplaySystem.instance, () -> wait.remove(p), 20 * 2);
				}
			}catch(Exception ex) {}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getInventory().getName().equalsIgnoreCase("§3Replays")) {
			e.setCancelled(true);
			try {
				if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§eVorherige Seite")) {
					if(pages.get(p) > 1) {
						pages.put(p, pages.get(p) - 1);
						buildMenuGui(p);
					}
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§eNächste Seite")) {
					if(e.getInventory().getItem(35) == null || e.getInventory().getItem(35).getType() == Material.AIR) return;
					pages.put(p, pages.get(p) + 1);
					buildMenuGui(p);
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§b§b")) {
					e.getView().close();
					Bukkit.dispatchCommand(p, "replay play " + e.getCurrentItem().getItemMeta().getDisplayName().replace("§b§b", ""));
				}
			}catch(Exception ex) {}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Player p = e.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(ReplaySystem.instance, () -> join(p), 5);
	}
	
	@SuppressWarnings("deprecation")
	private void join(Player p) {
		p.teleport(new Location(Bukkit.getWorld("world"), 0.0, 65.5, 1.0, -90F, 0F));
		p.sendMessage("");
		p.sendMessage(ReplaySystem.PREFIX + "Willkommen §f§l" + p.getDisplayName() + " §7auf dem §bReplay §7Server!");
		p.sendMessage("§7Hier kannst du Replays von den Modi §cTDM§7, §4Muder§7 und §eBedwars §7anschauen.");
		p.sendMessage("§7Benutzte §b/replay list §7um dir die Liste aller Replays zu zeigen. §7Mit §b/replay play §8<Name> §7startest du das jeweilige Replay.");
		p.sendMessage("§7Mit §b/replay §7bekommst du eine Übersicht der Befehle für das Replaysystem.");
		p.sendMessage("§7Mit §6/lobby §7geht es wieder zurück.");
		
		p.getInventory().clear();
		p.getInventory().setItem(0, skullItem("§bReplays", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFlM2EzYTRhMWFhNTBkODVkYmNkYWM4ZGE2M2Q3Y2JmZDQ1ZTUyMGRmZWMyZDUwYmVkZjhlOTBlOGIwZTRlYSJ9fX0=", "§7Zeigt die Liste aller Replays"));
		p.getInventory().setItem(1, normalItem(new ItemStack(Material.BARRIER), "§4Bekannte Fehler", "§cZeigt alle aktuell bekannten Fehler"));
		p.getInventory().setItem(8, normalItem(new ItemStack(Material.MAGMA_CREAM), "§cZurück zur Lobby", "§7Gehe zur Lobby zurück"));
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			if(all != p) {
				all.hidePlayer(p);
				p.hidePlayer(all);
			}
		}
	}
	
	/** Lite ItemBuilder */
	public static ItemStack normalItem(ItemStack item, String name, String lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if(lore != null) {
			ArrayList<String> a = new ArrayList<>();
			String[] larray = lore.split("%s%");
			for(String types : larray) {
				a.add(types);
			}
			meta.setLore(a);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack skullItem(String name, String owner, String lore) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", new String(owner)));
		Field profileField = null;
		try {
			profileField = meta.getClass().getDeclaredField("profile");
		}catch(NoSuchFieldException | SecurityException ex) {}
		profileField.setAccessible(true);
		try {
			profileField.set(meta, profile);
		}catch(IllegalArgumentException | IllegalAccessException ex) {}
		
		meta.setDisplayName(name);
		ArrayList<String> a = new ArrayList<>();
		String[] larray = lore.split("%s%");
		for(String types : larray) {
			a.add(types);
		}
		meta.setLore(a);
		item.setItemMeta(meta);
		return item;
	}
	
	HashMap<Player, Integer> pages = new HashMap<>();
	
	public void buildMenuGui(Player p) {
		Inventory inv = Bukkit.createInventory(p, 54, "§3Replays");
		
		ArrayList<ItemStack> replaysItems = new ArrayList<>();
		List<String> replaysRaw = ReplaySaver.getReplays();
		replaysRaw.sort(dateComparator());
		for(String replay : ReplaySaver.getReplays()) {
			String texVal = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFlM2EzYTRhMWFhNTBkODVkYmNkYWM4ZGE2M2Q3Y2JmZDQ1ZTUyMGRmZWMyZDUwYmVkZjhlOTBlOGIwZTRlYSJ9fX0=";
			if(replay.contains("-bw-")) texVal = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiMjkwYTEzZGY4ODI2N2VhNWY1ZmNmNzk2YjYxNTdmZjY0Y2NlZTVjZDM5ZDQ2OTcyNDU5MWJhYmVlZDFmNiJ9fX0=";
			else if(replay.contains("-tdm-")) texVal = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODcwYWMyOTE2ODA5MGNhMDg5NGYwYzY4YTQ2M2JiZmQ2YmYyZThjMmVhNzhhMDg5NzFkZDA1YTM3ODM3ODI5ZiJ9fX0=";
			else if(replay.contains("-murder-")) texVal = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJlYzk1MmZjMzdkYTAzZDUyYjhjM2UxOTYxM2E4NGQyOTcyMzY5ODVlMThlNzlhM2E1MmRiYTc1MCJ9fX0=";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
			String date = String.format("§3%s", (getCreationDate(replay) != null ? format.format(getCreationDate(replay)) : ""));
			ReplayInfo info = DatabaseReplaySaver.getInfo(replay);
			ItemStack item = skullItem("§b§b" + replay, texVal, "%s%" + date + "%s%§7Erstellt von§8: §6" + info.getCreator() + "%s%§7Dauer§8: §3" + (info.getDuration() / 20) + " §7Sek%s%%s%§aKlicke zum abspielen");
			replaysItems.add(item);
		}
		
		int page = pages.get(p);
		int entriesPerPage = 27;
		int startIndex = (page - 1) * entriesPerPage;
		int endIndex = startIndex + entriesPerPage;
		if(endIndex > replaysItems.size()) {
			endIndex = replaysItems.size();
		}
		
		for(ItemStack is : replaysItems.subList(startIndex, endIndex)) {
			inv.addItem(new ItemStack[] {is});
		}
		
		for(int i = 36; i < 45; i++) {	
			inv.setItem(i, normalItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) AceLoungeAPI.getGlassColor(p.getUniqueId().toString())), " ", null));
		}
		
		inv.setItem(49, skullItem("§bReplays", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFlM2EzYTRhMWFhNTBkODVkYmNkYWM4ZGE2M2Q3Y2JmZDQ1ZTUyMGRmZWMyZDUwYmVkZjhlOTBlOGIwZTRlYSJ9fX0=", "§7Advanced Replays%s%§8(§7AceLounge edit§8)%s%%s%§7Replays§8: §3" + replaysItems.size()));
		
		inv.setItem(45, normalItem(new ItemStack(Material.PAPER), "§eVorherige Seite", "§7Seite§8: §e" + pages.get(p)));
		inv.setItem(53, normalItem(new ItemStack(Material.PAPER), "§eNächste Seite", "§7Seite§8: §e" + pages.get(p)));
		
		p.openInventory(inv);
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
}
