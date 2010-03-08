package gov.lanl.yadas;

/**
 * Probability function for biased sampling from a finite population.  
 * The five arguments are the data y, the sample size n, the population 
 * size N, the number of special units C, and the bias parameter (log odds 
 * ratio) theta.  
 * the way this is parameterized: out of a lot with N items, C of which 
 * have features, we select a sample of size n, and obtain y features.  
 * theta is the log odds ratio.  
 * @author tgraves 1/28/03
 */
public class TiltedHypergeometric implements Likelihood {
// 
    public double compute (double[][] args) {
	double[] y = args[0];
	double[] n = args[1];
	double[] N = args[2];
	double[] C = args[3];
	double[] theta = args[4];
	double ss =  0;
	for (int i = 0; i < y.length; i++) {
	    double r1 = logchoose (n[i], y[i], -1);
	    double r2 = logchoose (N[i]-n[i], C[i]-y[i], -1);
	    double temp = 0;
	    for (int k = ((int)Math.max(0, n[i] - N[i] + C[i]));
		 k <= Math.min(n[i], C[i]); k++) {
		temp += Math.exp(logchoose(n[i], k, -1) - r1 + 
				 logchoose(N[i]-n[i], C[i]-k, -1) - r2 + 
				 theta[i] * (k - y[i]));
	    }
	    ss += 1.0 / temp; 
	    /*
	    ss += logchoose (C[i], y[i], -1) + 
		logchoose(N[i]-C[i], n[i]-y[i], -1) - 
		logchoose (N[i], n[i], 1);
	    */
	    //System.out.println("C = " + C[i] + ", ss = " + ss);
	}
	//System.out.println("To reiterate, ss = " + ss);
	return Math.log(ss);
    }
    
    public double logchoose (double n, double x, int direction) {
	if ((x>n) || (x<0) || (n<0)) {
	    if (direction < 0) {
		return java.lang.Double.NEGATIVE_INFINITY;
	    } else {
		return java.lang.Double.POSITIVE_INFINITY;
	    }
	} else if ((x==0) || (x==n)) {
	    return 0.0; }
	else {
	    return (Tools.loggamma (n+1) - Tools.loggamma(x+1) - 
		    Tools.loggamma (n-x+1));
	}
    }
}
