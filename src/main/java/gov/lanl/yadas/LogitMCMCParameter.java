package gov.lanl.yadas;

/**
 * A LogitMCMCParameter is just like an ordinary MCMCParameter, except that 
 * it is updated on the logit scale.  Useful for parameters which are 
 * probabilities close to either zero or one.  
 */
public class LogitMCMCParameter extends MCMCParameter {

    public LogitMCMCParameter (double[] v, double[] mss, String name)
    {
	super (v, mss, name);
    }

    public double[] candidate() {
	//if (value[whoseTurn] == 0.0) return new double[] {ep};
	//if (value[whoseTurn] == 1.0) return new double[] {1-ep};
	double[] temp = new double[1];
	scale = Math.exp(MetropolisStepSize[whoseTurn] * rand.nextGaussian());
	temp[0] = 1 / (1 + scale * (1 - value[whoseTurn]) / value[whoseTurn]);
	adj = temp[0] * (1 - temp[0]) / value[whoseTurn] / 
	    (1 - value[whoseTurn]);
	return temp;
    }

    public double acceptanceProbability () {
	return adj * super.acceptanceProbability();
    }
    
    public void setEp (double ep) {
	this.ep = ep;
    }

    private double scale = 1.0;
    private double adj = 1.0;
    private double ep = 0.01;
}
