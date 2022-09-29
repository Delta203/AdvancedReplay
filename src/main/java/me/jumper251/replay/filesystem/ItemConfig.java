package me.jumper251.replay.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.utils.MaterialBridge;
import me.jumper251.replay.utils.VersionUtil;
import me.jumper251.replay.utils.VersionUtil.VersionEnum;

public class ItemConfig {

	public static File file = new File(ReplaySystem.getInstance().getDataFolder(), "items.yml");
	public static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	
	public static HashMap<ItemConfigType, ItemConfigOption> items = new HashMap<>(); 

	
	public static void loadConfig() {
		addDefaults();
		
		if (!file.exists()) {
			for (ItemConfigType type : items.keySet()) {
				String name = type.name().toLowerCase();
				ItemConfigOption item = items.get(type);
				
				cfg.set("items." + name + ".name" , item.getName());
				cfg.set("items." + name + ".id" , item.getMaterial().name() + (item.getData() != 0 ? ":" + item.getData() : ""));
				cfg.set("items." + name + ".slot" , item.getSlot());

				if (item.getOwner() != null) {
					cfg.set("items." + name + ".owner" , item.getOwner());
				}
				
				cfg.set("items." + name + ".enabled" , item.isEnabled());


			}
			
			try {
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void loadData() {
		for (ItemConfigType type : ItemConfigType.values()) {
			String name = type.name().toLowerCase();
			
			String displayName = cfg.getString("items." + name + ".name");
			String owner = cfg.getString("items." + name + ".owner");
			String matString = cfg.getString("items." + name + ".id").toUpperCase();
			int slot = cfg.getInt("items." + name + ".slot");
			boolean enabled = cfg.getBoolean("items." + name + ".enabled", true);
			
			int data = 0;
			if (matString.contains(":")) {
				String[] split = matString.split(":");
				matString = split[0];
				data = Integer.valueOf(split[1]);
			}
			
			Material material = Material.valueOf(matString);
			
			items.put(type, new ItemConfigOption(material, displayName, slot, owner, data).enable(enabled));

		}
	}

	
	public static ItemConfigOption getItem(ItemConfigType type) {
		return items.get(type);
	}
	
	public static ItemConfigType getByIdAndName(Material material, String name) {
		for (ItemConfigType type : items.keySet()) {
			ItemConfigOption option = items.get(type);
						
			if (option.getMaterial() == Material.WOOD_DOOR && (VersionUtil.isCompatible(VersionEnum.V1_13) || VersionUtil.isCompatible(VersionEnum.V1_14) || VersionUtil.isCompatible(VersionEnum.V1_15) || VersionUtil.isCompatible(VersionEnum.V1_16) || VersionUtil.isCompatible(VersionEnum.V1_17) || VersionUtil.isCompatible(VersionEnum.V1_18) || VersionUtil.isCompatible(VersionEnum.V1_19))) {
				if (material.name().equals(MaterialBridge.WOOD_DOOR.getMaterialName()) && option.getName().equals(name)) return type;
			}
			
			if (option.getMaterial() == material && option.getName().equals(name)) return type;
		}
		return null;
	}
	
	private static void addDefaults() {
		items.put(ItemConfigType.TELEPORT, new ItemConfigOption(Material.COMPASS, "&bTeleporter", 0));
		items.put(ItemConfigType.SPEED, new ItemConfigOption(Material.WATCH, "&8[&7Links&8] &cLangsamer &7&l| &aSchneller &8[&7Rechts&8]", 1));
		items.put(ItemConfigType.LEAVE, new ItemConfigOption(Material.IRON_DOOR, "&cReplay verlassen", 8));
		items.put(ItemConfigType.FORWARD, new ItemConfigOption(Material.SKULL_ITEM, "&a» &e10 Sekunden", 5, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU0ZmFiYjE2NjRiOGI0ZDhkYjI4ODk0NzZjNmZlZGRiYjQ1MDVlYmE0Mjg3OGM2NTNhNWQ3OTNmNzE5YjE2In19fQ==", 3));
		items.put(ItemConfigType.BACKWARD, new ItemConfigOption(Material.SKULL_ITEM, "&c« &e10 Sekunden", 3, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzMDFhMTdjOTU1ODA3ZDg5ZjljNzJhMTkyMDdkMTM5M2I4YzU4YzRlNmU0MjBmNzE0ZjY5NmE4N2ZkZCJ9fX0=", 3));
		items.put(ItemConfigType.RESUME, new ItemConfigOption(Material.SLIME_BALL, "&aWeiter", 4));
		items.put(ItemConfigType.PAUSE, new ItemConfigOption(Material.REDSTONE, "&cPause", 4));

	}
	
	
}
