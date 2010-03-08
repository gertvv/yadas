package gov.lanl.yadas;

/**
 * The log of the gamma density function.  
 * The first argument x is the data, the second 
 * argument alpha is the shape parameter, and the third argument beta is 
 * the scale parameter, so the expected value of x is alpha * beta. 
 */
public class Gamma extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
		data = args[0][i];
		shape = args[1][i];
		scale = args[2][i];
		if ((data > 0.0) && (shape > 0.0) && (scale > 0.0)) 
			return - shape * Math.log(scale) - 
				Tools.loggamma ( shape ) +
				(shape - 1) * Math.log(data) - data / scale;
		return java.lang.Double.NEGATIVE_INFINITY;
    }
	
	double data, shape, scale; 
}
