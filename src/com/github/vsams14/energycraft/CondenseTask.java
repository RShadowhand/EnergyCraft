package com.github.vsams14.energycraft;

public class CondenseTask implements Runnable {
	@Override
	public void run() {
		for (String s : Main.instance.con.keySet()) {
			Condenser c =  Main.instance.con.get(s);
			c.condense(1);
			c.updateSign();
		}
	}
}