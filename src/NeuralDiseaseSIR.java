import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

	

    //private int unusedCityArea = 2; //The rest will be commercial and industrial. measured in sq km



    private PrintWriter fileWriter;
    protected NeuralDiseaseSIR(double timeStepLength, int population, int residentialDensity, int cityXSize,
                               int cityYSize, int unusedCityArea, int interactionRadius, double chanceOfHealthyLeavingHome,
                               double chanceOfSickLeavingHome, double infectionRate, double recoveryRate) throws FileNotFoundException, UnsupportedEncodingException {


        //Set all the values given

        this.timeStepLength = timeStepLength;
        this.interactionRadius = interactionRadius;
        this.population = population;
        this.chanceOfHealthyLeavingHome = chanceOfHealthyLeavingHome;
        this.chanceOfSickLeavingHome = chanceOfSickLeavingHome;
        this.infectionRate = infectionRate;
        this.recoveryRate = recoveryRate;


        fileWriter = new PrintWriter("NeuralDisease.csv", "UTF-8");
        fileWriter.write("Time, Susceptible, Infected, Recovered\n");



        int[][] cityRCI = new int[cityXSize][cityYSize]; //Positive = residential, Neg = unused, 0 = industrial/commercial


		
		shopIndex = new ArrayList<>();
		
		
        int cityArea = cityXSize * cityYSize - unusedCityArea;

        Random locationGenerator = new Random(System.nanoTime());

        System.out.println("Init 1 completed");

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

        System.out.println("Init 2 completed");

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

        System.out.println("Init 3 completed");

		for (int x = 0; x < cityXSize; x++){
			for (int y = 0; y < cityYSize; y++){
				if (cityRCI[x][y] == 0){
					shopIndex.add(new Point(x,y));
				}
			}
		}

        System.out.println("Init 4 completed");

		for (int x = 0; x < shopLocations.capacity(); x++){
			shopLocations.add(determineViableShops( (int) houseIndex.get(x).getX(), (int) houseIndex.get(x).getY()));
		}


        System.out.println("Was able to produce a city with a population of" +
                "\n" + populationTiles * residentialDensity + "" +
                "\nusing these numbers");

        people = new int[(int)(populationTiles * residentialDensity)][4]; //0 is current location, 1 is home location,
        // 2 is tendency to leave house, 3 is SIR status

        timeStepsAtLocation = new double[population];
        for (int t = 0; t < population; t++){
            people[t][2] = (int) (1000000000 * (0.75 * Math.random() + 0.25));
        }
        for (int i = 0; i < populationTiles; i++){
            for (int l = 0; l < population; l++){
                people[l][1] = -i;
                people[l][0] = -i; //homes portrayed as negative location when described by where people live
            }
        }
        int randomPersonToInfect = (int) ((population - 11) * Math.random());
        for (int k = 0; k < 10; k++){
            people[randomPersonToInfect + k][3] = 1;
        }

        locationalInfections = new int[shopIndex.size()];


        while (timeStep < 300 && infectedPopulation > 0){
            runSimulationTick();
        }


        fileWriter.close();

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
                int randomShop = (int) Math.floor(Math.random() * (double) possibleShops.length);
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
                    if (people[i][3] == 0 && chanceOfHealthyLeavingHome > Math.random()){
                        timeStepsAtLocation[i] = (double) people[i][2] / 750000000.0;
                        movePersonOutsideHome(i);
                    } else if (people[i][3] > 0 && chanceOfSickLeavingHome > Math.random()){
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
                if (people[i][3] == 1 && people[i][0] >= 0 && (infectionRate * timeStepLength) > Math.random()){
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
                if (people[i][3] == 1 && (recoveryRate * timeStepLength) > Math.random()){
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
        recoveredPopulation = population - infectedPopulation - susceptiblePopulation;
        timeStep += timeStepLength;

        fileWriter.write(timeStep + ", " + susceptiblePopulation + ", " + infectedPopulation + ", " + recoveredPopulation + "\n");

        if (timeStep > lastPrintedTimestep){
            System.out.println("at timestep " + timeStep);
            System.out.println("S: " + susceptiblePopulation);
            System.out.println("I: " + infectedPopulation);
            System.out.println("R: " + recoveredPopulation);
            lastPrintedTimestep++;
        }


    }




}
