package route.route;

import java.util.Comparator;

import route.circuit.resource.RouteNode;

/*
 * obj1 and obj2 are the objects to be compared.
 * This method returns zero if the objects are equal.
 * It returns a positive value if obj1 is greater than obj2.
 * Otherwise, a negative value is returned.
 */

public class Comparators {
    public static Comparator<QueueElement> PRIORITY_COMPARATOR = new Comparator<QueueElement>() {
        @Override
        public int compare(QueueElement node1, QueueElement node2) {
            if(node1.cost < node2.cost) {
            	return -1;
            } else {
            	return 1;
            }
        }
    };
    public static Comparator<RouteNode> CONGESTION_COMPARATOR = new Comparator<RouteNode>() {
        @Override
        public int compare(RouteNode node1, RouteNode node2) {
            // The node with the largest overUse() is `the smallest
            if(node1.overUse() == node2.overUse()) {
                return node1.compareTo(node2);
            }
            else if(node1.overUse() > node2.overUse()) {
                return -1;
            } else {
                return 1;
            }
        }
    };
    public static Comparator<BoundingBox> X_AXIS_COMPARATOR = new Comparator<BoundingBox>() {
        @Override
        public int compare(BoundingBox b1, BoundingBox b2) {
            if (b1.x_max < b2.x_min) {
                return -1; // b1 completely left from b2
            } else if (b1.x_min > b2.x_max) {
                return  1; // b1 completely right from b2
            } else if (b1.x_min < b2.x_min && b1.x_max < b2.x_max) {
                return -1;// b1 overlap left
            } else if (b1.x_min > b2.x_min && b1.x_max > b2.x_max) {
                return  1; // b2 overlap right
            } else if (b1.x_min < b2.x_min && b1.x_max > b2.x_max) {
                return -1; // b1 completely over b2
            } else if (b1.x_min > b2.x_min && b1.x_max < b2.x_max) {
                return  1; // b1 completely inside b2
            }
            else // equal with respect to x-axis
                return b1.compareTo(b2);
        }
    };
    public static Comparator<BoundingBox> LEFT_COMPARATOR = new Comparator<BoundingBox>() {
        @Override
        public int compare(BoundingBox b1, BoundingBox b2) {
            if (b1.x_min == b2.x_min) {
                return b1.compareTo(b2);
            }
            else if (b1.x_min < b2.x_min) {
                return -1;
            }
            else {
                return 1;
            }
        }
    };
    public static Comparator<BoundingBox> Y_AXIS_COMPARATOR = new Comparator<BoundingBox>() {
        @Override
        public int compare(BoundingBox b1, BoundingBox b2) {
            if (b1.y_max < b2.y_min) {
                return -1; // b1 completely under b2
            }
            else if (b1.y_min > b2.y_max) {
                return 1; // b1 completely above b2
            }
            else
                return b1.compareTo(b2);
        }
    };
    
    public static Comparator<Connection>  FanoutConnection = new Comparator<Connection>() {
    	@Override
    	public int compare(Connection a, Connection b) {
    		if(a.net.fanout < b.net.fanout){
    			return 1;
    		}else if(a.net.fanout == b.net.fanout){
    			if(a.connectionBoxSize > b.connectionBoxSize){
    				return 1;
    			}else if(a.connectionBoxSize == b.connectionBoxSize){
    				if(a.hashCode() > b.hashCode()){
    					return 1;
    				}else if(a.hashCode() < b.hashCode()){
    					return -1;
    				}else{
					// if elements differ, but hashes are equal: Error
    					if(a != b) System.err.println("Failure: Error while comparing 2 connections. HashCode of Two Connections was identical");
    					return 0;
    				}
    			}else{
    				return -1;
    			}
    		}else{
    			return -1;
    		}
    	}
    };
    
	public static Comparator<Net> FanoutNet = new Comparator<Net>() {
    	@Override
    	public int compare(Net n1, Net n2) {
    		if(n1.fanout < n2.fanout){
    			return 1;
    		}else if(n1.fanout == n2.fanout){
    			if(n1.hpwl > n2.hpwl){
    				return 1;
    			}else if(n1.hpwl == n2.hpwl){
    				if(n1.hashCode() > n2.hashCode()){
    					return 1;
    				}else if(n1.hashCode() < n2.hashCode()){
    					return -1;
    				}else{
    					if(n1 != n2) System.err.println("Failure: Error while comparing 2 nets. HashCode of Two Nets was identical");
    					return 0;
    				}
    			}else{
    				return -1;
    			}
    		}else{
    			return -1;
    		}
    	}
    };
   
    public static Comparator<Connection> ConnectionCriticality = new Comparator<Connection>() {
    	@Override
    	public int compare(Connection a, Connection b) {
    		if(a.getCriticality() < b.getCriticality()){
    			return 1;
    		}else if(a.getCriticality() == b.getCriticality()) {
    			if(a.hashCode() > b.hashCode()){
    				return 1;
    			}else if(a.hashCode() < b.hashCode()){
    				return -1;
    			}else{
    				if(a != b) System.out.println("Failure: Error while comparing 2 connections. HashCode of Two Connections was identical");
    				return 0;
    			}
    		}else{
    			return -1;
    		}
    	}
    };
}