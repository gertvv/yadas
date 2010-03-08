package gov.lanl.yadas;

/**
 * Used to store parameters that are permutations of {0,1,,,,,n-1}.  
 * This parameter updates itself by looping over its elements, and 
 * for each element, proposing another element to exchange it with.  
 */
public class PermutationParameter extends MCMCParameter {

    public PermutationParameter (double[] init, int n, String name) {
	super (new double[n], Tools.rep(1, n), name);
	this.n = n;
	value = new double[n];
	for (int i = 0; i < n; i++) {
	    value[i] = init[i];
	}
    }

    public PermutationParameter (int n, String name) {
	super (new double[n], Tools.rep(1, n), name);
	this.n = n;
	value = new double[n];
	for (int i = 0; i < n; i++) {
	    value[i] = i;
	}
    }

    /* 
       I had to make minor modifications to most methods.  
       In update() and acceptanceProbability() for example I 
       had to make candidate an array and call the appropriate
       compute() method.  Bad design.  (It was easy to change 
       but still should have been more elegant).  
    */

    public void update () {
	if (isConstant) { return; }
	while (whoseTurn < value.length) {
	    candidate = candidate();
	    double ap = acceptanceProbability ();
	    if (ap > rand.nextFloat()) {
		takeStep();
	    }
	    whoseTurn++;
	}
	whoseTurn = 0;
    }
    
    public double[] candidate () {
	int trade = (int)(n * rand.nextDouble());
	double[] temp = new double[n];
	System.arraycopy (value, 0, temp, 0, n);
	temp[whoseTurn] = value[trade];
	temp[trade] = value[whoseTurn];
	return temp;
    }
    
    public double acceptanceProbability () {
	double lr = 0;
	MCMCBond[] baunds = relevantBonds();
	for (int j = 0; j < baunds.length; j++) {
	    lr = lr + baunds[j].compute(whatami[j], candidate); 
	}
	return Math.exp(lr);
    }
    
    public void takeStep () {
	System.arraycopy(candidate, 0, value, 0, n);
    }

    private int n;
    double[] candidate;
}
