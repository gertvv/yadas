package gov.lanl.yadas;

import java.util.*;
import java.io.*;

/** 
 * A weakness of DataFrame is that the input files are hard to read 
 * when there are too many variables, and this problem is most common 
 * when each variable has length one.  This class takes input files 
 * where all variables are real-valued, and where there is no first 
 * line containing the number of rows.  An example input file for this 
 * class would look like 
 *<p>
 * x|7.0<p>
 * y|3
 *<p>
 * Otherwise, this class behaves exactly like DataFrame.  In particular, 
 * the i() method converts the real variable into an integer.  
 * @see gov.lanl.yadas.DataFrame
 */
public class ScalarFrame implements MCMCInput {

    public ScalarFrame (String filename) {
	this (filename, "|");
    }

    public ScalarFrame (String filename, String delimiter) {
	this.delimiter = delimiter;
	try {
	    BufferedReader in = new BufferedReader (new FileReader(filename));
	    
	    ArrayList tempvarnames = new ArrayList();
	    ArrayList tempvalues = new ArrayList();
	    
	    while (in.ready()) {	    
		// read a line
		String s = in.readLine ();
		StringTokenizer t = new StringTokenizer (s, delimiter);
		tempvarnames.add(t.nextToken()); 
		if (t.hasMoreTokens()) { 
		    tempvalues.add(new Double(Double.parseDouble
					      (t.nextToken()))); 
		} else {
		    System.out.println(filename + "had a line with a " 
				        + "variable " 
				   + "name and no value.");
		    System.exit(0);
		}
	    }

	    m = tempvarnames.size();
	    md = m;
	    mi = ms = 0;
	    varnames = new String[m];
	    values = new double[m];
	    for (int i = 0; i < m; i++) {
		varnames[i] = (String)(tempvarnames.get(i));
		values[i] = ((Double)(tempvalues.get(i))).doubleValue();
	    }

	    String[] realnames = new String[tempvarnames.size()];

	    System.arraycopy (varnames, 0, realnames, 0, md);
	}
	catch (IOException e)
	    {
		System.out.print("Error: " + e);
		System.exit(1);
	    }
    }

    public int length () {
	return 1;
    }

    public int getVariableLength () {
	return 1;
    }
  
    // include all accessor methods here.  
    public double[][] getRealvars() {
	double[][] temp = new double[md][1];
	for (int ii = 0; ii < md; ii++) {
	    temp[ii][0] = values[ii];
	}
	return temp;
    }

    public int[][] getIntvars () {
	int[][] temp = new int[md][1];
	for (int ii = 0; ii < md; ii++) {
	    temp[ii][0] = (int)values[ii];
	}
	return temp;
    }

    public String[][] getStringvars () {
	String[][] temp = new String[md][1];
	for (int ii = 0; ii < md; ii++) {
	    temp[ii][0] = "" + values[ii];
	}
	return temp;
    }

    public double[] r (String varname) {
	int ii;
	for (ii = 0; ii < m && !varname.equals(varnames[ii]); ii++) {};
	if (ii == m) {
	    System.out.println("No variable by the name " + varname);
	    System.exit(1);
	}
	double[] temp = new double[n];
	temp[0] = values[ii];
	return temp;     
    }

    public double[] r (String varname, int numcopies) {
	// make numcopies copies of real vector
	// hamada 11.14.01 wrote DataFrame method
	int jj;
	int ii;
	for (ii = 0; ii < m && !varname.equals(varnames[ii]); ii++) {};
	if (ii == m) {
	    System.out.println("No variable by the name " + varname);
	    System.exit(1);
	}
	double[] temp = new double[numcopies];
	for(jj=0;jj< numcopies;jj++){
	    temp[jj] = values[ii];
	}
	return temp;     
    }

    public double[] r (double val) {
	double[] temp = new double[1];
	temp[0] = val;
	return temp;
    }

    public static double[] r (double val, int len) {
	double[] temp = new double[len];
	for (int ii = 0; ii < len; ii++) {
	    temp[ii] = val;
	}
	return temp;
    }  

    public int[] i (String varname) {
	int ii;
	for (ii = 0; ii < m && !varname.equals(varnames[ii]); ii++) {};
	if (ii == m) {
	    System.out.println("No variable by the name " + varname);
	    System.exit(1);
	}
	int[] temp = new int[1];
	temp[0] = (int)values[ii];
	return temp;     
    }

    public int[] i (int val) {
	int[] temp = new int[1];
	temp[0] = val;
	return temp;
    }

    public static int[] i (int val, int len) {
	int[] temp = new int[len];
	for (int ii = 0; ii < len; ii++) {
	    temp[ii] = val;
	}
	return temp;
    }

    public String[] s (String varname) {
      int ii;
      for (ii = 0; ii < m && !varname.equals(varnames[ii]);
	   ii++) {};
      if (ii == m) {
	System.out.println("No variable by the name " + varname);
	System.exit(1);
      }
      String[] temp = new String[1];
      temp[0] = "" + values[ii];
      return temp;     
    }

    public int[] u () {
	return new int[] {0};
    }

    public static int[] u (int len) {
	int[] temp = new int[len];
	for (int ii = 0; ii < len; ii++) {
	    temp[ii] = ii;
	}
	return temp;
    }

    // hamada 11.10.01	
    public static int[] u (int len, int numcopies) {
	// array containing ***numcopies*** copies of 0 to len-1
	int[] temp = new int[len*numcopies];
	int ind=0;
	for (int jj = 0; jj < numcopies; jj++) {
	    for (int ii = 0; ii < len; ii++) {
		temp[ind] = ii;
		ind++;
	    }	
	}
	return temp;
    }	
    
    public static void main (String args[]) {
	System.out.print("Testing DataFrame.\n");
	System.out.println("No tests written yet.");
    }

    final int n = 1;
    int m, mi, md, ms;
    
    String[] varnames;
    double[] values;
    String delimiter;
}
