package gov.lanl.yadas;

/**
 * The Dirichlet (log) probability density function.  The first argument 
 * p is the array of probabilities sampled from the Dirichlet distribution.  
 * The second argument alpha is the array of exponents.  Thus, this function  
 * computes the sum of alpha[i] * log p[i] plus the normalizing constant.  
 */
public class Dirichlet implements Likelihood {

    // should probably check to make sure probabilities are nonnegative,
    // and they sum to 1 etc.  

    // two arguments: the probabilities and the exponents in the "prior"

    public double compute (double[][] args) {
	double[] probs = args[0];
	double[] exponents = args[1];
	double out = 0;
	double sumnu = 0;
	for (int i = 0; i < exponents.length; i++) {
	    sumnu += exponents[i];
	}
	out = Tools.loggamma(sumnu);
	for (int i = 0; i < probs.length; i++) {
	    out -= Tools.loggamma(exponents[i]);
	    if (exponents[i] > 0) {
		out += (exponents[i] - 1) * Math.log(probs[i]);
	    }
	}
	return out;
    }

	public static double[] rep ( double r, int n ) {
		double[] out = new double[n];
		for (int i = 0; i < n; i++) {
			out[i] = r;
		}
		return out;
	}

	public static void main (String[] args) {
		int n = 10;
		try { 
			if (args.length > 0) 
					n = Integer.parseInt(args[0]); } 
		catch (NumberFormatException e) {
			System.out.println ("first argument should be an integer");
		}
		Dirichlet di = new Dirichlet ();
		System.out.println (di.compute( new double[][] { {.4,.3,.2,.1}, 
			{10,.1,.1,.1} }));
	}

}
