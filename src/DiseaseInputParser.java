import java.util.Vector;

import org.apfloat.Apfloat;

/**
 * Created by scc on 4/4/2016.
 */
public class DiseaseInputParser {


    int processorThreads;
    protected static int entropy = 128;


    public DiseaseInputParser () {
        processorThreads = Runtime.getRuntime().availableProcessors();
        runDieseaseModeller();
    }

    public DiseaseInputParser (int threads) {
        processorThreads = threads;
        runDieseaseModeller();



    }

    private void runDieseaseModeller () {
        convertUserInputsToSuperFloat(getUserInputs());




    }


    private Vector<String> getUserInputs () {
        Vector<String> allUserInputs = new Vector<String>(7);

        System.out.println("Hello and welcome to Void Plague disease calculator");

        System.out.println("\nIn order, enter entropy bits, timestep size, starting and ending infection rate, infection time step, starting and ending recovery rate, and recovery timestep");

        System.out.println("You can enter all these values automatically by running void-plague < input.txt");

        entropy = Integer.parseInt(System.console().readLine());

        allUserInputs.set(0, System.console().readLine());
        allUserInputs.set(1, System.console().readLine());
        allUserInputs.set(2, System.console().readLine());
        allUserInputs.set(3, System.console().readLine());
        allUserInputs.set(4, System.console().readLine());
        allUserInputs.set(5, System.console().readLine());
        allUserInputs.set(6, System.console().readLine());


        return allUserInputs;
    }

    private Vector<Apfloat> convertUserInputsToSuperFloat (Vector<String> userInputs) {
        Vector<Apfloat> userInputFloatingPoints = new Vector<Apfloat>(7);
        for (int i = 0; i < userInputFloatingPoints.capacity(); i++){
            userInputFloatingPoints.add(new Apfloat(userInputs.get(i)));
        }
        return userInputFloatingPoints;
    }






}
