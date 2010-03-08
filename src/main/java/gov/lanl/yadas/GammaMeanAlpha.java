package gov.lanl.yadas;

/**
 * Gamma density, parameterized by its mean and shape parameter ("alpha"). 
 * May be useful in gamma hierarchical models.   
 */
public class GammaMeanAlpha extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
	data = args[0][i];
	shape = args[2][i];
	scale = args[1][i] / shape;
	if ((data > 0.0) && (shape > 0.0) && (scale > 0.0)) 
	    return - shape * Math.log(scale) - 
		Tools.loggamma ( shape ) +
		(shape - 1) * Math.log(data) - data / scale;
	return java.lang.Double.NEGATIVE_INFINITY;
    }
    
    double data, shape, scale; 
}
