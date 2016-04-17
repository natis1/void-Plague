import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import org.apfloat.Apfloat;

public class DiseaseInputParser {


    private final boolean IDE_MODE = false;


    int processorThreads;
    private int runningThreads = 0;
    private Apfloat startingSusceptible;
    private Apfloat startingInfected;

    private boolean hasInputFile = false;
    private String inputFileLocation;

    public DiseaseInputParser () {
        processorThreads = Runtime.getRuntime().availableProcessors();
        runDiseaseModeller();
    }

    public DiseaseInputParser (int threads) {
        processorThreads = threads;
        runDiseaseModeller();
    }

    public DiseaseInputParser (String inputFileLocation){
        this.inputFileLocation = inputFileLocation;
        processorThreads = Runtime.getRuntime().availableProcessors();
        hasInputFile = true;
        runDiseaseModeller();
    }

    public DiseaseInputParser (String inputFileLocation, int threads){
        this.inputFileLocation = inputFileLocation;
        processorThreads = threads;
        hasInputFile = true;
        runDiseaseModeller();
    }

    private void runDiseaseModeller() {

        ArrayList<Double> inputData;


        if (hasInputFile){
            inputData = parseInputFile();
        } else {
            inputData = convertUserInputsToSuperFloat(getUserInputs());
        }

        switch (inputData.get(0).intValue()){
            case 0:
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
                        System.out.println("You do not have UTF-8? I am seriously amazed");
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
                        System.out.println("No idea why I can't make file Something is seriously wrong with your system");
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("You do not have UTF-8? I am seriously amazed");
                    }
                } else {
                    System.exit(108);//108 = not enough memory
                }
                break;
            case 1:

                if (new File("infectionCSV").mkdir()){
                    System.out.println("Created directory for output files");
                } else {
                    System.out.println("Unable to create output file directory, this may not be an error in some cases");
                }

                try {
                    NeuralDiseaseSIR runMe = new NeuralDiseaseSIR(inputData.get(1), inputData.get(2).intValue(),
                            inputData.get(3).intValue(), inputData.get(4).intValue(), inputData.get(5).intValue(),
                            inputData.get(6).intValue(), inputData.get(7).intValue(), inputData.get(8), inputData.get(9),
                            inputData.get(10), inputData.get(11), false, new int[3]);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.exit(0);//No error

                break;

            case 2:

                if (new File("infectionCSV").mkdir()){
                    System.out.println("Created directory for output files");
                } else {
                    System.out.println("Unable to create output file directory, either you don't have permissions or it already exists");
                }


                Double[][] khaZixStorage = new Double[4][3];
                int[]    runPlagueNTimes = new int[4];
                khaZixStorage[0][0] = inputData.get(2);
                khaZixStorage[0][1] = inputData.get(3);
                khaZixStorage[0][2] = inputData.get(4);
                khaZixStorage[1][0] = inputData.get(5);
                khaZixStorage[1][1] = inputData.get(6);
                khaZixStorage[1][2] = inputData.get(7);
                khaZixStorage[2][0] = inputData.get(14);
                khaZixStorage[2][1] = inputData.get(15);
                khaZixStorage[2][2] = inputData.get(16);
                khaZixStorage[3][0] = inputData.get(17);
                khaZixStorage[3][1] = inputData.get(18);
                khaZixStorage[3][2] = inputData.get(19);
                int bufferSize = 1;

                for (int i = 0; i < 4; i++){
                    if (khaZixStorage[i][0].intValue() == khaZixStorage[i][1].intValue()){
                        runPlagueNTimes[i] = 1;
                    } else {
                        runPlagueNTimes[i] = (int) ((khaZixStorage[i][1] - khaZixStorage[i][0])/khaZixStorage[i][2]);
                        bufferSize *= runPlagueNTimes[i];
                    }
                }

                int[][][] maxInfectedBuffer = new int[runPlagueNTimes[1]] [runPlagueNTimes[2]] [runPlagueNTimes[3]];
                int[][][] totalInfectedBuffer = new int[runPlagueNTimes[1]] [runPlagueNTimes[2]] [runPlagueNTimes[3]];

                System.out.println("Created buffer for program.");
                ArrayList<NeuralSIRThread> threadManager = new ArrayList<NeuralSIRThread>();
                for (int populationMult = 0; populationMult < runPlagueNTimes[0]; populationMult++){
                    for (int densityMult = 0; densityMult < runPlagueNTimes[1]; densityMult++){
                        for (int infectMult = 0; infectMult < runPlagueNTimes[2]; infectMult++){
                            for (int recoveryMult = 0; recoveryMult < runPlagueNTimes[3]; recoveryMult++){
                                //WOW 4 freaking for loops what has this world become?
                                int[] location = new int[3];
                                location[0] = densityMult;
                                location[1] = infectMult;
                                location[2] = recoveryMult;


                                        runningThreads++;
                                        threadManager.add(new NeuralSIRThread(inputData.get(1),
                                                (int) (khaZixStorage[0][0] + (khaZixStorage[0][2] * populationMult)),
                                                (int) (khaZixStorage[1][0] + (khaZixStorage[1][2] * densityMult)),
                                                (khaZixStorage[2][0] + (khaZixStorage[2][2] * infectMult)),
                                                (khaZixStorage[3][0] + (khaZixStorage[3][2] * recoveryMult)),
                                                inputData.get(8).intValue(), inputData.get(9).intValue(), inputData.get(10).intValue(),
                                                inputData.get(11).intValue(), inputData.get(12), inputData.get(13), location));
                                        threadManager.get(threadManager.size() - 1).start();
                                    if (runningThreads >= processorThreads) {
                                        while (runningThreads >= processorThreads) {
                                            try {
                                                Thread.sleep(50); // Why not, good time to check
                                                //Going in reverse order index out of bounds error.
                                                for (int i = threadManager.size() - 1; i >= 0; i--) {
                                                    //Just kidding 5 nested loops

                                                    if (threadManager.get(i).didFinish){
                                                        runningThreads--;
                                                        bufferSize--;
                                                        System.out.println(bufferSize + " operations left to complete, I think");

                                                        maxInfectedBuffer[threadManager.get(i).SIRRunner.pointLocation[0]]
                                                                [threadManager.get(i).SIRRunner.pointLocation[1]]
                                                                [threadManager.get(i).SIRRunner.pointLocation[2]]
                                                                = threadManager.get(i).SIRRunner.maxInfected;

                                                        totalInfectedBuffer[threadManager.get(i).SIRRunner.pointLocation[0]]
                                                                [threadManager.get(i).SIRRunner.pointLocation[1]]
                                                                [threadManager.get(i).SIRRunner.pointLocation[2]]
                                                                = threadManager.get(i).SIRRunner.totalInfected;

                                                        threadManager.remove(i);
                                                    }
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                                System.out.println("\nJust a heads up something went wrong while the manager was sleeping" +
                                                        "\nthis was probably you interrupting it\n Attempting to continue regardless");
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }


                for (int density = 0; density < runPlagueNTimes[1]; density++){
                    int[][] maxInfectedAt = maxInfectedBuffer[density];
                    int[][] totalInfectedAt = totalInfectedBuffer[density];
                    Double densityAtPoint = khaZixStorage[1][0] + (khaZixStorage[1][2] * density);
                    writeResultsToCSV(densityAtPoint, maxInfectedAt, totalInfectedAt, khaZixStorage[2][0], khaZixStorage[2][2],
                            khaZixStorage[3][0], khaZixStorage[3][2]);




                }




                break;
        }
    }


    private void writeResultsToCSV(double density, int[][] maxInfected, int[][] totalInfected, double xStarting,
                                   double xIncrement, double yStarting, double yIncrement){

        PrintWriter fileWriter;

        try {
            fileWriter = new PrintWriter("infectionCSV/MaxI Density:" + new DecimalFormat("#.#").format(density) + ".csv", "UTF-8");
            fileWriter.println("Max Infected");
            fileWriter.print("Space Left Blank, ");
            for (int x = 0; x < maxInfected.length; x++){
                fileWriter.print(", " + (xStarting + (xIncrement * x)));
            }
            for (int y = 0; y < maxInfected[0].length; y++){
                fileWriter.print("\n" + (yStarting + (yIncrement * y)));
                for (int x = 0; x < maxInfected.length; x++){
                    fileWriter.print(", ");
                    fileWriter.print(maxInfected[x][y]);
                }
            }
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("No idea why I can't make file Something is seriously wrong with your system");
        } catch (UnsupportedEncodingException e) {
            System.out.println("You do not have UTF-8? I am seriously amazed");
        }

        try {
            fileWriter = new PrintWriter("infectionCSV/TotalI Density:" + new DecimalFormat("#.#").format(density) + ".csv", "UTF-8");
            fileWriter.println("Max Infected");
            fileWriter.print("Space Left Blank, ");
            for (int x = 0; x < totalInfected.length; x++){
                fileWriter.print(", " + (xStarting + (xIncrement * x)));
            }
            for (int y = 0; y < totalInfected[0].length; y++){
                fileWriter.print("\n" + (yStarting + (yIncrement * y)));
                for (int x = 0; x < totalInfected.length; x++){
                    fileWriter.print(", ");
                    fileWriter.print(totalInfected[x][y]);
                }
            }
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("No idea why I can't make file Something is seriously wrong with your system");
        } catch (UnsupportedEncodingException e) {
            System.out.println("You do not have UTF-8? I am seriously amazed");
        }



    }



    private ArrayList<Double> parseInputFile(){
        ArrayList<Double> userInputs = new ArrayList<>();


        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new File(inputFileLocation));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found, please specify an actual file.");
        }

        while (fileScanner.hasNextDouble()){
            userInputs.add(fileScanner.nextDouble());
        }

        return userInputs;
    }





    private ArrayList<String> getUserInputs () {
        ArrayList<String> allUserInputs = new ArrayList<String>();

        System.out.println("Hello and welcome to Void Plague disease calculator");

        System.out.println("Please enter the method you want to calculate diseases (neural, basic, multi neural)");



        if (IDE_MODE){
            allUserInputs.add("2");
            allUserInputs.add("0.001");
            allUserInputs.add("7900000");
            allUserInputs.add("7900000");
            allUserInputs.add("50");
            allUserInputs.add("10000");
            allUserInputs.add("20000");
            allUserInputs.add("1000");
            allUserInputs.add("32");
            allUserInputs.add("32");
            allUserInputs.add("0");
            allUserInputs.add("4");
            allUserInputs.add("0.9");
            allUserInputs.add("0.75");
            allUserInputs.add("0.5");
            allUserInputs.add("5");
            allUserInputs.add("0.1");
            allUserInputs.add("0.5");
            allUserInputs.add("5");
            allUserInputs.add("0.1");
        } else {
            Console c = System.console();
            String methodToUse = c.readLine();
            if (methodToUse.contains("neural") || methodToUse.contains("Neural")){
                if ((methodToUse.contains("multi"))){
                    System.out.println("Enter 2 and timestep size");
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    System.out.println("Enter minimum population, maximum population, and population step");//2-4 = pop
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    System.out.println("Enter minimum population density, maximum density, and density range");//5-7 = density
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    System.out.println("Enter city X size, city Y size, and number of unused city tiles");
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    System.out.println("Enter person interaction radius, chance for healthy people to leave home, and chance for the sick to leave home");
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    System.out.println("Enter minimum infection rate, maximum infection rate, and infection rate step");//14-16 = infection
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    System.out.println("Enter minimum recovery rate, maximum recovery rate, and recovery rate step");//17-19 = recovery
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());
                    allUserInputs.add(c.readLine());

                } else {
                    System.out.println("In order, enter 1, timestep size, population, population density, city X, city Y, unused city tiles," +
                            "interaction radius, chance for healthy to leave house, chance for sick to leave house infection rate, and recovery rate");
                    for (int i = 0; i < 12; i++) {
                        allUserInputs.add(c.readLine());
                    }
                }
            } else if (methodToUse.contains("basic") || methodToUse.contains("Basic")){
                System.out.println("\nIn order, enter 0, timestep size, starting and ending infection rate, infection size step, starting and ending recovery rate, and recovery timestep. Finally, total people and infected people");
                for (int i = 0; i < 10; i++) {
                    allUserInputs.add(c.readLine());
                }
            }


        }




        return allUserInputs;
    }

    private ArrayList<Double> convertUserInputsToSuperFloat (ArrayList<String> userInputs) {
        ArrayList<Double> userInputFloatingPoints = new ArrayList<Double>();
        for (int i = 0; i < userInputs.size(); i++){
            userInputFloatingPoints.add(Double.parseDouble(userInputs.get(i)));
        }
        return userInputFloatingPoints;
    }






}
