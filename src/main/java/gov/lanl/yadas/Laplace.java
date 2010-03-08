package gov.lanl.yadas;

/**
 * The log of the Laplace (double exponential) density function.  
 * The first argument x is the data, the second argument mu is the 
 * median, and the third argument beta is the scale parameter. 
 */
public class Laplace extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
	data = args[0][i];
	mu = args[1][i];
	scale = args[2][i];
	if (scale > 0.0) 
	    return - Math.log(2. * scale) - 
		Math.abs(data-mu) / scale;
	return java.lang.Double.NEGATIVE_INFINITY;
    }
    
    double data, mu, scale; 
}
