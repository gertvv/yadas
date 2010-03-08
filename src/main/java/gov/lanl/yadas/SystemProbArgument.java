package gov.lanl.yadas;

/**
 * Class used together with ReliableSystem to extract the reliability of a given 
 * node as a function of unknown parameters.  
 */
public class SystemProbArgument implements ArgumentMaker {

    public SystemProbArgument (ReliableSystem system, int j) {
	this.system = system;
	this.j = j;
    }

    public double[] getArgument (double[][] params) {
	return new double[] { system.getIntegrator(j).combineProbabilities
			      (params) };
    }

    ReliableSystem system;
    private int j;
}
