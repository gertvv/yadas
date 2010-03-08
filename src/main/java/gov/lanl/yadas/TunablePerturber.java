package gov.lanl.yadas;

/**
 * Interface used by proposal mechanisms in TunableMultipleParameterUpdates.  
 * @see gov.lanl.yadas.TunableMultipleParameterUpdate
 */
public interface TunablePerturber extends Perturber {

       public double[] getStepSizes ();
       public void setStepSize (double s, int i);
       public void setStepSizes (double[] s);

}
