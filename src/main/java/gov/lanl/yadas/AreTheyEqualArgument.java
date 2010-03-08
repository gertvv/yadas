package gov.lanl.yadas;

/**
 * AreTheyEqualArgument: used, for example, in BinomialHypothesisTest.java.  
 * If p[0] = p[1], returns the array (0, 1-weight), whereas if 
 * p[0] != p[1], returns the array (weight, 0).  Therefore, this can 
 * be used in a situation where with probability 'weight', p[0] and p[1] 
 * are nonequal (exchangeable, for example), and with the remaining 
 * probability, p[0] and p[1] are equal.  This class cannot handle 
 * the case where 'weight' is random: to do this, create a new class 
 * which accepts a MCMCParameter instead of the double w.  
 * @see gov.lanl.yadas.BinomialHypothesisTest
 * @author TLG
 */
public class AreTheyEqualArgument implements ArgumentMaker {

    /** 
     * @param which this integer describes which of the parameters 
     * involved in this bond represents 'p'.  
     */
    public AreTheyEqualArgument (int which, double w) {
	which_is_p = which;
	weight = w;
    }

    public double[] getArgument (double[][] params) {
	double temp0 = (params[which_is_p][0] == params[which_is_p][1]) ?
	    0 : weight;
	double temp1 = (params[which_is_p][0] == params[which_is_p][1]) ?
	    (1-weight) : 0;
	return new double[] {temp0, temp1};
    }

    int which_is_p;
    double weight;

}
