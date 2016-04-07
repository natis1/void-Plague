import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Vector;

import org.apfloat.Apfloat;

/**
 * Created by scc on 4/4/2016.
 */
public class DiseaseInputParser {


    private final boolean IDE_MODE = false;


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

        long totalXToTest = (long) ((inputData.get(3)-(inputData.get(2)))/(inputData.get(4)));
        long totalYToTest = (long) ((inputData.get(6)-(inputData.get(5)))/(inputData.get(7)));
        if (totalXToTest < 0){
            totalXToTest =- totalXToTest;
        } if (totalYToTest < 0){
            totalYToTest =- totalYToTest;
        }

        long totalBufferSize = totalXToTest * totalYToTest;
        if (totalBufferSize < (Runtime.getRuntime().maxMemory() / 128)){
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
                                new Apfloat (inputData.get(5)+ (x * inputData.get(7))), new Apfloat (inputData.get(1)), x, y));
                        threadManager.get(threadManager.size() - 1).start();

                        y++;
                    } else {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.println("\nJust a heads up something went wrong while the manager was sleeping" +
                                    "\nthis was probably you interrupting it");
                        }
                    }
                    for (int i = threadManager.size() - 1; i >= 0; i--) {
                        if (threadManager.get(i).didComplete){
                            runningThreads--;
                            totalBufferSize--;
                            System.out.println(totalBufferSize + " operations left to complete");
                            Vector<Apfloat> exportedVector = threadManager.get(i).returnGeneratedValues();
                            long threadX = threadManager.get(i).x;
                            long threadY = threadManager.get(i).y;


                            //Finally we can convert it to doubles now that all the math is done
                            totalInfectedBuffer[(int) threadX] [(int) threadY] = exportedVector.get(1).add(exportedVector.get(2)).doubleValue();
                            equilibriumTimeBuffer[(int) threadX] [(int) threadY] = exportedVector.get(5).doubleValue();
                            maxInfectedBuffer[(int) threadX] [(int) threadY] = exportedVector.get(3).doubleValue();
                            maxInfectedTimeBuffer[(int) threadX] [(int) threadY] = exportedVector.get(4).doubleValue();

                            threadManager.remove(i);
                        }
                    }
                }
            }



            PrintWriter fileWriter;
            try {
                fileWriter = new PrintWriter("Total_Infected.csv", "UTF-8");
                fileWriter.println("Total Infected Buffer");
                fileWriter.print("Space Left Blank");

                Double xIncrement = inputData.get(4);
                Double xStarting  = inputData.get(2);

                for (long x = 0; x < totalXToTest; x++){
                    fileWriter.print(", " + (xStarting + (xIncrement * x)));
                }
                Double yIncrement = inputData.get(5);
                Double yStarting  = inputData.get(7);
                for (long y = 0; y < totalYToTest; y++){
                    fileWriter.print("\n" + (yIncrement + (yIncrement * y)));
                    for (long x = 0; x < totalXToTest; x++){
                        fileWriter.print(", ");
                        fileWriter.print(totalInfectedBuffer[(int) x][(int) y]);

                    }
                }
                fileWriter.close();
            } catch (FileNotFoundException e) {
                System.out.println(
                        "No idea why I can't make file Something is seriously wrong with your system");

            } catch (UnsupportedEncodingException e) {
                System.out.println(
                        "You do not have UTF-8? I am seriously amazed");
            }

            try {
                fileWriter = new PrintWriter("Equilibrium_Time.csv", "UTF-8");
                fileWriter.println("Equilibrium_Time");
                fileWriter.print("Space Left Blank");

                Double xIncrement = inputData.get(4);
                Double xStarting  = inputData.get(2);

                for (long x = 0; x < totalXToTest; x++){
                    fileWriter.print(", " + (xStarting + (xIncrement * x)));
                }
                Double yIncrement = inputData.get(5);
                Double yStarting  = inputData.get(7);
                for (long y = 0; y < totalYToTest; y++){
                    fileWriter.print("\n" + (yIncrement + (yIncrement * y)));
                    for (long x = 0; x < totalXToTest; x++){
                        fileWriter.print(", ");
                        fileWriter.print(equilibriumTimeBuffer[(int) x][(int) y]);

                    }
                }
                fileWriter.close();
            } catch (FileNotFoundException e) {
                System.out.println(
                        "No idea why I can't make file Something is seriously wrong with your system");

            } catch (UnsupportedEncodingException e) {
                System.out.println(
                        "You do not have UTF-8? I am seriously amazed");
            }


            try {
                fileWriter = new PrintWriter("Max_Infected.csv", "UTF-8");
                fileWriter.println("Max_Infected");
                fileWriter.print("Space Left Blank");

                Double xIncrement = inputData.get(4);
                Double xStarting  = inputData.get(2);

                for (long x = 0; x < totalXToTest; x++){
                    fileWriter.print(", " + (xStarting + (xIncrement * x)));
                }
                Double yIncrement = inputData.get(5);
                Double yStarting  = inputData.get(7);
                for (long y = 0; y < totalYToTest; y++){
                    fileWriter.print("\n" + (yIncrement + (yIncrement * y)));
                    for (long x = 0; x < totalXToTest; x++){
                        fileWriter.print(", ");
                        fileWriter.print(maxInfectedBuffer[(int) x][(int) y]);

                    }
                }
                fileWriter.close();
            } catch (FileNotFoundException e) {
                System.out.println(
                        "No idea why I can't make file Something is seriously wrong with your system");

            } catch (UnsupportedEncodingException e) {
                System.out.println(
                        "You do not have UTF-8? I am seriously amazed");
            }


            try {
                fileWriter = new PrintWriter("Max_Infected_Time.csv", "UTF-8");
                fileWriter.println("Max Infected Time");
                fileWriter.print("Space Left Blank, ");

                Double xIncrement = inputData.get(4);
                Double xStarting  = inputData.get(2);

                for (long x = 0; x < totalXToTest; x++){
                    fileWriter.print(", " + (xStarting + (xIncrement * x)));
                }
                Double yIncrement = inputData.get(5);
                Double yStarting  = inputData.get(7);
                for (long y = 0; y < totalYToTest; y++){
                    fileWriter.print("\n" + (yIncrement + (yIncrement * y)));
                    for (long x = 0; x < totalXToTest; x++){
                        fileWriter.print(", ");
                        fileWriter.print(maxInfectedTimeBuffer[(int) x][(int) y]);

                    }
                }
                fileWriter.close();
            } catch (FileNotFoundException e) {
                System.out.println(
                        "No idea why I can't make file Something is seriously wrong with your system");

            } catch (UnsupportedEncodingException e) {
                System.out.println(
                        "You do not have UTF-8? I am seriously amazed");
            }






        } else {
            System.exit(108);//108 = not enough memory
        }

    }





    private Vector<String> getUserInputs () {
        Vector<String> allUserInputs = new Vector<String>(10);

        System.out.println("Hello and welcome to Void Plague disease calculator");

        System.out.println("\nIn order, enter 0, timestep size, starting and ending infection rate, infection size step, starting and ending recovery rate, and recovery timestep. Finally, total people and infected people");

        System.out.println("You can enter all these values automatically by running void-plague < input.txt");



        if (IDE_MODE){
            allUserInputs.add("0");
            allUserInputs.add("0.1");
            allUserInputs.add("0");
            allUserInputs.add("5");
            allUserInputs.add("0.1");
            allUserInputs.add("0");
            allUserInputs.add("5");
            allUserInputs.add("0.1");
            allUserInputs.add("7800000");
            allUserInputs.add("10");
        } else {
            for (int i = 0; i < 10; i++) {
                allUserInputs.set(i, System.console().readLine());
            }
        }




        return allUserInputs;
    }

    private ArrayList<Double> convertUserInputsToSuperFloat (Vector<String> userInputs) {
        ArrayList<Double> userInputFloatingPoints = new ArrayList<Double>();
        for (int i = 0; i < 10; i++){
            userInputFloatingPoints.add(Double.parseDouble(userInputs.get(i)));
        }
        return userInputFloatingPoints;
    }






}
