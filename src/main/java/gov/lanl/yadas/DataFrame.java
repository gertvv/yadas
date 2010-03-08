package gov.lanl.yadas;

import java.util.*;
import java.io.*;

/**
 * A class used for most specification of data (including initial values 
 * and step sizes for Metropolis steps) in YADAS.  The format of an input 
 * file is as follows: the first line should contain an integer (the number 
 * of lines of data in the file).  The next line should contain variable 
 * names, separated by pipes ("|").  The third line contains type information
 * for each variable: 'r' for real, 'i' for integer, 's' for string.  These 
 * too are pipe-separated.  The fourth and succeeding lines contain the 
 * data values, separated by pipes.  Spaces are not recommended.  
 * All variables in a data frame must have the same length.  This was 
 * nearly the first YADAS class written so may not be ideal.  
 * @author TLG, plus hamada's revision for multiple response 
 * experimental design analysis
 */
public class DataFrame implements MCMCInput {

    //k public static final int MAX_VARS = 200;

    public DataFrame (String filename) {
	this (filename, "|");
    }

    /**
     * Reads the contents of the file 'filename' into a YADAS DataFrame.  
     */
    public DataFrame (String filename, String delimiter) {
	this.delimiter = delimiter;
	try {
	    BufferedReader in = new BufferedReader 
		(new FileReader(filename));
	    
	    // read number of records from first line
	    String s = in.readLine ();
	    n = Integer.parseInt(s);
	    
	    // read variable names
	    //k String[] tempvarnames = new String[MAX_VARS];
	    ArrayList tempvarnames = new ArrayList ();
	    s = in.readLine ();
	    StringTokenizer t = new StringTokenizer (s, delimiter);
	    int j;
	    for (j = 0; t.hasMoreTokens(); j++) {
		//k tempvarnames[j] = t.nextToken();
		tempvarnames.add (t.nextToken ());
	    }

	    int m = j;
	    varnames = new String[m];
	    
	    // read variable types
	    s = in.readLine ();
	    t = new StringTokenizer (s, delimiter);
	    char[] vartypes = new char[m];
	    
	    // figure out how to convert to char e.g. 
	    // java.lang.Character.parseChar()?
	    
	    for (int ii = 0; ii < m; ii++) {
		vartypes[ii] = t.nextToken().charAt(0);
	    }
	    
	    String[] intnames = new String[m];
	    String[] realnames = new String[m];
	    String[] stringnames = new String[m];	
	    
	    mi = md = ms = 0;
	    for (int ii = 0; ii < m; ii++) {
		switch (vartypes[ii]) {
		case 'i':
		    intnames[mi] = (String)(tempvarnames.get(ii));
		    mi++;
		    break;
		case 'd':
		case 'r':
		case 'f':
		    realnames[md] = (String)(tempvarnames.get(ii));
		    md++;
		    break;
		case 's':
		    stringnames[ms] = (String)(tempvarnames.get(ii));
		    ms++;
		    break;
		default:
		    System.out.println
			("Input file had an unknown variable type.");
		    break;
		}
	    }
	    
	    System.arraycopy (intnames, 0, varnames, 0, mi);
	    System.arraycopy (realnames, 0, varnames, mi, md);
	    System.arraycopy (stringnames, 0, varnames, mi + md, ms);
	    
	    // define intvars, realvars and stringvars arrays
	    
	    intvars = new int[mi][n];
	    realvars = new double[md][n];
	    stringvars = new String[ms][n];
	    String[][] tempvars = new String[m][n];
	    
	    int k;
	    
	    boolean more = true;
	    for (k = 0; k < n && more; k++) {
		s = in.readLine();
		if (s == null) {
		    more = false;
		}
		else {
		    t = new StringTokenizer (s, delimiter);
		    for (int i = 0; i < m; i++) {
			tempvars[i][k] = t.nextToken();
		    }
		}
	    }
	    
	    // copy records to appropriate typed arrays
	    
	    int ni = 0, nd = 0, ns = 0;
	    for (int ii = 0; ii < m; ii++) {
		switch (vartypes[ii]) {
		case 'i':
		    for (int l = 0; l < n; l++) {
			intvars[ni][l] = Integer.parseInt(tempvars[ii][l]);
		    }
		    ni++;
		    break;
		case 'd':
		case 'r':
		case 'f':
		    for (int l = 0; l < n; l++) {
			realvars[nd][l] = Double.parseDouble(tempvars[ii][l]);
		    }
		    nd++;
		    break;
		case 's':
		    System.arraycopy(tempvars[ii], 0, stringvars[ns], 0, n);
		    ns++;
		    break;
		default:
		    break;
		}
	    }
	}
	catch (IOException e)
	    {
		System.out.print("Error: " + e);
		System.exit(1);
	    }
    }
    
    /** The length of all the variables in the DataFrame.
     */
    public int length () {
	return n;
    }
    public int getVariableLength () {
	return n;
    }
    
    /** returns a two-dimensional array of all the real-valued variables 
     *  (ie not the integers) in the data frame.  Note that variables 
     *  that look like columns in the input file are stored as rows here 
     *  (ie dataframe.getRealvars()[0] returns the first real-valued column 
     *  in the input file).
     */
    public double[][] getRealvars() {
	double[][] temp = new double[md][n];
	for (int ii = 0; ii < md; ii++) {
	    System.arraycopy (realvars[ii], 0, temp[ii], 0, n);
	}
	return temp;
    }
    /** returns a two-dimensional array of all the integer-valued variables 
     *  in the data frame.
     */
    public int[][] getIntvars () {
	int[][] temp = new int[mi][n];
	for (int ii = 0; ii < mi; ii++) {
	    System.arraycopy (intvars[ii], 0, temp[ii], 0, n);
	}
	return temp;
    }
    
    /** like getRealVars, but for the String variables.
     */
    public String[][] getStringvars () {
	String[][] temp = new String[ms][n];
	for (int ii = 0; ii < ms; ii++) {
	    System.arraycopy (stringvars[ii], 0, temp[ii], 0, n);
	}
	return temp;
    }

    /** Accesses a real-valued variable in the data frame identified 
	with the name 'varname'.  
    */    
    public double[] r (String varname) {
	int ii;
	for (ii = mi; ii < (mi+md) && !varname.equals(varnames[ii]); ii++) {};
	if (ii == (mi+md)) {
	    System.out.println("No variable by the name " + varname);
	    System.exit(1);
	}
	double[] temp = new double[n];
	System.arraycopy (realvars[ii-mi], 0, temp, 0, n);
	return temp;     
    }

    public double[] r (String varname, int numcopies) {
      // make numcopies copies of real vector
      // hamada 11.14.01
      int jj;
      int ii;
      for (ii = mi; ii < (mi+md) && !varname.equals(varnames[ii]); ii++) {};
      if (ii == (mi+md)) {
	System.out.println("No variable by the name " + varname);
	System.exit(1);
      }
      double[] temp = new double[n*numcopies];
      for(jj=0;jj< numcopies;jj++){
         System.arraycopy (realvars[ii-mi], 0, temp, jj*n, n);
      }
      return temp;     
    }

    /**
       Creates an array of reals, all equal to 'val', of the same length
       as the variables in this data frame.  
    */
    public double[] r (double val) {
	double[] temp = new double[n];
	for (int ii = 0; ii < n; ii++) {
	    temp[ii] = val;
	}
	return temp;
    }

    /** creates an array of reals of length 'len', each entry equal to 
        'val'
    */
    public static double[] r (double val, int len) {
	double[] temp = new double[len];
	for (int ii = 0; ii < len; ii++) {
	    temp[ii] = val;
	}
	return temp;
    }  

    /**
     * Accesses the array of integers identified with the name 'varname'.
     */
    public int[] i (String varname) {
      int ii;
      for (ii = 0; ii < mi && !varname.equals(varnames[ii]); ii++) {
	// System.out.println("ii: " + ii + " " + varname);
	// System.out.println( varnames[ii]);
      };
      if (ii == mi) {
	System.out.println("No variable by the name " + varname);
	System.exit(1);
      }
      int[] temp = new int[n];
      System.arraycopy (intvars[ii], 0, temp, 0, n);
      return temp;     
    }

    /** Array of integers, all equal to 'val', of the same length as the 
        variables in the data frame.
    */
    public int[] i (int val) {
	int[] temp = new int[n];
	for (int ii = 0; ii < n; ii++) {
	    temp[ii] = val;
	}
	return temp;
    }

    /** 'len' copies of the integer 'val'.
     */
    public static int[] i (int val, int len) {
	int[] temp = new int[len];
	for (int ii = 0; ii < len; ii++) {
	    temp[ii] = val;
	}
	return temp;
    }

    /** Accesses the String array identified with the name 'varname'.  
     */
    public String[] s (String varname) {
      int ii;
      for (ii = (mi+md); ii < (mi+md+ms) && !varname.equals(varnames[ii]);
	   ii++) {};
      if (ii == (mi+md+ms)) {
	System.out.println("No variable by the name " + varname);
	System.exit(1);
      }
      String[] temp = new String[n];
      System.arraycopy (stringvars[ii-mi-md], 0, temp, 0, n);
      return temp;     
    }

    public int[] findRealVars (String firstchar) {
	// for linear model purposes, for example.  
	// find all real variables whose names start with 'x', for example.
	int count = 0;
	int ii;
	for (ii = mi; ii < (mi+md); ii++) {
	    if (varnames[ii].substring(0,firstchar.length()).equals(firstchar))
		count++;
	}
	if (count == 0) {
	    System.out.println ("No real variables in the data frame match the requested pattern");
	    System.exit (1);
	}
	int[] outvec = new int[count];
	for (ii = mi; ii < (mi+md); ii++) {
	    if (varnames[ii].substring(0,firstchar.length()).equals(firstchar))
		outvec[outvec.length-(count--)] = ii - mi;
	}
	return outvec;	
    }

    public int findVar (String varname) {
	// just copied from double[] r (String)
	int ii;
	for (ii = 0; ii < (mi+md+ms) && !varname.equals(varnames[ii]); ii++) {};
	if (ii == (mi+md+ms)) {
	    return -1;
	}
	if (ii < mi) 
	    return ii;
	if (ii < (mi+md)) 
	    return ii - mi;
	return ii - mi - md;
    }

    public int findRealVar (String varname) {
	// just copied from double[] r (String)
	int ii;
	for (ii = mi; ii < (mi+md) && !varname.equals(varnames[ii]); ii++) {};
	if (ii == (mi+md)) {
	    System.out.println("No variable by the name " + varname);
	    System.exit(1);
	}
	return (ii-mi);
    }

    public int findIntVar (String varname) {
      int ii;
      for (ii = 0; ii < mi && !varname.equals(varnames[ii]); ii++) {};
      if (ii == mi) {
	System.out.println("No variable by the name " + varname);
	System.exit(1);
      }
      return ii;
    }

    public int findStringVar (String varname) {
      int ii;
      for (ii = (mi+md); ii < (mi+md+ms) && !varname.equals(varnames[ii]);
	   ii++) {};
      if (ii == (mi+md+ms)) {
	System.out.println("No variable by the name " + varname);
	System.exit(1);
      }
      return (ii-mi-md);
    }

    /**
     * Creates an array of integers, starting with 0 and working up to 
     * one less than its length (the length is the same as the variables 
     * in the data frame).  
     */
    public int[] u () {
	int[] temp = new int[n];
	for (int ii = 0; ii < n; ii++) {
	    temp[ii] = ii;
	}
	return temp;
    }

    /**
       Creates an integer array, 0, 1, up to len - 1.  
     */
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

    public static int[] u_offset (int len, int start) {
	int[] temp = new int[len];
	for (int ii = start; ii < (len + start); ii++) {
	    temp[ii-start] = ii;
	}
	return temp;
    }

  public static void main (String args[]) {
    System.out.print("Testing DataFrame.\n");
/*
    DataFrame df = new DataFrame ("test.txt");
   System.out.println (df.varnames[0] + df.varnames[1] + df.varnames[2] + 
		      df.varnames[3] + df.varnames[4]);
    System.out.println ("n: " + df.n + " mi: " + df.mi + " md: " + df.md + 
			" ms: " + df.ms);
    System.out.println (df.realvars[0][0]);
    int[] one = df.i("one");
    System.out.println(one.length);
    System.out.println ("one: " + one[0] + "|" + one[1] + "|" + one[2] + "|" + 
			one[3] + "|" + one[4]);
    double[] y = df.r("y");
    System.out.println(y.length);
    System.out.println ("y: " + y[0] + "|" + y[1] + "|" + y[2] + "|" + 
			y[3] + "|" + y[4]);
*/
   DataFrame df = new DataFrame ("c:/hamada/java/yadasExamples/mshDegradYThetalm.txt");
   System.out.println (df.varnames[0] + df.varnames[1] + df.varnames[80]);
    System.out.println ("n: " + df.n + " mi: " + df.mi + " md: " + df.md + 
			" ms: " + df.ms);
    System.out.println ("1"+df.realvars[0][0]);
    System.out.println ("2"+df.realvars[80][399]);
    double[] one = df.r("x80");
    System.out.println(one.length);
    System.out.println ("one: " + one[0] + "|" + one[1] + "|" + one[2] + "|" + 
			one[3] + "|" + one[399]);
    double[] y = df.r("y");
    System.out.println(y.length);
    System.out.println ("y: " + y[0] + "|" + y[1] + "|" + y[2] + "|" + 
			y[3] + "|" + y[399]);
    System.exit(0);
  }

  // n is the number of records, the m's are the number of variables
  // of each type

  // For "varnames", the integer variables come first, then the reals, 
  // then the strings.  

  int n;
  int mi, md, ms;

  String[] varnames;
  double[][] realvars;
  int[][] intvars;
  String[][] stringvars;
    String delimiter = "|";

}
