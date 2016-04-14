public class Main {

    public static void main(String[] args) {

        int threadsToUse = 0;

        if (args.length > 0){
            if (args[0].contains("-t")|| args[0].contains("-t")) {
                if (args.length > 1){
                    new DiseaseInputParser(Integer.parseInt(args[1]));
                } else {
                    System.exit(22); //22 is invalid arg
                }
            } else if (args[0].contains("-i")) {
                //TODO parse input file
                String s = args[1];
                new DiseaseInputParser(s);

            } else {
                System.exit(22); //22 is invalid arg
            }
        } else {
            new DiseaseInputParser();
        }



	// write your code here
    }
}
