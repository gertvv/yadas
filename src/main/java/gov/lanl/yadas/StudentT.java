package gov.lanl.yadas;

/**
 * The Student t density function.  The four arguments are the data, 
 * the mean, the scale parameter, and the number of degrees of freedom.  
 */
public class StudentT extends AbstractLogDensity {

    public double compute (double[][] args, int i) {
	data =	args[0][i];
	mean = args[1][i];
	sd = args[2][i];
	df = args[3][i];
	return cnst + Tools.loggamma((df+1.)/2.) - Tools.loggamma(df/2.) - 
	    Math.log(sd) - Math.log(df)/2. - ((df+1.)/2.) * 
	    Math.log(1. + (data-mean)/sd * (data-mean)/sd /df);
    }
    double data, mean, sd, df;
    static final double cnst = -0.5 * Math.log(Math.PI);
}
