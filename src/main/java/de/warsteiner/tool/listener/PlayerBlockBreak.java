package de.warsteiner.tool.listener;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
 
import de.warsteiner.tool.ToolsPlugin;
import net.md_5.bungee.api.ChatColor;

public class PlayerBlockBreak implements Listener {
	
	private YamlConfiguration cfg;

	private static final Pattern pattern = Pattern.compile("(?<!\\\\)(#[a-fA-F0-9]{6})");
 
	
	public PlayerBlockBreak(YamlConfiguration cfg) {
		this.cfg = cfg;
	}
 
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(ToolsPlugin.getPlugin(), () -> {
			try {
				
				Player player = event.getPlayer();
				
				if(event.isCancelled()) {
					event.setCancelled(true);
					return;
				}
				
				if(player.getItemInHand() == null) {
					return;
				}
		 
				if(player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR) {
					return;
				}
				ItemStack i = player.getItemInHand();
				
				if(!cfg.getStringList("Items").contains(""+i.getType())) {
					return;
				}
				
				if(!cfg.getStringList("Worlds").contains(player.getWorld().getName())) {
					return;
				}
				
				short dua = i.getDurability();
				short item = i.getType().getMaxDurability();
				
				int warning = cfg.getInt("WarnAt");
				int rechnung = item - dua;
				 
				if(rechnung <= warning) { 
				 
					if(cfg.getBoolean("Enable_Title")) {
						
						String title1 = cfg.getString("Title_First").replaceAll("&", "�");
						String title2 = cfg.getString("Title_Second").replaceAll("&", "�");
						
						player.sendTitle(toHex(title1), toHex(title2));
					}
					
					if(cfg.getBoolean("Enable_Message")) {
						
						String m = cfg.getString("Message").replaceAll("&", "�");
					 
						player.sendMessage(toHex(m));
					}
					
				}
			} catch (Exception e) { 
				e.printStackTrace();
			}
		});
	}
	 
	public String toHex(String motd) {
		Matcher matcher = pattern.matcher(motd);
		while (matcher.find()) {
			String color = motd.substring(matcher.start(), matcher.end());
			motd = motd.replace(color, "" + ChatColor.of(color));
		}

		return motd;
	}
	
}
