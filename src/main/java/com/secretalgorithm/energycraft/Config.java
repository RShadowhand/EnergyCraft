package com.secretalgorithm.energycraft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Config {
  private Main main;
  private FileConfiguration emcConfig = null;
  private File emcConfigFile = null;

  public Config(Main main) {
    this.main = main;
  }

  @SuppressWarnings("deprecation")
  public float getEMC(ItemStack i) {
    if (i != null) {
      String s = i.getType().getId() + "-" + i.getDurability();
      String z = emcConfig.getString(s);
      float dur;
      if (z == null) {
        s = i.getType().getId() + "-A";
        z = emcConfig.getString(s);
        if (z == null) {
          s = i.getType().getId() + "-X";
          z = emcConfig.getString(s);
          if (z == null) {
            return 0;
          }
          if (z.contains("//")) {
            String[] p = z.split("//");
            dur = Float.parseFloat(p[0]);
          } else {
            dur = Float.parseFloat(z);
          }
          float ddur = 1.0F - Float.valueOf(i.getDurability()) / getMaxDur(i);
          return (dur * ddur);
        }
        if (z.contains("//")) {
          String[] p = z.split("//");
          return Float.parseFloat(p[0]);
        } else {
          return Float.parseFloat(z);
        }
      }
      if (z.contains("//")) {
        String[] p = z.split("//");
        return Float.parseFloat(p[0]);
      } else {
        return Float.parseFloat(z);
      }
    }

    return 0;
  }

  public float getTaxEMC(ItemStack i, int tax) {
    float EMC = getEMC(i);
    float dTax = (float) tax / 100;
    dTax = 1 - dTax;
    // main.getServer().broadcastMessage(tax+"% tax * "+EMC+" EMC = "+EMC*dTax);
    return EMC * dTax;
  }

  @SuppressWarnings("deprecation")
  public String getName(ItemStack i, boolean shorten) {
    if (i != null) {
      String s = i.getType() + "-" + i.getDurability();
      String z = emcConfig.getString(s);
      String[] p;
      if (z != null) {
        if (z.contains("//")) {
          p = z.split("//");
          if (p.length >= 2) {
            if (p[1].length() < 16 || !shorten) {
              return p[1];
            } else {
              return main.util.getItemType(i);
            }
          } else
            return main.util.getItemType(i);
        } else
          return main.util.getItemType(i);
      } else {
        s = i.getType() + "-A";
        z = emcConfig.getString(s);
        if (z != null) {
          if (z.contains("//")) {
            p = z.split("//");
            if (p[1].length() < 16 || !shorten) {
              return p[1];
            } else
              return main.util.getItemType(i);
          } else
            return main.util.getItemType(i);
        } else {
          s = i.getType() + "-X";
          z = emcConfig.getString(s);
          if (z != null) {
            if (z.contains("//")) {
              p = z.split("//");
              if (p[1].length() < 16 || !shorten) {
                return p[1];
              } else
                return main.util.getItemType(i);
            } else
              return main.util.getItemType(i);
          } else
            return main.util.getItemType(i);
        }
      }

    }
    return null;
  }

  @SuppressWarnings({ "deprecation" })
  public void loadEMCConfig() {
    if (emcConfigFile == null) {
      emcConfigFile = new File(main.getDataFolder(), "emcConfig.yml");
    }
    if (!emcConfigFile.exists()) {
      try {
        emcConfigFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    emcConfig = YamlConfiguration.loadConfiguration(emcConfigFile);
    try {
      emcConfig.load(emcConfigFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidConfigurationException e) {
      e.printStackTrace();
    }

    InputStream defConfigStream = main.getResource("emcConfig.yml");
    Reader r = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8);
    if (defConfigStream != null) {
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(r);
      emcConfig.options().copyDefaults(true);
      emcConfig.setDefaults(defConfig);
    }

    String s = emcConfig.getString("Version");
    if (s != null) {
      if (!s.equals(main.getDescription().getVersion())) {
        emcConfigFile.delete();
        loadEMCConfig();
      }
    } else {
      emcConfigFile.delete();
      loadEMCConfig();
    }

    try {
      emcConfig.save(emcConfigFile);
      emcConfig.load(emcConfigFile);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("deprecation")
  public float getMaxDur(ItemStack i) {
    switch (i.getType()) {
      case IRON_SHOVEL:
        return 251.0F; // Iron Shovel
      case IRON_PICKAXE:
        return 251.0F; // Iron Pick
      case IRON_AXE:
        return 251.0F; // Iron Axe
      case FLINT_AND_STEEL:
        return 65.0F; // Flint and Steel
      case BOW:
        return 385.0F; // Bow
      case IRON_SWORD:
        return 251.0F; // Iron Sword
      case WOODEN_SWORD:
        return 60.0F; // Wooden Sword
      case WOODEN_SHOVEL:
        return 60.0F; // Wooden Shovel
      case WOODEN_PICKAXE:
        return 60.0F; // Wooden Pick
      case WOODEN_AXE:
        return 60.0F; // Wooden Axe
      case STONE_SWORD:
        return 132.0F; // Stone Sword
      case STONE_SHOVEL:
        return 132.0F; // Stone Shove
      case STONE_PICKAXE:
        return 132.0F; // Stone Pick
      case STONE_AXE:
        return 132.0F; // Stone Axe
      case DIAMOND_SWORD:
        return 1562.0F; // Diamond Sword
      case DIAMOND_SHOVEL:
        return 1562.0F; // Diamond Shovel
      case DIAMOND_PICKAXE:
        return 1562.0F; // Diamond Pick
      case DIAMOND_AXE:
        return 1562.0F; // Diamond Axe
      case GOLDEN_SWORD:
        return 33.0F; // Gold Sword
      case GOLDEN_SHOVEL:
        return 33.0F; // Gold Shovel
      case GOLDEN_PICKAXE:
        return 33.0F; // Gold Pick
      case GOLDEN_AXE:
        return 33.0F; // Gold Axe
      case WOODEN_HOE:
        return 60.0F; // Wooden Hoe
      case STONE_HOE:
        return 132.0F; // Stone Hoe
      case IRON_HOE:
        return 251.0F; // Iron Hoe
      case DIAMOND_HOE:
        return 1562.0F; // Diamond Hoe
      case GOLDEN_HOE:
        return 33.0F; // Gold Hoe
      case LEATHER_HELMET:
        return 56.0F; // Leather Cap
      case LEATHER_CHESTPLATE:
        return 82.0F; // Leather Tunic
      case LEATHER_LEGGINGS:
        return 76.0F; // Leather Pants
      case LEATHER_BOOTS:
        return 66.0F; // Leather Boots
      case IRON_HELMET:
        return 166.0F; // Iron Helm
      case IRON_CHESTPLATE:
        return 242.0F; // Iron Chestplate
      case IRON_LEGGINGS:
        return 226.0F; // Iron Leggings
      case IRON_BOOTS:
        return 196.0F; // Iron Boots
      case DIAMOND_HELMET:
        return 364.0F; // Diamond Helm
      case DIAMOND_CHESTPLATE:
        return 529.0F; // Diamond Chestplate
      case DIAMOND_LEGGINGS:
        return 496.0F; // Diamond Pants
      case DIAMOND_BOOTS:
        return 430.0F; // Diamond Boots
      case GOLDEN_HELMET:
        return 78.0F; // Golden Helm
      case GOLDEN_CHESTPLATE:
        return 114.0F; // Golden Chestplate
      case GOLDEN_LEGGINGS:
        return 106.0F; // Golden Pants
      case GOLDEN_BOOTS:
        return 92.0F; // Golden Boots
      case FISHING_ROD:
        return 65.0F; // Fishing Rod
      case SHEARS:
        return 239.0F; // Shears
      default:
        return 1.0f;
    }
  }

  public void loadCon() {
    File in = new File(main.getDataFolder(), File.separator + "condensers.sav");
    try {
      BufferedReader readFile = new BufferedReader(new FileReader(in));
      String s;
      while ((s = readFile.readLine()) != null) {
        if (s.contains(":") && !s.contains("#")) {
          main.util.stringToCondenser(s);
        }
      }
      readFile.close();
    } catch (FileNotFoundException e) {
      main.log.severe("condensers.sav could not be found! Make sure that this is not an error!");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void saveCon() {
    File out = new File(main.getDataFolder(), File.separator + "condensers.sav");
    try {
      BufferedWriter fileWriter = new BufferedWriter(new FileWriter(out));
      fileWriter.write("# DO NOT EDIT THIS FILE!\r\n");
      fileWriter.write("# world:x:y:z:orientation:EMC:targetID:targetDur:paused:owner\r\n");
      fileWriter.flush();
      for (String s : main.con.keySet()) {
        Condenser c = (Condenser) main.con.get(s);
        fileWriter.write(c.saveString() + "\r\n");
        fileWriter.flush();
      }
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
