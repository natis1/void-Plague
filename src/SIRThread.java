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





    protected Vector<Apfloat> SIRThread (Apfloat susceptible, Apfloat infected, Apfloat infectionRate, Apfloat recoveryRate, Apfloat timerate){
        this.susceptible = susceptible;
        this.infected = infected;
        this.infectionRate = infectionRate;
        this.recoveryRate = recoveryRate;
        this.start();



        Vector<Apfloat> values = new Vector<Apfloat>(5);
        values.add(this.susceptible);
        values.add(this.infected);
        values.add(this.recovered);
    }

    private void advanceOneTimestep () {






        timestep.add(timerate);
        if (timestep.longValue() > 300){
            this.interrupt();
        }
    }



    @Override
    public void run() {






    }



}