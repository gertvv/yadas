package gov.lanl.yadas;
import java.util.*;

/**
 * One of the most important classes in YADAS, MultipleParameterUpdate 
 * allows users to define Metropolis-Hastings moves to multiple parameters 
 * simultaneously.  Since YADAS already contains the machinery necessary 
 * for calculating ratios of posterior distributions, the user needs only 
 * to define a "Perturber", which generates a candidate new value of the 
 * parameters, and which also knows how to compute its "Hastings ratio", 
 * the ratio of candidate generating densities T(x, x')/T(x', x).  
 * @see gov.lanl.yadas.Perturber
 */
public class MultipleParameterUpdate implements MCMCUpdate {

    /**
     * @param params is the array of MCMCParameters that will potentially 
     * be perturbed by this update.
     * @param perturber will contain the information about how to generate 
     * the candidate.
     */
    public MultipleParameterUpdate (MCMCParameter[] params, 
				    Perturber perturber) {
	if (!warned) {
	    System.out.println("Be sure to double check that you are not " +
			       "trying to update \nany constant parameters.  "+
			       "MultipleParameterUpdate will let you do it.");
	    warned = true;
	}
	this.params = params;
	this.perturber = perturber;
	numTurns = perturber.numTurns ();
	accs = new int[numTurns];
	for (int i = 0; i < numTurns; i++) {
	    accs[i] = 0;
	}
	n = params.length;
	candarray = new double[n][];
	len = new int[n];
	for (int i = 0; i < n; i++) {
	    len[i] = params[i].length();
	    candarray[i] = new double[len[i]];
	}
	Set bondset = new HashSet();
	MCMCBond[] b1;
	for (int ii = 0; ii < n; ii++) {
	    b1 = params[ii].relevantBonds ();
	    for (int i = 0; i < b1.length; i++) {
		bondset.add(b1[i]);
	    }
	}
	MCMCBond[] b = new MCMCBond[bondset.size()];
	int jj = 0;
	for (Iterator x = bondset.iterator(); x.hasNext();) {
	    b[jj++] = (MCMCBond) x.next();
	}
	bonds = b;

	// now go through the bonds to see where the parameters lie
	// whatami[j][i]: parameter i plays this role in bond j.  
	// If parameter i has nothing to do with bond j, -1 or something < 0.

	whatami = new int[bonds.length][];
	num = new int[bonds.length];
	for (int j = 0; j < bonds.length; j++) {
	    num[j] = 0;
	    whatami[j] = new int[n];
	    ArrayList al = b[j].getParamList();
	    for (int i = 0; i < n; i++) {
		boolean contains1 = al.contains(params[i]);
		if (contains1) {
		    whatami[j][i] = al.indexOf(params[i]);
		    num[j]++;
		}
		else {
		    whatami[j][i] = -1;
		}
	    }
	}
    }		
    
    public void update () {
	// assume user is smart enough not to try to update constant parameters
	// if (intcp.isConstant || slope.isConstant) { return; }
	while (whoseTurn < numTurns) {
	    double candidate = candidate()[0]; // unnecessary kludge
	    double ap = acceptanceProbability ();
	    //System.out.println("MultipleParameterUpdate with " + bonds.length
	    //	       + " bonds and a.p. " + ap);
	    if (ap > rand.nextFloat()) {
		takeStep();
	    }
	    whoseTurn++;
	}
	whoseTurn = 0;
    }
    
    public double[] candidate () {
	for (int i = 0; i < params.length; i++) {
	    System.arraycopy (params[i].value, 0, candarray[i], 0, len[i]);
	    //System.out.println(i + " " + params[i]);
	}	
	perturber.perturb(candarray, whoseTurn);
	/*
	for (int i = 0; i < candarray.length; i++) {
	    System.out.println(i + ": " + candarray[i][0]);
	}
	*/
	return new double[] {0.0};
    }
    
    public MCMCBond[] relevantBonds () {
	return bonds;
    }
    
    public double acceptanceProbability () {
	double lr = 0;

	// compute(int[] which, double[][] newpar, int[] whatami):
	// the which[i]th component of newpar plays the whatami[i]th role 
	// in the bond, for each i = 0,..., which.length - 1.  

	for (int j = 0; j < bonds.length; j++) {
	    int[] temp = whatami[j];
	    int[] whatamivec = new int[num[j]];
	    int[] whichvec = new int[num[j]];
	    int k = 0;
	    for (int i = 0; i < whatami[j].length; i++) {
		if (whatami[j][i] >= 0) {
		    whichvec[k] = i;
		    whatamivec[k] = whatami[j][i];
		    k++;
		}
	    }
	    //System.out.println(bonds[j].getName()+ " " + bonds[j].compute(whatamivec, candarray, whichvec));
	    lr = lr + bonds[j].compute(whatamivec, candarray, whichvec); 
	}
	return perturber.jacobian() * Math.exp(lr);
    }
    
    public void takeStep () {
	//System.out.println("Took a step. old = " + params[0].getValue()[0] + 
	//		   "; new = " + candarray[0][0]);
	for (int i = 0; i < n; i++) {
	    params[i].setValue(candarray[i]);
	}
	for (int j = 0; j < bonds.length; j++) {
	    bonds[j].revise();
	}
	accs[whoseTurn]++;
    }

    public void ignoreBond (MCMCBond ignorable) {
	Set bondset = new HashSet();
	for (int j = 0; j < bonds.length; j++) {
	    bondset.add(bonds[j]);
	}
	boolean removed = bondset.remove(ignorable); 
	if (!removed) { return; }
	bonds = new MCMCBond[bondset.size()];
	int k = 0;
	for (Iterator iter = bondset.iterator(); iter.hasNext();) {
	    bonds[k++] = (MCMCBond) iter.next();
	}
	
	// I just copied the following code from the constructor.  Presumably 
	// not efficient but it should at least work.  

	whatami = new int[bonds.length][];
	num = new int[bonds.length];
	for (int j = 0; j < bonds.length; j++) {
	    num[j] = 0;
	    whatami[j] = new int[n];
	    ArrayList al = bonds[j].getParamList();
	    for (int i = 0; i < n; i++) {
		boolean contains1 = al.contains(params[i]);
		if (contains1) {
		    whatami[j][i] = al.indexOf(params[i]);
		    num[j]++;
		}
		else {
		    whatami[j][i] = -1;
		}
	    }
	}
	System.out.println("Update now considers " + bonds.length + " bonds");
    } 
    
    public String accepted () {
	String ac = "";
	for (int i = 0; i < accs.length; i++) {
	    ac = ac + i +  ":" +  accs[i] + " ";
	}
	return ac;
    }

    public void updateoutput () {
    }

    public void finish () {}

    public int[] acceptances () {
	return accs;
    }

    MCMCParameter[] params;
    Perturber perturber;

    private int whoseTurn = 0;
    private int numTurns;
    int n; // params.length
    int[] len; // params[i].length
    double[][] candarray;
    MCMCBond[] bonds;
    private int[] num; // number of parameters in each bond
    private int[][] whatami;
    static Random rand = new Random(System.currentTimeMillis());
    int[] accs;
    static boolean warned = false;
}

