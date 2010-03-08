package gov.lanl.yadas;

/** 
 * Interface that a class must implement if one wants to attempt to 
 * tune the step sizes to achieve a given acceptance rate.  
 * Classes that implement this interface include TunableMCMCParameter, 
 * TunableMultipleParameterUpdate, and TunableMultiplicativeMCMCParameter.  
 */ 
public interface TunableMCMCUpdate extends MCMCUpdate {

    public double[] getStepSizes ();
    public void setStepSize (double s, int i);
    public void setStepSizes (double[] s);
    public int[] acceptances ();
    public void tuneoutput ();

    /* Still going to try to avoid the following: 
       public int[] attempts ();
     */
}
