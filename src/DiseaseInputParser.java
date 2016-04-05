import java.util.Vector;

import org.apfloat.Apfloat;

/**
 * Created by scc on 4/4/2016.
 */
public class DiseaseInputParser {


    int processorThreads;
    protected static int entropy = 128;

    private Apfloat startingSusceptible;
    private Apfloat startingInfected;


    public DiseaseInputParser () {
        processorThreads = Runtime.getRuntime().availableProcessors();
        runDieseaseModeller();
    }

    public DiseaseInputParser (int threads) {
        processorThreads = threads;
        runDieseaseModeller();



    }

    private void runDieseaseModeller () {
        Vector<Apfloat> inputData = convertUserInputsToSuperFloat(getUserInputs());

        startingSusceptible = inputData.get(8).subtract(inputData.get(9));
        startingInfected = inputData.get(9);

        long totalXToTest = ((inputData.get(2).subtract(inputData.get(3))).divide(inputData.get(4))).longValue();
        long totalYToTest = ((inputData.get(5).subtract(inputData.get(6))).divide(inputData.get(7))).longValue();
        if (totalXToTest < 0){
            totalXToTest =- totalXToTest;
        } if (totalYToTest < 0){
            totalYToTest =- totalYToTest;
        }

        long totalBufferSize = totalXToTest * totalYToTest;
        if (totalBufferSize < (Runtime.getRuntime().maxMemory() / 32)){
            double[][] buffer = new double[(int)totalXToTest][(int)totalYToTest];

            long runner = 0;

            for (long x = 0; x < totalXToTest; x++){
                for (long y = 0; y < totalYToTest; y++){




                }
            }






        } else {
            System.exit(108);//108 = not enough memory
        }






    }


    private Vector<String> getUserInputs () {
        Vector<String> allUserInputs = new Vector<String>(10);

        System.out.println("Hello and welcome to Void Plague disease calculator");

        System.out.println("\nIn order, enter entropy bits, timestep size, starting and ending infection rate, infection time step, starting and ending recovery rate, and recovery timestep. Finally, total people and infected people");

        System.out.println("You can enter all these values automatically by running void-plague < input.txt");

        entropy = Integer.parseInt(System.console().readLine());

        allUserInputs.set(0, System.console().readLine());
        allUserInputs.set(1, System.console().readLine());
        allUserInputs.set(2, System.console().readLine());
        allUserInputs.set(3, System.console().readLine());
        allUserInputs.set(4, System.console().readLine());
        allUserInputs.set(5, System.console().readLine());
        allUserInputs.set(6, System.console().readLine());
        allUserInputs.set(7, System.console().readLine());
        allUserInputs.set(8, System.console().readLine());
        allUserInputs.set(9, System.console().readLine());


        return allUserInputs;
    }

    private Vector<Apfloat> convertUserInputsToSuperFloat (Vector<String> userInputs) {
        Vector<Apfloat> userInputFloatingPoints = new Vector<Apfloat>(10);
        for (int i = 0; i < userInputFloatingPoints.capacity(); i++){
            userInputFloatingPoints.add(new Apfloat(userInputs.get(i)));
        }
        return userInputFloatingPoints;
    }






}
