package gov.lanl.yadas;

/**
 * One can define a parameter that can take on only integer values, and 
 * update it using something akin to random walk Metropolis.  
 * Another approach is to use the FiniteUpdate class.  
 * If the current value of the parameter is x, the proposed new value is 
 * obtained by letting Z ~ N(0,1), 
 * x = x + sgn(sZ) * (1 + floor(abs(sZ))), where s>0 is a step size.  
 */ 
public class IntegerMCMCParameter extends MCMCParameter {

    public IntegerMCMCParameter (double[] v, double[] mss, String name)
    {
	super (v, mss, name);
    }

    public double[] candidate() {
	double[] temp = new double[1];
	double step = MetropolisStepSize[whoseTurn] * rand.nextGaussian();
	step = sgn(step) * (1.0 + (int)(Math.abs(step)));
	temp[0] = value[whoseTurn] + step;
	return temp;
    }

    public double sgn (double x) {
	return (x==0.0 ? 0.0 : x/Math.abs(x));
    }
    
}
