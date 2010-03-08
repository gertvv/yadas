package gov.lanl.yadas;

/**
 * Class used to extract the rate of failures at a given node in a ReliableSystem as 
 * a function of unknown parameters.  
 */
public class SystemRateArgument implements ArgumentMaker {

    public SystemRateArgument (ReliableSystem system, double[] tot, int j) {
	this.system = system;
	timeontest = tot;
	this.j = j;
    }

    public double[] getArgument (double[][] params) {
	return new double[] { system.getIntegrator(j).combineFailureRates
			      (params[0]) * timeontest[j] };
    }

    ReliableSystem system;
    double[] timeontest;
    private int j;
}
