package gov.lanl.yadas;

// note has been changed to deal in standard deviations!!!

/** 
 * The log of the lognormal density function.  
 * The first argument is the 
 * data, the second argument is the mean of the logged data, 
 * and the third argument is 
 * the STANDARD DEVIATION of the logged data.  
 */
public class Lognormal implements Likelihood {

    public double compute (double[][] args) {
	double[] data = args[0];
	double[] mean = args[1];
	double[] sd = args[2];
	double ssqs =  0;
	for (int i = 0; i < data.length; i++) {
		if (data[i] < 0.0) return java.lang.Double.NEGATIVE_INFINITY;
	    ssqs += - (Math.pow((Math.log(data[i]) - mean[i]) / sd[i], 2.0)) / 2.0 
		- Math.log(sd[i]*data[i]);
	}
	return ssqs;
    }

}
