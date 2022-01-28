package de.warsteiner.tool.listener;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
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
				
				if(player.getGameMode() == GameMode.CREATIVE) {
					return;
				}
				
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
				
				if(cfg.getBoolean("Permissions")) {
					if(!player.hasPermission("Permission")) {
						return;
					}
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
					
					String dis = i.getItemMeta().getDisplayName();
				 
					if(cfg.getBoolean("Enable_Title")) {
						
						String title1 = cfg.getString("Title_First").replaceAll("<item>", dis).replaceAll("&", "§");
						String title2 = cfg.getString("Title_Second").replaceAll("<item>", dis).replaceAll("&", "§");
						
						player.sendTitle(toHex(title1), toHex(title2));
					}
					
					if(cfg.getBoolean("Enable_Message")) {
						
						String m = cfg.getString("Message").replaceAll("<item>", dis).replaceAll("&", "§");
					 
						player.sendMessage(toHex(m));
					}
					
					if(cfg.getBoolean("Enable_Sound")) {
						String type = cfg.getString("Sound.Type").toUpperCase();
						if(Sound.valueOf(type) == null) {
							player.sendMessage("§cToolWarnings §8-> §cError on playing Sound! Sound type not found! ");
							return;
						}
						int vol = cfg.getInt("Sound.Volume");
						int pitch = cfg.getInt("Sound.Pitch");
						 
						player.playSound(player.getLocation(), Sound.valueOf(type), vol, pitch);
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
