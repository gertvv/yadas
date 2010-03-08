package gov.lanl.yadas;

import java.util.*;

/**
 * Interface that defines terms in the posterior distribution; a 
 * statistical model is a product of MCMCBonds.  
 * The critical methods are the compute methods, which calculate 
 * differences of logged values of the terms, at the proposed new value 
 * of the parameters minus the current value.  
 */
public interface MCMCBond {

    //public double compute (ParameterChanger pc);

    public double compute (int whatami, double cand, int whichgroup);

    public double compute (int whatami, double[] newpar);

    public double compute (int[] whatami, double[][] newpar);

    public double compute (int[] whatami, double[][] newpar, int[] which);

    public void revise ();

    public void blank_value ();

    public double getCurrentValue ();

    public ArrayList getParamList ();

    public String getName();

    public void setName(String name);

}
