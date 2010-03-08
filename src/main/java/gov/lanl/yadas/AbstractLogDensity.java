package gov.lanl.yadas;

/**
 * Abstract subclass for all classes that implement the LogDensity interface.  
 * Applies to probability densities that are products of several independent 
 * terms (e.g. y_i has a normal distribution with mean mu_i and SD sigma_i, 
 * for i = 0, 1,...,n-1).  
 */
public abstract class AbstractLogDensity implements LogDensity {

    public double compute (double[][] args) {
	ssqs =  0;
	for (int i = 0; i < args[0].length; i++) {
	    ssqs += compute (args, i); 
	}
	return ssqs;
    }
    
    public abstract double compute (double[][] args, int i);
    
    double ssqs;
}
