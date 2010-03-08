package gov.lanl.yadas;

/**
 * This class is used to take the values of a Parameter and send them 
 * without alteration to a Likelihood.  
 */
public class IdentityArgument implements ArgumentMaker {

    /**
     * @param i is an index demonstrating which of the parameters 
     * in the definition of the BasicMCMCBond is the one to be run 
     * through the identity function.  
     */
    public IdentityArgument (int i) {
	which = i;
    }

    public double[] getArgument (double[][] params) {
	double[] temp = new double[params[which].length];
	System.arraycopy (params[which], 0, temp, 0, temp.length);
	return temp;
    }

    public static IdentityArgument[] IdentityArgumentArray (int j) {
	IdentityArgument[] ia = new IdentityArgument[j];
	for (int i = 0; i < j; i++) {
	    ia[i] = new IdentityArgument (i);
	}
	return ia;
    }

    private int which;
}
