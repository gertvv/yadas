package gov.lanl.yadas;

/** 
 * Beta: computes the beta density function.
 * Parameterized in the most common way: if the three arguments are 
 * denoted by x, a, and b, the mean of x is a/(a+b), for example.  
 * @author TLG
 */
public class Beta extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
		data = args[0][i];
		a = args[1][i];
		b  = args[2][i];
	    if ( (a <= 0) || (b <= 0) || (data <= 0) || (data >= 1) ) 
			return java.lang.Double.NEGATIVE_INFINITY;
		return Tools.loggamma (a + b) - Tools.loggamma (a) - 
		    Tools.loggamma (b) + (a - 1) * Math.log (data) + 
		    (b - 1) * Math.log ( 1 - data ); 
    }

	double data, a, b;
}
