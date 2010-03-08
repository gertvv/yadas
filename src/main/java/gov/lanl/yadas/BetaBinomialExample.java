package gov.lanl.yadas;
import java.util.*;

/** 
 * BetaBinomialExample: 
 * this example was taken from "Understanding the Gibbs Sampler"
 * y has a beta prior distribution,  
 * while conditional on y, x is Binomial (n, y).  
 * We wish to sample from the joint distribution of (y, x) using MCMC.  
 * This example illustrates the FiniteUpdate class, 
 * which can be used to update a parameter that takes on 
 * a finite number of values 0, 1, ..., n-1.
 * (The class should also be used in the event that the parameter takes 
 * on a finite number of values even if they are not 0, 1, ..., n-1, 
 * because the parameter with support {0, 1, ..., n-1} can be used as 
 * a subscripting vector.)
 * @see gov.lanl.yadas.FiniteUpdate
 * @author TLG
 */
public class BetaBinomialExample {

    public static void main (String[] args) {

	int B = 1000;
	String direc = "";
	String filename = "scalars.dat";
	
	try {
	    if (args.length > 0)
		B = Integer.parseInt(args[0]);
	    if (args.length > 1) 
		direc = args[1];
	    if (args.length > 2)
		filename = args[2];
	}
	catch (NumberFormatException e) {
	    System.out.println("Poor argument list!" + e);
	}
	
	DataFrame d = new DataFrame (direc + filename);

	// define MCMCParameters.  

	MCMCParameter x, y;
	
	// the step size for x (1.0) given below is meaningless, because 
	// x will be updated using FiniteUpdate instead of the default mechanism.  
	// Still, in such cases one should not set the step size to zero
	// because in the future it may be impossible to change the 
	// values of such parameters.

	MCMCParameter[] paramarray = new MCMCParameter[] 
	{ 
	    x = new MCMCParameter ( d.r("x"), d.r(1.0), direc + "x"),
	    y = new MCMCParameter ( d.r("y"), d.r("ymss"), direc + "y"),
	};

	MCMCBond betabond, binomialbond;

	ArrayList bondlist = new ArrayList ();

	// y ~ Beta(a,b) and x ~ Binomial (n, y).  

	bondlist.add ( betabond = new BasicMCMCBond 
	    ( new MCMCParameter[] { y }, 
	      new ArgumentMaker[] {
		  new IdentityArgument (0),
		  new ConstantArgument (d.r("alpha")),
		  new ConstantArgument (d.r("beta")) },
	      new Beta () ));

	bondlist.add ( binomialbond = new BasicMCMCBond 
	    ( new MCMCParameter[] { x, y }, 
	      new ArgumentMaker[] {
		  new IdentityArgument (0),
		  new ConstantArgument (d.r("n")),
		  new IdentityArgument (1) },
	      new Binomial () ));

	/* 
	   define MCMCUpdates:
	*/

	/* 
	   y is updated in the usual random walk Metropolis style, while 
	   x is updated using a FiniteUpdate.  The second argument to the 
	   FiniteUpdate constructor is an array of integers that indicate 
	   the number of values each element in the parameter can take on.  
	   In this case, x has a single element, so this array does as well.  
	   Note that the input file has two columns containing the value 
	   of n, one as a real number and one as an integer. 
	*/

	MCMCUpdate[] updatearray = new MCMCUpdate[] {
	    y, 
	    new FiniteUpdate (x, d.i("ni")), 
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
