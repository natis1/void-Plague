import org.apfloat.Apfloat;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public class SIRThread extends Thread {
    protected Apfloat susceptible;
    protected Apfloat infected;

    protected Apfloat recovered  = new Apfloat(0);
    protected Apfloat timestep = new Apfloat(0);

    protected Apfloat timerate;
    protected Apfloat infectionRate;
    protected Apfloat recoveryRate;
    protected Apfloat maxInfected = new Apfloat(0);
    protected Apfloat maxInfectedTimestep = new Apfloat(0);

    public boolean didComplete = false;
    public boolean removeMe = false;

    protected long x = 0;
    protected long y = 0;

    private CountDownLatch countDownLatch = new CountDownLatch(1);



    protected SIRThread (Apfloat susceptible, Apfloat infected, Apfloat infectionRate, Apfloat recoveryRate, Apfloat timerate, long x, long y){
        this.susceptible = susceptible.divide((susceptible.add(infected)));
        this.infected = infected.divide((susceptible.add(infected)));
        this.infectionRate = infectionRate.multiply(timerate);
        this.recoveryRate = recoveryRate.multiply(timerate);
        this.timerate = timerate;
        this.x = x;
        this.y = y;



    }

    public Vector<Apfloat> returnGeneratedValues() {
        Vector<Apfloat> values = new Vector<Apfloat>(6);
        values.add(this.susceptible);
        values.add(this.infected);
        values.add(this.recovered);
        values.add(this.maxInfected);
        values.add(this.maxInfectedTimestep);
        values.add(this.timestep);
        return values;
    }

    private void advanceOneTimestep () {

        recovered = recovered.add( (infected.multiply(recoveryRate).multiply(timerate)) );
        infected = infected.add( (susceptible.multiply(infected.multiply(infectionRate))
                .subtract(infected.multiply(recoveryRate))).multiply(timerate) );
        susceptible = (new Apfloat(1)).subtract(recovered).subtract(infected);

        if (infected.doubleValue() > maxInfected.doubleValue()){
            maxInfected = infected;
            maxInfectedTimestep = timestep;
        }


        timestep = timestep.add(timerate);
        if (timestep.longValue() > 300){
            countDownLatch.countDown();
            didComplete = true;
        }
    }



    @Override
    public void run() {

        long startTime = System.nanoTime();
        while (!didComplete){
            advanceOneTimestep();

        }
        System.out.println("Time taken: " + (System.nanoTime() - startTime) + " ns");

    }



}