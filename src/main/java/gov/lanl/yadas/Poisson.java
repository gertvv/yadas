package gov.lanl.yadas;

/**
 * Poisson probability function: the first argument is the "data" and 
 * the second argument is the "mean".  
 * @author written by hamada 05.07.01, edited in a small way by tgraves 
 * 05.22.01 (nothing personal, mike)
 */
public class Poisson implements Likelihood {
// return Poisson loglikelihood

    public double compute (double[][] args) {
	double[] data = args[0];
	double[] mean = args[1];
	double ss =  0;
	for (int i = 0; i < data.length; i++) {
	    if (mean[i] != 0) {
		ss += -Tools.loggamma (data[i] + 1)
                    +data[i]*Math.log(mean[i])-mean[i];
	    }
	}
	return ss;
    }

    public double compute (double[][] args, int[] indices) {
	double[] data = args[0];
	double[] mean = args[1];
	double ss =  0;
	for (int j = 0; j < indices.length; j++) {
	    int i = indices[j];
	    if (mean[i] != 0)
		ss += -Tools.loggamma (data[i] + 1)
                    +data[i]*Math.log(mean[i])-mean[i];
	}
	return ss;
    }

}
