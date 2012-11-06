package com.github.vsams14.energycraft;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Condenser {
	public Block[] blocks = new Block[7];
	public int ort = 0;
	public Chest in;
	public Chest out;
	public float EMC = 0; public float targetEMC = 0;
	public ItemStack target = new ItemStack(Material.AIR, -1, (short) 0);
	public Map<String, Location> locs = new HashMap<String, Location>();
	public Sign s;
	public boolean pause = false;
	private Main main;
	public Chunk chunk;
	public World w;
	public String owner = "";

	public Condenser(Block[] b, int ort, float emc, ItemStack t, boolean pause, Main main, String owner) {
		blocks = b;
		this.ort = ort;
		EMC = emc;
		target = t;
		this.main = main;
		targetEMC = main.conf.getEMC(target);
		this.pause = pause;
		getChests();
		makesign();
		getLocs();
		chunk = blocks[0].getChunk();
		w = blocks[0].getWorld();
		this.owner = owner;
	}

	public void condense() {
		if (!pause){
			if (targetEMC > 0) {
				getChests();
				if (hasEmptySpace(out.getBlockInventory())) {
					for (int x = 0; x < in.getBlockInventory().getSize(); x++) {
						ItemStack h = in.getBlockInventory().getItem(x);
						if (h != null) {
							ItemStack j = h.clone();
							j.setAmount(1);
							if (main.conf.getEMC(h) > 0) {
								getChests();
								in.getBlockInventory().removeItem(j);
								EMC += main.conf.getEMC(h);
								//main.getServer().broadcastMessage(bString()+" condensed "+main.conf.getName(h, false)+" for "+main.conf.getEMC(h)+" EMC!");
								if (EMC < targetEMC) break;
								EMC -= targetEMC;
								getChests();
								out.getBlockInventory().addItem(target.clone());
								//main.getServer().broadcastMessage(bString()+" created "+main.conf.getName(target, false)+"!");
								this.cleanInventory(out.getInventory());
								this.cleanInventory(in.getInventory());
								break;
							}
						}
					}
					int count = 0;
					while (EMC >= targetEMC) {
						count++;
						EMC -= targetEMC;
						getChests();
						out.getBlockInventory().addItem(target.clone());
						//main.getServer().broadcastMessage(bString()+" created "+main.conf.getName(target, false)+"!");
						if (count == main.maxStackCondense) break;
					}
					this.cleanInventory(out.getInventory());
					this.cleanInventory(in.getInventory());
				}
			}
		}
	}

	public void cleanInventory(Inventory i) {
		for (ItemStack is : i) {
			if (is == null) {
				i.remove(is);
				continue;
			}
			if (is.getAmount() <= 0)
				i.remove(is);
		}
	}

	public boolean hasEmptySpace(Inventory i) {
		return i.firstEmpty() >= 0;
	}

	public void setTarget(ItemStack i){
		i.setAmount(1);
		target = i;
		targetEMC = main.conf.getEMC(i);
	}
	
	public void reset(String p){
		EMC = 0;
		target = new ItemStack(Material.AIR, -1, (short) 0);
		targetEMC = 0;
		pause = false;
		getChests();
		for(ItemStack i : in.getBlockInventory()){
			if(i!=null){
				in.getBlockInventory().removeItem(i);
			}
		}
		getChests();
		for(ItemStack i : out.getBlockInventory()){
			if(i!=null){
				out.getBlockInventory().removeItem(i);
			}
		}
		owner = p;
	}

	public void getChests()	{
		Block b = blocks[3];
		BlockState bs = b.getState();
		if ((bs instanceof Chest)) {
			in = (Chest) bs;
		}
		b = blocks[5];
		bs = b.getState();
		if ((bs instanceof Chest))
			out = (Chest) bs;
	}

	public void makesign() {
		blocks[6] = main.util.lFO(blocks[3], ort, 1);
		blocks[6] = main.util.lFO(blocks[6], main.util.rotOrt(ort), 1);
		blocks[6].setType(Material.WALL_SIGN);
		blocks[6].setData((byte) main.util.ortToByte(ort));
		BlockState bs = blocks[6].getState();
		s = (Sign) bs;
	}

	public void removesign(){
		blocks[6].setType(Material.AIR);
	}
	
	public void getLocs() {
		locs.clear();
		locs.put("base", blocks[0].getLocation());
		locs.put("obs", blocks[1].getLocation());
		locs.put("db", blocks[2].getLocation());
		locs.put("in", blocks[3].getLocation());
		locs.put("et", blocks[4].getLocation());
		locs.put("out", blocks[5].getLocation());
		locs.put("sign", blocks[6].getLocation());
	}

	public void updateSign() {
		String type = main.conf.getName(target, true);
		s.setLine(0, "["+owner+"]");
		if (target.getDurability() > 0)
			s.setLine(1, type + ":" + target.getDurability());
		else {
			s.setLine(1, type);
		}
		String e, et;
		e = EMC + "";
		if(e.endsWith(".0"))e = e.substring(0, e.lastIndexOf("."));
		et = main.conf.getEMC(target) + "";
		if(et.endsWith(".0"))et = et.substring(0, et.lastIndexOf("."));
		
		s.setLine(2, e + " / " + et);
		if (pause)
			s.setLine(3, "PAUSED");
		else {
			s.setLine(3, "");
		}
		s.update();
		blocks[6].setData((byte) main.util.ortToByte(ort));
	}

	public String toString() {
		Location base = (Location)locs.get("base");
		String s = base.getWorld().getName() + ":" + base.getBlockX() + ":" + 
				base.getBlockY() + ":" + base.getBlockZ() + ":" + ort;
		return s;
	}

	public String saveString() {
		Location base = (Location)locs.get("base");
		String s = base.getWorld().getName() + ":" + base.getBlockX() + ":" + 
				base.getBlockY() + ":" + base.getBlockZ() + ":" + ort + ":" + 
				EMC + ":" + target.getTypeId() + ":" + target.getDurability() + 
				":" + pause + ":" + owner;
		return s;
	}

	public String bString() {
		Location base = (Location)locs.get("base");
		String s = "(" + base.getWorld().getName() + ": " + base.getBlockX() + 
				", " + base.getBlockY() + ", " + base.getBlockZ() + ")";
		return s;
	}
}