package gov.lanl.yadas;

import java.util.*;

/**
 * Another JumpPerturber used in the reversible jump example 
 * BinomialHypothesisTest.  When the proposed move to the unknown 
 * parameter (p[0], p[1]) is supposed to set them equal when they 
 * aren't already, it proposes to set them to a weighted average of 
 * themselves with weights given by the argument of that name to 
 * the constructor.  
 * @see gov.lanl.yadas.BinomialHypothesisTest
 */
public class EqualizingPerturber implements JumpPerturber {

    /**
     * @param which identifies which of the parameters corresponds to p
     */
    public EqualizingPerturber (int which, double[] weights) {
	this.which = which;
	this.weights = weights;
	double temp = 0.0;
	for (int i = 0; i < weights.length; i++) {
	    temp += weights[i];
	}
	for (int i = 0; i < weights.length; i++) {
	    weights[i] /= temp;
	}
    }

    public void perturb (double[][] candarray, int whoseTurn) { 
	double temp = 0.0;
	for (int i = 0; i < candarray[which].length; i++) {
	    temp += weights[i] * candarray[which][i];
	}
	//	temp /= candarray[which].length;
	for (int i = 0; i < candarray[which].length; i++) {
	    candarray[which][i] = temp;
	}
    }
    public int numTurns () { 
	return 1;
    }
    public double jacobian () { 
	// meaningless?  
	return 1.0;
    }
    public double density (double[][] oldarr, double[][] newarr, 
			   int whoseTurn) {
	return 1.0;
    }

    int which;
    double[] weights;
    static Random rand = new Random(System.currentTimeMillis());
}

