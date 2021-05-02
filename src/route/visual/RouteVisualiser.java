package route.visual;

import route.circuit.Circuit;
import route.circuit.block.GlobalBlock;
import route.main.Logger;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RouteVisualiser {

    private Logger logger;

    private JFrame frame;
    private JLabel placementLabel;

    private boolean enabled = false;
    private Circuit circuit;

    public RouteVisualiser(Logger logger) {
        this.logger = logger;
    }

    public void setCircuit(Circuit circuit) {
        this.enabled = true;
        this.circuit = circuit;
    }

    public void createAndDrawGUI() {
        if(!this.enabled) {
        	logger.print("The visualiser is disabled");
            return;
        }
        logger.print("The visualiser is enabled");
        //do the drawing
    }
}