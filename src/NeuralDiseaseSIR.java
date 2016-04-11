import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class NeuralDiseaseSIR {

    private int population = 79000;
    private int residentialDensity = 1000; //per km^2
    private int cityXSize = 10; //In km
    private int cityYSize = 10;
	
	private int interactionRadius = 2;


    private double chanceOfHealthyLeavingHome = 0.90;
    private double chanceOfSickLeavingHome    = 0.60;
    private double timeStepLength = 0.001;
	
	
    private int[][] people;//This is gonna be yuge :O
    private double[] timeStepsAtLocation;//This is gonna be yuge too.

    private ArrayList<Point> houseIndex;
	private Vector<int[]> shopLocations;
    private ArrayList<Point> shopIndex;
	
	

    private int unusedCityArea = 2; //The rest will be commercial and industrial. measured in sq km




    protected NeuralDiseaseSIR(){

        int[][] cityRCI = new int[cityXSize][cityYSize]; //Positive = residential, Neg = unused, 0 = industrial/commercial


		
		shopIndex = new ArrayList<>();
		
		
        int cityArea = cityXSize * cityYSize - unusedCityArea;

        Random locationGenerator = new Random(System.nanoTime());


        while (unusedCityArea >= 0){
            int x = locationGenerator.nextInt() % cityXSize;
            int y = locationGenerator.nextInt() % cityYSize;
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
		
		houseIndex = new ArrayList<>();
		shopLocations = new Vector<>(populationTilesToGenerate);
		
        while (populationTilesToGenerate >= 0){
            int x = locationGenerator.nextInt() % cityXSize;
            int y = locationGenerator.nextInt() % cityYSize;
            if (cityRCI[x][y] == 0){
                cityRCI[x][y] = 1;
				houseIndex.add(new Point(x, y));
                unusedCityArea--;
            }
        }
		for (int x = 0; x < cityXSize; x++){
			for (int y = 0; y < cityYSize; y++){
				if (cityRCI[x][y] == 0){
					shopIndex.add(new Point(x,y));
				}
			}
		}
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
            people[t][2] = (int) (100000 * (0.25 * Math.random() + 0.75));
        }
        for (int i = 0; i < populationTiles; i++){
            for (int l = 0; l < population; l++){
                people[l][1] = i;
                people[l][0] = i;
            }
        }
        int randomPersonToInfect = (int) ((population - 11) * Math.random());
        for (int k = 0; k < 10; k++){
            people[randomPersonToInfect + k][3] = 1;
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


    protected void runSimulationTick() {
        //Iter over the entire damn world while we are at it :O.
        for (int i = 0; i < population; i++){
            if (people[i][1] == people[i][0] && timeStepsAtLocation[i] <= 0){

            } else if (people[i][1] != people[i][0] && timeStepsAtLocation[i] <= 0){
                int tendancy = people[i];
            } else {
                timeStepsAtLocation[i] -= timeStepLength;
            }


        }





    }




}
