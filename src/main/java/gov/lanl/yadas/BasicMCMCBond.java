package gov.lanl.yadas;

import java.util.*;

/**
 * BasicMCMCBond: the fundamental class used in defining statistical 
 * models in YADAS.  A bond first consists of one or more parameters.   
 * It also includes one or more ArgumentMakers, which convert the 
 * values of the parameters into arguments for the Likelihood function.
 * "Likelihood" is a poor choice of names, for two reasons: these are used 
 * for prior distributions as well, and they actually define <i>log</i>-likelihoods 
 * or log-priors.  
 * Most commonly, the "Likelihood" will be a LogDensity.  
 * @see gov.lanl.yadas.ArgumentMaker
 * @see gov.lanl.yadas.Likelihood
 * @see gov.lanl.yadas.LogDensity
 * @author TLG
 */
public class BasicMCMCBond implements MCMCBond {

    public BasicMCMCBond (MCMCParameter[] parameters, Likelihood lik) {
	this (lik, parameters);
    }
    
    public BasicMCMCBond (Likelihood lik, MCMCParameter[] parameters) {
	this (IdentityArgument.IdentityArgumentArray(parameters.length),
	      lik, parameters);
    }
    
    public BasicMCMCBond (MCMCParameter[] parameters, ArgumentMaker[] am,
			  Likelihood lik) {
	this (am, lik, parameters);
    }
    
    public BasicMCMCBond (ArgumentMaker[] am, Likelihood lik,
			  MCMCParameter[] parameters) {
	this.am = am;
	this.lik = lik;
	params = parameters;
	paramlist = new ArrayList();
	for (int i = 0; i < params.length; i++) {
	    paramlist.add(params[i]);
	    params[i].addBond(this, i);
	}
    }
    
    /**
     * Computes the difference in the log of the model term when the 
     * current value of the parameters is stored in the values of 
     * params, and the proposed new value of the parameters is obtained 
     * by changing the which'th element of the whatami'th parameter to 
     * the new value of cand.  
     */
    // this is going to supersede all the other signatures of compute!
    public double compute (ParameterChanger h) {
	stage1 ();
	h.change(preargs);
	stage3 ();
	return logr;
    }
   
    void stage1 () {
	logr = 0;
	prepareArgs ();
	subtractOld ();
    }    
    void stage3 () {
	addNew ();
	if (pr) System.out.println(getName() + " " + logr);
    }    
    public double compute (int whatami, double cand, int which) {
	stage1 ();
	if (pr) System.out.print(whatami + " " + which + " " + 
				 preargs[whatami][which] + " " + cand + " ");
	changeParameters (whatami, cand, which);
	stage3 ();		
	return logr;
    }
    void changeParameters (int whatami, double cand, int which) {
	preargs[whatami][which] = cand;
    }
    
    /** 
     * Another signature for the method that computes the change in 
     * log posterior: here the proposed new value changed the 
     * whatami'th parameter to newpar.  
     */ 
    public double compute (int whatami, double[] newpar) {
	double[][] twoway = new double[1][newpar.length];
	twoway[0] = newpar;
	return compute (new int[] {whatami}, twoway);
    }
    
    // This may slow things down; be prepared to use code commented out at 
    // the end of this file
    /** 
     * A commonly used signature for the compute method: changes several 
     * parameters (whose indices are given in whatami) to entirely new 
     * values given in newpar.  
     */ 
    public double compute (int[] whatami, double[][] newpar) {
	int[] temp = new int[newpar.length];
	for (int i = 0; i < newpar.length; i++) {
	    temp[i] = i;
	}
	return compute(whatami, newpar, temp);
    }
    
    public double compute (int[] whatami, double[][] newpar, int[] which) {
	stage1 ();
	changeParameters (whatami, newpar, which);
	stage3 ();
	return logr;
    }
    void changeParameters (int[] whatami, double[][] newpar, int[] which) {
	for (int i = 0; i < whatami.length; i++) {
	    preargs[whatami[i]] = newpar[which[i]];
	}		
    }
    
    public void prepareArgs () {
	// collect copies of each param,
	preargs = new double[params.length][];
	args = new double[am.length][];
	newargs = new double[am.length][];
	for (int i = 0; i < params.length; i++) {
	    preargs[i] = params[i].getValue();
	}
    }
    
    void subtractOld () {
	if (value_computed) {
	    logr -= current_value;
	} else {
	    for (int i = 0; i < args.length; i++) {
		args[i] = am[i].getArgument (preargs);
	    }
	    just_computed ();
	    current_value = lik.compute (args);
	    logr -= current_value;
	}	
    }
    
    void addNew () {
	for (int i = 0; i < args.length; i++) {
	    newargs[i] = am[i].getArgument (preargs);
	}
	new_value = lik.compute (newargs);
	logr += new_value;
    }
    
    /**
     * For computational semiefficiency.  If the value of the bond has 
     * already been computed, don't compute it again. 
     */ 
    public void revise () {
	current_value = new_value;
    }
    
    public void blank_value () {
	value_computed = false;
    }
    
    public void just_computed () {
	value_computed = true;
    }
    
    public double[] getArgumentExternally (int i) {
	double[][] preargs = new double[params.length][];
	for (int j = 0; j < preargs.length; j++) {
	    preargs[j] = params[j].getValue();
	}
	return am[i].getArgument (preargs);
    }
    
    public ArrayList getParamList () {
	return paramlist;
    }
    
    public double getCurrentValue () {
	return current_value;
    }

    public void setArg (double[] narg, int which) {
	args[which] = narg;
    }

    public double[][] getArgs () {
	return args;
    }
    
    public boolean pr = false;
    
    public ArgumentMaker[] am;
    public Likelihood lik;
    public MCMCParameter[] params;
    ArrayList paramlist;
    boolean value_computed = false;
    double current_value = 0.0;
    double new_value = 0.0;
    
    public String getName () { return name; }
    
    public String name = "Unnamed";
    
    public void setName ( String nam ) { name = nam; }
    
    public double[][] preargs;
    public double[][] args;
    double[][] newargs;
    double logr;
}

