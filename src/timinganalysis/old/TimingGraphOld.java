package timinganalysis.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import circuit.BlockType;
import circuit.Flipflop;
import circuit.Net;
import circuit.Pin;
import circuit.PrePackedCircuit;
import circuit.Input;
import circuit.Lut;

public class TimingGraphOld 
{

	private static final double MHD_DELAY = 0.5;
	private static final double LUT_DELAY = 1.0;
	
	private PrePackedCircuit circuit;
	
	private List<Pin> startNodes;
	private List<Pin> endNodes;
	private Map<Pin,Vector<Pin>> edges;
	private Map<Pin,Vector<Double>> edgeWeights; //only valid boundingbox data for circuit inputs, lut outputs and ff outputs
	
	public TimingGraphOld(PrePackedCircuit circuit)
	{
		this.circuit = circuit;
		startNodes = new ArrayList<>();
		endNodes = new ArrayList<>();
		edges = new HashMap<>();
		edgeWeights = new HashMap<>();
	}
	
	public void buildTimingGraph()
	{
		Map<String,Net> nets = circuit.getNets();
		
		//Build all trees starting from circuit inputs
		for(Input input:circuit.getInputs().values())
		{
			startNodes.add(input.output);
			Net startNet = nets.get(input.name);
			edges.put(input.output, startNet.sinks);
			Vector<Double> delayVector = new Vector<>();
			for(Pin sinkPin:startNet.sinks)
			{
				int bb = Math.abs(input.getSite().x - sinkPin.owner.getSite().x) + Math.abs(input.getSite().y - sinkPin.owner.getSite().y);
				delayVector.add(bb * MHD_DELAY);
			}
			edgeWeights.put(input.output, delayVector);
			processStartPin(startNet);
		}
		
		//Build all trees starting from flipflop outputs
		for(Flipflop flipflop:circuit.getFlipflops().values())
		{
			startNodes.add(flipflop.getOutput());
			Net startNet = nets.get(flipflop.name);
			edges.put(flipflop.getOutput(), startNet.sinks);
			Vector<Double> delayVector = new Vector<>();
			for(Pin sinkPin:startNet.sinks)
			{
				int bb = Math.abs(flipflop.getSite().x - sinkPin.owner.getSite().x) + Math.abs(flipflop.getSite().y - sinkPin.owner.getSite().y);
				delayVector.add(bb * MHD_DELAY);
			}
			edgeWeights.put(flipflop.getOutput(), delayVector);
			processStartPin(startNet);
		}
	}
	
	public void updateDelays()
	{
		edgeWeights = new HashMap<>();
		for(Pin startPin:startNodes)
		{
			Pin currentPin = startPin;
			Vector<Pin> currentSinks = edges.get(startPin);
			if(currentSinks.size() == 0)
			{
				continue;
			}
			int currentIndex = 0;
			Stack<Pin> pinStack = new Stack<>();
			Stack<Integer> indexStack = new Stack<>();
			boolean keepGoing = true;
			while(keepGoing)
			{
				currentSinks = edges.get(currentPin);
				if(currentSinks != null && currentIndex < currentSinks.size()) //Move deeper
				{
					if(!edgeWeights.containsKey(currentPin))
					{
						Vector<Double> delayVector = new Vector<>();
						if(currentPin.owner == currentSinks.get(0).owner)
						{
							delayVector.add(LUT_DELAY);
						}
						else
						{
							
							for(Pin sinkPin:currentSinks)
							{
								int bb = Math.abs(currentPin.owner.getSite().x - sinkPin.owner.getSite().x) + Math.abs(currentPin.owner.getSite().y - sinkPin.owner.getSite().y);
								delayVector.add(bb * MHD_DELAY);
							}
						}
						edgeWeights.put(currentPin, delayVector);
					}
					pinStack.push(currentPin);
					currentPin = currentSinks.get(currentIndex);
					indexStack.push(currentIndex + 1);
					currentIndex = 0;
				}
				else
				{
					if(pinStack.isEmpty()) //We are back at the top level
					{
						keepGoing = false;
					}
					else
					{
						if(!edgeWeights.containsKey(currentPin))
						{
							edgeWeights.put(currentPin, null);
						}
						currentPin = pinStack.pop();
						currentIndex = indexStack.pop();
					}
				}
			}
		}
	}
	
	public double calculateMaximalDelay()
	{
		double maxDelay = 0.0;
		for(Pin startPin:startNodes)
		{
			Pin currentPin = startPin;
			Vector<Pin> currentSinks = edges.get(startPin);
			if(currentSinks.size() == 0)
			{
				continue;
			}
			double currentDelay = 0.0;
			int currentIndex = 0;
			Stack<Pin> pinStack = new Stack<>();
			Stack<Double> delayStack = new Stack<>();
			Stack<Integer> indexStack = new Stack<>();
			boolean keepGoing = true;
			while(keepGoing)
			{
				currentSinks = edges.get(currentPin);
				if(currentSinks != null && currentIndex < currentSinks.size()) //Move deeper
				{
					double edgeDelay = edgeWeights.get(currentPin).get(currentIndex);
					currentDelay += edgeDelay;
					delayStack.push(edgeDelay);
					pinStack.push(currentPin);
					currentPin = currentSinks.get(currentIndex);
					indexStack.push(currentIndex + 1);
					currentIndex = 0;
				}
				else
				{
					if(pinStack.isEmpty()) //We are back at the top level
					{
						keepGoing = false;
					}
					else
					{
						if(currentDelay > maxDelay)
						{
							maxDelay = currentDelay;
						}
						currentPin = pinStack.pop();
						currentDelay -= delayStack.pop();
						currentIndex = indexStack.pop();
					}
				}
				
			}
		}
		return maxDelay;
	}

//	public void printGraph()
//	{
//		for(Pin startPin:startNodes)
//		{
//			System.out.print(startPin.name);
//			Vector<Pin> currentPins = edges.get(startPin);
//			if(currentPins.size() == 0)
//			{
//				System.out.println();
//				continue;
//			}
//			while(currentPins != null)
//			{
//				System.out.print(" --> " + currentPins.get(0).name);
//				currentPins = edges.get(currentPins.get(0));
//			}
//			System.out.println();
//		}
//		System.out.println();
//	}
	
	private void processStartPin(Net startNet)
	{
		Map<String,Net> nets = circuit.getNets();
		Stack<Net> netsStack = new Stack<>();
		Stack<Integer> sinkIndexStack = new Stack<>();
		Net currentNet = startNet;
		int currentIndex = 0;
		if(currentNet.sinks.size() == 0) //Can happen with clock nets which are declared as an input in the blif file
		{
			return;
		}
		boolean keepGoing = true;
		while(keepGoing)
		{
			Pin currentSink = currentNet.sinks.get(currentIndex);
			if(currentSink.owner.type == BlockType.FLIPFLOP || currentSink.owner.type == BlockType.OUTPUT)
			{
				if(!endNodes.contains(currentSink))
				{
					endNodes.add(currentSink);
				}
				edges.put(currentSink, null);
				edgeWeights.put(currentSink, null);
			}
			else //Must be a LUT ==> keep on going
			{
				Vector <Pin> pinVector= new Vector<Pin>();
				pinVector.add(((Lut)(currentSink.owner)).getOutputs()[0]);
				edges.put(currentSink, pinVector);
				Vector<Double> delayVector1 = new Vector<>();
				delayVector1.add(LUT_DELAY);
				edgeWeights.put(currentSink,delayVector1);
				netsStack.push(currentNet);
				sinkIndexStack.push(currentIndex);
				currentNet = nets.get(currentSink.owner.name);
				currentIndex = -1; //will immediately be increased (see below)
				edges.put(currentNet.source, currentNet.sinks);
				Vector<Double> delayVector2 = new Vector<>();
				for(Pin sinkPin:currentNet.sinks)
				{
					int bb = Math.abs(currentNet.source.owner.getSite().x - sinkPin.owner.getSite().x) + 
							Math.abs(currentNet.source.owner.getSite().y - sinkPin.owner.getSite().y);
					delayVector2.add(bb * MHD_DELAY);
				}
				edgeWeights.put(currentNet.source, delayVector2);
			}
			
			++currentIndex;
			if(!(currentIndex < currentNet.sinks.size()))
			{
				while(!(currentIndex < currentNet.sinks.size()) && keepGoing)
				{
					if(netsStack.isEmpty())
					{
						keepGoing = false;
					}
					else
					{
						currentNet = netsStack.pop();
						currentIndex = sinkIndexStack.pop();
						++currentIndex;
					}
				}
			}
		}
	}
	
}