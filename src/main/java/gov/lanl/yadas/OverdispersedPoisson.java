package gov.lanl.yadas;

/**
 * Exactly the same as NegativeBinomial.  
 * gamma mixture of Poisson's, as in McCullagh and Nelder p. 199
 * note that the variance is (1 + phi)/phi times the mean.  
 */
public class OverdispersedPoisson implements Likelihood {
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
