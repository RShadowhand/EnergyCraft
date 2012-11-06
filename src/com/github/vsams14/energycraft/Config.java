package com.github.vsams14.energycraft;

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
					return (int)(dur * ddur);
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
			emcConfig.options().copyDefaults(true);
			emcConfig.setDefaults(defConfig);
		}
		
		String s = emcConfig.getString("Version");
		if(!s.equals(main.getDescription().getVersion())){
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

	public float getMaxDur(ItemStack i) {
		switch (i.getTypeId()) {
		case 276:
			return 1562.0F;
		case 277:
			return 1562.0F;
		case 278:
			return 1562.0F;
		case 279:
			return 1562.0F;
		case 293:
			return 1562.0F;
		case 310:
			return 364.0F;
		case 311:
			return 529.0F;
		case 312:
			return 496.0F;
		case 313:
			return 430.0F;
		}
		return 1.0F;
	}

	public void loadCon() {
		File in = new File(main.getDataFolder(), File.separator + "condensers.sav");
		try {
			BufferedReader readFile = new BufferedReader(new FileReader(in));
			String s;
			while ((s = readFile.readLine()) != null) {
				if (s.contains(":")) {
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