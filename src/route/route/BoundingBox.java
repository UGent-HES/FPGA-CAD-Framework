package route.route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;

import route.circuit.resource.RouteNode;

// It's a class because Java has no structs
// Purpose: group simple named values together (seperated into 'BoundingBox' and a 'BoundingBoxRange', for static analysis)

abstract class Box implements Comparable<Box> {
	public short x_min;
	public short x_max;
	public short y_min;
	public short y_max;
	
	// this should be subclass specific
	public Box expand(int uniformRange) {
		short v = (short) uniformRange;
		return this.expand(new BoundingBoxRange(v));
	}
	// bbRange : 	each integer represents how much the bounding box expands at that side.
	// 				Negative integers represent shrinking.
	public abstract Box expand(BoundingBoxRange bbRange);
	
	@Override
	public int compareTo(Box o) {
	    // arbitrary comparator, but consistent with a.compareTo(b) == - b.compareTo(a) and only equals when same object (same parameters)
        if (!(o.getClass() == this.getClass())) throw new ClassCastException();
        if (this.equals(o)) return 0; // For this case, equal boxes
	    if (x_min != o.x_min) {
	        return x_min - o.x_min;
	    }
	    if (x_max != o.x_max) {
            return x_max - o.x_max;
        }
	    if (y_min != o.y_min) {
            return y_min - o.y_min;
        }
	    if (y_max != o.y_max) {
            return y_max - o.y_max;
        }
	    else return this.hashCode() - o.hashCode(); // Should never be called...
	}
	
	public boolean equals(Box o) {
	    return x_min == o.x_min && x_max == o.x_max && y_min == o.y_min && y_max == o.y_max;
	}
}

class BoundingBox extends Box {
	public BoundingBox() {}
	public BoundingBox(short uniformRange) {
		this(uniformRange, uniformRange, uniformRange, uniformRange);
	}
	public BoundingBox(short x_min, short x_max, short y_min, short y_max) {
		this.x_min = x_min;
		this.x_max = x_max;
		this.y_min = y_min;
		this.y_max = y_max;
	}
	// copy constructor
	public BoundingBox(BoundingBox original) {
		this(original.x_min, original.x_max, original.y_min, original.y_max);
	}
	public BoundingBox expand(BoundingBoxRange bbRange) {
		this.x_min -= bbRange.x_min;
		this.x_max += bbRange.x_max;
		this.y_min -= bbRange.y_min;
		this.y_max += bbRange.y_max;
		return this;
	}
	public BoundingBox expand(RouteNode rn) {
    	// The router interprets RR nodes which cross the boundary as being
        // 'within' of the BB. Only those which are *strictly* out side the
        // box are excluded, hence we use the nodes xhigh/yhigh for xmin/xmax,
        // and xlow/ylow for xmax/ymax calculations
		x_min = (short) Math.min(x_min, rn.xhigh);
		y_min = (short) Math.min(y_min, rn.yhigh);
		x_max = (short) Math.max(x_max, rn.xlow);
		y_max = (short) Math.max(y_max, rn.ylow);
		return this;
	}
	public BoundingBox expand(CongestedZone zone) {
	    // Route around congested zones
	    x_min = (short) Math.min(x_min, zone.x_max);
        y_min = (short) Math.min(y_min, zone.y_max);
        x_max = (short) Math.max(x_max, zone.x_min);
        y_max = (short) Math.max(y_max, zone.y_min);
	    return this;
	}
	
	public BoundingBox add(BoundingBoxRange bbRange) {
		BoundingBox bb = new BoundingBox(this);
		return bb.expand(bbRange);
	}
	
	public BoundingBoxRange delta(BoundingBox inner) {
		// if inner is the smallest Box, then the resulting delta is positive
		BoundingBoxRange delta = new BoundingBoxRange();
		delta.x_min = (short) (inner.x_min - this.x_min);
		delta.x_max = (short) (this.x_max - inner.x_max);
		delta.y_min = (short) (inner.y_min - this.y_min);
		delta.y_max = (short) (this.y_max - inner.y_max);
		return delta;
	}
}

class BoundingBoxRange extends Box {
	public BoundingBoxRange() {}
	public BoundingBoxRange(short uniformRange) {
		this(uniformRange, uniformRange, uniformRange, uniformRange);
	}
	public BoundingBoxRange(short x_min, short x_max, short y_min, short y_max) {
		this.x_min = x_min;
		this.x_max = x_max;
		this.y_min = y_min;
		this.y_max = y_max;
	}
	// copy constructor
	public BoundingBoxRange(BoundingBoxRange original) {
		this(original.x_min, original.x_max, original.y_min, original.y_max);
	}
	public BoundingBoxRange expand(short uniformRange) {
	    return this.expand(uniformRange, uniformRange, uniformRange, uniformRange);
	}
	public BoundingBoxRange expand(BoundingBoxRange bbRange) {
//		this.x_min += bbRange.x_min;
//		this.x_max += bbRange.x_max;
//		this.y_min += bbRange.y_min;
//		this.y_max += bbRange.y_max;
		return this.expand(bbRange.x_min, bbRange.x_max, bbRange.y_min, bbRange.y_max);
	}
	public BoundingBoxRange expand(short x_min, short x_max,short y_min,short y_max) {
        this.x_min += x_min;
        this.x_max += x_max;
        this.y_min += y_min;
        this.y_max += y_max;
        return this;
    }
}

class CongestedZone extends BoundingBox {
	public CongestedZone(RouteNode center) {
		super(center.xlow, center.xhigh, center.ylow, center.yhigh);
	}

	// TODO: revise this function
	public static CongestedZone findCongestionZone(RouteNode congestionCenter, SortedSet<RouteNode> congestedRouteNodes) {
		// TODO: check if this is the best way to access all neighbours (because of the difference between parents and children)
		Queue<RouteNode> q = new LinkedList<>();
		final Collection<RouteNodeData> nodesTouched = new ArrayList<>();

		// initial node
		addNodeToQueue(congestionCenter, nodesTouched, q);
		CongestedZone zone = new CongestedZone(congestionCenter);

//		for (int i = 0; i < 5; i++) { // i is the MAX range for this zone
//			for nodes in {
//				congestedRouteNodes.remove(o);
//				//
//			}
//		}
		// BFS
		int i = 0;
		while (!q.isEmpty() && i<10) { // just try 10 nodes
			RouteNode node = q.remove();
			congestedRouteNodes.remove(node); // also remove from the set
			// process
			zone.expand(node);
			// continue search
			for (RouteNode child : node.children) {
				// if not yet used AND is congested
				addNodeToQueue(child, nodesTouched, q);
			}
			i++;
		}
		// Clean up
		resetPathCost(nodesTouched);
		if (i > 2) {
			return zone;
		} else {
			return null;
		}
	}

	private static void resetPathCost(Collection<RouteNodeData> nodesTouched) {
		for (RouteNodeData node : nodesTouched) {
			node.touched = false;
		}
		nodesTouched.clear();
	}
	private static void addNodeToQueue(RouteNode node, Collection<RouteNodeData> nodesTouched, Queue<RouteNode> q) {
		RouteNodeData data = node.routeNodeData;
		if (!data.touched && node.overUsed()) {
			nodesTouched.add(data);
			data.touched = true;
			q.add(node);
		}
	}
}