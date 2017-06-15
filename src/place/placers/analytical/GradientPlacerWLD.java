package place.placers.analytical;

import place.circuit.Circuit;
import place.interfaces.Logger;
import place.interfaces.Options;
import place.placers.analytical.GradientPlacerTD.CritConn;
import place.visual.PlacementVisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GradientPlacerWLD extends GradientPlacer {

    public GradientPlacerWLD(Circuit circuit, Options options, Random random, Logger logger, PlacementVisualizer visualizer) {
        super(circuit, options, random, logger, visualizer);
    }

    @Override
    protected boolean isTimingDriven() {
        return false;
    }

    @Override
    protected void initializeIteration(int iteration) {
        if(iteration > 0) {
            this.anchorWeight += this.anchorWeightStep;
            this.learningRate *= this.learningRateMultiplier;
            this.quality *= this.qualityMultiplier;
        }
    }
    
    public List<CritConn> getCriticalConnections(){
    	return new ArrayList<CritConn>();
    }

    @Override
    public String getName() {
        return "Wirelength driven gradient descent placer";
    }

	@Override
	protected void calculateTimingCost() {
		this.timingCost = 0;
	}
}
