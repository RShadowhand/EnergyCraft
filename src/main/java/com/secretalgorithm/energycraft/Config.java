package com.secretalgorithm.energycraft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

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
			String s = i.getTypeId() + "-" + i.getDurability();
			String z = emcConfig.getString(s);
			float dur;

			if (z == null) {
				s = i.getTypeId() + "-A";
				z = emcConfig.getString(s);
				if (z == null) {
					s = i.getTypeId() + "-X";
					z = emcConfig.getString(s);
					if (z == null) {
						return 0;
					}
					if(z.contains("//")){
						String[] p = z.split("//");
						dur = Float.parseFloat(p[0]);	
					}else{
						dur = Float.parseFloat(z);
					}
					float ddur = 1.0F - Float.valueOf(i.getDurability()) / getMaxDur(i);
					return (dur * ddur);
				}
				if(z.contains("//")){
					String[] p = z.split("//");
					return Float.parseFloat(p[0]);	
				}else{
					return Float.parseFloat(z);
				}
			}
			if(z.contains("//")){
				String[] p = z.split("//");
				return Float.parseFloat(p[0]);	
			}else{
				return Float.parseFloat(z);
			}
		}

		return 0;
	}
	
	public float getTaxEMC(ItemStack i, int tax){
		float EMC = getEMC(i);
		float dTax = (float)tax / 100;
		dTax = 1 - dTax;
		//main.getServer().broadcastMessage(tax+"% tax * "+EMC+" EMC = "+EMC*dTax);
		return EMC*dTax;
	}
	
	@SuppressWarnings("deprecation")
	public String getName(ItemStack i, boolean shorten){
		if (i != null) {
			String s = i.getTypeId() + "-" + i.getDurability();
			String z = emcConfig.getString(s);
			String[] p;
			if(z!=null){
				if(z.contains("//")){
					p = z.split("//");
					if(p.length>=2){
						if(p[1].length()<16 || !shorten){
							return p[1];	
						}else{
							return main.util.getItemType(i);
						}	
					}else return main.util.getItemType(i);
				}else return main.util.getItemType(i);
			}else{
				s = i.getTypeId() + "-A";
				z = emcConfig.getString(s);
				if(z!=null){
					if(z.contains("//")){
						p = z.split("//");
						if(p[1].length()<16 || !shorten){
							return p[1];	
						}else return main.util.getItemType(i);
					}else return main.util.getItemType(i);
				}else{
					s = i.getTypeId() + "-X";
					z = emcConfig.getString(s);
					if(z!=null){
						if(z.contains("//")){
							p = z.split("//");
							if(p[1].length()<16 || !shorten){
								return p[1];	
							}else return main.util.getItemType(i);
						}else return main.util.getItemType(i);
					}else return main.util.getItemType(i);
				}
			}
			
		}
		return null;
	}


	@SuppressWarnings({ "deprecation", "unused" })
	public void loadEMCConfig() {
		if (emcConfigFile == null) {
			emcConfigFile = new File(main.getDataFolder(), "emcConfig.yml");
		}
		if (!emcConfigFile.exists()){
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
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(main.getResource("emcConfig.yml"));
			//emcConfig.options().copyDefaults(true);
			//emcConfig.setDefaults(defConfig);
		}
		
		String s = emcConfig.getString("Version");
		if(s!=null){
			if(!s.equals(main.getDescription().getVersion())){
				emcConfigFile.delete();
				loadEMCConfig();
			}	
		}else{
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
		switch (i.getTypeId()) {
		case 256: return 251.0F; //Iron Shovel
		case 257: return 251.0F; //Iron Pick
		case 258: return 251.0F; //Iron Axe
		case 259: return 65.0F; //Flint and Steel
		case 261: return 385.0F; //Bow
		case 267: return 251.0F; //Iron Sword
		case 268: return 60.0F; //Wooden Sword
		case 269: return 60.0F; //Wooden Shovel
		case 270: return 60.0F; //Wooden Pick
		case 271: return 60.0F; //Wooden Axe
		case 272: return 132.0F; //Stone Sword
		case 273: return 132.0F; //Stone Shove
		case 274: return 132.0F; //Stone Pick
		case 275: return 132.0F; //Stone Axe
		case 276: return 1562.0F; //Diamond Sword
		case 277: return 1562.0F; //Diamond Shovel
		case 278: return 1562.0F; //Diamond Pick
		case 279: return 1562.0F; //Diamond Axe
		case 283: return 33.0F; //Gold Sword
		case 284: return 33.0F; //Gold Shovel
		case 285: return 33.0F; //Gold Pick
		case 286: return 33.0F; //Gold Axe
		case 290: return 60.0F; //Wooden Hoe
		case 291: return 132.0F; //Stone Hoe
		case 292: return 251.0F; //Iron Hoe
		case 293: return 1562.0F; //Diamond Hoe
		case 294: return 33.0F; //Gold Hoe
		case 298: return 56.0F; //Leather Cap
		case 299: return 82.0F; //Leather Tunic
		case 300: return 76.0F; //Leather Pants
		case 301: return 66.0F; //Leather Boots
		case 306: return 166.0F; //Iron Helm
		case 307: return 242.0F; //Iron Chestplate
		case 308: return 226.0F; //Iron Leggings
		case 309: return 196.0F; //Iron Boots
		case 310: return 364.0F; //Diamond Helm
		case 311: return 529.0F; //Diamond Chestplate
		case 312: return 496.0F; //Diamond Pants
		case 313: return 430.0F; //Diamond Boots
		case 314: return 78.0F; //Golden Helm
		case 315: return 114.0F; //Golden Chestplate
		case 316: return 106.0F; //Golden Pants
		case 317: return 92.0F; //Golden Boots
		case 346: return 65.0F; //Fishing Rod
		case 359: return 239.0F; //Shears
		}
		return 1.0F;
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
				Condenser c = (Condenser)main.con.get(s);
				fileWriter.write(c.saveString() + "\r\n");
				fileWriter.flush();
			}
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}