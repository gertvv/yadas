package gov.lanl.yadas;

/**
 * Negative binomial density function.  We have used it to define 
 * Poisson distributions with overdispersion.  The result is a
 * gamma mixture of Poissons, as in McCullagh and Nelder p. 199
 * note that the variance is (1 + phi)/phi times the mean.  
 * @author tgraves 4/24/02
 */
public class NegativeBinomial implements Likelihood {
// 
    public double compute (double[][] args) {
	double[] data = args[0];
	double[] mean = args[1];
	double[] phi = args[2];
	double ss =  0;
	for (int i = 0; i < data.length; i++) {
	    if ((mean[i] > 0) && (data[i] >= 0) && (phi[i] > 0)) {
		ss += Tools.loggamma(data[i] + phi[i] * mean[i]) + 
		    phi[i] * mean[i] * Math.log(phi[i]) - 
		    Tools.loggamma (data[i] + 1) - 
		    Tools.loggamma (phi[i] * mean[i]) - 
		    (data[i] + phi[i] * mean[i]) * Math.log(1 + phi[i]);
	    } else {
		return java.lang.Double.NEGATIVE_INFINITY;
	    }
	}
	return ss;
    }
    
}
