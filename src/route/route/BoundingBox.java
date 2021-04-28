package route.route;

import route.circuit.resource.RouteNode;

// It's a class because Java has no structs
// Purpose: group simple named values together (seperated into 'BoundingBox' and a 'BoundingBoxRange', for static analysis)

abstract class Box {
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
	public BoundingBoxRange expand(BoundingBoxRange bbRange) {
		this.x_min += bbRange.x_min;
		this.x_max += bbRange.x_max;
		this.y_min += bbRange.y_min;
		this.y_max += bbRange.y_max;
		return this;
	}
}