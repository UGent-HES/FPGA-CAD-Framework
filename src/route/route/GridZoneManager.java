package route.route;

import route.circuit.Circuit;
import route.circuit.resource.RouteNode;

public class GridZoneManager implements ZoneManager {
	private int zonewidth, zoneheight;
	private int width, height; //amount of zones
	
	public int zonecongestion[][];
	private float normalizedzonecongestion[][];
	
	public GridZoneManager(int zonewidth, int zoneheight) {
		this.zoneheight = zoneheight;
		this.zonewidth = zonewidth;
		this.width = Circuit.maxWidth/zonewidth + 1;
		this.height = Circuit.maxHeight/zoneheight + 1;
		
		zonecongestion = new int[this.width][this.height];
		normalizedzonecongestion = new float[this.width][this.height];
	}
	
	public int[][] getZoneCoords(RouteNode node) {
		//returns coordinates of zones that node goes through
		int xlow = node.xlow/ this.zonewidth;
		int ylow = node.ylow/ this.zoneheight;
		int xhigh = node.xhigh/ this.zonewidth;
		int yhigh = node.yhigh/ this.zoneheight;
		
		int coords[][] = {{xlow,ylow},{xhigh,yhigh}};
		
		return coords;
	}
	
	public void AddCongestionData(Connection con) {
		//add overuse of all routenodes in connection to all the zones the node passes through
		for (RouteNode node : con.routeNodes) {
			if(node.overUsed()) {
				int[][] coords = getZoneCoords(node);
				for (int i=coords[0][0];i<=coords[1][0];i++) {
					for (int j=coords[0][1];j<=coords[1][1];j++) {
						zonecongestion[i][j]+=node.overUse();
						//zonecongestion[i][j]+=1;
					}
				}
			}
		}
	}
	
	public float getZoneCongestion(RouteNode node) {
		int coords[][] = getZoneCoords(node);
		float congestion = 0;
		//look at all zones node goes trough, take max
		for (int i=coords[0][0];i<=coords[1][0];i++) {
			for (int j=coords[0][1];j<=coords[1][1];j++) {
				congestion = Math.max(congestion, normalizedzonecongestion[i][j]);
			}
		}
		return congestion;
	}
	
	public void clear() {
		zonecongestion = null;
		zonecongestion = new int[this.width][this.height];
	}
	
	public void normalize() {
		int max = 0;
		for(int i=0;i<this.width;i++) {
			for(int j=0;j<this.height;j++) {
				max = Math.max(max, zonecongestion[i][j]);
			}
		}
		
		if(max == 0) {
			normalizedzonecongestion = new float[this.width][this.height];
			return;
		}
		
		for(int i=0;i<this.width;i++) {
			for(int j=0;j<this.height;j++) {
				normalizedzonecongestion[i][j] = zonecongestion[i][j]/(float)max;
			}
		}
	}
}
