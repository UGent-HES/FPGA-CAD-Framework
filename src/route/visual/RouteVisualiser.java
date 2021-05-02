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

    private int currentRouting;
    private List<Routing> routings = new ArrayList<Routing>();
    
    public RouteVisualiser(Logger logger) {
        this.logger = logger;
    }

    public void setCircuit(Circuit circuit) {
        this.enabled = true;
        this.circuit = circuit;
    }
    
    public void addRouting(String name) {
    	if (this.enabled) {
    		this.routings.add(new Routing(name, this.circuit));
    	}
    }
    
    //public void with Map<GlobalBlock>

    public void createAndDrawGUI() {
        if(!this.enabled) {
            return;
        }
        
        this.addRouting("Final routing");
        
        this.frame = new JFrame("Routing visualiser");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.frame.setSize(500, 450);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.frame.setLocation(
        		(int) (screen.getWidth() - this.frame.getWidth()) / 2,
        		(int) (screen.getHeight() - this.frame.getHeight()) / 2);
        
        this.frame.setExtendedState(this.frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
        this.frame.setVisible(true);
        
        Container pane = this.frame.getContentPane();
        
        JPanel navigationPanel = new JPanel();
        pane.add(navigationPanel, BorderLayout.PAGE_START);
        
        JPanel titlePanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.add(titlePanel, BorderLayout.CENTER);
        navigationPanel.add(buttonPanel, BorderLayout.CENTER);
        
        this.placementLabel = new JLabel("");
        titlePanel.add(this.placementLabel, BorderLayout.CENTER);
        
        /*	fast backward button?
        for (Routing routing : this.routings) {
        	
        }
        */
        JButton previousGradientButton = new JButton("<");
        //previousGradientButton.addActionListener(new NavigateActionListener(this, -1));
        
        //do the drawing
        
        
        
        
    }
}





