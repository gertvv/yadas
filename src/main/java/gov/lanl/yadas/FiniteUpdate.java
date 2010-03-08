package gov.lanl.yadas;
import java.util.*;

/**
 * This class updates a parameter that takes on finite numbers of values.  
 * These possible values are 0, 1, ... up to some limit.  
 * (Note that this is potentially convenient for use in new ArgumentMakers; 
 *  if a parameter is intended to take on a finite number of arbitary (even 
 *  changeable) values, it can be used as a subscripting variable.)
 * The i'th component of 'param' can take on numvalues[i] different values.  
 * This class does exact componentwise Gibbs updates.  
 */
public class FiniteUpdate implements MCMCUpdate {

    public FiniteUpdate (MCMCParameter param, int numvalues) { 
	this (param, new int[] { numvalues }); 
    }

    /**
     * The i'th component of 'param' can take on numvalues[i] different 
     * values.  
     */
    public FiniteUpdate (MCMCParameter param, int[] numvalues) {

	this.param = param;
	this.numvalues = numvalues;
	numTurns = param.length();
	acceptances = new int[numTurns];
	for (int i = 0; i < numTurns; i++) {
	    acceptances[i] = 0;
	}

	// one parameter => no need for set construct
	// but see MultipleParameterUpdate for more general code
	bonds = param.relevantBonds();

	// attention: lousy java coming.  Accessed the whatami record 
	// in the parameter.  
	whatami = new int[bonds.length];
	System.arraycopy(param.whatami, 0, whatami, 0, bonds.length);

    }
    
    // get conditional cdf of the categorical variable from candidate();
    // then sample from this distribution
    public void update() {
	while (whoseTurn < numTurns) {
	    /*
	    System.out.println("whoseTurn = " + whoseTurn + 
			       ", param[whoseTurn] = " + 
			       param.getValue()[whoseTurn]);
	    */
	    if (!param.areConstant(whoseTurn)) {
		double[] candprob = candidate();
		double samp = rand.nextDouble();
		int k = 0;
		while (samp > candprob[k]) { k++; }
		if ((int)(param.getValue()[whoseTurn]) != k) 
		    acceptances[whoseTurn]++;
		param.setValue(whoseTurn, k);
		for (int j = 0; j < bonds.length; j++) {
		    bonds[j].blank_value();
		}
	    }
	    whoseTurn++;
	}
	whoseTurn = 0;
    }

    // the possible values are 0, 1, ..., numvalues[whoseTurn] - 1.  
    // Compute the log posterior ratio for each value, then exponentiate.    
    // A Gibbs step samples proportionally to these ratios.  
    public double[] candidate () {
	int num = numvalues[whoseTurn];
	double[] probs = new double[num];
	for (int i = 0; i < num; i++) {
	    probs[i] = 0.0;
	    for (int j = 0; j < bonds.length; j++) {
		probs[i] += bonds[j].compute(whatami[j], i, whoseTurn);
		/* use two lines below eventually
		   paramchanger.setAll(whatami[j], i, whoseTurn);
		   probs[i] += bonds[j].compute(paramchanger);
		*/
		//System.out.println("after bond " + j + ": " + probs[i]);
	    }
	    probs[i] = Math.exp(probs[i]);
	}
	/* System.out.println("");
	for (int i = 0; i < probs.length; i++) {
	    System.out.print("|"+ probs[i]);
	}
	System.out.println(""); */
	double summ = 0.0;
	for (int i = 0; i < probs.length; i++) {
	    summ += probs[i];
	}
	for (int i = 0; i < probs.length; i++) {
	    probs[i] /= summ;
	}
	for (int i = 1; i < probs.length; i++) {
	    probs[i] += probs[i-1];
	}/*
	for (int i = 0; i < probs.length; i++) {
	    System.out.print("|"+ probs[i]);
	}
	System.out.println("");
	System.out.println("Exp(-infty) = " + Math.exp(java.lang.Double.NEGATIVE_INFINITY)); */
	return probs; 
    }
    
    public MCMCBond[] relevantBonds () {
	return bonds;
    }

    // I can't imagine ignoreBond being relevant; if it is, get it from
    // MultipleParameterUpdate.java
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
	}
    
    // accepted() is a little different here: we are sampling from the 
    // full conditionals, so in a sense every move is accepted, but this 
    // is intended to count the number of changes.  
    public String accepted () {
	String ac = "";
	for (int i = 0; i < acceptances.length; i++) {
	    ac = ac + i +  ":" +  acceptances[i] + " ";
	}
	return ac;
    }

    public void updateoutput () {
    }

    public void finish () {}

    MCMCParameter param;
    int[] numvalues;

    private int whoseTurn = 0;
    private int numTurns;
    MCMCBond[] bonds;
    private int[] whatami;
    static Random rand = new Random(System.currentTimeMillis());
    int[] acceptances;
    SingleParameterChanger paramchanger;
}

