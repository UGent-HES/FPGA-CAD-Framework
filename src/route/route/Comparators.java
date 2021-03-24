package route.route;

import java.util.Comparator;

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