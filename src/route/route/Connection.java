package route.route;

import java.util.ArrayList;
import java.util.List;

import route.circuit.pin.GlobalPin;
import route.circuit.resource.Opin;
import route.circuit.resource.RouteNode;
import route.circuit.timing.TimingEdge;
import route.circuit.timing.TimingNode;


public class Connection implements Comparable<Connection>  {
	public final int id;//Unique ID number
    
	public final GlobalPin source;
	public final GlobalPin sink;
	
	private final TimingNode sourceTimingNode;
	private final TimingNode sinkTimingNode;
	private final TimingEdge timingEdge;
	private float criticality;

    public Net net;
    
    // boundingBoxRange is used f
    public int boundingBoxRange;
    //boundingBox is used for sorting Connections
    public int boundingBox;
    public short x_min_b;
	public short x_max_b;
	public short y_min_b;
	public short y_max_b;
	
	public final String netName;
	
	public final RouteNode sourceRouteNode;
	public final RouteNode sinkRouteNode;
	
	public final List<RouteNode> routeNodes;
	
	public Connection(int id, GlobalPin source, GlobalPin sink) {
		this.id = id;

		//Source
		this.source = source;
		String sourceName = null;
		if(this.source.getPortType().isEquivalent()) {
			sourceName = this.source.getPortName();
		}else{
			sourceName = this.source.getPortName() + "[" + this.source.getIndex() + "]";
		}
		this.sourceRouteNode = this.source.getOwner().getSiteInstance().getSource(sourceName);
		if(!source.hasTimingNode()) System.err.println(source + " => " + sink + " | Source " + source + " has no timing node");
		this.sourceTimingNode = this.source.getTimingNode();
		
		//Sink
		this.sink = sink;
		String sinkName = null;
		if(this.sink.getPortType().isEquivalent()) {
			sinkName = this.sink.getPortName();
		}else{
			sinkName = this.sink.getPortName() + "[" + this.sink.getIndex() + "]";
		}
		this.sinkRouteNode = this.sink.getOwner().getSiteInstance().getSink(sinkName);
		if(!sink.hasTimingNode()) System.out.println(source + " => " + sink + " | Sink " + sink + " has no timing node");
		this.sinkTimingNode = this.sink.getTimingNode();
		
		//Timing edge of the connection
		if(this.sinkTimingNode.getSourceEdges().size() != 1) {
			System.err.println("The connection should have only one edge => " + this.sinkTimingNode.getSourceEdges().size());
		}
		if(this.sourceTimingNode != this.sinkTimingNode.getSourceEdge(0).getSource()) {
			System.err.println("The source and sink are not connection by the same edge");
		}
		this.timingEdge = this.sinkTimingNode.getSourceEdge(0);
		
		//Bounding box		
		this.boundingBox = this.calculateBoundingBox(0);
		
		//Route nodes
		this.routeNodes = new ArrayList<>();
		
		//Net name
		this.netName = this.source.getNetName();
		
		this.net = null;
	}
	
	private int calculateBoundingBox(int range) {
		int min_x, max_x, min_y, max_y;
		
		int sourceX = this.source.getOwner().getColumn();
		int sinkX = this.sink.getOwner().getColumn();
		if(sourceX < sinkX) {
			min_x = sourceX;
			max_x = sinkX;
		} else {
			min_x = sinkX;
			max_x = sourceX;
		}
		
		int sourceY = this.source.getOwner().getRow();
		int sinkY = this.sink.getOwner().getRow();
		if(sourceY < sinkY) {
			min_y = sourceY;
			max_y = sinkY;
		} else {
			min_y = sinkY;
			max_y = sourceY;
		}
		
		this.boundingBoxRange = range;
		this.x_max_b = (short) (max_x + this.boundingBoxRange);
		this.x_min_b = (short) (min_x - this.boundingBoxRange);
		this.y_max_b = (short) (max_y + this.boundingBoxRange);
		this.y_min_b = (short) (min_y - this.boundingBoxRange);

		return (max_x - min_x + 1) + (max_y - min_y + 1);
	}
	
	public void setNet(Net net) {
		this.net = net;
	}
		
	public void SetBoundingBoxRange(int range) {
		calculateBoundingBox(range);
	}

	public boolean isInNetBoundingBoxLimit(RouteNode node) {
		return this.net.isInBoundingBoxLimit(node);
	}
	
	public boolean isInConBoundingBoxLimit(RouteNode node) {
		return node.xlow < this.x_max_b && node.xhigh > this.x_min_b && node.ylow < this.y_max_b && node.yhigh > this.y_min_b;

	}
	
	public void addRouteNode(RouteNode routeNode) {
		this.routeNodes.add(routeNode);
	}
	public void resetConnection() {
		this.routeNodes.clear();
	}
	
	public void setWireDelay(float wireDelay) {
		this.timingEdge.setWireDelay(wireDelay);
	}
	public void calculateCriticality(float maxDelay, float maxCriticality, float criticalityExponent) {
		this.timingEdge.calculateCriticality(maxDelay, maxCriticality, criticalityExponent);
		
		this.criticality = this.timingEdge.getCriticality();
	}
	public void resetCriticality() {
		this.timingEdge.resetCriticality();
	}
	
	public float getCriticality() {
		return this.criticality;
	}
	
	@Override
	public String toString() {
		return this.id + "_" + this.netName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
	    if (!(o instanceof Connection)) return false;
	   
	    Connection co = (Connection) o;
		if(this.id == co.id){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	@Override
	public int compareTo(Connection other) {
		if(this.id > other.id) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public boolean congested() {
		//Connection is congested when at least 1 RouteNode is overused
		for(RouteNode rn : this.routeNodes){
			if(rn.overUsed()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean illegal() {
		for(RouteNode rn : this.routeNodes){
			if(rn.illegal()) {
				return true;
			}
		}
		return false;
	}
	
	public Opin getOpin() {
		if(this.routeNodes.isEmpty()) {
			return null;
		} else {
			return (Opin) this.routeNodes.get(this.routeNodes.size() - 2);
		}
	}
	public int getManhattanDistance() {
		int horizontalDistance = Math.abs(this.source.getOwner().getColumn() - this.sink.getOwner().getColumn());
		int verticalDistance = Math.abs(this.source.getOwner().getRow() - this.sink.getOwner().getRow());
		int manhattanDistance = horizontalDistance + verticalDistance;
		
		return manhattanDistance;
	}
}
