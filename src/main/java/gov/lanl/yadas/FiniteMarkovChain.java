package gov.lanl.yadas;
// does not include any term for P{X0 = k}!!!

/**
 * For simple Bayesian inference on the successive values of a Markov 
 * chain with finite state space.  The first argument should be the 
 * successive data points (whose state space is 0, 1, ... dim - 1).  
 * The second argument should be a vectorized version of the transition 
 * probability matrix in the order P{X1=0|X0=0}, P{X1=1|X0=0}, etc.
 * The likelihood calculation does not include a term for P{X0=k}.  
 */
public class FiniteMarkovChain implements Likelihood {
    /** The constructor defines the set of possible values of the 
     * chain (0, 1, ..., dim - 1).  
     */
    public FiniteMarkovChain ( int dim ) {
	this.dim = dim;
    }
    public double compute (double[][] args) {
	double[] x = args[0];
	double[] p  = args[1];
	double ss = 0;
	for (int i = 1; i < x.length; i++) {
	    //System.out.println(p[((int)x[i-1])*dim + ((int)x[i])]);
	    ss += Math.log ( p[((int)x[i-1])*dim + ((int)x[i]) ] );
	}
	//System.out.println(ss);
	return ss;
    }
    private final int dim;
}
