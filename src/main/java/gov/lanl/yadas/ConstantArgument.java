package gov.lanl.yadas;

/** 
 * ConstantArgument: YADAS's approach to sending an array of constants 
 * to a term in a posterior distribution.  
 * @author TLG
 */
public class ConstantArgument implements ArgumentMaker {

    /**
     * Makes an array of length one.  
     */
    public ConstantArgument (double x) {
	arg = new double[] {x};
    }

    /**
     * Makes an array of n identical values, all equal to x.
     */
    public ConstantArgument (double x, int n) {
	arg = new double[n];
	for (int i = 0; i < n; i++) {
	    arg[i] = x;
	}
    }

    /* 
     * Makes an array equal to the array x.  
     */
    public ConstantArgument (double[] x) {
	arg = x;
    }

    public double[] getArgument (double[][] params) {
	return arg;
    }

    private double[] arg;

}
