package com.mk7smp.PermPotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class Main extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.addRecipe(getFlyPotionRecipe());
	}

	@Override
	public void onDisable() {

	}

	public ItemStack getItem() {
		
		ItemStack pot = new ItemStack(Material.POTION);
		ItemMeta meta = pot.getItemMeta();
		
		meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Potion of Flight");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "" + "Flight:    " + ChatColor.RED + "" + "20 seconds");
		lore.add(ChatColor.GOLD + "" + "Slow Fall: " + ChatColor.RED + "" + "7 seconds");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Slow falling is granted");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "when flight is disabled.");
		meta.setLore(lore);
		
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);
		
		pot.setItemMeta(meta);
		
		return pot;
	}
	

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		Player player = (Player) event.getPlayer();
		String pname = player.getName();
		if (player.getInventory().getItemInMainHand() != null) {
			if (player.getInventory().getItemInMainHand().getItemMeta() != null) {
				if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Potion of Flight")) {
					if (player.getInventory().getItemInMainHand().getItemMeta().hasLore()) {
						if (player.hasPermission("permpotion.flight")) { // CHECK IF PLAYER ALREADY HAS FLIGHT
							player.sendMessage("You already have active flight. Item was not consumed.");
							event.setCancelled(true);
						} else {
							// RUN CODE HERE AFTER CHECKING FOR POTION
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuaddp " + pname + " permpotion.flight");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuaddp " + pname + " lands.bypass.wilderness.fly");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuaddp " + pname + " lands.bypass.fly");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "essentials:fly " + player.getName());
							player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 1)); // 100 = 5 seconds
							
							ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
							String command = "essentials:fly " + player.getName();
							// begin timer
							// 20L = 1 Second
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {
								public void run() {
									//player.sendMessage("5 seconds!");
									if (player.isOnline() == true) {
										if (player.hasPermission("permpotion.flight")){
											Bukkit.dispatchCommand(console, command);
											player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 150, 1)); // 100 = 5 seconds
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manudelp " + pname + " permpotion.flight");
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manudelp " + pname + " lands.bypass.wilderness.fly");
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manudelp " + pname + " lands.bypass.fly");
										}
									} else {
										// console.sendMessage("");
										System.out.println(pname + " disconnected before flight potion expired. Player will need 'permpotion.flight' removed from their permissions to be able to use the flight potion again.");
									}
								}
							}, 400L);
						}
					}
				}
			}
		}
		
	}

	public ShapedRecipe getFlyPotionRecipe() {
		NamespacedKey key = new NamespacedKey(this, "potionofflight"); // custom name, can be anything
		ShapedRecipe recipe = new ShapedRecipe(key, getItem());
		recipe.shape("GFG", "FFF", "GFG");
		recipe.setIngredient('G', Material.GLASS_BOTTLE);
		recipe.setIngredient('F', Material.FEATHER);
		return recipe;
	}
}
