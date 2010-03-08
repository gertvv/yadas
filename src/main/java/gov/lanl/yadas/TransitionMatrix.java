package gov.lanl.yadas;

/** 
 * When a Markov chain's transition matrix is needed in a term in a 
 * posterior distribution, use this class.  This class takes care of 
 * converting a vector of dim*(dim-1) probabilities into an argument of 
 * length dim*dim by calculating 1 - a sum of probabilities.  
 */
public class TransitionMatrix implements ArgumentMaker {

    public TransitionMatrix (int which, int dim) {
	this.which = which;
	this.dim = dim;
	out = new double[dim * dim];
    }

    public double[] getArgument (double[][] params) {
	int k = 0;
	for (int i = 0; i < dim; i++) {
	    double pii = 1;
	    for (int j = 0; j < (dim-1); j++) {
		//System.out.println(out[(i*dim)+j]);
		out[(i*dim)+j] = params[which][k++];
		pii -= out[(i*dim)+j];
	    }
	    out[(i*dim) + (dim-1)] = pii;
	}
	return out;
    }

    private int which;
    private int dim;
    private double[] out;
}

