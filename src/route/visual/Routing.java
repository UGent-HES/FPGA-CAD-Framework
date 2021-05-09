package route.visual;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

import route.circuit.Circuit;
import route.circuit.block.GlobalBlock;
import route.circuit.resource.RouteNode;

class Routing {
	
	private String name; //really needed?
	private int iteration;
	private Circuit circuit;
	private int numBlocks;
	
	Routing(int iteration, Circuit circuit) {
		this.initialiseData(iteration, circuit);
		
		//do some stuff
	}
	
	Routing(int iteration, List<RouteNode> routeNodeList) {
		this.initialiseData(iteration, circuit);
	}
	
	//some other constructors
	
	private void initialiseData(int iteration, Circuit circuit) {
		this.name = circuit.getName();
		this.iteration = iteration;
		this.circuit = circuit;
		//numblocks?
		//blocks?
	}
	
	public int getIteration() {
		return this.iteration;
	}
	
	//get numblocks?
	
	public int getWidth() {
		return this.circuit.getWidth();
	}
	
	public int getHeight() {
		return this.circuit.getHeight();
	}
}