package gov.lanl.yadas;

/** 
 * Log of the Logistic density function: the parameters are the "data" parameter, 
 * its location parameter, and the scale parameter.  
 */
public class Logistic extends AbstractLogDensity {

    public double compute (double[][] args, int i) {
	//data = args[0][i];
	//mean = args[1][i];
	scale = args[2][i];
	//std = (data - mean) / scale;
	std = (args[0][i] - args[1][i]) / scale;
	return std - 2. * Math.log ( 1. + Math.exp (std) ) - Math.log(scale);
    }
    
    double data, mean, scale, std;
}
