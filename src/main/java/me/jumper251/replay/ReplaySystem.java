package me.jumper251.replay;


import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.delta203.replay.acelounge.AceLoungeStuff;
import me.jumper251.replay.filesystem.ConfigManager;
import me.jumper251.replay.filesystem.saving.DatabaseReplaySaver;
import me.jumper251.replay.filesystem.saving.DefaultReplaySaver;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.replaysystem.utils.ReplayCleanup;
import me.jumper251.replay.utils.Metrics;
import me.jumper251.replay.utils.ReplayManager;
import me.jumper251.replay.utils.Updater;


public class ReplaySystem extends JavaPlugin {

	
	public static ReplaySystem instance;
	
	public static Updater updater;
	public static Metrics metrics;
	
	public final static String PREFIX = "§f[§3Replay§f] §r§7";

	
	@Override
	public void onDisable() {
		for (Replay replay : new HashMap<>(ReplayManager.activeReplays).values()) {
		    if (replay.isRecording() && replay.getRecorder().getData().getActions().size() > 0) {
				replay.getRecorder().stop(ConfigManager.SAVE_STOP);
				
			}
		}

	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		Long start = System.currentTimeMillis();

		getLogger().info("Loading Replay v" + getDescription().getVersion() + " by " + getDescription().getAuthors().get(0));
		
		ConfigManager.loadConfigs();
		ReplayManager.register();
		
		ReplaySaver.register(ConfigManager.USE_DATABASE ? new DatabaseReplaySaver() : new DefaultReplaySaver());
		
		updater = new Updater();
		metrics = new Metrics(this, 2188);
		
		if (ConfigManager.CLEANUP_REPLAYS > 0) {
			ReplayCleanup.cleanupReplays();
		}
		
		if(Bukkit.getServer().getMotd().equalsIgnoreCase("Replay Server")) {
			Bukkit.getPluginManager().registerEvents(new AceLoungeStuff(), instance);
		}
		
		getLogger().info("Plugin geladen (" + (System.currentTimeMillis() - start) + "ms)");

	}
	
	
	public static ReplaySystem getInstance() {
		return instance;
	}
}
