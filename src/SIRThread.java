import org.apfloat.Apfloat;

import java.util.Vector;

public class SIRThread extends Thread {
    protected Apfloat susceptible;
    protected Apfloat infected;

    protected Apfloat recovered  = new Apfloat(0);
    protected Apfloat timestep = new Apfloat(0);

    protected Apfloat timerate;
    protected Apfloat infectionRate;
    protected Apfloat recoveryRate;
    protected Apfloat maxInfected;
    protected Apfloat maxInfectedTimestep;

    public boolean didComplete = false;
    public boolean removeMe = false;



    protected SIRThread (Apfloat susceptible, Apfloat infected, Apfloat infectionRate, Apfloat recoveryRate, Apfloat timerate){
        this.susceptible = susceptible;
        this.infected = infected;
        this.infectionRate = infectionRate.multiply(timerate);
        this.recoveryRate = recoveryRate.multiply(timerate);
        this.start();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            //This could never possibly happen
            System.out.println("HELPPPPP");
        }

        didComplete = true;
    }

    public Vector<Apfloat> returnGeneratedValues() {
        Vector<Apfloat> values = new Vector<Apfloat>(5);
        values.add(this.susceptible);
        values.add(this.infected);
        values.add(this.recovered);
        values.add(this.maxInfected);
        values.add(this.maxInfectedTimestep);
        return values;
    }

    private void advanceOneTimestep () {




        timestep.add(timerate);
        if (timestep.longValue() > 300){
            this.interrupt();
        }
    }



    @Override
    public void run() {

        advanceOneTimestep();

    }



}