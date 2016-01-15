package circuit.block;

public class TimingEdge {

    private double fixedDelay, totalDelay;
    private double slack, criticality;
    private double stagedTotalDelay;


    TimingEdge(double fixedDelay) {
        this.fixedDelay = fixedDelay;
    }


    public double getFixedDelay() {
        return this.fixedDelay;
    }
    void setFixedDelay(double fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    public double getTotalDelay() {
        return this.totalDelay;
    }
    public void setWireDelay(double wireDelay) {
        this.totalDelay = this.fixedDelay + wireDelay;
    }


    void resetSlack() {
        this.slack = 0;
        this.criticality = 0;
    }
    void setSlack(double slack) {
        this.slack = slack;
        if(slack < -5e-8) {
            int d = 0;
        }
    }
    double getSlack() {
        return this.slack;
    }

    void calculateCriticality(double maxDelay, double criticalityExponent) {
        this.criticality = Math.pow(1 - (maxDelay + this.slack) / maxDelay, criticalityExponent);
    }
    public double getCriticality() {
        return this.criticality;
    }


    /*************************************************
     * Functions that facilitate simulated annealing *
     *************************************************/

    double getStagedTotalDelay() {
        return this.stagedTotalDelay;
    }
    void setStagedWireDelay(double stagedWireDelay) {
        this.stagedTotalDelay = this.fixedDelay + stagedWireDelay;
    }
    void resetStagedDelay() {
        this.stagedTotalDelay = this.totalDelay;
    }

    void pushThrough() {
        this.totalDelay = this.stagedTotalDelay;
    }



    @Override
    public String toString() {
        return String.format("%e", this.totalDelay);
    }
}
