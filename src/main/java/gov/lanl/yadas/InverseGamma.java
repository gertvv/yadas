package gov.lanl.yadas;

/**
 * Inverse gamma log density function, commonly used as a prior distribution for a 
 * variance.  Parameterization is x^(-shape-1)exp(-x/scale) as in Gelman et al 1995.
 * mean is scale/(shape-1), var is scale^2/((shape-1)^2*(shape-2)).
 * @author hamada 11.30.01 as in  Gelman et al 1995
 * Edited by tgraves to conform with LogDensity interface
 */
public class InverseGamma extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
	data = args[0][i];
	shape = args[1][i];
	scale = args[2][i];
	if (data < 0.0)
	    return java.lang.Double.NEGATIVE_INFINITY;
	return shape * Math.log(scale)  
	    -Tools.loggamma ( shape )
	    -(shape + 1) * Math.log(data) -  scale/data ;
    }
    
    double data, shape, scale;
}
