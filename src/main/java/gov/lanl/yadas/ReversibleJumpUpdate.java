package gov.lanl.yadas;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * For defining steps in reversible jump MCMC algorithms.   
 * This should be rethought sometime; currently there is only one current 
 * state and one next state, etc.  In principle there should be an array 
 * of each so that multivariate parameters with mixture distributions can 
 * be handled with a single instance of ReversibleJumpUpdate rather than 
 * with a whole array of them.  TG, 7/18/02.
 * @see gov.lanl.yadas.JumpPerturber
 */
public class ReversibleJumpUpdate implements MCMCUpdate { 

    public ReversibleJumpUpdate 
	( MCMCParameter[] params, int numstates, int initial_state,
	  double[] transitionprobs, JumpPerturber[] perturberarray) {
	this (params, numstates, initial_state, transitionprobs, 
	      perturberarray, "");
    }
    /**
     * @param params is the array of MCMCParameters that can potentially be 
     * changed by the reversible jump update.  
     * @param numstates is the number of possible megastates that the 
     * parameter vector can belong to
     * @param initial_state indicates which state the algorithm begins in 
     * (the initial values of the parameters should be consistent with this, 
     * since YADAS does not check, and no other update should be capable 
     * of changing this before this update does)
     * @param transitionprobs is an array of length numstates^2 which 
     * will be converted into a transition probability matrix.  The rows 
     * don't have to add to one, but the elements should be nonnegative.  
     * @param perturberarray is an array of length numstates^2 of 
     * objects implementing the JumpPerturber interface.  This should  
     * also be thought of as a matrix, and the [i,j] element tells how 
     * to generate candidate values of the parameters when the 
     * algorithm is currently in state i and the proposed move is to 
     * be into state j.  
     */
    public ReversibleJumpUpdate 
	( MCMCParameter[] params, int numstates, int initial_state,
	  double[] transitionprobs, JumpPerturber[] perturberarray,
	  String direc) {
	
	if (!warned) {
	    System.out.println("Be sure to double check that you are not trying " +
			       "to update \nany constant parameters.  " + 
			       "ReversibleJumpUpdate will let you do it.");
	    warned = true;
	}
	nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(4);
	nf.setGroupingUsed(false);
	this.direc = direc;
	current_state = initial_state;
	previous_state = current_state;
	this.params = params;
	this.numstates = numstates;
	transitionmat = new double[numstates][numstates];
	cdfmat = new double[numstates][numstates];
	int k = 0;
	for (int i = 0; i < numstates; i++) {
	    for (int j = 0; j < numstates; j++) {
		transitionmat[i][j] = transitionprobs[k];
		cdfmat[i][j] = transitionprobs[k++];
	    }
	}
	// convert to cdf form
	for (int i = 0; i < numstates; i++) {
	    for (int j = 0; j < numstates - 1; j++) {
		for (int ell = j + 1; ell < numstates; ell++) {
		    cdfmat[i][ell] += transitionmat[i][j];
		}
	    }
	}
	
	perturbers = new JumpPerturber[numstates][numstates];
	k = 0;
	for (int i = 0; i < numstates; i++) {
	    for (int j = 0; j < numstates; j++) {
		perturbers[i][j] = perturberarray[k++];
	    }
	}

	acceptances = new int[numstates][numstates][];
	attempts = new int[numstates][numstates][];
	for (int i = 0; i < numstates; i++) {
	    for (int j = 0; j < numstates; j++) {
		int n1 = perturbers[i][j].numTurns();
		if ((i != j) && (n1 > 1)) {
		    System.out.println("You may have an ingenious idea, " + 
				       "but JumpPerturbers that change\n" + 
				       "the state have to have numTurns = 1.");
		    System.exit(0);
		}
		acceptances[i][j] = new int[n1];
		attempts[i][j] = new int[n1];
		for (int ell = 0; ell < n1; ell++) {
		    acceptances[i][j][ell] = 0;
		    attempts[i][j][ell] = 0;
		}
	    }
	}

	// code from here to comment 1 was taken from MultipleParameterUpdate,
	// 02/04/02
	n = params.length;
	candarray = new double[n][];
	oldcands = new double[n][];
	len = new int[n];
	for (int i = 0; i < n; i++) {
	    len[i] = params[i].length();
	    candarray[i] = new double[len[i]];
	    oldcands[i] = new double[len[i]];
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
	// comment 1
    }

    public void update () {
	// assume user is smart enough not to try to update constant parameters
	// pick a move.  
	double chooser = rand.nextDouble();
	next_state = numstates;
	while ((next_state > 0) && 
	       (cdfmat[current_state][next_state-1] > chooser)) {
	    next_state--;
	}
	int turns = perturbers[current_state][next_state].numTurns();
	while (whoseTurn < turns) {
	    double candidate = candidate()[0]; // unnecessary kludge
	    double ap = acceptanceProbability ();
	    stringvalue = perturbers[current_state][next_state].toString() + 
		"|" + nf.format(ap);
	    //System.out.println("MultipleParameterUpdate with " + bonds.length
	    //	       + " bonds and a.p. " + ap);
	    attempts[current_state][next_state][whoseTurn]++;
	    if (ap > rand.nextFloat()) {
		takeStep();
	    }
	    whoseTurn++;
	}
	whoseTurn = 0;
    };

    public double[] candidate () {
	for (int i = 0; i < params.length; i++) {
	    System.arraycopy (params[i].value, 0, candarray[i], 0, len[i]);
	    System.arraycopy (params[i].value, 0, oldcands[i], 0, len[i]);
	    //System.out.println(i + " " + params[i]);
	}
	perturbers[current_state][next_state].perturb(candarray, whoseTurn);
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
	    /*
	    System.out.println(bonds[j].getName()+ " " + 
	    bonds[j].compute(whatamivec, candarray, whichvec));
	    */
	    lr = lr + bonds[j].compute(whatamivec, candarray, whichvec); 
	}
	/*
	if ((current_state==1) && (next_state==0)) 
	{
	System.out.println(current_state + " -> " + next_state);
	System.out.println(Math.exp(lr));
	System.out.println(transitionmat[next_state][current_state]);  
	System.out.println(transitionmat[current_state][next_state]); 
	System.out.println(perturbers[current_state][next_state].density 
			   (oldcands, candarray, whoseTurn));  
	System.out.println(perturbers[next_state][current_state].density 
			   (candarray, oldcands, whoseTurn));
	}
	*/
	return Math.exp(lr) * 
	    transitionmat[next_state][current_state] / 
	    transitionmat[current_state][next_state] / 
	    perturbers[current_state][next_state].density 
	    (oldcands, candarray, whoseTurn) *  
	    perturbers[next_state][current_state].density 
	    (candarray, oldcands, whoseTurn);
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
	acceptances[current_state][next_state][whoseTurn]++;	
	current_state = next_state;
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
	// keep track of all the acceptance rates, and print them in 
	// an understandable way
	String ac = "";
	for (int i = 0; i < numstates; i++) {
	    for (int j = 0; j < numstates; j++) {
		ac = ac + i + " -> " + j + ": "; 
		for (int k = 0; k < acceptances[i][j].length; k++) {
		    ac = ac + acceptances[i][j][k] + " / " + 
			attempts[i][j][k] + "; ";
		}
		ac = ac + "\n";
	    }
	}
	return ac;
    };

    public void updateoutput () {
	if (firstupdateoutput) {
	    firstupdateoutput = false;
	    try {
		out = new PrintWriter ( new FileWriter
		    ( direc + "RJU" + numoutputs + ".out") );
	    }
	    catch (IOException e) {
		System.out.print("Error: " + e);
		System.exit(1);
	    }
	    numoutputs++;
	}
	out.println(stringvalue);
    }

    public void finish () {
	if (!firstupdateoutput) 
	    out.close ();
    }

    public static int numoutputs = 0;
    private boolean firstupdateoutput = true;
    private String stringvalue = "";
    PrintWriter out;  
    private String direc = "";
    NumberFormat nf;

    int current_state;
    int previous_state;
    int next_state;
    MCMCParameter[] params;
    int numstates;
    double[][] transitionmat;
    double[][] cdfmat;
    JumpPerturber[][] perturbers;

    private int whoseTurn = 0;
    private int numTurns;
    int n; // params.length
    int[] len; // params[i].length
    double[][] candarray;
    MCMCBond[] bonds;
    private int[] num; // number of parameters in each bond
    private int[][] whatami;
    static Random rand = new Random(System.currentTimeMillis());
    static boolean warned = false;

    double[][] oldcands;

    int[][][] acceptances;
    int[][][] attempts;
}
