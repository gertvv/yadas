package gov.lanl.yadas;

/** 
 * Class for taking two or more parameters (a and b, say) and interleaving them
 * to generate an argument (a[0], b[0], a[1], b[1],...).  Useful when, for example, 
 * the (a[i], b[i]) are exchangeably multivariate normal, in which case a class 
 * like MultivariateNormalCov can be used.  
 */
public class InterleaveArgument implements ArgumentMaker {

    public InterleaveArgument (int[] which) {
	this.which = which;
	n = which.length;
    }

    public double[] getArgument (double[][] params) {
		int k = 0;
		out = new double[params[which[0]].length * n];
		for (int i = 0; i < params[which[0]].length; i++) {
			for (int j = 0; j < n; j++) {
				out[k++] = params[which[j]][i];
			}
		}
		return out;
    }

    int[] which;
	int n;
	double[] out;
}
