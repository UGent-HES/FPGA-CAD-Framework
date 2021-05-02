package route.route;

import java.util.ArrayList;
import java.util.List;

import route.circuit.Circuit;
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
    
	// connectionBoxSize is used for sorting Connections, not adjusted at runtime
	public int connectionBoxSize;
	public BoundingBox cb; // connectionBox - contains 4 sides separately.
	//public BoundingBox bb; - not used to prevent consistency problems, we compute it on the fly // boundingBox
	// boundingBoxRange is used for routing, adjusted at runtime
	public BoundingBoxRange bbRange; // ranges for 4 sides, will be adjusted & can be negative
	
	public final String netName;
	
	public final RouteNode sourceRouteNode;
	public final RouteNode sinkRouteNode;
	
	public final List<RouteNode> routeNodes;
	
	public Connection(int id, GlobalPin source, GlobalPin sink) {
		this.id = id;

		// Source
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
		
		// Sink
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
		
		// Timing edge of the connection
		if(this.sinkTimingNode.getSourceEdges().size() != 1) {
			System.err.println("The connection should have only one edge => " + this.sinkTimingNode.getSourceEdges().size());
		}
		if(this.sourceTimingNode != this.sinkTimingNode.getSourceEdge(0).getSource()) {
			System.err.println("The source and sink are not connection by the same edge");
		}
		this.timingEdge = this.sinkTimingNode.getSourceEdge(0);
		
		// Bounding box
		short sourceXlow = this.sourceRouteNode.xlow;
		short sourceXhigh = this.sourceRouteNode.xhigh;
		short sinkXlow = this.sinkRouteNode.xlow;
		short sinkXhigh = this.sinkRouteNode.xhigh;
		this.cb = new BoundingBox();
		
		if(sourceXlow < sinkXlow) {
			cb.x_min = sourceXlow;
		} else {
			cb.x_min = sinkXlow;
		}
		
		if(sourceXhigh > sinkXhigh) {
			cb.x_max = sourceXhigh;
		} else {
			cb.x_max = sinkXhigh;
		}
		
		short sourceYlow = this.sourceRouteNode.ylow;
		short sourceYhigh = this.sourceRouteNode.yhigh;
		short sinkYlow = this.sinkRouteNode.ylow;
		short sinkYhigh = this.sinkRouteNode.yhigh;
		
		if(sourceYlow < sinkYlow) {
			cb.y_min = sourceYlow;
		} else {
			cb.y_min = sinkYlow;
		}
		
		if(sourceYhigh > sinkYhigh) {
			cb.y_max = sourceYhigh;
		} else {
			cb.y_max = sinkYhigh;
		}
		
		this.connectionBoxSize = (cb.x_max - cb.x_min + 1) + (cb.y_max - cb.y_min + 1);
		// empty initialization, is overwritten at net init
		// If you want to initialize the bounding box range to 0:
		//this.bbRange = new BoundingBoxRange((short) 0);
		
		// Route nodes
		this.routeNodes = new ArrayList<>();
		
		// Net name
		this.netName = this.source.getNetName();
		
		this.net = null;
	}
	
	public void expandBoundingBoxRange(int uniformRange) {
		this.bbRange.expand(uniformRange);
	}

	// Returns the bounding box of a connection's used routing resources
	public BoundingBox calculateUsedBoundingBox() {
	    BoundingBox bb = new BoundingBox(Short.MAX_VALUE, (short) 0, Short.MAX_VALUE, (short) 0);
	    
	    for(RouteNode rn : this.routeNodes){
	    	bb.expand(rn); // expand to encompass rn
		}
	    return bb;
	}
	
	public boolean dynamicUpdateBoundingBox(short dynamicBBDeltaThreshold) {
		boolean updatedBB = false;
		BoundingBox bb = getBB();
		BoundingBox usedBB = this.calculateUsedBoundingBox();
		BoundingBoxRange delta = bb.delta(usedBB);
		
		if (delta.x_min <= dynamicBBDeltaThreshold && bb.x_min > 0) {
			this.bbRange.x_min++; // we increase the range, thus decreasing the BB
            updatedBB = true;
        }
		if (delta.y_min <= dynamicBBDeltaThreshold && bb.y_min > 0) {
			this.bbRange.y_min++;
            updatedBB = true;
        }
        if (delta.x_max <= dynamicBBDeltaThreshold && bb.x_max < Circuit.maxWidth - 1) {
            this.bbRange.x_max++;
            updatedBB = true;
        }
        if (delta.y_max <= dynamicBBDeltaThreshold && bb.y_max < Circuit.maxHeight - 1) {
            this.bbRange.y_max++;
            updatedBB = true;
        }
		
		return updatedBB;
	}
	
	public void setNet(Net net) {
		this.net = net;
	}
		
	public void setBoundingBoxRange(BoundingBoxRange bbRange) {
		this.bbRange = bbRange;
	}

	public boolean isInNetBoundingBoxLimit(RouteNode node) {
		return this.net.isInBoundingBoxLimit(node);
	}
	
	public boolean isInConBoundingBoxLimit(RouteNode node) {
		BoundingBox bb = getBB();
		return node.xlow < bb.x_max && node.xhigh > bb.x_min && node.ylow < bb.y_max && node.yhigh > bb.y_min;
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
	
	public BoundingBox getBB() {
		return cb.add(bbRange);
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