package com.secretalgorithm.energycraft;

public class CondenseTask implements Runnable {
	@Override
	public void run() {
		for (String s : Main.instance.con.keySet()) {
			Condenser c =  Main.instance.con.get(s);
			c.w.loadChunk(c.chunk);
			c.condense();
			c.updateSign();
		}
	}
}