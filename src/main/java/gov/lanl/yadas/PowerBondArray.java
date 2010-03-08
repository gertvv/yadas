package gov.lanl.yadas;
import java.util.ArrayList;
import java.io.*;
import java.text.*;
/**
 * PowerBondArray is intended to be used to facilitate importance sampling
 * in YADAS.  Adding a PowerBondArray to a list of bonds has the effect 
 * of multiplying the unnormalized posterior by all the bonds included 
 * in the PowerBondArray, raised to some power.  The MCMC samples can 
 * then be reweighted by the values sent to the PowerBondArray's output 
 * file, after they are exponentiated.  
 * For example, all the bonds in the posterior can be included in 
 * the PowerBondArray, with power -1, and then another bond can be added, 
 * in which case we sample from the new bond.  Alternatively, the 
 * PowerBondArray can include all the bonds in the posterior with power 
 * (say) -0.75, in which case we sample from a flattened version of the 
 * posterior in order to improve communication between modes.  
 * C. Jennison suggested the latter approach in 1993.  
 */
public class PowerBondArray implements gov.lanl.yadas.MCMCBond {

    public PowerBondArray (MCMCBond[] rawbond, double powr) {
	this (rawbond, powr, "", "weights");
    }

    /**
     * @param rawbond is an array of MCMCBonds; the function of 
     * PowerBondArray is to multiply the posterior by each of these 
     * bonds raised to the 'powr' power.  Most commonly, these bonds 
     * will already be terms in the model.  
     * @param direc and nayme define the file where output will be 
     * sent (<direc><name>).  direc should include the trailing '/'.  
     */
    public PowerBondArray (MCMCBond[] rawbond, double powr, String direc,
			   String nayme) {
	this.rawbond = rawbond;
	this.powr = powr;
	this.direc = direc;
	this.nayme = nayme;

	paramlist = new ArrayList();
	translator = new int[rawbond.length][];
	// first loop: count number of parameters.  
	for (int i = 0; i < rawbond.length; i++) {
	    ArrayList params = rawbond[i].getParamList ();
	    for (int j = 0; j < params.size(); j++) {
		MCMCParameter tparam = (MCMCParameter)(params.get(j));
		if (!paramlist.contains(tparam)) {
		    paramlist.add(tparam);
		    tparam.addBond(this, paramlist.size()-1);
		}
	    }
	}

	// second loop: create translator.  
	for (int i = 0; i < rawbond.length; i++) {
	    translator[i] = new int[paramlist.size()];
	    ArrayList templist = new ArrayList();
	    ArrayList params = rawbond[i].getParamList ();
	    for (int j = 0; j < params.size(); j++) {
		templist.add(params.get(j));
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
	try {
	    out = new PrintWriter ( new FileWriter(direc + nayme + ".out"));
	} catch (IOException e) {
	    System.out.println("IO Error: " + e);
	    System.exit (0);
	}
	nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(3);
	nf.setGroupingUsed(false);
    }

    public double compute (int whatami, double cand, int which) {
	double summ = 0.0;
	for (int i = 0; i< rawbond.length; i++) {
	    if (translator[i][whatami] >= 0) {
		summ += rawbond[i].compute
		    (translator[i][whatami], cand, which);
	    }
	}
	return powr * summ;
    }

    public double compute (int whatami, double[] newpar) {
	double summ = 0.0;
	for (int i = 0; i< rawbond.length; i++) {
	    if (translator[i][whatami] >= 0) {
		summ += rawbond[i].compute
		    (translator[i][whatami], newpar);
	    }
	}
	return powr * summ;
    }

    public double compute (int[] whatami, double[][] newpar) { 
	double summ = 0.0;
	for (int i = 0; i< rawbond.length; i++) {
	    int[] fromvec = new int[whatami.length];
	    int[] transvec = new int[whatami.length];
	    int k=0;
	    for (int j = 0; j < whatami.length; j++) {
		if (translator[i][whatami[j]] >= 0) {
		    fromvec[k] = j;
		    transvec[k++] = translator[i][whatami[j]];
		}
	    }
	    int[] newwhatami = new int[k];
	    double[][] newnewpar = new double[k][];
	    for (int j = 0; j < k; j++) {
		newwhatami[j] = transvec[j];
		newnewpar[j] = newpar[fromvec[j]];
	    }
	    summ += rawbond[i].compute(newwhatami, newnewpar);
	}
	return powr * summ;
    }

    public double compute (int[] whatami, double[][] newpar, int[] which) {
	double summ = 0.0;
	for (int i = 0; i< rawbond.length; i++) {
	    int[] fromvec = new int[whatami.length];
	    int[] transvec = new int[whatami.length];
	    int k=0;
	    for (int j = 0; j < whatami.length; j++) {
		if (translator[i][whatami[j]] >= 0) {
		    fromvec[k] = j;
		    transvec[k++] = translator[i][whatami[j]];
		}
	    }
	    int[] newwhatami = new int[k];
	    double[][] newnewpar = new double[k][];
	    for (int j = 0; j < k; j++) {
		newwhatami[j] = transvec[j];
		newnewpar[j] = newpar[which[fromvec[j]]];
	    }
	    summ += rawbond[i].compute(newwhatami, newnewpar);
	}
	return powr * summ;
    }

    public void revise () {}

    public void blank_value () {}

    public ArrayList getParamList () {
	// wrong
	return rawbond[0].getParamList ();
    }

    public double getCurrentValue () {
	double summ = 0.0;
	for (int i = 0; i< rawbond.length; i++)
	    summ += rawbond[i].getCurrentValue();
	return powr * summ;
    }

    public String getName() {
	return "PowerBond_" + nayme;
    }

    public void setName(String name) {
	nayme = name;
    }

    /** 
     * Sends output to the output file.  These are the sums of the logged 
     * values of the bonds, multiplied by the negative of the power they 
     * are raised to.  If the PowerBondArray has been added to the model 
     * in order to do importance sampling, the data should be reweighted 
     * by this output, exponentiated.  
     */
    public void output () {
	out.println(nf.format(-getCurrentValue()));
    }
    
    public void finish () {
	out.close ();
    }

    MCMCBond[] rawbond;
    double powr;
    ArrayList paramlist;
    int[][] translator;    
    PrintWriter out;
    NumberFormat nf;
    String direc, nayme;
}
