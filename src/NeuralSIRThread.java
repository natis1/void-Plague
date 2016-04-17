import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public class NeuralSIRThread extends Thread {

    double timeStepLength;
    private int population;
    private int residentialDensity;
    private int cityXSize;
    int cityYSize;
    private int unusedCityArea;
    private int interactionRadius;
    private double chanceOfHealthyLeavingHome;
    double chanceOfSickLeavingHome;
    private double infectionRate;
    private double recoveryRate;

    private int[] location;


    public boolean didFinish = false;


    public NeuralDiseaseSIR SIRRunner;
    
    
    public NeuralSIRThread(double timeStepLength, int population, int residentialDensity, double infectionRate,
                           double recoveryRate, int cityXSize, int cityYSize, int unusedCityArea, int interactionRadius,
                           double chanceOfHealthyLeavingHome, double chanceOfSickLeavingHome, int[] location) {
        this.timeStepLength = timeStepLength;
        this.population = population;
        this.residentialDensity = residentialDensity;
        this.cityXSize = cityXSize;
        this.cityYSize = cityYSize;
        this.unusedCityArea = unusedCityArea;
        this.interactionRadius = interactionRadius;
        this.chanceOfHealthyLeavingHome = chanceOfHealthyLeavingHome;
        this.chanceOfSickLeavingHome = chanceOfSickLeavingHome;
        this.infectionRate = infectionRate;
        this.recoveryRate = recoveryRate;
        this.location     = location;
        //Wow


    }
    
    
    
    
    
    @Override
    public void run() {

        long startTime = System.nanoTime();

        try {
            SIRRunner = new NeuralDiseaseSIR(timeStepLength, population, residentialDensity, cityXSize, cityYSize, unusedCityArea, interactionRadius,
                    chanceOfHealthyLeavingHome, chanceOfSickLeavingHome, infectionRate, recoveryRate, true, location);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error 404, file not found, attempting to continue anyway");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Unknown encoding for saving the file, Please install UTF-8 on your system.");
        }
        System.out.println("Time taken: " + ((System.nanoTime() - startTime)/1000000) + " ms");
        System.out.println("Max Infected: " + SIRRunner.maxInfected + ", Total Infected: " + SIRRunner.totalInfected);
        didFinish = true;

    }
    
}
