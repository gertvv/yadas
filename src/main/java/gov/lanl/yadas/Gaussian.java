package gov.lanl.yadas;

// note has been changed to deal in standard deviations!!!

/** 
 * Gaussian log density function, parameterized by its mean and standard deviation.  
 * Can be used as a model for writing one's own classes that implement LogDensity.  
 */
public class Gaussian extends AbstractLogDensity {
    
    public double compute (double[][] args, int i) {
	data = args[0][i];
	mean = args[1][i];
	sd = args[2][i];
	return -(Math.pow((data - mean) / sd, 2.0)) / 2.0 - Math.log(sd) + cst;
    }
    
    double data, mean, sd;
    static final double cst = -Math.log(2. * Math.PI) / 2.;
}
