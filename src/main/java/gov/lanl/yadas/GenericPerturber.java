package gov.lanl.yadas;

/** 
 * Abstract class that can be used to help write new classes implementing 
 * the TunablePerturber interface if one wants useless step size methods
 */
public abstract class GenericPerturber implements TunablePerturber { 
    public double[] getStepSizes () {
	return new double[] { };
    }
    public void setStepSize (double s, int i) {
    }
    public void setStepSizes (double[] s) {
    }
}
