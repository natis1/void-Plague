public class Main {

    public static void main(String[] args) {

        int threadsToUse = 0;

        if (args.length > 0){
            if (args[0] == "-t" || args[0] == "-T") {
                if (args.length > 1){
                    new DiseaseInputParser(Integer.parseInt(args[1]));
                } else {
                    System.exit(22); //22 is invalid arg
                }
            } else {
                System.exit(22); //22 is invalid arg
            }


        } else {
            new DiseaseInputParser();
        }



	// write your code here
    }
}
