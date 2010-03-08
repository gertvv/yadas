package gov.lanl.yadas;

import java.util.*;

/**
 * The class used in system reliability modeling: can handle "any" 
 * system structure where the subsystems are formed by combining 
 * components in series or in parallel, provided that there are no 
 * "common cause failures": that is, the graph must be a tree.  
 * The data file components.dat has one row for each component.  
 * leaves.dat has one row for each leaf node.
 * ptilde.dat and nu.dat describe prior distributions.    
 */
public class GenericSeriesSystem {

    public static void main (String[] args) {

	int B = 1000;
	String direc = "h:/projects/systemreliability/";
	String filename = "components.dat";
	String shortfilename = "leaves.dat";
	String priorfilename = "ptilde.dat";
	String nufilename = "nu.dat";
	
	try {
	    if (args.length > 0)
		B = Integer.parseInt(args[0]);
	    if (args.length > 1) 
		direc = args[1];
	    if (args.length > 2)
		filename = args[2];
	    if (args.length > 3) 
		shortfilename = args[3];
	    if (args.length > 4) 
		priorfilename = args[4];
	    if (args.length > 5) 
		nufilename = args[5];
	}
	catch (NumberFormatException e) {
	    System.out.println("Poor argument list!" + e);
	}
	
	DataFrame d = new DataFrame (direc + filename);
	DataFrame leafd = new DataFrame (direc + shortfilename);
	DataFrame priord = new DataFrame (direc + priorfilename);
	DataFrame nud = new DataFrame(direc + nufilename);

	// define MCMCParameters.  

	MCMCParameter p, ptilde, nu;
	
	MCMCParameter[] paramarray = new MCMCParameter[] 
	{ 
	    p = new LogitMCMCParameter (leafd.r("p"), leafd.r("pmss"), 
					direc + "p"),
	    ptilde = new MCMCParameter (priord.r("ptilde"), priord.r(0), 
					direc + "ptilde"),
	    nu = new MCMCParameter (nud.r("nu"), nud.r("numss"), direc + "nu"),
	};

	ReliableSystem system = new ReliableSystem (d.length());
	system.SeriesIntegratorsFromFile (d.i("parents"));

	MCMCBond binomialdata, p_prior, nu_prior;

	ArrayList bondlist = new ArrayList ();

	bondlist.add( binomialdata = new BasicMCMCBond
	    ( new MCMCParameter[] { p },
	      new ArgumentMaker[] { new ConstantArgument (d.r("x")),
				    new ConstantArgument (d.r("n")),
				    system.fillProbs (d.i("pexpand"),
						      d.i("order")) },
	      new Binomial () ));
	bondlist.add ( p_prior = new BasicMCMCBond 
	    ( new MCMCParameter[] { p, ptilde, nu },
	      new ArgumentMaker[] 
		{ system.fillProbs (d.i("pexpand"), d.i("order")),
		  new FunctionalArgument 
		      (d.length(), 3, new int[] {0, 1, 2},
		       new int[][] { d.i(0), d.i("priorexpand"),
				     d.i("nuexpand") },
		       new Function () 
			   { public double f(double[] args)
			       { return args[1] * args[2] + 1;
			       }}),
		  new FunctionalArgument 
		      (d.length(), 3, new int[] {0, 1, 2},
		       new int[][] { d.i(0), d.i("priorexpand"),
				     d.i("nuexpand") },
		       new Function () 
			   { public double f(double[] args)
			       { return (1-args[1])* args[2] + 1;
			       }}),
		},
	      new Beta() ));
	bondlist.add (nu_prior = new BasicMCMCBond
	    ( new MCMCParameter[] { nu },
	      new ArgumentMaker[] { new IdentityArgument (0),
				    new ConstantArgument (nud.r("anu")),
				    new ConstantArgument (nud.r("bnu")) },
	      new Gamma () ));	    

	p.nf.setMaximumFractionDigits(6);

	binomialdata.setName("binomialdata");
	p_prior.setName("p_prior");
	nu_prior.setName("nu_prior");

	/* 
	   define MCMCUpdates:
	*/

	MCMCUpdate[] updatearray = new MCMCUpdate[] 
	{ 
	    p, nu,
	};

	for (int b = 0; b < B; b++) {
	    if ((b/20.0 - (int)(b/20))== 0) System.out.println(b);
	    for (int i = 0; i < updatearray.length; i++) {
		updatearray[i].update ();
	    }
	    for (int i = 0; i < paramarray.length; i++) {
		paramarray[i].output ();
	    }
	}
      
	for (int i = 0; i < paramarray.length; i++) {
	    paramarray[i].finish();
	}
    }
}

