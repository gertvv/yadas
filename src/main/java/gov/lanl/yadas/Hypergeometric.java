package gov.lanl.yadas;

/**  
 * The log of the hypergeometric probability function.  
 * The function takes four arguments: y, n, N, and C.  
 * The way this is parameterized: out of a lot with N items, C of which 
 * have features, we select a sample of size n, and obtain y features.  
 * @author tgraves 1/27/03
 */
public class Hypergeometric extends AbstractLogDensity {
// 
    public double compute (double[][] args, int i) {
		y = args[0][i];
		n = args[1][i];
		N = args[2][i];
		C = args[3][i];
		return logchoose (n, y, -1) + 
			logchoose(N-n, C-y, -1) - 
			logchoose (N, C, 1);
    }
    
    public static double logchoose (double n, double x, int direction) {
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
	
	double y, n, N, C;
}
