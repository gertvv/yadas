package gov.lanl.yadas;

/**
 * Density function shaped like the beta but with support [0,g].  
 * The four arguments are the data, the two beta parameters, and the 
 * scale parameter g.  
 * @author tgraves 8.22.02.  
 */
public class ScaledBeta implements Likelihood {
    public double compute (double[][] args) {
	double[] data = args[0];
	double[] a = args[1];
	double[] b  = args[2];
	double[] g = args[3];
	double ss = 0;
	for (int i = 0; i < data.length; i++) {
	    /*
	      probably the below will just make things slower typically
	    */
	    if ( (a[i] <= 0) || (b[i] <= 0) || (data[i] <= 0) || 
		 (data[i] >= g[i]) ) 
		return java.lang.Double.NEGATIVE_INFINITY;
	    /*
	     */
	    if (data[i] > 0) 
		ss += Tools.loggamma (a[i] + b[i]) - Tools.loggamma (a[i]) - 
		    Tools.loggamma (b[i]) + (a[i] - 1) * Math.log (data[i]) -
		    a[i] * Math.log(g[i]) + 
		    (b[i] - 1) * Math.log ( 1 - data[i]/g[i] ); 
	}
	return ss;
    }
}
