/*
 * Reads IOParsing text file. For incorrect input tokens an error should be displayed.
 * See end of code for a template of IOParsing text file.
 * @author Simon Karlsson
 * @email simkarls@student.chalmers.se
 */


/*
 * Temporarily thoughts: 
 * 		Adding monitors with same index yield more monitors? perhaps same monitor is added to the array list
 */
import java.io.*;
import java.util.*;
import circuitNetwork.*;
import circuitNetwork.TwoPortElement;
import readData.ReadFromTextFile2DArray;
import java.util.Random;


public class IOParser{
	// these variable needs to be seen by the main
	int numberOfMemristors = 0;
	int numberOfVoltageMonitors = 0;
	public Network DefineNetwork(String textFileName, Network network) {
		
		// declaration of variables
		int numberOfNodes = 0;
		BufferedReader br = null;
		Random r = new Random();
		int nPosSource; int nNegSource; int numberOfAmpIndices; double[] ampls;	int DC_multiplier = 4;			
		int nPos; int nNeg;
		double alpha_mu = 1.0; double alpha_sigma = 0.4; double beta_mu = 10; double beta_sigma = 4.0;
		double alpha; double beta; double vthres = 0.4;
		double initR; double minR; double maxR;
		double dt; String fileName; 
		String dataSetDirectory = "/users/simon/eclipse-workspace/simulator/src/data/datasets/";
		String monitorArgument; String addMonitorArgument;
		ReadFromTextFile2DArray textToArrayReader = new ReadFromTextFile2DArray();
		
		try {
			br = new BufferedReader(new FileReader(textFileName));
			boolean END_NETWORK = false;
			String line = br.readLine();
			
			if(line.equals("BEGIN_NETWORK")) {
				System.out.println(" ** Starting to read parser text file ** ");
				while(END_NETWORK != true) {
					line = br.readLine();
					
					if(line == null) {
						System.out.println("Null line detected: missing or incorrect END_NETWORK line");
						break;
					}
					
					else if(line.equals("END_NETWORK")){
						System.out.println(" ** Finished reading parser text file ** ");
						END_NETWORK = true;
					}
					
					
					// reads command token, as long as the line is not empty
					if(!line.isEmpty()) { 
						StringTokenizer st = new StringTokenizer(line);
						String command = st.nextToken();
						
						// sets number of nodes
						if(command.equals("#NODES")) {
							numberOfNodes = Integer.parseInt(st.nextToken());
							if(numberOfNodes > 0) {
								network = new Network(numberOfNodes);
								System.out.println("Number of nodes is set to " + numberOfNodes);
							}
							else {
								System.out.println("Number of nodes must be greater than zero");
							}
						}

						// Sets frequency signal with amplitude values
						else if(command.equals("FREQUENCY_INPUT")){
							nPosSource = Integer.parseInt(st.nextToken(" ,"));
							nNegSource = Integer.parseInt(st.nextToken(",|"));
							numberOfAmpIndices =  Integer.parseInt(st.nextToken("|,"));
							ampls = new double[numberOfAmpIndices];
							int i = 0;
							while(st.hasMoreTokens()) {
								ampls[i] = Double.parseDouble(st.nextToken());
								i = i + 1;
							}
							ampls[0] = ampls[0] * DC_multiplier;
							VoltageSource voltageSource = new VoltageSource(ampls, nPosSource, nNegSource);
							network.addsource(voltageSource);
							System.out.print("Voltage source set between node " + nPosSource + " and " + nNegSource);	
							System.out.print(". DC set to: " + ampls[0] / DC_multiplier);
							System.out.print(", frequency amplitudes: ");
							int j = 1;
							while( j < (numberOfAmpIndices - 1) ) {
									System.out.print(ampls[j] + ", ");
									j = j + 1;
								}
							System.out.println("and frequency: " + ampls[numberOfAmpIndices - 1]);
						}
						
						// sets voltage source from data file
						else if(command.equals("DATA_INPUT")){
							dt = 0.01;
							nPosSource = Integer.parseInt(st.nextToken(" ,"));
							nNegSource = Integer.parseInt(st.nextToken(",|"));
							fileName = st.nextToken();
							fileName = dataSetDirectory + fileName;
							double[][] dataset = textToArrayReader.readInTheArray(fileName);
							ampls = new double[0];
							VoltageSourceDictated vg = new VoltageSourceDictated(ampls, nPosSource, nNegSource);
							vg.dictate(dataset, 1);
							vg.defineDTtoSuggest(dt);
							network.addsource(vg);
							System.out.println("Input voltage set between node " + nPosSource + " and " + nNegSource + ". Dataset from file " + fileName + " has been extracted and added as voltage source");
						}


								
						// adds memristors to the network
						else if(command.equals("ADD_MEM")) {
							nPos = Integer.parseInt(st.nextToken(" ,"));
							nNeg = Integer.parseInt(st.nextToken(",|"));
							initR = Double.parseDouble(st.nextToken("|,"));
							minR = Double.parseDouble(st.nextToken());
							maxR = Double.parseDouble(st.nextToken());
							alpha = r.nextGaussian()*alpha_sigma + alpha_mu;
							beta = r.nextGaussian()*beta_sigma + beta_mu;
							Memristor memristor = new Memristor(initR, maxR, minR, 1, 10, vthres, nPos, nNeg);
							memristor.beUsedForState();
							network.addbranch(memristor);
							System.out.print("Memristor added between node " + nPos + " and " + nNeg + " with initR = " + initR + 
									", minR = " + minR + " and maxR = " + maxR + ". Also v_tresh = " + vthres);

							System.out.println(". Alpha is set to: " + alpha + ", and beta is set to: " + beta);
							numberOfMemristors = numberOfMemristors + 1;
						}
						
						// adds voltage monitors
						else if(command.equals("ADD_MONITOR")) {
							monitorArgument = st.nextToken(" ,");
							addMonitorArgument = st.nextToken();
							if(monitorArgument.equals("voltage") & addMonitorArgument.equals("all")){
								for(int i=1;i<=numberOfNodes;i++) {
									network.addmonitor(i);
									numberOfVoltageMonitors = numberOfVoltageMonitors + 1;
								}
								System.out.println("Voltage monitor added at every node ");
							}
							else if(monitorArgument.equals("voltage") & (!addMonitorArgument.equals("all"))) {
								int i = Integer.parseInt(addMonitorArgument);
								if(i <= numberOfNodes & i >= 0) {
									network.addmonitor(i);
									numberOfVoltageMonitors = numberOfVoltageMonitors + 1;
									System.out.println("Voltage monitor added at node " + i);
									if(i == 0)
										System.out.println("Caution: voltage monitor at ground added");
								}
								else{
									System.out.println("Error in adding voltage monitor");
								}
							}
							System.out.println("Number of voltage monitors: " + numberOfVoltageMonitors);

						// for clarity surpose, memristor commands should be encapsulated by a BEGIN_MEM and END_MEM commands. 
						}
						else if(command.equals("BEGIN_MEM")) {
							System.out.println(" ** Starting to add memristors. Alpha and beta is drawned from a normal distribution where alpha has mu = " + alpha_mu + " and sigma = " + alpha_sigma + ". Beta is drawned with mu = " + beta_mu + " and sigma = " + beta_sigma + ". Mu stands for expected value, and sigma for standard deviation");
						}
						else if(command.equals("END_MEM"))
							System.out.println(" ** Finished adding memristors. A monitor has been added to each memristor.");
						
						else if(command.equals("SET_VTHRES")) {
							vthres = Double.parseDouble(st.nextToken());
							System.out.println("voltage threshold set to " + vthres);
						}
						
						
						else if(!command.equals("END_NETWORK")){
							System.out.println("Unknown command: " + command);
						}
								
						
					} else {
						System.out.println("Empty line detected");
					}
					
					

				}
			}
			
			else {
				System.out.println("Incorrect first line, should be BEGIN_NETWORK");
			}
		}
		catch(NoSuchElementException stringTokenizer) {
			System.out.println("Error with delimiter/tokenizer");
			System.out.println("Error message: " + stringTokenizer.getLocalizedMessage() );
		}
		
		catch(NumberFormatException stringToIntegerConvertion) {
			System.out.println("Error in converting from string to integer/double, parameter must be an integer/double ");
			System.out.println("Error message: " + stringToIntegerConvertion.getLocalizedMessage() );
		}
		
		catch(FileNotFoundException fileReader){
			System.out.println("Parser text file not found");
		}
		catch(IOException readLine){
			System.out.println("Error reading from file");
		}
		
		if (br != null) try { br.close(); } catch (IOException e) {}
		
		return network;
	}
	
}

// ** Below is a template for text file for IOParsing (this line is not included in the template).**
//BEGIN_NETWORK
//#NODES 4
//DRIVE_V 1,2|6|0, 0.4, -0.3, 0.7, -0.4, 6.28
//MEM 1, 2|2.0, 3.0, 4.5
//END_NETWORK

// ** Below is a generic version trying to explain the different parameters. #values in DRIVE_V row is needed to define array size**
//BEGIN_NETWORK
//#NODES #nodes
//DRIVE_V node1, node2|#values|DC_value, ampl_1, ampl_2, ampl_3, ampl_4, frequency
//MEM node1,node2|initR, minR, maxR
//END_NETWORK

