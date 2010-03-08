package gov.lanl.yadas;

/**
 * This class constructs arguments to likelihood functions when the 
 * parameter vector is short, and several data points have the same 
 * mean, for example.  A typical example is one-way anova, when 
 * Y_{i,j} is normal with mean mu_i and standard deviation sigma.  
 * The Likelihood classes in YADAS generally require their arguments 
 * to be rectangular arrays, so that to specify this model, one needs 
 * to make as many copies of mu_i as ther are Y_{i,j}'s (for each i), 
 * and one needs to make as many copies of sigma as there are data points.  
 * If mu has length three, and there are respectively three, one, and four 
 * data points corresponding to these three means, the appropriate expander 
 * is {0,0,0,1,2,2,2,2}, recalling that arrays in Java start at index zero.  
 * The appropriate expander for sigma is {0,0,0,0,0,0,0,0}.  One way to 
 * see this is to think in S or R: the Y vector would have mean 
 * mu[c(0,0,0,1,2,2,2,2)] if arrays started at index zero,
 * and standard deviation sigma[c(0,0,0,0,0,0,0,0)].  
 */
public class GroupArgument implements ArgumentMaker {

    /**
     * @param which is an index indicating which of the parameters 
     * is to be expanded to make this argument.  
     */
    public GroupArgument (int which, int[] expander) {
	this.which = which;
	this.expander = expander;
	out = new double[expander.length];
    }

    public double[] getArgument (double[][] params) {
	for (int i = 0; i < out.length; i++) {
	    out[i] = params[which][expander[i]];
	}
	return out;
    }

    private int which;
    private int[] expander;
    private double[] out;
}
