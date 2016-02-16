package com.github.vsams14.energycraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	Map<String, Condenser> con = new HashMap<String, Condenser>();
	public Util util = new Util(this);
	public Config conf = new Config(this);
	public Logger log;
	public static Main instance;
	private int checkInterval = 1;
	public int maxStackCondense = 4;
	public boolean permuse = false;
	public boolean broadcast = false;
	public List<Material> baseblocks = new ArrayList<Material>();
	public List<Material> varBase = new ArrayList<Material>();
	public int l1, l2, l3, l4;
	public Material m1, m2, m3, m4;

	public void onEnable() {
		new Events(this);
		instance = this;
		log = getLogger();
		reloadConfig();
		loadConfig();
		conf.loadEMCConfig();
		conf.loadCon();
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new CondenseTask(), 0L, checkInterval * 5L);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (cmd.getName().equalsIgnoreCase("emc") && (sender instanceof Player)) {
			Player p = (Player) sender;
			if (p.getItemInHand() == null) {
				p.sendMessage("You are not holding anything.");
				return true;
			}
			p.sendMessage("The EMC value of: " + Main.instance.conf.getName(p.getItemInHand(), false) + " is " + Main.instance.conf.getEMC(p.getItemInHand()));
			return true;
		}else if(cmd.getName().equalsIgnoreCase("ec") && args[0].equalsIgnoreCase("reload")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(p.hasPermission("ec.reload")){
					conf.loadEMCConfig();
					for(String s : con.keySet()){
						Condenser c =  Main.instance.con.get(s);
						c.reload();
					}
					getConfig().options().copyDefaults(true);
					saveConfig();
					reloadConfig();
					return true;
				}
			}else{
				conf.loadEMCConfig();
				for(String s : con.keySet()){
					Condenser c =  Main.instance.con.get(s);
					c.reload();
				}
				getConfig().options().copyDefaults(true);
				saveConfig();
				reloadConfig();
				return true;
			}
		}
		return true;
	}

	private void loadConfig(){
		reloadConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		checkInterval = getConfig().getInt("check-interval");
		maxStackCondense = getConfig().getInt("max-stack-condense");
		permuse = getConfig().getBoolean("permission-to-use");
		broadcast = getConfig().getBoolean("broadcast-messages");
		
		//set up baseblocks
		Material m;
		String s = getConfig().getString("level1");
		String[] p = s.split("-");
		m = Material.getMaterial(Integer.parseInt(p[0]));
		if(m.equals(null)) m = Material.GOLD_BLOCK;
		baseblocks.add(m);
		m1 = m;
		l1 = Integer.parseInt(p[1].replace("%", ""));
		s = getConfig().getString("level2");
		p = s.split("-");
		m = Material.getMaterial(Integer.parseInt(p[0]));
		if(m.equals(null)) m = Material.EMERALD_BLOCK;
		baseblocks.add(m);
		m2 = m;
		l2 = Integer.parseInt(p[1].replace("%", ""));
		s = getConfig().getString("level3");
		p = s.split("-");
		m = Material.getMaterial(Integer.parseInt(p[0]));
		if(m.equals(null)) m = Material.DIAMOND_BLOCK;
		baseblocks.add(m);
		m3 = m;
		l3 = Integer.parseInt(p[1].replace("%", ""));
		s = getConfig().getString("level4");
		p = s.split("-");
		m = Material.getMaterial(Integer.parseInt(p[0]));
		if(m.equals(null)) m = Material.BEACON;
		baseblocks.add(m);
		m4 = m;
		l4 = Integer.parseInt(p[1].replace("%", ""));
		varBase.add(m1);
		varBase.add(m2);
		varBase.add(m3);
		varBase.add(m4);
		//defaults for base
		baseblocks.add(Material.CHEST);
		baseblocks.add(Material.ENCHANTMENT_TABLE);
		baseblocks.add(Material.IRON_BLOCK);
		baseblocks.add(Material.OBSIDIAN);
	}

	public void onDisable()	{
		getConfig().options().copyDefaults(true);
		saveConfig();
		conf.saveCon();
	}
}