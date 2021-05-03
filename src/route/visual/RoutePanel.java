package route.visual

import route.circuit.architecture.BlockCategory;
import route.circuit.block.GlobalBlock;
import route.main.Logger;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class RoutePanel extends JPanel {
	private static final long serialVersionUID = -2621200118414334047L;
	
	private Logger logger;
	
	private final Color gridColorLight = new Color(150, 150, 150);
    private final Color gridColorDark = new Color(0, 0, 0);
    private final Color clbColor = new Color(255, 0, 0, 50);
    private final Color macroColor = new Color(100, 0, 0, 50);
    private final Color ioColor = new Color(255, 255, 0, 50);
    private final Color dspColor = new Color(0, 255, 0, 50);
    private final Color m9kColor = new Color(0, 0, 255, 50);
    private final Color m144kColor = new Color(0, 255, 255, 50);
    private final Color hardBlockColor = new Color(0, 0, 0, 50);
    
    private transient Routing routing;
    
    private int blockSize;
    private int left, top, right, bottom;
    
    private boolean mouseEnabled = false, plotEnabled = false;
    //bbCost?
    
    RoutePanel(Logger logger) {
    	this.logger = logger;
    }
    
    void setRouting(Routing routing) {
    	this.routing = routing;
    	
    	super.repaint();
    }
    
    void setMouseEnabled(boolean mouseEnabled) {
    	this.mouseEnabled = mouseEnabled;
    }
    
    void setPlotEnabled(boolean plotEnabled) { //plotEnabled, double[] bbCost) {
    	this.plotEnabled = plotEnabled;
    	//this.bbCost = bbCost;
    }
    
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	if (this.routing != null) {
    		if (!this.plotEnabled) {
    			this.setDimensions();
    			this.drawGrid(g);
    			this.drawWires(g);
    			if (this.mouseEnabled)this.drawBlockInformation(g);
    		} else {
    			this.drawPlot(g);
    		}
    	}
    }
    
    private void drawPlot(Graphics g) {
    	int iteration = this.routing.getIteration();
    	
    	//set boundaries of plot
    	double alpha = 0.2;
    	double left = this.getWidth() * alpha;
    	double top = this.getHeight() * alpha;
    	double right = this.getWidth() * (1-alpha);
    	double bottom = this.getHeight() * (1-alpha);
    	
    	//double maxbbcost = 0.0; for (double bbCost:this.bbCost){maxbbcost = Math.max(bbCost, maxbbcost);}
    	
    	
    }
    
    private void setDimensions() {
    	int maxWidth = this.getWidth();
    	int maxHeight = this.getHeight();
    	
    	int circuitWidth = this.routing.getWidth() + 2;
    	int circuitHeight = this.routing.getHeight() +2;
    	
    	this.blockSize = Math.min((maxWidth - 1) / circuitWidth, (maxHeight - 1) / circuitHeight);
    
    	int width = circuitWidth * this.blockSize + 1;
    	int height = circuitHeight * this.blockSize + 1;
    	
    	this.left = (maxWidth - width) / 2;
    	this.top = (maxHeight - height) / 2;
    	
    	this.right = this.left + this.blockSize * circuitWidth;
    	this.bottom = this.top + this.blockSize * circuitHeight;
    }
    
    private void drawGrid(Graphics g) {
    	g.setColor(this.gridColorLight);
    	for (int x = this.left; x <= this.right; x += this.blockSize) {
    		if (x == this.left || x == this.right) {
    			g.drawLine(this.top + this.blockSize, x, this.bottom - this.blockSize, y);
    		}
    	}
    }
    
    private void drawWires(Graphics g) {
    	//for (Map.Entry<Wires, Coordinates> wireEntry : this.routing.wires()) {
    		//this.drawWire(wireEntry.getKey(), wireEntry.getValue(), g);
    	//}
    }
    
    
}