package gov.lanl.yadas;

/**
 * Actually, this class can be used for defining generalized linear models
 * and handles both continuous and categorical predictor variables.  
 */
public class LinearModelArgument implements ArgumentMaker {

    /*
     * I apologize wholeheartedly for the lack of symmetry between the 
     * interpretations of which_xs and which_cats.  
     * @param which_xs If which_xs[j] = k, then the j (or (j+1)) 
     * element of the 
     * beta covariate vector goes with the kth REAL-VALUED variable in the 
     * DataFrame.  The "(or (j+1))" refers to the case where there is an 
     * intercept.  
     * @param which_cats If which_cats[j] = k, then the j'th INTEGER-VALUED 
     * variable in the data frame goes with the k'th parameter.  
     * @param d is the DataFrame which contains the values of the covariates.
     * @param intcp is 1 if the model contains an intercept, 0 otherwise. 
     * @param is the inverse link function (the linear predictor is run 
     * through this function).   
     */
    public LinearModelArgument (DataFrame d, int intcp, int which_is_beta,
				int[] which_xs, int[] which_cats,
				gov.lanl.yadas.Function f, boolean center) {
	this (d, intcp, which_is_beta, which_xs, which_cats, f);
	if (center) {
	    centerX ();
	}
    }

    public LinearModelArgument (DataFrame d, int intcp, int which_is_beta,
				int[] which_xs, int[] which_cats,
				gov.lanl.yadas.Function f) {
	this.d = d;
	this.intercept = intcp;
	double[][] pre_x = d.getRealvars();
	xcat = d.getIntvars();
	n = d.length();
	which = which_is_beta;
	this.which_xs = which_xs;
	x = new double[which_xs.length][n];
	for (int i = 0; i < which_xs.length; i++) {
	    System.arraycopy (pre_x[which_xs[i]], 0, x[i], 0, n);
	}
	this.which_cats = which_cats;
	this.f = f;
    }

    public LinearModelArgument (DataFrame d, int intcp, int which_is_beta,
				int[] which_xs, int[] which_cats, 
				boolean center) {
	this (d, intcp, which_is_beta, which_xs, which_cats);
	if (center) {
	    centerX ();
	}
    }

    public LinearModelArgument (DataFrame d, int intcp, int which_is_beta,
				int[] which_xs, int[] which_cats) {
	this (d, intcp, which_is_beta, which_xs, which_cats,
	      new Function () { public double f(double[] args) {
		  return args[0]; }});
	nonidentity_link = false;
    }

    public LinearModelArgument (DataFrame d, int intcp, int which_is_beta, 
				int[] which_xs, Function f) {
	this (d, intcp, which_is_beta, which_xs, new int[] {}, f);
    }

    public LinearModelArgument (DataFrame d, int intcp, int which_is_beta, 
				int[] which_xs) {
	this (d, intcp, which_is_beta, which_xs, 
	      new Function () { public double f(double[] args) {
		  return args[0]; }});
	nonidentity_link = false;
    }

    public LinearModelArgument (DataFrame d, int intcp, int which_is_beta) {
	this (d, intcp, which_is_beta, all(d.length()));
    }

    public LinearModelArgument (DataFrame d, int which_is_beta) {
	this (d, 0, which_is_beta);
    }

    void centerX () {
	System.out.println 
	    ("Centering the continuous predictors in the linear model.");
	System.out.println ("Their means were:");
	for (int j = 0; j < which_xs.length; j++) {
	    double tempmean = mean (x[j]);
	    System.out.println ("Variable " + j + "(" + which_xs[j] + ") : "
				+ tempmean);
	    for (int i = 0; i < x[j].length; i++) {
		x[j][i] -= tempmean;
	    }
	}
    }

    public static int[] all (int m) { 
	int[] out = new int[m];
	for (int i = 0; i < m; i++) {
	    out[i] = i;
	}
	return out;
    }

    public double[] getArgument (double[][] params) {
	double[] beta = params[which];
	double[] out = new double[n];
	for (int i = 0; i < n; i++) {
	    if (intercept == 1) {
		out[i] += beta[0];
	    }
	    for (int j = 0; j < which_xs.length; j++) {
		out[i] += beta[j + intercept] * x[j][i];
	    }
	    for (int j = 0; j < which_cats.length; j++) {
		out[i] += params[which_cats[j]][xcat[j][i]];
	    }
	}
	if (nonidentity_link) {
	    for (int i = 0; i < n; i++) {
		out[i] = f.f(new double[] {out[i]});
	    }
	}
	return out;
    }

    public double mean(double[] vec) {
	double out = 0.0;
	for (int i = 0; i < vec.length; i++) {
	    out += vec[i];
	}
	return out / vec.length;
    }

    public double meansquare (double[] vec, double ctr) {
	double out = 0.0;
	for (int i = 0; i < vec.length; i++) {
	    out += (vec[i]-ctr)*(vec[i]-ctr);
	}
	return out / vec.length;
    }

    /* Implements the prior given in Gelman, Jakulin, Pittau & Su, 
       "A default prior distribution for logistic and other regression 
       models", under which the predictors are rescaled to have SD 0.5
       and then given a t or Cauchy prior with common scale
       Note that we have not yet scaled binary variables.  
    */
    public class ScaledByPredictorsArgument implements ArgumentMaker {
	public ScaledByPredictorsArgument 
	    (ArgumentMaker argh) {
	    this.argh = argh;
	    covariateSDs = new double[which_xs.length];
	    covariateSDs[0] = 1.0;
	    for (int i = 0; i < which_xs.length; i++) {
		double[] temp = x[i];
		covariateSDs[i] = 
		    Math.sqrt(meansquare(temp, mean(temp)));
		//System.out.println(covariateSDs[i]);
	    }
	}
	public double[] getArgument (double[][] params) {
	    double[] innerarg = argh.getArgument (params);
	    outarg = new double[innerarg.length];
	    System.arraycopy (innerarg, 0, outarg, 0, innerarg.length);
	    for (int i = 0; i < covariateSDs.length; i++) {
		outarg[i+intercept] /= (covariateSDs[i] * 2.0);
	    }
	    return outarg;
	}
	ArgumentMaker argh;
	double[] covariateSDs, outarg;
    }

    public ScaledByPredictorsArgument argumentScaledByPredictors 
	(ArgumentMaker argh) {
	return new ScaledByPredictorsArgument (argh);
    }

    DataFrame d;    double[][] x;
    int[][] xcat;
    int n;
    int which;
    int[] which_xs;
    int[] which_cats;
    Function f;
    boolean nonidentity_link = true;
    int intercept = 0;
}

