import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class NeuralDiseaseSIR {

    private int population;
    //private int residentialDensity = 50000; //per km^2
    //private int cityXSize = 10; //In km
    //private int cityYSize = 10;
	private int interactionRadius;
    private int recoveredPopulation;
    private int infectedPopulation = 10;

    private double recoveredPercentage = 0.0;

    private int lastPrintedTimestep = 0;

    private double infectionRate;
    private double recoveryRate;


    private double chanceOfHealthyLeavingHome;
    private double chanceOfSickLeavingHome;
    private double timeStepLength;
    private double timeStep = 0;



    private int[]  locationalInfections;
    private int[][] people;//This is gonna be yuge :O
    private double[] timeStepsAtLocation;//This is gonna be yuge too.

    private ArrayList<Point> houseIndex;
	private Vector<int[]> shopLocations;
    private ArrayList<Point> shopIndex;

    private Random neuralRNG = new Random(System.nanoTime());
    private boolean isQuiet;



    public int maxInfected = 0;
    public int totalInfected = 0;
    public int[] pointLocation;

	

    //private int unusedCityArea = 2; //The rest will be commercial and industrial. measured in sq km



    private PrintWriter fileWriter;
    protected NeuralDiseaseSIR(double timeStepLength, int population, int residentialDensity, int cityXSize,
                               int cityYSize, int unusedCityArea, int interactionRadius, double chanceOfHealthyLeavingHome,
                               double chanceOfSickLeavingHome, double infectionRate, double recoveryRate, boolean isQuiet, int[] pointLocation) throws FileNotFoundException, UnsupportedEncodingException {


        //Set all the values given


        this.isQuiet = isQuiet;
        if (isQuiet){
            lastPrintedTimestep = 50000;
        }


        this.timeStepLength = timeStepLength;
        this.interactionRadius = interactionRadius;
        this.population = population;
        this.chanceOfHealthyLeavingHome = chanceOfHealthyLeavingHome;
        this.chanceOfSickLeavingHome = chanceOfSickLeavingHome;
        this.infectionRate = infectionRate;
        this.recoveryRate = recoveryRate;
        this.pointLocation = pointLocation;






        int[][] cityRCI = new int[cityXSize][cityYSize]; //Positive = residential, Neg = unused, 0 = industrial/commercial


		
		shopIndex = new ArrayList<>();
		
		
        int cityArea = cityXSize * cityYSize - unusedCityArea;

        Random locationGenerator = new Random(System.nanoTime());

        if (!isQuiet){
            System.out.println("Init 1 completed");
        }

        while (unusedCityArea >= 0){
            int x = Math.abs(locationGenerator.nextInt() % cityXSize);
            int y = Math.abs(locationGenerator.nextInt() % cityYSize);


            if (cityRCI[x][y] == 0){
                cityRCI[x][y] = -1;
                unusedCityArea--;
            }
        }

        int populationTiles = population / residentialDensity;
        int populationTilesToGenerate = populationTiles;

        if (populationTilesToGenerate >= cityArea){
            populationTilesToGenerate = cityArea - 1;
        }

        if (!isQuiet){
            System.out.println("Init 2 completed");
        }

		houseIndex = new ArrayList<>();
		shopLocations = new Vector<>(populationTilesToGenerate);
		
        while (populationTilesToGenerate >= 0){
            int x = Math.abs(locationGenerator.nextInt() % cityXSize);
            int y = Math.abs(locationGenerator.nextInt() % cityYSize);
            if (cityRCI[x][y] == 0){
                cityRCI[x][y] = 1;
				houseIndex.add(new Point(x, y));
                populationTilesToGenerate--;
            }
        }

        if (!isQuiet){
            System.out.println("Init 3 completed");
        }
		for (int x = 0; x < cityXSize; x++){
			for (int y = 0; y < cityYSize; y++){
				if (cityRCI[x][y] == 0){
					shopIndex.add(new Point(x,y));
				}
			}
		}

        if (!isQuiet){
            System.out.println("Init 4 completed");
        }
		for (int x = 0; x < shopLocations.capacity(); x++){
			shopLocations.add(determineViableShops( (int) houseIndex.get(x).getX(), (int) houseIndex.get(x).getY()));
		}



        this.population = populationTiles * residentialDensity;
        population = this.population;

        System.out.println("Actual city size: " + population);


        if (!isQuiet){
            fileWriter = new PrintWriter("infectionCSV/NeuralP" + population + "D" + residentialDensity +
                    "I" + new DecimalFormat("#.##").format(infectionRate) + "R" + new DecimalFormat("#.##").format(recoveryRate) + ".csv", "UTF-8");
            fileWriter.write("Population, density, infection rate, recovery rate," +
                    "chance for sick to leave, chance for healthy to leave, interaction radius\n");
            fileWriter.write((populationTiles * residentialDensity) + ", " + residentialDensity + ", " + infectionRate + ", " + recoveryRate + ", "
                    + ", " + chanceOfSickLeavingHome + ", " + chanceOfHealthyLeavingHome + ", " + interactionRadius + "\n");
            fileWriter.write("Time, Susceptible, Infected, Recovered\n");
        }


        people = new int[population][4]; //0 is current location, 1 is home location,
        // 2 is tendency to leave house, 3 is SIR status

        timeStepsAtLocation = new double[population];
        for (int t = 0; t < population; t++){
            people[t][2] = (int) (1000000000 * (0.75 * neuralRNG.nextDouble() + 0.25));
        }
        for (int i = 0; i < populationTiles; i++){
            for (int l = 0; l < residentialDensity; l++){
                people[l + i * (residentialDensity) ][1] = -i;
                people[l + i * (residentialDensity) ][0] = -i; //homes portrayed as negative location when described by where people live
            }
        }
        int randomPersonToInfect = (int) ((population - 11) * neuralRNG.nextDouble());
        for (int k = 0; k < 10; k++){
            people[randomPersonToInfect + k][3] = 1;
        }

        locationalInfections = new int[shopIndex.size()];


        while (timeStep < 300 && infectedPopulation > 0){
            runSimulationTick();
        }
        totalInfected = infectedPopulation + recoveredPopulation + 1;//not sure why 1 needs to be added here but it makes it work.

        if (!isQuiet){
            fileWriter.close();
        }


        //TODO: Make a new disease model
    }
	
	protected int[] determineViableShops(int x, int y){
		int numberOfViableShops = 0;
		Rectangle areaSurroundingBuilding = new Rectangle((x - interactionRadius), (y - interactionRadius),
		(x + interactionRadius), (y + interactionRadius));
		ArrayList<Integer> viableIndexes = new ArrayList<>();
		
		for (int i = 0; i < shopIndex.size(); i++){
			if (areaSurroundingBuilding.contains(shopIndex.get(i))){
				numberOfViableShops++;
				viableIndexes.add(i);
			}
		}
		
		int[] viableShopIndexes = new int[viableIndexes.size()];
		
		for (int a = 0; a < viableIndexes.size(); a++){
			viableShopIndexes[a] = viableIndexes.get(a);
		}
		
		return viableShopIndexes;
	}

    protected void movePersonOutsideHome(int person){
        int index = -people[person][1];
        if (index >= 0){
            if (shopLocations.get(index).length != 0){
                int[] possibleShops = shopLocations.get(index);
                int randomShop = (int) Math.floor(neuralRNG.nextDouble() * (double) possibleShops.length);
                if (randomShop >= possibleShops.length){
                    randomShop = possibleShops.length - 1; //This should be rare
                }

                people[person][0] = possibleShops[randomShop];
            }
        }
    }


    protected void runSimulationTick() {
        //Iter over the entire damn world while we are at it :O.
        int susceptiblePopulation = 0;
        infectedPopulation = 0;



        for (int i = 0; i < population; i++){
            //Skip all recovered people to save time
            if (people[i][3] != -1){
                if (people[i][1] == people[i][0] && timeStepsAtLocation[i] <= 0){
                    //Resets the time spend outside. Extraverts spend lots more time out compared to intraverts
                    if (people[i][3] == 0 && chanceOfHealthyLeavingHome > neuralRNG.nextDouble()){
                        timeStepsAtLocation[i] = (double) people[i][2] / 750000000.0;
                        movePersonOutsideHome(i);
                    } else if (people[i][3] > 0 && chanceOfSickLeavingHome > neuralRNG.nextDouble()){
                        timeStepsAtLocation[i] = (double) people[i][2] / 750000000.0;
                        movePersonOutsideHome(i);
                    } else {
                        timeStepsAtLocation[i] = 1.5 - ((double) people[i][2] / 750000000.0);
                    }
                } else if (people[i][1] != people[i][0] && timeStepsAtLocation[i] <= 0){
                    timeStepsAtLocation[i] = 1.5 - ((double) people[i][2] / 750000000.0);
                    people[i][1] = people[i][0]; //Go home, you're drunk
                } else {
                    timeStepsAtLocation[i] -= timeStepLength;
                }

                //First infect anyone who isn't in bed right now.
                if (people[i][3] == 1 && people[i][0] >= 0 && (infectionRate * timeStepLength) * (1 - (0.5 * recoveredPercentage)) > neuralRNG.nextDouble()){
                    locationalInfections[people[i][0]]++;
                }
                if (people[i][0] >= 0){
                    if (locationalInfections[people[i][0]] > 0){
                        if (people[i][3] == 0){
                            people[i][3] = 1;
                            locationalInfections[people[i][0]]--;
                        }
                    }
                }
                //Now recover those who are sick
                if (people[i][3] == 1 && (recoveryRate * timeStepLength) > neuralRNG.nextDouble()){
                    people[i][3] = -1;
                }

                //Now count SIR
                if (people[i][3] == 1){
                    infectedPopulation++;
                } else {
                    susceptiblePopulation++;
                }


            }

        }
        if (infectedPopulation > maxInfected){
            maxInfected = infectedPopulation;
        }
        recoveredPopulation = population - infectedPopulation - susceptiblePopulation;
        recoveredPercentage = recoveredPopulation / population;
        timeStep += timeStepLength;

        if (!isQuiet){
            fileWriter.write(timeStep + ", " + susceptiblePopulation + ", " + infectedPopulation + ", " + recoveredPopulation + "\n");
        }

        if (timeStep > lastPrintedTimestep){
            System.out.println("at timestep " + timeStep);
            System.out.println("S: " + susceptiblePopulation);
            System.out.println("I: " + infectedPopulation);
            System.out.println("R: " + recoveredPopulation);
            lastPrintedTimestep++;
        }


    }




}
