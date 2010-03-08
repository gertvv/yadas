package gov.lanl.yadas;

/**
 * The same as StudentT.  
 */
public class TDistribution implements Likelihood {

    public double compute (double[][] args) {
	double[] data = args[0];
	double[] mean = args[1];
	double[] sd = args[2];
	double[] df = args[3];
	double ssqs =  0;
	for (int i = 0; i < data.length; i++) {
	    ssqs += Tools.loggamma((df[i]+1)/2) - Tools.loggamma(df[i]/2) - 
		Math.log(sd[i]) - Math.log(df[i])/2 - 
		((df[i]+1)/2) * Math.log(1 + (data[i]-mean[i]) * 
					 (data[i]-mean[i]) / sd[i] / sd[i]/
					 df[i]);
	}
	return ssqs;
    }

}
