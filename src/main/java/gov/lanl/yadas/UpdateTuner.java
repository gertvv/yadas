package gov.lanl.yadas;

/**
 * This class is used in YADAS to define algorithms in which step sizes are tuned 
 * automatically.  It should work with any MCMCUpdate that features an array of step sizes.  
 * (In practice, it has been observed to work well with the default update, log and logit 
 * scale updates, and most MultipleParameterUpdates in nonpathological situations.  
 * It is based on the observation (in a very simple problem) that the 
 * logit of the acceptance rate is very linearly related to the log of the 
 * step size.  By default, the trial experiment tries 13 different step sizes for 50 
 * iterations each (so that the burn-in period is 650 iteration), and attempts to set 
 * each acceptance rates to about 1/e.  An UpdateTuner is defined by beginning with 
 * a TunableMCMCUpdate (for example, a parameter called theta) and inserting the text 
 * new UpdateTuner (theta) in the array that defines the YADAS algorithm.    
*/
public class UpdateTuner implements MCMCUpdate {

    public UpdateTuner ( TunableMCMCUpdate upd ) {
	this (upd, upd.getStepSizes(), 13, 50, 1, Math.exp(-1), 
	      true, -3.0, 5.0);
    }

    public UpdateTuner ( TunableMCMCUpdate upd, int numbsizes,
		int numits, int numcycles, double probtarget) {
	this (upd, upd.getStepSizes(), numbsizes, numits, numcycles, probtarget, 
	      true, -3.0, 5.0);
    }

    public UpdateTuner ( TunableMCMCUpdate upd, double[] sss, 
			 int numbsizes, int numits, int numcycles, 
			 double probtarget) {
	this (upd, sss, numbsizes, numits, numcycles, 
	      probtarget, true, -3.0, 5.0);
    }
    
    public UpdateTuner ( TunableMCMCUpdate upd, double[] sss, 
			 int numbsizes, int numits, int numcycles, 
			 double probtarget, boolean b_fixed, 
			 double mua, double sigmaa) {
	this.upd = upd;
	this.sss = sss;
	this.numsizes = numbsizes;
	this.numits = numits;
	this.numcycles = numcycles;
	this.probtarget = probtarget;
	this.b_fixed = b_fixed;
	this.mua = mua;
	this.sigmaa = sigmaa;
	numsizes += (((numsizes % 2) == 1) ? 0 : 1); // make it odd
	// make matrix of candidate step sizes
	css = new double[numsizes][sss.length];
	accmat = new int[numsizes][sss.length];
	oldacc = new int[sss.length];
	int os = ((int)(numsizes/2));
	for (int i = 0; i < numsizes; i++) {
	    for (int j = 0; j < sss.length; j++) {
		css[i][j] = sss[j] * Math.pow(radix, (i - os));
		accmat[i][j] = 0;
	    }
	}
	for (int j = 0; j < sss.length; j++) {
	    oldacc[j] = 0;
	}
    }

    public void update () {
	// depending on iteration number (itno), 
	// set upd's step sizes to be
	int which = itno % numsizes;
	double[] ttemp;
	if (!done) {
	    if (itno == (numsizes * numits)) {
		done = true;
		int[] xvec = new int[numsizes];
		double[] svec = new double[numsizes];
		double[] temp = new double[sss.length];
		for (int j = 0; j < sss.length; j++) {
		    if (sss[j] != 0.0) {
			//System.out.println("j loop: " + j);
			for (int i = 0; i < numsizes; i++) {
			    xvec[i] = accmat[i][j];
			    svec[i] = css[i][j];
			}
			//System.out.println(expandvec(xvec));
			//System.out.println(expandvec(svec));
			ttemp = nrlogist(xvec, numits, svec, nrit, b_fixed, 
					 mua, sigmaa);
			temp[j] = Math.exp((Math.log(probtarget/(1-probtarget))
					    - ttemp[0])/ttemp[1]);
			//System.out.println ("Recommended step size: " + temp[j]);
			upd.setStepSize(temp[j], j);
		    }
		}
		upd.tuneoutput ();
		// write output somewhere
	    } else {
		upd.setStepSizes(css[which]);
	    }
	}
	upd.update ();
	if (!done) {
	    int[] acc = upd.acceptances ();
	    for (int j = 0; j < acc.length; j++) {
		accmat[which][j] += acc[j] - oldacc[j];
	    }
	    System.arraycopy(acc, 0, oldacc, 0, acc.length);
	}
	// when itno reaches numits * numsizes, perform logistic regression, 
	// set all step sizes to the appropriate values from then on.  
	itno += 1;
    }

    public static String expandvec (int[] vec) {
	String out = "";
	for (int i = 0; i < vec.length; i++) {
	    out += vec[i] + "|";
	}
	return out;
    }

    public static String expandvec (double [] vec) {
	String out = "";
	for (int i = 0; i < vec.length; i++) {
	    out += vec[i] + "|";
	}
	return out;
    }

    public String accepted () {
	return upd.accepted ();
    }
    public void updateoutput () {
	upd.updateoutput ();
    }
    public void finish () {
	upd.finish ();
    }
    public double[] getStepSizes () { 
	return upd.getStepSizes ();
    }
    public void setStepSize (double s, int i) {
	upd.setStepSize(s, i);
    }
    public void setStepSizes (double[] s) {
	upd.setStepSizes (s);
    }
    /*
    public int[] acceptances () {
	
    }
    */
    //public int[] attempts () {}

    public static double[] nrlogist (int[] x, int n, double[] es, int nrit,
				     boolean b_fixed, 
				     double mua, double sigmaa) {
	int[] nv = new int[x.length];
	for (int i = 0; i < x.length; i++) 
	    nv[i] = n;
	return nrlogist(x, nv, es, nrit, b_fixed, mua, sigmaa);
    }

    /* tgraves 14 March 2007: 
       made the modification to check whether the recommended step size 
       is infinite, and if so to run the algorithm with estimated b.  
       This was in response to a problem of Hamada's (ADT).  
     */
    public static double[] nrlogist (int[] x, int[] n, double[] es, int nrit,
				     boolean b_fixed, 
				     double mua, double sigmaa) {
	double[] out; 
	double test;
	if (b_fixed) {
	    out = nrlogist_b_fixed (x, n, es, nrit, mua, sigmaa);
	    test = Math.exp(-out[0]/out[1]);
	    if (!Double.isInfinite (test) & !Double.isNaN (test)) return out;
	} 
	return nrlogist_b_estimated (x, n, es, nrit, mua, sigmaa);
    }

    public static double[] nrlogist_b_fixed 
	(int[] x, int[] n, double[] es, int nrit, double mua, double sigmaa) {
	double a = 0.0, b = -1.12145;
	double[] npobs = new double[x.length];
	double[] nphat = new double[x.length];
	double[] np1p = new double[x.length];
	double[] s = new double[x.length];
	for (int i = 0; i < x.length; i++) {
	    npobs[i] = (x[i] + 0.0);
	    s[i] = Math.log(es[i]);
	}
	//System.out.println("x = " + expandvec(x));
	//System.out.println("n = " + expandvec(n));
	//System.out.println("s = " + expandvec(s));
	for (int k = 0; k < nrit; k++) {
	    for (int i = 0; i < x.length; i++) {
		nphat[i] = n[i]/(1+Math.exp(-a-b*s[i]));
		np1p[i] = nphat[i] * (1 - nphat[i]/n[i]);
	    }
	    //System.out.println("npobs = " + expandvec(npobs));
	    //System.out.println("nphat = " + expandvec(nphat));
	    a += ((sum(npobs) - sum(nphat) - (a - mua)/sigmaa/sigmaa)) / 
		(sum(np1p) + 1/sigmaa/sigmaa);
	    //System.out.println("a = " + a + ", b= " + b);
	}
	//System.out.println(sum(new double[] { 1, 2, 3, 4, -25 }));
	//System.out.println ("a = " + a);
	//System.out.println ("b = " + b);
	return new double[] { a, b };
    }

    public static double[] nrlogist_b_estimated 
	(int[] x, int[] n, double[] es, int nrit, double mua, double sigmaa) {
	// this method replaced the previous, 10/06/04, TLG.
	double a = 0.0, b = 0.0, A = 0.0, B = 0.0, C = 0.0, dev0 = 0.0, 
	    dev1 = 0.0;
	//double[] pobs = new double[x.length];
	double[] phat = new double[x.length];
	double[] s = new double[x.length];
	for (int i = 0; i < x.length; i++) {
	    //pobs[i] = (x[i] + 0.0) / n[i];
	    s[i] = Math.log(es[i]);
	}
	for (int k = 0; k < nrit; k++) {
	    A = B = C = dev0 = dev1 = 0.0;
	    for (int i = 0; i < x.length; i++) {
		phat[i] = 1/(1+Math.exp(-a-b*s[i]));
		A += n[i] * phat[i] * (1-phat[i]);
		B += n[i] * s[i] * phat[i] * (1-phat[i]);
		C += n[i] * s[i] * s[i] * phat[i] * (1-phat[i]);
		dev0 += (x[i] + 0.0) - (n[i] + 0.0) * phat[i];
		dev1 += s[i] * ((x[i] + 0.0) - (n[i] + 0.0) * phat[i]);
	    }
	    a += (C*dev0 - B*dev1)/(A*C-B*B);
	    b += (-B*dev0 + A*dev1)/(A*C-B*B);
	}
	return new double[] { a, b };
    }

    public static double sum (double[] x) {
	double out = 0.0;
	for (int i = 0; i < x.length; i++) 
	    out += x[i];
	return out;
    }

    public static void main (String[] args) { 
	double[] ttemp = nrlogist 
	    ( new int[] { 32, 31, 35, 28, 13, 9, 5, 3, 0, 0, 0, 0, 1 },
	      new int[] { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50,50},
	      new double[] { 0.00625, 0.0125, 0.025, 0.05, 0.1, 0.2, 0.4, 0.8, 
			     1.6, 3.2, 6.4, 12.8, 25.6 }, 50, true, -3, 5 );
	System.out.println(" a = " + ttemp[0] + ", b = " + ttemp[1]);
    }

    private TunableMCMCUpdate upd;
    private double[] sss;
    private int numsizes, numits, numcycles;
    private int itno = 0;
    private double probtarget = Math.exp(-1);
    public int nrit = 50;
    public double radix = 2.0;
    private int[] successes;
    private int[] trials;
    private double[][] css;
    private int[][] accmat;
    private boolean done = false;
    private int[] oldacc;
    private boolean b_fixed = true;
    private double mua = -3.0, sigmaa = 5.0;
}

    /* The version with the cross terms is almost certainly necessary
    public double nrlogist (int[] x, int[] n, double[] es) {
	// return the value that sets the acceptance prob to probtarget.  
	double a = 0.0, b = 0.0;
	double[] pobs = new double[x.length];
	double[] phat = new double[x.length];
	double[] p1p = new double[x.length];
	double[] spobs = new double[x.length];
	double[] sphat = new double[x.length];
	double[] s2p1p = new double[x.length];
	double[] s = new double[x.length];
	for (int i = 0; i < x.length; i++) {
	    pobs[i] = (x[i] + 0.0)/ n[i];
	    s[i] = Math.log(es[i]);
	    spobs[i] = s[i] * pobs[i];
	}
	//System.out.println("x = " + expandvec(x));
	//System.out.println("n = " + expandvec(n));
	//System.out.println("s = " + expandvec(s));
	for (int k = 0; k < nrit; k++) {
	    for (int i = 0; i < x.length; i++) {
		phat[i] = 1/(1+Math.exp(-a-b*s[i]));
		p1p[i] = phat[i] * (1 - phat[i]);
		sphat[i] = s[i] * phat[i];
		s2p1p[i] = s[i] * s[i] * p1p[i];
	    }
	    //System.out.println("pobs = " + expandvec(pobs));
	    //System.out.println("phat = " + expandvec(phat));
	    a += (sum(pobs) - sum(phat)) / sum(p1p);
	    b += (sum(spobs) - sum(sphat)) / sum(s2p1p);
	    //System.out.println("a = " + a + ", b= " + b);
	}
	//System.out.println(sum(new double[] { 1, 2, 3, 4, -25 }));
	return Math.exp((Math.log(probtarget/(1-probtarget)) - a)/b);
    }
    */

