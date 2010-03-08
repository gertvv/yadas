package gov.lanl.yadas;

/**
   This is the prior distribution of sigma, when the precision 1/sigma^2 has 
   a gamma distribution.  The parameterization is as follows.  
   invrootmean is 1 over the square root of the mean of the gamma 
   distribution (i.e. take the mean of the precision, and convert to 
   SD scale).  shape is the shape of the gamma distribution.  Presumably 
   it is nearly the case that the standard deviation of sigma is 
   invrootmean / sqrt(shape).  
   @author tgraves 04/06/07.
 */
public class InverseRootGamma extends AbstractLogDensity {
    public double compute (double[][] args, int i) {
	data = args[0][i];
	invrootmean = args[1][i];
	shape = args[2][i];
	if (data < 0.0)
	    return java.lang.Double.NEGATIVE_INFINITY;
	return cnst + 2.* shape * Math.log(invrootmean) + 
	    shape * Math.log(shape) - Tools.loggamma ( shape ) - 
	    (2.*shape + 1.) * Math.log(data) -  
	    shape*invrootmean*invrootmean/data/data;
    }
    
    static final double cnst = -Math.log(2.);
    double data, invrootmean, shape;
}
