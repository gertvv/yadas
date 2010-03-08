package gov.lanl.yadas;

import java.util.*;

/** 
 * BinomialHypothesisTest: runs an MCMC analysis that tests the 
 * equality of two binomial probabilities.  Used to illustrate the 
 * ReversibleJumpUpdate class and the MixtureBond class.  
 * x[i] ~ Binomial(n[i], p[i]) 
 * for i=0,1.  The prior distribution for p is a mixture: with probability 
 * lambda, the two p's are independent Beta with parameters given in the  
 * file p.dat (they do not need to be exchangeable), and with probability 
 * 1-lambda, the two p's are equal, and their common value has a Beta prior 
 * distribution with parameters given in the file scalars.dat.  To analyze 
 * this model, reversible jump MCMC is used.  
 * @see gov.lanl.yadas.ReversibleJumpUpdate
 * @see gov.lanl.yadas.MixtureBond
 * @see gov.lanl.yadas.AreTheyEqualArgument
 * @author TLG
 */
public class BinomialHypothesisTest {

    public static void main (String[] args) {

	int B = 1000;
	String direc = "c:/Java/MCMC/results/BinomialTest/";
	String filename = "p.dat";
	String shortfilename = "scalars.dat";
	
	try {
	    if (args.length > 0)
		B = Integer.parseInt(args[0]);
	    if (args.length > 1) 
		direc = args[1];
	    if (args.length > 2)
		filename = args[2];
	    if (args.length > 3)
		shortfilename = args[3];
	}
	catch (NumberFormatException e) {
	    System.out.println("Poor argument list!" + e);
	}
	
	DataFrame d = new DataFrame (direc + filename);
	DataFrame d0 = new DataFrame (direc + shortfilename);

	// define MCMCParameters.  

	MCMCParameter p, lambda;
	
	MCMCParameter[] paramarray = new MCMCParameter[] 
	{ 
	    p = new MCMCParameter (d.r("p"), d.r(1.0), direc + "p"),
	    lambda = new MCMCParameter (d0.r("lambda"), d0.r("lambdamss"),
					direc + "lambda")
	};

	MCMCBond databond, mixtureprior, lambdaprior;

	ArrayList bondlist = new ArrayList ();

	bondlist.add( databond = new BasicMCMCBond 
	    ( new MCMCParameter[] { p }, 
	      new ArgumentMaker[] {
		  new ConstantArgument (d.r("x")),
		  new ConstantArgument (d.r("n")),
		  new IdentityArgument (0) },
	      new Binomial () ));

	bondlist.add ( mixtureprior = new MixtureBond 
	    ( new MCMCParameter[][] { new MCMCParameter[] { p },
				      new MCMCParameter[] { p } },
	      new ArgumentMaker[][] 
		{ new ArgumentMaker[] { new IdentityArgument (0),
					new ConstantArgument (d.r("ap")),
					new ConstantArgument (d.r("bp")) },
		  new ArgumentMaker[] { new GroupArgument (0, new int[] {0}),
					new ConstantArgument (d0.r("ap")),
					new ConstantArgument (d0.r("bp")) } },
	      new Likelihood[] { new Beta(), new Beta() },
	      new AreTheyEqualArgument (0, d0.r("lambda")[0]) ) );
	/*
	bondlist.add ( lambdaprior = new BasicMCMCBond
	    ( new MCMCParameter[] { lambda },
	      new ArgumentMaker[] {
		  new IdentityArgument (0),
		  new ConstantArgument (d0.r("alambda")),
		  new ConstantArgument (d0.r("blambda")) },
	      new Beta () ));
	*/
	databond.setName("databond");
	mixtureprior.setName("mixtureprior");

	/* 
	   define MCMCUpdates:
	*/

	MCMCUpdate[] updatearray = new MCMCUpdate[] 
	{ 
	    new ReversibleJumpUpdate 
		(new MCMCParameter[] { p }, 2, 0, 
		 new double[] { d0.r("a00")[0], d0.r("a01")[0], d0.r("a10")[0],
				d0.r("a11")[0] },
		 new JumpPerturber[] 
		    { new LogitPerturber (0, d.r("pmss")),
		      new EqualizingPerturber (0, d.r("n")),
		      new SplittingLogitPerturber (0, d.r("psplitmss")),
		      new CommonLogitPerturber (0, d0.r("pmss")[0])},
		 direc),
	};

	for (int b = 0; b < B; b++) {
	    if ((b/1000.0 - (int)(b/1000))== 0) System.out.println(b);
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

