package gov.lanl.yadas;

/** 
 * Log of the multinomial density function.  Assumes there is a single sample from a 
 * single probability vector, and the arguments are the vector of counts and the 
 * vector of probabilities.  
 */
public class Multinomial implements Likelihood {
    
    public double compute (double[][] args) {
	double[] n = args[0];
	double[] p = args[1];
	double out = 0;
	double sumn = 0;
	for (int i = 0; i < n.length; i++) {
	    sumn += n[i];
	}
	out = Tools.loggamma(sumn + 1);
	for (int i = 0; i < p.length; i++) {
	    out -= Tools.loggamma(n[i] + 1);
	    if (n[i] > 0) {
		out += n[i] * Math.log(p[i]);
	    }
	}
	return out;
    }
    
}
