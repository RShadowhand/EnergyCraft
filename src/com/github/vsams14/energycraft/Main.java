package com.github.vsams14.energycraft;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	Map<String, Condenser> con = new HashMap<String, Condenser>();
	public Util util = new Util(this);
	public Config conf = new Config(this);
	public Logger log;
	public static Main instance;
	private int checkInterval = 1;
	public int maxStackCondense = 4;

	public void onEnable() {
		new Events(this);
		instance = this;
		log = getLogger();
		reloadConfig();
		conf.loadEMCConfig();
		conf.loadCon();
		loadConfig();
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new CondenseTask(), 0L, checkInterval * 5L);
		getCommand("emc").setExecutor(new EMCCommand());
	}

	private void loadConfig(){
		reloadConfig();
		checkInterval = getConfig().getInt("check-interval");
		maxStackCondense = getConfig().getInt("max-stack-condense");
	}

	public void onDisable()	{
		getConfig().options().copyDefaults(true);
		saveConfig();
		conf.saveCon();
	}
}