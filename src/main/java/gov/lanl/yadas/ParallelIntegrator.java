package gov.lanl.yadas;

/**
 *   For creating subsystems in parallel. 
 *   Written based on SeriesIntegrator, 3/20/03.  
 *   subsystem probability = 1 - product of ( 1 - component probabilities ),
 *   subsystem failure rate = sum of component failure rates 
 */
public class ParallelIntegrator implements ComponentIntegrator {

    public ParallelIntegrator (int[] components, int subsystem) {
	this.components = components;
	this.subsystem = subsystem;
    }

    public double combineProbabilities (double[][] componentprobs) {
	double out = 1;
	int[] temp = getComponents();
	for (int i = 0; i < temp.length; i++) {
	    out *= (1 - componentprobs[0][temp[i]]);
	}
	return 1 - out;
    }

    public double combineFailureRates (double[] componentrates) {
	//nonsense; only made sense for SeriesIntegrator
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
