package gov.lanl.yadas;

/**
 * An ArgumentMaker that extracts a single element of a constant vector.  
 * Used as a helper in SystemBinomialBonds.  
 * @see gov.lanl.yadas.SystemBinomialBonds
 */
public class ShortConstantArgument implements ArgumentMaker {

    public ShortConstantArgument (double[] x, int j) {
	arg = new double[x.length];
	System.arraycopy (x, 0, arg, 0, x.length);
	which2 = j;
    }

    public double[] getArgument (double[][] params) {
	return new double[] { arg[which2] };
    }

    private double[] arg;
    private int which2;
}
