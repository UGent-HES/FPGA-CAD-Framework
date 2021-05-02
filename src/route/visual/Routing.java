package route.visual;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import route.circuit.Circuit;
import route.circuit.block.GlobalBlock;

class Routing {
	
	private String name;
	private Circuit circuit;
	private int numBlocks;
	
	Routing(String name, Circuit circuit) {
		this.initialiseData(name, circuit);
		
		//do some stuff
	}
	
	//some other constructors
	
	private void initialiseData(String name, Circuit circuit) {
		this.name = circuit.getName() + " | " + name;
		this.circuit = circuit;
		//numblocks?
		//blocks?
	}
	
	public String getName() {
		return this.name;
	}
	
	//get numblocks?
	
	public int getWidth() {
		return this.circuit.getWidth();
	}
	
	public int getHeight() {
		return this.circuit.getHeight();
	}
}