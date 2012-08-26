package com.github.vsams14.energycraft;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	Map<String, Condenser> con = new HashMap<String, Condenser>();
	Util util = new Util(this);
	Config conf = new Config(this);
	Logger log;

	public void onEnable() {
		new Events(this);
		this.log = getLogger();
		reloadConfig();
		this.conf.loadCon();
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (String s : Main.this.con.keySet()) {
					Condenser c = con.get(s);
					c.condense(1);
					c.updateSign();
				}
			}
		}
		, 0L, 20L);

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (String s : Main.this.con.keySet()) {
					Condenser c = con.get(s);
					c.condense(1);
					c.updateSign();
				}
			}
		}
		, 5L, 20L);

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (String s : Main.this.con.keySet()) {
					Condenser c = con.get(s);
					c.condense(1);
					c.updateSign();
				}
			}
		}
		, 10L, 20L);

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (String s : Main.this.con.keySet()) {
					Condenser c = con.get(s);
					c.condense(1);
					c.updateSign();
				}
			}
		}
		, 15L, 20L);
	}

	public void onDisable()	{
		getConfig().options().copyDefaults(true);
		saveConfig();
		conf.saveCon();
	}
}