import java.util.Random;

public class NeuralDiseaseSIR {

    private int population = 79000;
    private Double residentialDensity = 1000.0; //per km^2
    private int cityXSize = 10; //In km
    private int cityYSize = 10;
	
	private int interactionRadius = 2;
	
	
	private int[][] neurons;
	private Vector<Integer[]> shopLocations;
	
	

    private int unusedCityArea = 2; //The rest will be commercial and industrial. measured in sq km




    protected NeuralDiseaseSIR(){

        int[][] cityRCI = new int[cityXSize][cityYSize]; //Positive = residential, Neg = unused, 0 = industrial/commercial
		int[][][] buildingStatus = new int[cityXSize][cityYSize][4]; //This is a lot of allocation
		
		ArrayList<Point> shopIndex = new ArrayList<Point>();
		
		
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

        Double populationTiles = (double) population / residentialDensity;
        int populationTilesToGenerate = populationTiles.intValue();

        if (populationTilesToGenerate >= cityArea){
            populationTilesToGenerate = cityArea - 1;
        }
		
		neurons = new int[populationTilesToGenerate][3];
		shopLocations = new Vector<Integer[]>(populationTilesToGenerate);
		
        while (populationTilesToGenerate >= 0){
            int x = locationGenerator.nextInt() % cityXSize;
            int y = locationGenerator.nextInt() % cityYSize;
            if (cityRCI[x][y] == 0){
                cityRCI[x][y] = 1;
				neurons[populationTilesToGenerate][0] = x;
				neurons[populationTilesToGenerate][1] = y;
                unusedCityArea--;
            }
        }
		for (int x = 0; x < cityXSize; x++){
			for (int y = 0; y < cityYSize; y++){
				if (cityRCI == 0){
					shopIndex.add(new Point(x,y));
				}
			}
		}
		for (int x = 0; x < shopLocations.capacity; x++){
			shopLocations.add(determineViableShops((neurons[x][0]), (neurons[x][1])));
		}
		
        System.out.println("Was able to produce a city with a population of" +
                "\n" + populationTiles * residentialDensity + "" +
                "\nusing these numbers");
				
        //TODO: Make a new disease model
    }
	
	protected int[] determineViableShops(int x, int y){
		int numberOfViableShops = 0;
		Rectangle2D areaSurroundingBuilding = new Rectangle2D((x - interactionRadius), (y - interactionRadius),
		(x + interactionRadius), (y + interactionRadius));
		ArrayList<Integer> viableIndexes = new ArrayList<Integer>();
		
		for (int i = 0; i < shopIndex.size(), i++){
			if (areaSurroundingBuilding.contains(shopIndex.get(i))){
				numberOfViableShops++;
				viableIndexes.add(i);
			}
		}
		
		int[] viableShopIndexes = new int[viableIndexes.size];
		
		for (int a = 0; a < viableIndexes.size; a++){
			viableShopIndexes [a] = viableIndexes.get(a);
		}
		
		return viableShopIndexes;
	}




}
