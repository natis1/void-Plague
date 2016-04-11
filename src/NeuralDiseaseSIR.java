import java.util.Random;

/**
 * Created by scc on 4/11/2016.
 */
public class NeuralDiseaseSIR {

    private int population = 7900000;
    private Double residentialDensity = 100000.0; //per km^2
    private int cityXSize = 10; //In km
    private int cityYSize = 10;


    private Double personalRadius = 500.0; //radius in meters


    private int unusedCityArea = 2; //The rest will be commercial and industrial. measured in sq km




    protected NeuralDiseaseSIR(){

        int[][] cityRCI = new int[cityXSize][cityYSize]; //Positive = residential, Neg = unused, 0 = industrial/commercial



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
        while (populationTilesToGenerate >= 0){
            int x = locationGenerator.nextInt() % cityXSize;
            int y = locationGenerator.nextInt() % cityYSize;
            if (cityRCI[x][y] == 0){
                cityRCI[x][y] = 1;
                unusedCityArea--;
            }
        }
        System.out.println("Was able to produce a city with a population of" +
                "\n" + populationTiles * residentialDensity + "" +
                "\nusing these numbers");










        //TODO: Make a new disease model


    }




}
