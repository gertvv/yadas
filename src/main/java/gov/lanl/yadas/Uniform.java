package gov.lanl.yadas;

/**
 * Uniform probability density function.  The three arguments are the 
 * data points, the lower limits, and the upper limits.  
 */
public class Uniform extends AbstractLogDensity {

    public double compute (double[][] args, int i) {
	data = args[0][i];
	lower = args[1][i];
	upper = args[2][i];
	if ((data > lower) && (data < upper)) {
	    return -Math.log(upper-lower);
	} else {
	    return java.lang.Double.NEGATIVE_INFINITY;
	}
    }
    double data, lower, upper;
}
