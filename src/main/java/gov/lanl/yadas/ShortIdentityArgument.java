package gov.lanl.yadas;

/**
 * ArgumentMaker that extracts a single element of a parameter.  
 * Used as a helper in SystemBinomialBonds.
 * @see gov.lanl.yadas.SystemBinomialBonds
 */
public class ShortIdentityArgument implements ArgumentMaker {

    /**
     * @param i is the indicator of which MCMCParameter we are to use
     * @param j indicates which element of that parameter we use.  
     */
    public ShortIdentityArgument (int i, int j) {
	which = i;
	which2 = j; 
    }

    // eventually allow the second argument to be an array

    public double[] getArgument (double[][] params) {
	return new double[] { params[which][which2] };
    }

    private int which;
    private int which2;
}
