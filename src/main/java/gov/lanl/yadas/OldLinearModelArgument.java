package gov.lanl.yadas;

/** 
 * Less intuitive way to specify linear models than LinearModelArgument.
 */
public class OldLinearModelArgument implements ArgumentMaker {

    /**
     * @param which is an array of indices that identify elements of 
     * the parameter array.  They should alternate between regression 
     * coefficients and covariates, so that the 2k'th and (2k+1)st parameters 
     * are multiplied together before being added to the output.  
     * @param expanders is an array that tells how to expand the parameters in 
     * "params" into arrays the same length as the desired output.  
     */
    public OldLinearModelArgument (boolean intcp, int[] which, 
				int[][] expanders) {
      this.intcp = intcp;
	this.which = which;
	this.expanders = expanders;
	out = new double[expanders[0].length];
	J = which.length / 2;
    }

    public double[] getArgument (double[][] params) {
      if (intcp == false) {
	for (int i = 0; i < out.length; i++) {
	  out[i] = 0.0;
	    for (int j = 0; j < J; j++) {
		out[i] += params[which[2*j]][expanders[2*j][i]] * 
		    params[which[2*j+1]][expanders[2*j+1][i]];
	    }
	}
	return out;
      } else {
	for (int i = 0; i < out.length; i++) {
	  out[i] = params[0][expanders[0][i]];
	    for (int j = 0; j < J; j++) {
		out[i] += params[which[2*j+1]][expanders[2*j+1][i]] * 
		    params[which[2*j+2]][expanders[2*j+2][i]];
	    }
	}
	return out;
      }      
    }

  private boolean intcp;
    private int J;
    private int[] which;
    private int[][] expanders;
    private double[] out;
}

/* 
*/
