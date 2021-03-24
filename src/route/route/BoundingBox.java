package route.route;
// It's a class because Java has no structs
// Purpose: group simple named values together (it doubles as a 'BoundingBox' and a 'BoundingBoxRange')

class BoundingBox {
	public short x_min;
	public short x_max;
	public short y_min;
	public short y_max;
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

	public void expand(int uniformRange) {
		short v = (short) uniformRange;
		this.expand(new BoundingBox(v));
	}
	public BoundingBox expand(BoundingBox bbRange) {
		// bbRange : 	each integer represents how much the bounding box expands at that side.
		// 				Negative integers represent shrinking.
		this.x_min -= bbRange.x_min;
		this.x_max += bbRange.x_max;
		this.y_min -= bbRange.y_min;
		this.y_max += bbRange.y_max;
		return this;
	}
	public BoundingBox add(BoundingBox bbRange) {
		BoundingBox bb = new BoundingBox(this);
		return bb.expand(bbRange);
	}
}