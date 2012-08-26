package com.github.vsams14.energycraft;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
	private Main main;

	public Events(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		Block b = event.getBlock();
		if ((b.getType() == Material.IRON_BLOCK) || (b.getType() == Material.OBSIDIAN) || (b.getType() == Material.DIAMOND_BLOCK)) {
			Block x;
			if (((x = main.util.getBase(b)) != null) && (main.util.hasTop(b)) && (!main.util.getsetCondenser(x))) {
				b.setTypeId(0);
			}

		}else if ((b.getType() == Material.CHEST) || (b.getType() == Material.ENCHANTMENT_TABLE)) {
			Block x = b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ());
			Block z;
			if (((z = main.util.getBase(x)) != null) && (!main.util.getsetCondenser(z))) {
				b.setTypeId(0);
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		Condenser s;
		if ((s = main.util.removeCondenser(b)) != null) {
			main.getServer().broadcastMessage("Broke Condenser " + s.toString());
			main.util.breakCondenser(s);
			main.con.remove(s.toString());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void enchant(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if ((b.getType() == Material.WALL_SIGN) || (b.getType() == Material.ENCHANTMENT_TABLE)) {
				Player p = event.getPlayer();
				Condenser c;
				if ((c = main.util.removeCondenser(b)) != null) {
					ItemStack i = p.getItemInHand();
					if (main.conf.getEMC(i) > 0) {
						ItemStack x = new ItemStack(i.getType(), 1, i.getDurability());
						c.makesign();
						String type = i.getType().toString().replaceAll("_", " ").replaceAll(" ON", "").replaceAll(" OFF", "");

						if (i.getDurability() > 0) {
							c.s.setLine(1, type + ":" + i.getDurability());
						}else{
							c.s.setLine(1, type);
						}

						c.s.setLine(2, c.EMC + " / " + main.conf.getEMC(i));
						c.updateSign();
						c.getChests();
						c.out.getBlockInventory().addItem(new ItemStack[] { x });
						p.getInventory().removeItem(new ItemStack[] { x });
						p.updateInventory();
						c.target = i.clone();
						c.target.setAmount(1);
						c.targetEMC = main.conf.getEMC(i);
					}else if (i.getAmount() <= 0) {
						if (c.pause) {
							c.pause = false;
						}else{
							c.pause = true;
						}
						c.updateSign();
					}

					event.setCancelled(true);
				}
			}
		}
	}
}