package gov.lanl.yadas;

/**
 * Specifies a point mass distribution for some parameter.  
 */
public class PointMass implements Likelihood {

    public PointMass (double value) {
	this.value = value;
    }

    public double compute (double[][] args) {
	double[] data = args[0];
	for (int i = 0; i < data.length; i++) {
	    if ( data[i] != value ) 
		return java.lang.Double.NEGATIVE_INFINITY;
	}
	return 0.0;
    }
    public double compute (double[][] args, int[] indices) {
	double[] data = args[0];
	for (int j = 0; j < indices.length; j++) {
	    int i = indices[j];
	    if (data[i] != value) 
		return java.lang.Double.NEGATIVE_INFINITY;
	}
	return 0.0;
    }

    private double value;
}
