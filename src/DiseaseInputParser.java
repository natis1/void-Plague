import java.util.ArrayList;
import java.util.Vector;

import org.apfloat.Apfloat;

/**
 * Created by scc on 4/4/2016.
 */
public class DiseaseInputParser {


    int processorThreads;
    protected static int entropy = 128;

    private int runningThreads = 0;
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
        ArrayList<Double> inputData = convertUserInputsToSuperFloat(getUserInputs());

        startingSusceptible = new Apfloat(inputData.get(8) - inputData.get(9));
        startingInfected = new Apfloat(inputData.get(9));

        long totalXToTest = (long) ((inputData.get(2)-(inputData.get(3)))/(inputData.get(4)));
        long totalYToTest = (long) ((inputData.get(5)-(inputData.get(6)))/(inputData.get(7)));
        if (totalXToTest < 0){
            totalXToTest =- totalXToTest;
        } if (totalYToTest < 0){
            totalYToTest =- totalYToTest;
        }

        long totalBufferSize = totalXToTest * totalYToTest;
        if (totalBufferSize < (Runtime.getRuntime().maxMemory() / 32)){
            double[][] maxInfectedBuffer         = new double[(int)totalXToTest][(int)totalYToTest];
            double[][] maxInfectedTimeBuffer     = new double[(int)totalXToTest][(int)totalYToTest];
            double[][] totalInfectedBuffer       = new double[(int)totalXToTest][(int)totalYToTest];
            double[][] equilibriumTimeBuffer     = new double[(int)totalXToTest][(int)totalYToTest];


            ArrayList<SIRThread> threadManager = new ArrayList<SIRThread>();

            for (long x = 0; x < totalXToTest; x++){
                long y = 0;
                while (y < totalYToTest){
                    if (runningThreads < processorThreads){
                        runningThreads++;
                        threadManager.add(new SIRThread(startingSusceptible, startingInfected,
                                new Apfloat (inputData.get(2)+ (y * inputData.get(4))),
                                new Apfloat (inputData.get(5)+ (x * inputData.get(7))), new Apfloat (inputData.get(1))));
                        y++;
                    } else {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.println("\nJust a heads up something went wrong while the manager was sleeping" +
                                    "\nthis was probably you interrupting it");
                        }
                    }
                    for (int i = 0; i < threadManager.size(); i++){
                        if (threadManager.get(i).didComplete){
                            Vector<>
                        }
                    }



                }
            }






        } else {
            System.exit(108);//108 = not enough memory
        }

    }




    private Vector<String> getUserInputs () {
        Vector<String> allUserInputs = new Vector<String>(10);

        System.out.println("Hello and welcome to Void Plague disease calculator");

        System.out.println("\nIn order, enter entropy bits, timestep size, starting and ending infection rate, infection size step, starting and ending recovery rate, and recovery timestep. Finally, total people and infected people");

        System.out.println("You can enter all these values automatically by running void-plague < input.txt");

        entropy = Integer.parseInt(System.console().readLine());

        for (int i = 0; i < 10; i++){
            allUserInputs.set(i, System.console().readLine());
        }

        return allUserInputs;
    }

    private ArrayList<Double> convertUserInputsToSuperFloat (Vector<String> userInputs) {
        ArrayList<Double> userInputFloatingPoints = new ArrayList<Double>;
        for (int i = 0; i < 10; i++){
            userInputFloatingPoints.add(Double.parseDouble(userInputs.get(i)));
        }
        return userInputFloatingPoints;
    }






}
