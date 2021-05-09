package route.route;

import route.circuit.resource.RouteNode;

public interface ZoneManager {
    void clear();
    default void normalize() {};
    default float getZoneCongestion(RouteNode node) {
        return 0;
    };
}