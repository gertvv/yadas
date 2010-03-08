package gov.lanl.yadas;

/**
 * Largely useless class used in ReliableSystem as a placeholder for 
 * real objects that implement ComponentIntegrator.  
 */
public class NullIntegrator implements ComponentIntegrator {

    public NullIntegrator (int i) {
	subsystem = i;
    }

    public double combineProbabilities (double[][] componentprobs) {
	return 1;
    }

    public double combineFailureRates (double[] componentrates) {
	return 1;
    }

    public int getSubsystem () {
	return subsystem;
    }

    public int[] getComponents () {
	return new int[] {};
    }

    private int subsystem;

}
