package me.jumper251.replay;

import org.bukkit.plugin.java.JavaPlugin;

import me.jumper251.replay.filesystem.ConfigManager;
import me.jumper251.replay.filesystem.saving.DefaultReplaySaver;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.utils.LogUtils;
import me.jumper251.replay.utils.ReplayManager;

public class ReplaySystem extends JavaPlugin{

	
	public static ReplaySystem instance;
	
	public final static String PREFIX = "�8[�3Replay�8] �r�7";

	
	@Override
	public void onDisable() {
		for (Replay replay : ReplayManager.activeReplays.values()) {
			if (replay.isRecording()) {
				replay.getRecorder().stop(ConfigManager.SAVE_STOP);
			}
		}

	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		Long start = System.currentTimeMillis();
		
		
		LogUtils.log("Loading Replay v" + getDescription().getVersion() + " by " + getDescription().getAuthors().get(0));
		
		ReplayManager.register();
		ConfigManager.loadConfigs();
		
		ReplaySaver.register(new DefaultReplaySaver());
		
		LogUtils.log("Finished (" + (System.currentTimeMillis() - start) + "ms)");

	}
	
	
	public static ReplaySystem getInstance() {
		return instance;
	}
}