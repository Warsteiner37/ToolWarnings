package de.warsteiner.tool.utils;
 
import org.bukkit.command.CommandSender;

import de.warsteiner.datax.utils.module.SimplePluginModule;
import de.warsteiner.tool.ToolsPlugin; 

public class PluginModule extends SimplePluginModule {

	@Override
	public String getPluginName() { 
		return "ToolWarnings";
	}

	@Override
	public void reloadConfig(CommandSender arg0) {
		ToolsPlugin.getPlugin().setupConfigs();
	}
 
 
}
