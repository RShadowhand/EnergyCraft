package com.github.vsams14.energycraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Util {
	private Main main;

	public Util(Main main) {
		this.main = main;
	}
	
	public void broadcast(String s){
		if(!main.broadcast) return;
		for(Player p : main.getServer().getOnlinePlayers()){
			if(p.hasPermission("ec.broadcast")){
				p.sendMessage(s);
			}
		}
	}

	public void setCondenser(Block b, String p)	{
		Block x = getBase(b);
		if(x == null)return;
		Block[] cb = new Block[7];
		if ((cb = getConv(x)) != null) {
			ItemStack t = new ItemStack(0, -1);
			Condenser c = new Condenser(cb, getOrt(x), 0, t, false, main, p, getEfficiency(cb));
			if (!main.con.containsKey(c.toString())) {
				main.con.put(c.toString(), c);
				broadcast("Added Condenser " + c.bString());
				return;
			}
			broadcast("Invalid attempt to build a condenser by "+p+"!");
		}
	}
	
	public boolean allowBuild(Block b){
		Block x = getBase(b);
		if(x == null){
			return true;
		}
		Block[] cb = getConv(x);
		if (cb == null)return true;
		else return false;
	}
	
	public int getEfficiency(Block[] cb){
		if(cb[2].getType().equals(main.m1)) return 1;
		if(cb[2].getType().equals(main.m2)) return 2;
		if(cb[2].getType().equals(main.m3)) return 3;
		if(cb[2].getType().equals(main.m4)) return 4;
		return 0;
	}
	
	public boolean willCreateCondenser(Block b){
		Block x = getBase(b);
		Block[] cb = getConv(x);
		if(cb != null){
			ItemStack t = new ItemStack(0, -1);
			Condenser c = new Condenser(cb, getOrt(x), 0, t, false, main, "Owner", getEfficiency(cb));
			if (!main.con.containsKey(c.toString())){
				for(Block z : cb){
					if(getCondenser(z)!=null){
						broadcast("Condensers should not intersect!");
						c.removesign();
						return false;
					}
				}
				c.removesign();
				return true;
			}else{
				broadcast("Condensers should not intersect!");
				c.removesign();
				return false;
			}
		}
		return false;
	}

	public void stringToCondenser(String s) {
		String[] p = s.split(":");
		if(p.length<10){
			main.log.severe("The string '"+s+"' is missing one or more elements. COULD NOT LOAD!");
			return;
		}
		World w = main.getServer().getWorld(p[0]);
		Block[] blocks = new Block[7];

		boolean pause = Boolean.parseBoolean(p[8]);
		int x = Integer.parseInt(p[1]);
		int y = Integer.parseInt(p[2]);
		int z = Integer.parseInt(p[3]);
		int ort = Integer.parseInt(p[4]);
		float EMC = Float.parseFloat(p[5]);
		if (w != null) {
			blocks[0] = w.getBlockAt(x, y, z);
			blocks[3] = w.getBlockAt(x, y + 1, z);
			blocks[1] = lFO(blocks[0], ort, 1);
			blocks[2] = lFO(blocks[0], ort, 2);
			blocks[4] = lFO(blocks[3], ort, 1);
			blocks[5] = lFO(blocks[3], ort, 2);
			if (cBlocks(blocks)) {
				ItemStack t = new ItemStack(Integer.parseInt(p[6]), 1, Short.parseShort(p[7]));
				Condenser c = new Condenser(blocks, ort, EMC, t, pause, main, p[9], getEfficiency(blocks));
				main.log.info("Added Condenser " + c.bString());
				if (!main.con.containsKey(s))
					main.con.put(c.toString(), c);
			} else {
				main.log.severe("The condenser (" + s + ") is missing or corrupt!");
			}
		}
	}
	
	public Material getMaterialfromEff(int eff){
		if (eff==1) return main.m1;
		if (eff==2) return main.m2;
		if (eff==3) return main.m3;
		if (eff==4) return main.m4;
		return null;
	}

	public void breakCondenser(Condenser c) {
		c.removesign();
		Material m = getMaterialfromEff(getEfficiency(c.blocks));
		Byte z = c.blocks[3].getData();
		for (int x = 0; x < 6; x++) {
			c.blocks[x].setTypeId(0);
		}
		c.blocks[1].setType(Material.CHEST);
		Block b = c.blocks[1];
		b.setData(z);
		BlockState bs = b.getState();
		if(bs instanceof Chest){
			Chest save = (Chest) bs;
			Inventory i = save.getInventory();
			i.addItem((new ItemStack(54, 1)), (new ItemStack(116, 1)), (new ItemStack(42, 1)),
					(new ItemStack(49, 1)), (new ItemStack(m, 1)));
		}
	}

	public Condenser getCondenser(Block b) {
		Location l = b.getLocation();
		if (b.getType() == Material.IRON_BLOCK) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("base");
				if (l.equals(lo)) return c;
			}
		}
		else if (b.getType() == Material.OBSIDIAN) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("obs");
				if (l.equals(lo)) return c;
			}
		}else if (b.getType() == main.m4) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("db");
				if (l.equals(lo) && c.efficiency==4) return c;
			}
		}else if (b.getType() == main.m3) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("db");
				if (l.equals(lo) && c.efficiency==3) return c;
			}
		}else if (b.getType() == main.m2) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("db");
				if (l.equals(lo) && c.efficiency==2) return c;
			}
		}else if (b.getType() == main.m1) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("db");
				if (l.equals(lo) && c.efficiency==1) return c;
			}
		}
		else if (b.getType() == Material.CHEST) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("in");
				if (l.equals(lo)) return c;
				lo = (Location)c.locs.get("out");
				if (l.equals(lo))
					return c;
			}
		}
		else if (b.getType() == Material.ENCHANTMENT_TABLE) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("et");
				if (l.equals(lo)) return c;
			}
		}
		else if (b.getType() == Material.WALL_SIGN) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("sign");
				if (l.equals(lo)) return c;
			}
		}
		return null;
	}

	public Block getBase(Block b) {
		if (b.getType() == Material.IRON_BLOCK) {
			for (int x = 1; x < 5; x++) {
				Block b2 = lFO(b, x, 2);
				Block b3 = lFO(b, x, 1);
				if ((main.varBase.contains(b2.getType())) && (b3.getType() == Material.OBSIDIAN)) return b;
			}
		}else if (b.getType() == Material.OBSIDIAN) {
			for (int x = 1; x < 5; x++) {
				Block b2 = lFO(b, x, 1);
				Block b3 = lFO(b, oppOrt(x), 1);
				if ((main.varBase.contains(b2.getType())) && (b3.getType() == Material.IRON_BLOCK)) return b3;
			}
		}else if (main.varBase.contains(b.getType())) {
			for (int x = 1; x < 5; x++) {
				Block b2 = lFO(b, x, 1);
				Block b3 = lFO(b, x, 2);
				if ((b2.getType() == Material.OBSIDIAN) && (b3.getType() == Material.IRON_BLOCK)) return b3;
			}
		}else if (b.getType() == Material.CHEST) {
			Block u = b.getWorld().getBlockAt(b.getX(), b.getY()-1, b.getZ());
			if(u.getType() == Material.IRON_BLOCK) return u;
			else if(u.getType() == Material.OBSIDIAN || main.varBase.contains(u.getType())){
				Block b2 = getBase(u);
				return b2;
			}
		}else if (b.getType() == Material.ENCHANTMENT_TABLE) {
			Block u = b.getWorld().getBlockAt(b.getX(), b.getY()-1, b.getZ());
			if(u.getType() == Material.OBSIDIAN){
				Block b2 = getBase(u);
				return b2;
			}
		}
		return null;
	}

	public int oppOrt(int i) {
		switch (i) {
		case 1:
			return 3;
		case 2:
			return 4;
		case 3:
			return 1;
		case 4:
			return 2;
		}
		return 0;
	}

	public int rotOrt(int i) {
		switch (i) {
		case 1:
			return 2;
		case 2:
			return 3;
		case 3:
			return 4;
		case 4:
			return 1;
		}
		return 0;
	}

	public Block lFO(Block b, int i, int length) {
		World w = b.getWorld();
		int x, y, z;
		x = b.getX();
		y = b.getY();
		z = b.getZ();
		switch (i) {
		case 1:
			return w.getBlockAt(x+length, y, z);
		case 2:
			return w.getBlockAt(x, y, z+length);
		case 3:
			return w.getBlockAt(x-length, y, z);
		case 4:
			return w.getBlockAt(x, y, z-length);
		}
		return null;
	}

	public boolean cBlocks(Block[] a) {
		return (a[0].getType() == Material.IRON_BLOCK) && (a[1].getType() == Material.OBSIDIAN) && 
				(main.varBase.contains(a[2].getType())) && (a[3].getType() == Material.CHEST) && 
				(a[4].getType() == Material.ENCHANTMENT_TABLE) && (a[5].getType() == Material.CHEST);
	}

	public Block[] getConv(Block b) {
		Block[] c = new Block[7];
		for (int x = 1; x < 5; x++) {
			c[0] = b;
			c[1] = lFO(b, x, 1);
			c[2] = lFO(b, x, 2);
			Block b2 = b.getWorld().getBlockAt(b.getX(), b.getY() + 1, b.getZ());
			c[3] = b2;
			c[4] = lFO(b2, x, 1);
			c[5] = lFO(b2, x, 2);
			if (cBlocks(c)) {
				return c;
			}
		}
		return null;
	}

	public int ortToByte(int i) {
		switch (i) {
		case 1:
			return 3;
		case 2:
			return 4;
		case 3:
			return 2;
		case 4:
			return 5;
		}
		return 0;
	}

	public int getOrt(Block b) {
		Block[] c = new Block[7];
		for (int x = 1; x < 5; x++) {
			c[0] = b;
			c[1] = lFO(b, x, 1);
			c[2] = lFO(b, x, 2);
			Block b2 = b.getWorld().getBlockAt(b.getX(), b.getY() + 1, b.getZ());
			c[3] = b2;
			c[4] = lFO(b2, x, 1);
			c[5] = lFO(b2, x, 2);
			if (cBlocks(c)) {
				return x;
			}
		}
		return 0;
	}

	public String getItemType(ItemStack i){
		return i.getType().toString().replaceAll("_", " ").replaceAll(" ON", "").replaceAll(" OFF", "");
	}
}