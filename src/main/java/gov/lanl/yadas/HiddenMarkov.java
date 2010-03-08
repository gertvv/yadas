package gov.lanl.yadas;

import java.util.*;

/** 
 * A simple analysis of a data set assumed to come from a hidden Markov 
 * model.  y has mean x and SD sigma, the unobserved x is assumed to be 
 * realization of a Markov chain with transition matrix given by p, 
 * the prior for the elements of p is independent beta, etc.  
 * The elements of x are updated samples from their full conditional 
 * distributions (a FiniteUpdate).  
 * @see gov.lanl.yadas.FiniteMarkovChain
 * @see gov.lanl.yadas.TransitionMatrix
 * @see gov.lanl.yadas.FiniteUpdate
 */ 
public class HiddenMarkov {

    public static void main (String[] args) {

	int B = 1000;
	String direc = "h:/projects/bickel/";
	String filename = "data.dat";
	String filename1 = "p.dat";
	String filename0 = "scalars.dat";

	try {
	    if (args.length > 0)
		B = Integer.parseInt(args[0]);
	    if (args.length > 1) 
		direc = args[1];
	    if (args.length > 2)
		filename = args[2];
	    if (args.length > 3)
		filename1 = args[3];
	    if (args.length > 4)
		filename0 = args[4];
	}
	catch (NumberFormatException e) {
	    System.out.println("Poor argument list!" + e);
	}
	
	DataFrame d = new DataFrame (direc + filename);
	DataFrame d1 = new DataFrame (direc + filename1);
	ScalarFrame d0 = new ScalarFrame (direc + filename0);

	// define MCMCParameters.  

	MCMCParameter x, sigma, p;
	
	MCMCParameter[] paramarray = new MCMCParameter[] 
	{ 
	    x = new MCMCParameter ( d.r(0), d.r(1), direc + "x"),
	    sigma = new MCMCParameter ( d0.r("sigma"), d0.r(0.5), 
					direc + "sigma" ),
	    p = new MCMCParameter ( d1.r("p"), d1.r(0.2), direc + "p"),
	};


	MCMCBond databond, chainbond, pprior, sigmaprior;

	ArrayList bondlist = new ArrayList ();

	bondlist.add ( databond = new BasicMCMCBond 
	    ( new MCMCParameter[] { x, sigma },
	      new ArgumentMaker[] {
		  new ConstantArgument (d.r("y")),
		  new IdentityArgument (0),
		  new GroupArgument (1, d.i(0)) },
	      new Gaussian () ));

	bondlist.add ( chainbond = new BasicMCMCBond 
	    ( new MCMCParameter[] { x, p },
	      new ArgumentMaker[] {
		  new IdentityArgument (0),
		  new TransitionMatrix (1, d1.length()) },
	      new FiniteMarkovChain (d1.length()) ));

	bondlist.add ( pprior = new BasicMCMCBond 
	    ( new MCMCParameter[] { p },
	      new ArgumentMaker[] {
		  new IdentityArgument (0),
		  new ConstantArgument (d1.r("ap")),
		  new ConstantArgument (d1.r("bp")) },
	      new Beta () ));

	bondlist.add ( sigmaprior = new BasicMCMCBond 
	    ( new MCMCParameter[] { sigma },
	      new ArgumentMaker[] {
		  new IdentityArgument (0),
		  new ConstantArgument (d0.r("asigma")),
		  new ConstantArgument (d0.r("bsigma")) },
	      new Gamma () ));

	/* 
	   define MCMCUpdates:
	*/

	MCMCUpdate[] updatearray = new MCMCUpdate[] {
	    sigma, p, 
	    new FiniteUpdate (x, d.i(d1.length())),
	};

	for (int b = 0; b < B; b++) {
	    if ((b/100.0 - (int)(b/100))== 0) System.out.println(b);
	    for (int i = 0; i < updatearray.length; i++) {
		updatearray[i].update ();
	    }
	    for (int i = 0; i < paramarray.length; i++) {
		if ((b/1.0 - (int)(b/1))== 0) paramarray[i].output ();
	    }
	}
      
	String acc;
	for (int iii = 0; iii < updatearray.length; iii++) {
	    acc = updatearray[iii].accepted();
	    System.out.println("Update " + iii + ": " + acc);
	}

	for (int i = 0; i < paramarray.length; i++) {
	    paramarray[i].finish();
	}
    }
}

/* 
  alpha <- rnorm(11)
  temp <- readjava(c("x", "y"), "h:/projects/BayesNets/BetaBinomial")
*/

