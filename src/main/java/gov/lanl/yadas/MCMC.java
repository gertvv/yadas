package gov.lanl.yadas;

// The class to end all YADAS classes.  

import java.util.ArrayList;
/**
 * Delete: renamed MCMCAnalysis
 */
public class MCMC {

    public MCMC (String direc) {
	this.direc = direc;
    }

    public void addDataFrame (DataFrame dframe) {
	this.d.add(dframe);
    }

    public void addScalarFrame (ScalarFrame sframe) {
	this.d0.add(sframe);
    }

    public void addParameter (MCMCParameter param) {
	this.paramlist.add(param);
    }

    public void addBond (MCMCBond bond) {
	bondlist.add(bond);
    }

    public void addUpdate (MCMCUpdate upd) {
	updatelist.add(upd);
    }

    public void setDirec (String direc) {
	this.direc = direc;
    }

    public void iterate (int B) {
	for (int b = 0; b < B; b++) {
	    if ((b/((double)progressreport) - (int)(b/progressreport))== 0) 
		System.out.println(b);
	    for (int i = 0; i < updatelist.size(); i++) {
		((MCMCUpdate)(updatelist.get(i))).update ();
	    }
	    for (int i = 0; i < paramlist.size(); i++) {
		((MCMCParameter)(paramlist.get(i))).output ();
	    }
	}
    }

    public void finish () {
	String acc;
	for (int iii = 0; iii < updatelist.size(); iii++) {
	    acc = ((MCMCUpdate)(updatelist.get(iii))).accepted();
	    System.out.println("Update " + iii + ": " + acc);
	}
	for (int i = 0; i < paramlist.size(); i++) {
	    ((MCMCParameter)(paramlist.get(i))).finish();
	}
    }

    int progressreport = 1000;
    int skip = 1;

    String direc;
    ArrayList filenames = new ArrayList(); 
    ArrayList d = new ArrayList();              // DataFrames
    ArrayList d0 = new ArrayList();             // ScalarFrames
    ArrayList paramlist = new ArrayList();
    ArrayList paramnames = new ArrayList();
    ArrayList bondlist = new ArrayList();
    ArrayList bondnames = new ArrayList();
    ArrayList updatelist = new ArrayList();
    ArrayList updatenames = new ArrayList();
}
