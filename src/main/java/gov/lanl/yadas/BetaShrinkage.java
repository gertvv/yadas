package gov.lanl.yadas;

/**
   Beta shrinkage prior, a generalization of a uniform shrinkage prior 
   (the uniform shrinkage prior arises when a = b = 1, which happens 
   by default for this class, if only two arguments are sent here).  

   When using this class, please cite Cindy Christiansen and Carl Morris,
   JASA 1997, I believe.     
 */
public class BetaShrinkage extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
	zeta = args[0][i];
	z0 = args[1][i];
	if (args.length < 4) {
	    a = b = 1.;
	} else {
	    a = args[2][i];
	    b = args[3][i];
	}
	if (zeta < 0.0)
	    return java.lang.Double.NEGATIVE_INFINITY;
	return Tools.loggamma (a+b) - Tools.loggamma (a) - 
	    Tools.loggamma (b) + b * Math.log(z0) + 
	    (a-1.) * Math.log (zeta) - (a+b) * Math.log (zeta + z0);
    }
    
    static final double cnst = -Math.log(2.);
    double zeta, z0, a, b;
}
