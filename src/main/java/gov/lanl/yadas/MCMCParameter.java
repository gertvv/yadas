package gov.lanl.yadas;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.text.*;

/**
 * Any quantity whose posterior distribution one seeks in YADAS 
 * has its values stored in an MCMCParameter (or one of its subclasses).  
 * A parameter has a value which can be a vector and which gets 
 * changed as the algorithm runs, a set of step sizes of the same 
 * length as the value, and a name.  Also, since the MCMCParameter
 * class implements the MCMCUpdate interface, a parameter knows how 
 * to update itself.  It does this by looping over its components, 
 * and for each one attempting to make a Gaussian move according to 
 * a random walk Metropolis proposal.  
 */
public class MCMCParameter implements MCMCNode, TunableMCMCUpdate, MCMCOutput {
    
    public static final int MAX_BONDS = 100;
    
    /**
     * @param v is the array of initial values for the parameter.
     * @param mss is the array of step sizes (standard deviations) 
     * used in the Gaussian Metropolis steps.  If any or all 
     * components of the parameter are to be treated as constant, 
     * set their step sizes to zero.  
     * @param name is the successive values of the parameter obtained 
     * in the algorithm will be stored in a file called <name>.out.
     */
    public MCMCParameter (double[] v, //int[] gr, 
			  double[] mss, String name)
    {
	value = v;
	proposedvalue = new double[v.length];
	probvalue = new double[v.length];
	for (int i = 0; i < v.length; i++) {
	    proposedvalue[i] = -1;
	    probvalue[i] = -1;
	}
	
	bonds = new MCMCBond[MAX_BONDS];
	whatami = new int[MAX_BONDS];
	numbonds = 0;
	
	MetropolisStepSize = new double[mss.length];
	System.arraycopy (mss, 0, MetropolisStepSize, 0, mss.length);
	int i;
	for (i = 0; i < mss.length && mss[i] == 0; i++) {}
	if (i < mss.length) {
	    isConstant = false;
	    try {
		out = new PrintWriter ( new FileWriter(name + ".out"));
	    }
	    catch (IOException e) {
		System.out.print("Error: " + e);
		System.exit(1);
	    }
	}
	else {
	    isConstant = true;
	}
	this.name = name;
	
	nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(3);
	nf.setGroupingUsed(false);

	probnf = NumberFormat.getNumberInstance();
	probnf.setMaximumFractionDigits(3);
	probnf.setGroupingUsed(false);

	nft = NumberFormat.getNumberInstance();
	nft.setMaximumFractionDigits(3);
	nft.setGroupingUsed(false);

	accs = new int[v.length];
	for (int ii = 0; ii < accs.length; ii++) {
	    accs[ii] = 0;
	}
    }
    
    public void update () {
	if (isConstant) { return; }
	//if (MetropolisStepSize[whoseTurn] == 0) { return; }
	while (whoseTurn < value.length) {
	    candidate = candidate()[0];
	    proposedvalue[whoseTurn] = candidate;
	    double ap = acceptanceProbability ();
	    probvalue[whoseTurn] = ap;
	    if (ap > rand.nextFloat()) {
		takeStep();
	    }
	    whoseTurn++;
	}
	whoseTurn = 0;
    }
    
    /** 
     * candidate() and acceptanceProbability() are methods used in 
     * update().  Subclasses of this class override these methods 
     * to create new Metropolis-Hastings proposals.  In particular, 
     * see MultiplicativeMCMCParameter, LogitMCMCParameter, and 
     * IntegerMCMCParameter.  
     */
    public double[] candidate () {
	double[] temp = new double[1];
	temp[0] = value[whoseTurn] + rand.nextGaussian() * 
	    MetropolisStepSize[whoseTurn];
	return temp;
    }
    
    public MCMCBond[] relevantBonds () {
	if (bondscomputed) {
	    return (bondss);
	}
	else {
	    bondss = new MCMCBond[numbonds];
	    System.arraycopy(bonds, 0, bondss, 0, numbonds);
	    bondscomputed = true;
	    return bondss;
	}
    }
    
    public double acceptanceProbability () {
	double lr = 0;
	MCMCBond[] baunds = relevantBonds();
	for (int j = 0; j < baunds.length; j++) {
	    //System.out.println(baunds[j].getName() + " being computed: " +
	    //baunds[j].compute(whatami[j], candidate, 
	    //whoseTurn));
	    //System.out.print(" " + j + ": " + baunds[j].compute
	    //		     (whatami[j], candidate, whoseTurn) + " ");
		//try {
	    lr = lr + baunds[j].compute(whatami[j], candidate, whoseTurn); 
		/*
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println (getName() + "!!!" + toString());
		}
		*/
	}
	//System.out.println("LR = " + lr);
	return Math.exp(lr);
    }
    
    public void takeStep () {
	value[whoseTurn] = candidate;
	MCMCBond[] baunds = relevantBonds();
	for (int j = 0; j < baunds.length; j++) {
	    baunds[j].revise();
	}
	accs[whoseTurn]++;
    }
    
    public String toString () {
	String out = "";
	for (int i = 0; i < value.length - 1; i++) {
	    out += (nf.format(value[i]) + "|");
	}
	out += nf.format(value[value.length - 1]);
	return out;
    }
    public String toStringTune () {
	String out = "";
	double[] temp = getStepSizes ();
	for (int i = 0; i < temp.length - 1; i++) {
	    out += (nft.format(temp[i]) + "|");
	}
	out += nft.format(temp[temp.length-1]);
	return out;
    }
    
    public String toStringPropose () {
	String out = "";
	for (int i = 0; i < proposedvalue.length - 1; i++) {
	    out += (nf.format(proposedvalue[i]) + "|");
	}
	out += nf.format(proposedvalue[proposedvalue.length - 1]);
	return out;
    }
    
    public String toStringProb () {
	String out = "";
	for (int i = 0; i < probvalue.length - 1; i++) {
	    out += (nf.format(probvalue[i]) + "|");
	}
	out += nf.format(probvalue[probvalue.length - 1]);
	return out;
    }
    // obviously there are more elegant ways of writing last three methods

    public void output () {
	if (!isConstant) {
	    out.println(toString());
	}
    }
    
    public void updateoutput () {
	if (firstupdateoutput) {
	    firstupdateoutput = false;
	    try {
		propose_out = new PrintWriter ( new FileWriter
		    ( name + "_propose.out") );
		prob_out = new PrintWriter ( new FileWriter
		    ( name + "_prob.out") );
	    }
	    catch (IOException e) {
		System.out.print("Error: " + e);
		System.exit(1);
	    }
	}
	if (!isConstant) {
	    propose_out.println(toStringPropose());
	    prob_out.println(toStringProb());
	}
    }

    public void tuneoutput () {
	if (!isConstant) {
	    if (firsttuneoutput) {
		firsttuneoutput = false;
		try {
		    tune_out = new PrintWriter ( new FileWriter
			( name + ".tun") );
		}
		catch (IOException e) {
		    System.out.print("Error: " + e);
		    System.exit(1);
		}
	    }
	    tune_out.println(toStringTune());
	}
    }

    public boolean sample_exact () {
	return false;
    }
    
    public double getValue (int i) {
	return value[i];
    }

    public double[] getValue () {
	double[] temp = new double[value.length];
	System.arraycopy (value, 0, temp, 0, value.length);
	return temp;
    }
    
    public void setValue (double[] newvalue) {
	// ouch
	System.arraycopy(newvalue, 0, value, 0, value.length);
    }
    
    public void setValue (int which, double newvalue) {
	// ouch
	value[which] = newvalue;
    }

    public double[] getStepSizes () {
	double[] cpy = new double[MetropolisStepSize.length];
	System.arraycopy(MetropolisStepSize, 0, cpy, 0, 
			 MetropolisStepSize.length);
	return cpy;
    }
    public void setStepSize (double s, int i) {
	if (!isConstant)
	    MetropolisStepSize[i] = s;
    }
    public void setStepSizes (double[] s) {
	if (!isConstant)
	    for (int i = 0; i < MetropolisStepSize.length; i++) {
		MetropolisStepSize[i] = s[i];
	    }
    }

    static void printints (int[] vec) {
	String op = "";
	for (int i = 0; i < vec.length; i++) {
	    op = op + vec[i] + " ";
	}
	System.out.println(op);
    }
    
    public void addBond (MCMCBond bond, int which) {
	if (numbonds < MAX_BONDS) {
	    bonds[numbonds] = bond;
	    whatami[numbonds] = which;
	    numbonds++;
	}
    }
    
    /**
     * This method must be called in order to be able to read 
     * all (sometimes any) of the output.  
     */
    public void finish() {
	if (!isConstant) {
	    out.close();
	    if (!firstupdateoutput) {
		propose_out.close ();
		prob_out.close ();
	    }
	    if (!firsttuneoutput) {
		tune_out.close ();
	    }
	}
    }
    
    public int length () {
	return value.length;
    }
    
    public static void main() {
	System.out.println("Testing MCMCParameter.class\n");
    }
    
    public boolean isConstant () {
	return isConstant;
    }

    public boolean areConstant (int i) {
	return (MetropolisStepSize[i] == 0);
    }

    /**
     * This method can be helpful in monitoring acceptance rates and 
     * therefore in tuning step sizes.  
     */
    public String accepted () {
	String ac = "";
	for (int i = 0; i < accs.length; i++) {
	    ac += i + ":" + accs[i] + " ";
	}
	return ac;
    }
    public int[] acceptances () {
	return accs;
    }

    public String getName () {
	return name;
    }
    
    /*
    public int[] attempts () {
    }
    */

    protected double[] value;    

    double[] proposedvalue, probvalue;
    private boolean firstupdateoutput = true;

    public int whoseTurn = 0;
    double candidate;

    MCMCBond[] bonds;
    MCMCBond[] bondss;
    boolean bondscomputed = false;
    public int[] whatami;
    int numbonds;
    
    public double[] MetropolisStepSize;
    boolean isConstant;
    String name;
    public static Random rand = new Random(System.currentTimeMillis());
    
    PrintWriter out, propose_out, prob_out;
    
    public NumberFormat nf, probnf;

    int[] accs;

    boolean firsttuneoutput = true;
    PrintWriter tune_out;
    public NumberFormat nft;
}

