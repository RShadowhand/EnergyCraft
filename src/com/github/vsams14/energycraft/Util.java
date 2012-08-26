package com.github.vsams14.energycraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class Util {
	private Main main;

	public Util(Main main) {
		this.main = main;
	}

	public boolean getsetCondenser(Block b)	{
		Block[] cb = new Block[7];
		if ((cb = getConv(b)) != null) {
			ItemStack t = new ItemStack(0, -1);
			Condenser c = new Condenser(cb, getOrt(b), 0, t, false, main);
			if (!main.con.containsKey(c.toString())) {
				main.con.put(c.toString(), c);
				main.getServer().broadcastMessage("Added Condenser " + c.bString());
				return true;
			}
			main.getServer().broadcastMessage("Invalid location for Condenser!");
			return false;
		}
		return true;
	}

	public void stringToCondenser(String s) {
		String[] p = s.split(":");
		World w = main.getServer().getWorld(p[0]);
		Block[] blocks = new Block[7];

		boolean pause = Boolean.parseBoolean(p[8]);
		int x = Integer.parseInt(p[1]);
		int y = Integer.parseInt(p[2]);
		int z = Integer.parseInt(p[3]);
		int ort = Integer.parseInt(p[4]);
		int EMC = Integer.parseInt(p[5]);
		if (w != null) {
			blocks[0] = w.getBlockAt(x, y, z);
			blocks[3] = w.getBlockAt(x, y + 1, z);
			blocks[1] = lFO(blocks[0], ort, 1);
			blocks[2] = lFO(blocks[0], ort, 2);
			blocks[4] = lFO(blocks[3], ort, 1);
			blocks[5] = lFO(blocks[3], ort, 2);
			if (cBlocks(blocks)) {
				ItemStack t = new ItemStack(Integer.parseInt(p[6]), 1, Short.parseShort(p[7]));
				Condenser c = new Condenser(blocks, ort, EMC, t, pause, main);
				main.log.info("Added Condenser " + c.bString());
				if (!main.con.containsKey(s))
					main.con.put(c.toString(), c);
			} else {
				main.log.severe("The converter (" + s + ") is missing or corrupt!");
			}
		}
	}

	public void breakCondenser(Condenser c) {
		c.blocks[6].setTypeId(0);
		for (int x = 0; x < 6; x++) {
			c.blocks[x].setTypeId(0);
		}
	}

	public Condenser removeCondenser(Block b) {
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
		}
		else if (b.getType() == Material.DIAMOND_BLOCK) {
			for (String s : main.con.keySet()) {
				Condenser c = main.con.get(s);
				Location lo = (Location)c.locs.get("db");
				if (l.equals(lo)) return c;
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
				if ((b2.getType() == Material.DIAMOND_BLOCK) && (b3.getType() == Material.OBSIDIAN)) return b;
			}
		}
		else if (b.getType() == Material.OBSIDIAN) {
			for (int x = 1; x < 5; x++) {
				Block b2 = lFO(b, x, 1);
				Block b3 = lFO(b, oppOrt(x), 1);
				if ((b2.getType() == Material.DIAMOND_BLOCK) && (b3.getType() == Material.IRON_BLOCK)) return b3;
			}
		}
		else if (b.getType() == Material.DIAMOND_BLOCK) {
			for (int x = 1; x < 5; x++) {
				Block b2 = lFO(b, x, 1);
				Block b3 = lFO(b, x, 2);
				if ((b2.getType() == Material.OBSIDIAN) && (b3.getType() == Material.IRON_BLOCK)) return b3;
			}
		}
		return null;
	}

	public boolean hasTop(Block b) {
		Block x = b.getWorld().getBlockAt(b.getX(), b.getY() + 1, b.getZ());
		if (b.getType() == Material.IRON_BLOCK) {
			if (x.getType() == Material.CHEST) return true;
		}
		else if (b.getType() == Material.OBSIDIAN) {
			if (x.getType() == Material.ENCHANTMENT_TABLE) return true;
		}
		else if ((b.getType() == Material.DIAMOND_BLOCK) && 
				(x.getType() == Material.CHEST)) {
			return true;
		}

		return false;
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
		Location loc = b.getLocation();
		switch (i) {
		case 1:
			loc.setX(loc.getX() + length);
			break;
		case 2:
			loc.setZ(loc.getZ() + length);
			break;
		case 3:
			loc.setX(loc.getX() - length);
			break;
		case 4:
			loc.setZ(loc.getZ() - length);
		}

		return loc.getBlock();
	}

	public boolean cBlocks(Block[] a) {
		return (a[0].getType() == Material.IRON_BLOCK) && (a[1].getType() == Material.OBSIDIAN) && 
				(a[2].getType() == Material.DIAMOND_BLOCK) && (a[3].getType() == Material.CHEST) && 
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
}