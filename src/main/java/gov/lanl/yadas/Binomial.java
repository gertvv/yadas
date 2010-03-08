package gov.lanl.yadas;

/** 
 * Binomial: computes the log of the binomial probability density function. 
 * x is i'th number of successes, n is the i'th sample size, 
 * and p is the i'th success probability.   
 * @author TLG
 */
public class Binomial extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
	x = args[0][i];
	n = args[1][i];
	p = args[2][i];
	ss = 0.0;
	ss += Tools.loggamma (n + 1) - Tools.loggamma (n - x + 1) 
	    - Tools.loggamma (x + 1);
	if (x > 0) 
	    ss += x * Math.log (p);
	if (n > x) 
	    ss += (n - x) * Math.log (1 - p);
	return ss;
    }

    double x, n, p, ss;
}
