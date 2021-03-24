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
		this.x_min = uniformRange;
		this.x_max = uniformRange;
		this.y_min = uniformRange;
		this.y_max = uniformRange;
	}
	public BoundingBox(short x_min, short x_max, short y_min, short y_max) {
		this.x_min = x_min;
		this.x_max = x_max;
		this.y_min = y_min;
		this.y_max = y_max;
	}
	public void expand(int uniformRange) {
		short v = (short) uniformRange;
		this.x_min += v;
		this.x_max += v;
		this.y_min += v;
		this.y_max += v;
	}
}