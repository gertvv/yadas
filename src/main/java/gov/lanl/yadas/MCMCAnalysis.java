package gov.lanl.yadas;

// The class to end all YADAS classes.  

import java.util.ArrayList;
import java.io.*;

/**
 * This class forms the basis of an alternate way of creating 
 * YADAS analyses.  It facilitates running YADAS applications 
 * from Jython or R.  
 */
public class MCMCAnalysis {
    
    public MCMCAnalysis () {
	this ("");
    }
    
    public MCMCAnalysis (String direc) {
	this.direc = direc;
	try {
	    out = new PrintWriter 
		( new FileWriter(direc + "logfile.txt"));
	}
	catch (IOException e) {
	    System.out.print("Error: " + e);
	    System.exit(1);
	}
    }
    
    public void addInput (MCMCInput innput) {
	inputlist.add(innput);
    }
    
    public void addInputs (MCMCInput[] innput) {
	for (int i = 0; i < innput.length; i++) {
	    inputlist.add(innput[i]);
	}
    }
    
    public void addNode (MCMCNode param) {
	nodelist.add(param);
    }
    
    public void addBond (MCMCBond bond) {
	bondlist.add(bond);
    }
    
    public void addUpdate (MCMCUpdate upd) {
	updatelist.add(upd);
    }
    
    public void addUpdates (MCMCUpdate[] upd) {
	for (int i = 0; i < upd.length; i++) {
	    updatelist.add(upd[i]);
	}
    }
    
    public void addOutput (MCMCOutput output) {
	outputlist.add(output);
    }
    
    public void addOutputs (MCMCOutput[] output) {
	for (int i = 0; i < output.length; i++) {
	    outputlist.add(output[i]);
	}
    }
    
    public void setDirec (String direc) {
	this.direc = direc;
    }
    
    public int numparams () {
	return numNodes ();
    }
    
    public int numNodes () {
	return nodelist.size();
    }
    
    public MCMCNode getNode (int i) {
	return (MCMCNode)(nodelist.get(i));
    }
    
    public int numbonds () {
	return bondlist.size();
    }
    
    public int numupdates () {
	return updatelist.size ();
    }
    /*
      public void Riterate () {
      int B = 10;
      for (int b = 0; b < B; b++) {
      for (int i = 0; i < updatelist.size(); i++) {
      ((MCMCUpdate)(updatelist.get(i))).update ();
      }
      for (int i = 0; i < paramlist.size(); i++) {
      ((MCMCOutput)(outputlist.get(i))).output ();
      }
      }
      for (int i = 0; i < paramlist.size(); i++) {
      ((MCMCParameter)(paramlist.get(i))).finish();
      }
      out.println("MCMCAnalysis.Riterate did something!");
      out.close ();
      }
      
      public void Rfinish () {
      iterate (2);
      finish ();
      out.println("MCMCAnalysis.Rfinish did something!");
      out.close ();
      }
      
      public void Rclose () {
      // this code works!!
      for (int i = 0; i < paramlist.size(); i++) {
      ((MCMCParameter)(paramlist.get(i))).finish();
      }
      }
    */
    public void outclose () {
	out.close ();
    }
    
    public void iterate (int B) {
	for (int b = 0; b < B; b++) {
	    if (((b/((double)progressreport) - (int)(b/progressreport))== 0)
		&& !neverprint) 
		System.out.println(b);
	    for (int i = 0; i < updatelist.size(); i++) {
		((MCMCUpdate)(updatelist.get(i))).update ();
	    }
	    for (int i = 0; i < outputlist.size(); i++) {
		((MCMCOutput)(outputlist.get(i))).output ();
	    }
	}
    }
    
    public void finish () {
	String acc;
	for (int iii = 0; iii < updatelist.size(); iii++) {
	    acc = ((MCMCUpdate)(updatelist.get(iii))).accepted();
	    if (!neverprint)
		System.out.println("Update " + iii + ": " + acc);
	}
	for (int i = 0; i < nodelist.size(); i++) {
	    ((MCMCNode)(nodelist.get(i))).finish();
	}
    }
    
    public void never_print () {
	neverprint = true;
    }
    
    int progressreport = 1000;
    int skip = 1;
    
    boolean neverprint = false;
    
    String direc;
    public ArrayList filenames = new ArrayList(); 
    public ArrayList inputlist = new ArrayList();              // DataFrames
    public ArrayList nodelist = new ArrayList();
    public ArrayList paramnames = new ArrayList();
    public ArrayList bondlist = new ArrayList();
    public ArrayList bondnames = new ArrayList();
    public ArrayList updatelist = new ArrayList();
    public ArrayList updatenames = new ArrayList();
    public ArrayList outputlist = new ArrayList();
    
    PrintWriter out;
}
