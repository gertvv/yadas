package gov.lanl.yadas;

/**
 * Three-parameter Weibull log density function.  The four arguments are 
 * the data, the scale parameter, the index parameter, and the 
 * shift (minimum value) parameter.  
 */
public class Weibull3 extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
	data = args[0][i];
	scale = args[1][i];
	index = args[2][i];
	shift = args[3][i];
	if (data > shift) {
	    return Math.log(index/scale) + 
		(index-1) * Math.log((data-shift)/scale) - 
		Math.pow ((data-shift)/scale, index);
	}
	else return java.lang.Double.NEGATIVE_INFINITY;
    }
    double data, scale, shift, index;
}
