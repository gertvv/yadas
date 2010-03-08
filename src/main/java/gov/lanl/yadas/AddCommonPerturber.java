package gov.lanl.yadas;

import java.util.*;

/** 
 * AddCommonPerturber: used to create MCMC algorithms that include  
 * Metropolis moves in which one adds the same random deviate to 
 * several different parameters simultaneously.  NewAddCommonPerturber 
 * is more intuitive.  
 * @see gov.lanl.yadas.MultipleParameterUpdate
 * @author TLG
 */
public class AddCommonPerturber implements TunablePerturber {

    // arguments to constructor:
    // int[] whichexpand: which parameters have expanders 
    // int dominant: which parameter determines how many updates?
    // int[][] expanders
    // double mss

    public AddCommonPerturber (int numparams, int paramlength, int dominant,
			       double[] mss) {
	this (numparams, paramlength, dominant,
	      new int[] {}, new int[][] { new int[] {} }, mss);
    }
				     

    public AddCommonPerturber (int numparams, int paramlength, int dominant, 
			       int[] whichexpand, int[][] expanders, 
			       double[] mss) {
	this.dominant = dominant;
	this.whichexpand = whichexpand;
	this.expanders = expanders;
	this.mss = mss;
	expandcodes = new int[numparams];
	expandmat = new ArrayList[numparams];
	int[] temp = new int[paramlength];
	for (int i = 0; i < paramlength; i++) {
	    temp[i] = i;
	}
	for (int i = 0; i < numparams; i++) {
	    expandmat[i] = new ArrayList();
	    expandcodes[i] = -1;
	    //	    System.arraycopy (temp, 0, expandmat[i], 0, paramlength);
	}
	for (int i = 0; i < whichexpand.length; i++) {
	    expandcodes[whichexpand[i]] = i;
	    //	    expandmat[whichexpand[i]] = expanders[i];
	}

	// put the expanders into a collection so that we can do random 
	// access on them.   the reason for this is that otherwise we 
	// might add the same constant to the same component multiple times.  

	for (int i = 0; i < numparams; i++) {
	    if (expandcodes[i] < 0) {
		for (int j = 0; j < paramlength; j++) {
		    expandmat[i].add(new Integer(temp[j]));
		}
	    }
	    else {
		for (int j = 0; j < expanders[expandcodes[i]].length; j++) {
		    expandmat[i].add(new Integer(expanders[expandcodes[i]][j]));
		}
	    }
	}
    }

  public double mean(double[] vec) {
    double out = 0.0;
    for (int i = 0; i < vec.length; i++) {
      out += vec[i];
    }
    return out / vec.length;
  }

    public void perturb (double[][] candarray, int whoseTurn) {
	Integer wt = new Integer(whoseTurn);
	double temp = mss[whoseTurn] * rand.nextGaussian();
	int where = 0;
	for (int j = 0; j < expandcodes.length; j++) {
	    for (int k = 0; k < candarray[j].length; k++) {
		Integer kk = new Integer(k);
		if (expandmat[j].contains(kk)) {
		    where = expandmat[j].indexOf(kk);
		    if (expandmat[dominant].get(where).equals(wt)) {
		      candarray[j][where] += temp;
		    }
		}
	    }
	}
    }

    public int numTurns () {
	return mss.length;
    }

    public double jacobian () {
	return 1.0;
    }

    public double[] getStepSizes () {
	double[] tem = new double[mss.length];
	System.arraycopy (mss, 0, tem, 0, mss.length);
	return tem;
    }
    public void setStepSize (double s, int i) {
	mss[i] = s;
    }
    public void setStepSizes (double[] s) {
	System.arraycopy (s, 0, mss, 0, s.length);
    }

    private int dominant;
    private int[] whichexpand;
    private int[][] expanders;
    private double[] mss;

    private int[] expandcodes;
    private ArrayList[] expandmat;

    static Random rand = new Random();
}
