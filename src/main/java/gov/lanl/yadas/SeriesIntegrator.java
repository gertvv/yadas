package gov.lanl.yadas;

/**
 * Used in system reliability problems to define subsystems that 
 * are combinations of components in series.  
 * subsystem probability = product of component probabilities,
 * subsystem failure rate = sum of component failure rates 
 * (assumes exponential failures.  Not sure how to get around that.)
 */
public class SeriesIntegrator implements ComponentIntegrator {

    public SeriesIntegrator (int[] components, int subsystem) {
	this.components = components;
	this.subsystem = subsystem;
    }

    public double combineProbabilities (double[][] componentprobs) {
	double out = 1;
	int[] temp = getComponents();
	for (int i = 0; i < temp.length; i++) {
	    out *= componentprobs[0][temp[i]];
	}
	return out;
    }

    public double combineFailureRates (double[] componentrates) {
	double out = 0;
	int[] temp = getComponents();
	for (int i = 0; i < temp.length; i++) {
	    out += componentrates[temp[i]];
	}
	return out;
    }

    public int getSubsystem () {
	return subsystem;
    }
    
    public int[] getComponents () {
	return components;
    }

    private int subsystem;
    private int[] components;

}
