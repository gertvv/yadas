package gov.lanl.yadas;

/**
 * Converts {p} into {p, 1-p}.  Used in SystemBinomialBonds and 
 * SystemPoissonBonds.  
 */
public class ProbConverterArgument implements ArgumentMaker {

    public ProbConverterArgument (int whichparam, int whichelement) {
	this.whichparam = whichparam;
	this.whichelement = whichelement;
    }

    public double[] getArgument (double[][] args) {
	double temp = args[whichparam][whichelement];
	return new double[] { temp, 1 - temp };
    }
    
    int whichparam, whichelement;
}
