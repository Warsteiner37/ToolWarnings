package de.warsteiner.tool;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
 
import de.warsteiner.tool.listener.PlayerBlockBreak;
import de.warsteiner.tool.utils.Metrics;
import de.warsteiner.tool.utils.UpdateChecker;
import de.warsteiner.tool.utils.YamlConfigFile;

public class ToolsPlugin extends JavaPlugin {

	private static ToolsPlugin plugin;

	private YamlConfigFile config;

	@Override
	public void onEnable() {

		plugin = this;

		createFolders();

		setupConfigs();

		Bukkit.getPluginManager().registerEvents(new PlayerBlockBreak(this.config.getConfig()), this);

		new Metrics(this, 14014);

		new UpdateChecker(this, 98981).getVersion(version -> {
			if (!this.getDescription().getVersion().equals(version)) {
				this.getLogger().warning("§7Theres a new Plugin Version §aavailable§7! You run on version : §c"+plugin.getDescription().getVersion()+" §8-> §7new version : §a"+version);
			}
		});

		this.getLogger().info("§4§lToolWarings §asuccessfull loaded!");

	}

	public static ToolsPlugin getPlugin() {
		return plugin;
	}

	private void createFolders() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
	}

	public void setupConfigs() {
		File file_config = new File(getDataFolder() + File.separator, "Config.yml");

		config = new YamlConfigFile(getPlugin(), file_config);

		try {
			config.load();
		} catch (IOException e) {
			this.getLogger().warning("§4§lFailed to create Config Files");
			e.printStackTrace();
		}
	}

	public YamlConfigFile getMainConfig() {
		return config;
	}
}
