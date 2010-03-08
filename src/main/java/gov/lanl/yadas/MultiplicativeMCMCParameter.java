package gov.lanl.yadas;
/**
 * Class that allows a parameter to be updated multiplicatively: 
 * on the log scale, proposed new values are Gaussian centered on 
 * the old values.  Otherwise the same as MCMCParameter.   
 */
public class MultiplicativeMCMCParameter extends MCMCParameter {

    public MultiplicativeMCMCParameter (double[] v, double[] mss, String name)
    {
	super (v, mss, name);
    }

    public double[] candidate() {
	if (value[whoseTurn] == 0.0) return new double[] {ep};
	double[] temp = new double[1];
	scale = Math.exp(MetropolisStepSize[whoseTurn] * rand.nextGaussian());
	temp[0] = value[whoseTurn] * scale;
	return temp;
    }

    public double acceptanceProbability () {
	return scale * super.acceptanceProbability();
    }
    
    public void setEp (double ep) {
	this.ep = ep;
    }

    private double scale = 1.0;
    private double ep = 0.01;
}
