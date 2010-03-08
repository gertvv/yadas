package gov.lanl.yadas;
import java.util.*;

/**
 * Can be used to specify terms in the posterior distribution that are 
 * themselves weighted averages of densities.  This class may not do 
 * quite what you want if you're trying to express a mixture distribution: 
 * for example, if a collection of data have either a normal distribution 
 * with small variance or a normal distribution with large variance, 
 * this class insists that they all have the same variance.  If one wants 
 * each data point to have a mixture distribution independently of the 
 * other data points, one needs to use one MixtureBond for each data point.   
 * @see gov.lanl.yadas.BinomialHypothesisTest
 * @see gov.lanl.yadas.SystemBinomialBonds
 */
public class MixtureBond implements MCMCBond {

    // add secondary constructors later!

    /** 
     * The arguments to the MixtureBond constructor parallel those of
     * BasicMCMCBond, except they are in arrays with more dimensions.  
     * @param probarg is the new argument, which calculates the array 
     * of mixing weights (and this array can be a function of unknown
     * parameters, so it is described with an ArgumentMaker).  This 
     * argument needs to refer to the parameters in parameters[0].  
     */
    public MixtureBond (MCMCParameter[][] parameters, ArgumentMaker[][] am, 
			Likelihood[] lik, ArgumentMaker probarg) {

	this.probarg = probarg;
	this.am = am;
	this.lik = lik;
	params = parameters;
	paramlist = new ArrayList();
	translator = new int[params.length][];
	// first loop: count number of parameters.  
	for (int i = 0; i < params.length; i++) {
	    for (int j = 0; j < params[i].length; j++) {
		if (! paramlist.contains(params[i][j])) {
		    paramlist.add(params[i][j]);
		    params[i][j].addBond(this, paramlist.size()-1);
		}
	    }
	}
	// second loop: create translator.  
	for (int i = 0; i < params.length; i++) {
	    translator[i] = new int[paramlist.size()];
	    ArrayList templist = new ArrayList();
	    for (int j = 0; j < params[i].length; j++) {
		templist.add(params[i][j]);
	    }
	    for (int j = 0; j < paramlist.size(); j++) {
		if (templist.contains(paramlist.get(j))) {
		    int ind = templist.indexOf(paramlist.get(j));
		    translator[i][j] = ind;
		}
		else {
		    translator[i][j] = -1;
		}
	    }
	}
	// call addBond only once for each parameter.  
	// also construct a translator between paramlist and params.  
    }
    /*
    public double compute (ParameterChanger h) {
	stage1 (); 
	h.change(preargs);
	stage3 ();
	return Math.log(temp2/temp1);
	} */
    void stage1 () {
	temp1 = temp2 = 0.0;
	prepareArgs ();
	subtractOld ();
    }
    void stage3 () {
	addNew ();
    }
    void prepareArgs () {
	// collect copies of each param,
	preargs = new double[params.length][][];
	args = new double[am.length][][];
	newargs = new double[am.length][][];
	for (int i = 0; i < params.length; i++) {
	    preargs[i] = new double[params[i].length][];
	}
	for (int i = 0; i < am.length; i++) {
	    args[i] = new double[am[i].length][];
	    newargs[i] = new double[am[i].length][];
	}
	for (int i = 0; i < params.length; i++) {
	    for (int j = 0; j < params[i].length; j++) {
		preargs[i][j] = params[i][j].getValue();
	    }
	}
    }
    void subtractOld () {
	for (int i = 0; i < args.length; i++) { // loop over models
	    for (int j = 0; j < args[i].length; j++) { 
		// loop over arguments
		args[i][j] = am[i][j].getArgument (preargs[i]);
	    }
	    preprob = probarg.getArgument (preargs[i]);
	    if (preprob[i] > 0) 
		temp1 += preprob[i] * Math.exp(lik[i].compute (args[i]));
	}
    }
    void addNew () {
	for (int i = 0; i < args.length; i++) { // loop over models
	    for (int j = 0; j < args[i].length; j++) {
		newargs[i][j] = am[i][j].getArgument (preargs[i]);
	    }
	    postprob = probarg.getArgument (preargs[i]);
	    if (postprob[i] > 0) 
		temp2 += postprob[i] * Math.exp(lik[i].compute (newargs[i]));
	}
    }
    public double compute (int whatami, double cand, int which) {
	stage1 (); 
	for (int i = 0; i < args.length; i++) { // loop over models	    
	    if (translator[i][whatami] >= 0) {
		preargs[i][translator[i][whatami]][which] = cand;
	    }
	}
	//h.change(preargs);
	stage3 ();
	return Math.log(temp2/temp1);
    }
    
    public double compute (int whatami, double[] newpar) {
	double[][] twoway = new double[1][newpar.length];
	twoway[0] = newpar;
	return compute (new int[] {whatami}, twoway);
    }
    
    // This may slow things down; be prepared to use code commented out at 
    // the end of BasicMCMCBond, suitably modified if necessary
    public double compute (int[] whatami, double[][] newpar) {
	int[] temp = new int[newpar.length];
	for (int i = 0; i < newpar.length; i++) {
	    temp[i] = i;
	}
	return compute(whatami, newpar, temp);
    }
    
    public double compute (int[] whatami, double[][] newpar, int[] which) {
	double temp0 = 0, temp1 = 0;
	double[][][] preargs;
	double[][][] args;
	double[][][] newargs;
	double[] preprob;
	double[] postprob;

	// collect copies of each param,
	preargs = new double[params.length][][];
	args = new double[am.length][][];
	newargs = new double[am.length][][];
	for (int i = 0; i < params.length; i++) {
	    preargs[i] = new double[params[i].length][];
	}
	for (int i = 0; i < am.length; i++) {
	    args[i] = new double[am[i].length][];
	    newargs[i] = new double[am[i].length][];
	}
	for (int i = 0; i < params.length; i++) {
	    for (int j = 0; j < params[i].length; j++) {
		preargs[i][j] = params[i][j].getValue();
	    }
	}

	for (int i = 0; i < args.length; i++) { // loop over models
	    for (int j = 0; j < args[i].length; j++) { // loop over arguments
		args[i][j] = am[i][j].getArgument (preargs[i]);
	    }
	    preprob = probarg.getArgument (preargs[i]);
	    if (preprob[i] > 0) {
		//System.out.println("preprob: " + i + ": " + preprob[i]);
		//System.out.println(lik[i].compute (args[i]));
		temp0 += preprob[i] * Math.exp(lik[i].compute (args[i]));
	    }
	    for (int k = 0; k < whatami.length; k++) {
		if (translator[i][whatami[k]] >= 0) {
		    preargs[i][translator[i][whatami[k]]] = newpar[k];
		}
	    }
	
	    for (int j = 0; j < args[i].length; j++) {
		newargs[i][j] = am[i][j].getArgument (preargs[i]);
	    }
	    postprob = probarg.getArgument (preargs[i]);
	    if (postprob[i] > 0) {
		//System.out.println("postprob: " + i + ": " + postprob[i]);
		//System.out.println(lik[i].compute (newargs[i]));
		temp1 += postprob[i] * Math.exp(lik[i].compute (newargs[i]));
	    }
	}
	return Math.log(temp1/temp0);
    }

    public void revise () {
	// empty.  could try to make everything more efficient above.  
	// maybe later.  presumably what we need to do is keep an 
	// array of current and new values.  
    }
    
    public ArrayList getParamList () {
	return paramlist;
    }

    public void blank_value () {
	value_computed = false;
    }
    
    public double getCurrentValue () {
	return current_value;
    }

    public boolean pr = false;
    
    ArgumentMaker[][] am;
    Likelihood[] lik;
    MCMCParameter[][] params;
    ArrayList paramlist;
    int[][] translator;
    ArgumentMaker probarg;
    boolean value_computed = false;
    double current_value = 0.0;
    double new_value = 0.0;

    public String getName () { return name; }
    
    public String name = "Unnamed MixtureBond";
    
    public void setName ( String nam ) { name = nam; }

    double temp1 = 0;
    double temp2 = 0;
    double[][][] preargs;
    double[][][] args;
    double[][][] newargs;
    double[] preprob;
    double[] postprob;    

}
