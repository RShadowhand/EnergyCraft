package com.secretalgorithm.energycraft;

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
    if (main.baseblocks.contains(b.getType())) {
      if (!main.util.allowBuild(b)) {
        if (main.util.willCreateCondenser(b) && event.getPlayer().hasPermission("ec.build")) {
          main.util.setCondenser(b, event.getPlayer().getName());
        } else {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void blockBreak(BlockBreakEvent event) {
    Block b = event.getBlock();
    Condenser s;
    Player p = event.getPlayer();
    if ((s = main.util.getCondenser(b)) != null) {
      if ((p.getName().equals(s.owner) && p.hasPermission("ec.build"))
          || (p.hasPermission("ec.edit") && p.hasPermission("ec.build"))) {
        if (b.getType() == Material.ACACIA_SIGN) {
          event.setCancelled(true);
        } else {
          main.util.broadcast("Broke Condenser " + s.bString());
          main.util.breakCondenser(s);
          main.con.remove(s.toString());
          s = null;
        }
      }
    }
  }

  @SuppressWarnings("deprecation")
  @EventHandler
  public void enchant(PlayerInteractEvent event) {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Block b = event.getClickedBlock();
      if ((b.getType() == Material.ACACIA_SIGN) || (b.getType() == Material.ENCHANTING_TABLE)) {
        Player p = event.getPlayer();
        Condenser c;
        if ((c = main.util.getCondenser(b)) != null) {
          if (p.getName().equals(c.owner) || p.hasPermission("ec.edit")) {
            ItemStack i = p.getItemInHand();
            if (i.getEnchantments().isEmpty()) {
              if (main.conf.getEMC(i) > 0) {
                ItemStack x = i.clone();
                x.setAmount(1);
                c.makesign();
                c.getChests();
                p.updateInventory();
                c.setTarget(i.clone());
                c.updateSign();
              } else if (i.getAmount() <= 0) {
                if (c.pause) {
                  c.pause = false;
                } else {
                  c.pause = true;
                }
                c.updateSign();
              }
            }
            event.setCancelled(true);
          }
        }
      } else if (b.getType() == Material.CHEST) {
        Player p = event.getPlayer();
        Condenser c;
        if ((c = main.util.getCondenser(b)) != null) {
          if (p.getName().equals(c.owner) || p.hasPermission("ec.use.*") || p.hasPermission("ec.use." + c.toString())
              || !main.permuse) {
          } else {
            event.setCancelled(true);
          }
        }
      }
    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      // line of sight blocks = losb
      Block b = event.getClickedBlock();
      // List<Block> losb = event.getPlayer().getTargetBlock(null, 10);
      if (b.getType() == Material.ACACIA_SIGN) {
        Player p = event.getPlayer();
        Condenser c;
        if ((c = main.util.getCondenser(b)) != null) {
          if (p.getName().equals(c.owner) || p.hasPermission("ec.edit")) {
            ItemStack i = p.getItemInHand();
            if (i.getEnchantments().isEmpty()) {
              if (main.conf.getEMC(i) > 0) {
                ItemStack x = i.clone();
                x.setAmount(1);
                c.makesign();
                c.getChests();
                p.updateInventory();
                c.setTarget(i.clone());
                c.updateSign();
              } else if (i.getAmount() <= 0) {
                if (c.pause) {
                  c.pause = false;
                } else {
                  c.pause = true;
                }
                c.updateSign();
              }
            }
            event.setCancelled(true);
          }
        }
      }
    } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
      Block b = event.getClickedBlock();
      if (b.getType() == Material.ACACIA_SIGN) {
        Player p = event.getPlayer();
        Condenser c;
        if ((c = main.util.getCondenser(b)) != null) {
          if (p.hasPermission("ec.reset")) {
            c.reset(event.getPlayer().getName());
          }
        }
      }
    }
  }
}
