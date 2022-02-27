package de.warsteiner.tool;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.warsteiner.datax.SimpleAPI; 
import de.warsteiner.tool.listener.PlayerBlockBreak;
import de.warsteiner.tool.utils.Metrics;
import de.warsteiner.tool.utils.PluginModule; 
import de.warsteiner.tool.utils.YamlConfigFile;

public class ToolsPlugin extends JavaPlugin {

	private static ToolsPlugin plugin;
	private ExecutorService executor;
	private YamlConfigFile config;

	public void onLoad() {
		
		plugin = this;
		executor = Executors.newFixedThreadPool(1);
		
		createFolders();

		setupConfigs();
		if(isInstalledAPI()) {
		this.getExecutor().execute(() -> {   
			if(config.getConfig().getBoolean("CheckForUpdates")) {
				SimpleAPI.getPlugin().getWebManager().loadVersionAndCheckUpdate("https://api.war-projects.com/v1/toolwarnings/version.txt", "ToolWarnings",getDescription().getVersion());
			}
			SimpleAPI.getPlugin().getModuleRegistry().getModuleList().add(new PluginModule());
			this.getLogger().info("§6Hooked into SimpleAPI!");
		});
		}
	}
	
	@Override
	public void onEnable() {
 
		Bukkit.getPluginManager().registerEvents(new PlayerBlockBreak(this.config.getConfig()), this);

		new Metrics(this, 14014);
 
		this.getLogger().info("§4§lToolWarings §asuccessfull loaded!");

	}
	
	public ExecutorService getExecutor() {
		return executor;
	}

	public static ToolsPlugin getPlugin() {
		return plugin;
	}

	private void createFolders() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
	}
	
	public boolean isInstalledAPI() {
		Plugin wgPlugin = Bukkit.getServer().getPluginManager().getPlugin("SimpleAPI");
		if (wgPlugin != null) {
			return true;
		}
		return false;
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
