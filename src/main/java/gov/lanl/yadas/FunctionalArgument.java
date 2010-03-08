package gov.lanl.yadas;

/**
 * Used for defining constructs in statistical models where the 
 * argument to a likelihood function is an arbitrary function of 
 * unknown parameters.  
 */
public class FunctionalArgument implements ArgumentMaker {
    /**
     * @param arglength is the length of the argument that this 
     * class is intended to generate.  
     * @param numparams is the number of parameters used in the bond 
     * that includes this Argument.
     * @param whichexpand is a vector of integers indicating which of 
     * the parameters need to be "expanded" into a longer list of values.
     * @param expanders is a two-dimensional array of integers, i.e. an
     * array of expanders, each of which is an array of integers.  
     * For example, suppose that we have two parameters, mu and gamma, 
     * in the bond, their lengths are 2 and 1 respectively, and we want 
     * to construct an argument {|mu0|gamma, |mu0|gamma, |mu0|gamma, 
     * |mu1|gamma}.  Then we would need to expand both parameters, so 
     * that whichexpand is {0, 1}; the expander for mu is {0,0,0,1}, 
     * the expander for gamma is {0,0,0,0}, and the function takes the 
     * absolute value of mu and multiplies it by gamma.  
     * In S or R terminology, 
     * pretending for the moment that arrays in S/R begin with index 
     * zero as they do in Java, we could write
     * argument <- abs(mu[c(0,0,0,1)]) * gamma[c(0,0,0,0)].  
     * @see gov.lanl.yadas.Function
     */
    public FunctionalArgument (int arglength, int numparams, 
			       int[] whichexpand, int[][] expanders, 
			       Function f) {
	this.arglength = arglength;
	this.numparams = numparams;
	this.whichexpand = whichexpand;
	this.expanders = expanders;
	this.f = f;
	expandcodes = new int[numparams];
	for (int i = 0; i < numparams; i++) {
	    expandcodes[i] = -1;
	}
	for (int i = 0; i < whichexpand.length; i++) {
	    expandcodes[whichexpand[i]] = i;
	}
    } 			       

    public double[] getArgument (double[][] params) {
	double[] out = new double[arglength];
	double[] x = new double[numparams];
	for (int j = 0; j < arglength; j++) {
	    for (int i = 0; i < x.length; i++) {
		if (expandcodes[i] < 0) { 
		    if (params[i].length > j) {
			x[i] = params[i][j];
		    } else {
			// default expander
			x[i] = params[i][0];
		    }
		} else {
		    x[i] = params[i][expanders[expandcodes[i]][j]];
		}
	    }
	    out[j] = f.f(x);
	}
	//System.out.println(out[0]);
	return out;
    }

    private int arglength; 	    
    private int numparams;
    private int[] whichexpand;
    private int[][] expanders;
    private Function f;    
    private int[] expandcodes;
}
/*  old version before default expander
    public double[] getArgument (double[][] params) {
	double[] out = new double[arglength];
	double[] x = new double[params.length];
	for (int j = 0; j < arglength; j++) {
	    for (int i = 0; i < x.length; i++) {
		if (expandcodes[i] < 0) {
		    x[i] = params[i][j];
		} else {
		    x[i] = params[i][expanders[expandcodes[i]][j]];
		}
	    }
	    out[j] = f.f(x);
	}
	//System.out.println(out[0]);
	return out;
    }
*/

