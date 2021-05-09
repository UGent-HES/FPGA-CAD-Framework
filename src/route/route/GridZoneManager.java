package route.route;

import java.util.Iterator;

import route.circuit.Circuit;
import route.circuit.resource.RouteNode;

public class GridZoneManager implements ZoneManager, Iterable<CongestedZone> {
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
	
	private int[][] getZoneCoords(RouteNode node) {
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

    @Override
    public Iterator<CongestedZone> iterator() {
        return new Iterator<CongestedZone>() {
            int i = 0;
            int j = 0;
            @Override
            public boolean hasNext() {
                return i < width;
            }

            @Override
            public CongestedZone next() {
                System.err.println("Unfinished function, check the code again");
                //CongestedZone zone = new CongestedZone((short) (i*zonewidth), (short) ((i+1)*zonewidth), (short) ((j+1)*zoneheight), (short) ((j+1)*zoneheight));
                // TODO: skip uncongested zones (if normalizedzonecongestion is beneath threshold or something)
                j++;
                if (j == height) {
                    i++;
                    j = 0;
                }
                return null;
            }
        };
    }
}
