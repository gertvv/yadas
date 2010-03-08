package gov.lanl.yadas;

/**
 * Weibull log probability density function.  The arguments are the 
 * data, the scale parameter, and the "index" parameter.
 */
public class Weibull extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
	data = args[0][i];
        scale = args[1][i];
	index = args[2][i];
	return  Math.log(index/scale) + (index-1) * Math.log(data/scale) - 
	    Math.pow (data/scale, index);
    }
    double data, scale, index;
}
