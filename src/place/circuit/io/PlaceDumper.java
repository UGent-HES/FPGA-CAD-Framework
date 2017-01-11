package place.circuit.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import place.circuit.Circuit;
import place.circuit.block.AbstractSite;
import place.circuit.block.GlobalBlock;

public class PlaceDumper {

    private Circuit circuit;
    private File netFile, placeFile, architectureFileVPR;
    private String netPath, architecturePath;

    public PlaceDumper(Circuit circuit, File netFile, File placeFile, File architectureFileVPR) {
        this.circuit = circuit;
        this.netFile = netFile;
        this.placeFile = placeFile;
        this.architectureFileVPR = architectureFileVPR;


        //String userDir = System.getProperty("user.dir");
        //this.netPath = this.netFile.getAbsolutePath().substring(userDir.length() + 1);
        //this.architecturePath = this.architectureFileVPR.getAbsolutePath().substring(userDir.length() + 1);
        
        this.netPath = this.netFile.getAbsolutePath();
        this.architecturePath = this.architectureFileVPR.getAbsolutePath();
    }


    public void dump() throws IOException {
        this.placeFile.getAbsoluteFile().getParentFile().mkdirs();

        PrintWriter writer = null;
        writer = new PrintWriter(new BufferedWriter(new FileWriter(this.placeFile)));

        int length = 0;
        for(GlobalBlock block : this.circuit.getGlobalBlocks()) {
            if(block.getName().length() > length) {
                length = block.getName().length();
            }
        }
        length = Math.max(length, 11);

        int width = this.circuit.getWidth(), height = this.circuit.getHeight();

        this.dumpHeader(writer, width, height, length);

        int maxIndex = 0;
        for(GlobalBlock block : this.circuit.getGlobalBlocks()){
        	if(block.getIndex() > maxIndex){
        		maxIndex = block.getIndex();
        	}
        }
        GlobalBlock[] blocks = new GlobalBlock[maxIndex+1];
        for(GlobalBlock block : this.circuit.getGlobalBlocks()){
        	if(blocks[block.getIndex()] != null){
        		System.out.println("Duplicate index: " + block.getIndex());
        	}
        	blocks[block.getIndex()] = block;
        }
        for(int index=0;index<blocks.length;index++){
        	if(blocks[index] == null){
        		System.out.println("Unused index: " + index);
        	}
        }

        Map<AbstractSite, Integer> siteOccupations = new HashMap<AbstractSite, Integer>();
        for(GlobalBlock block : blocks) {
            AbstractSite site = block.getSite();
            int x = site.getColumn();
            int y = site.getRow();
            int index = block.getIndex();

            int z;
            if(siteOccupations.containsKey(site)) {
                z = siteOccupations.get(site);
            } else {
                z = 0;
            }
            siteOccupations.put(site, z + 1);

            //writer.printf("%-"+length+"s %-7d %-7d %-7d #%d\n",block.getName(), x, y, z, index);
            writer.printf("%-"+length+"s\t%d\t%d\t%d\t#%d\n",block.getName(), x, y, z, index);
        }
        writer.close();
    }

    private void dumpHeader(PrintWriter writer, int width, int height, int length) {
        // Print out the header
        writer.printf("Netlist file: %s   Architecture file: %s\n", this.netPath, this.architecturePath);
        writer.printf("Array size: %d x %d logic blocks\n\n", width, height);

        writer.printf("%-"+length+"s\tx\ty\tsubblk\tblock number\n", "#block name");
        writer.printf("%-"+length+"s\t--\t--\t------\t------------\n", "#----------");
    }
}