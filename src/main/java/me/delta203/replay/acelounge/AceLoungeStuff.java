package me.delta203.replay.acelounge;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import me.jumper251.replay.ReplaySystem;

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
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Player p = e.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(ReplaySystem.instance, () -> join(p), 5);
	}
	
	private void join(Player p) {
		p.teleport(new Location(Bukkit.getWorld("world"), 0.0, 65.5, 1.0));
		p.sendMessage("");
		p.sendMessage(ReplaySystem.PREFIX + "Willkommen §f§l" + p.getDisplayName() + " §7auf dem §bReplay §7Server!");
		p.sendMessage("§7Hier kannst du Replays von den Modi §cTDM§7, §4Muder§7 und §eBedwars §7anschauen.");
		p.sendMessage("§7benutzte §b/replay list §7um dir die Liste aller Replays zu zeigen. §7Mit §b/replay play §8<Name> §7startest du das jeweilige Replay.");
		p.sendMessage("§7Mit §b/replay §7Bekommst du eine Übersicht der Befehle für das Replaysystem.");
		p.sendMessage("§7Mit §6/lobby §7geht es wieder zurück.");
	}
}
