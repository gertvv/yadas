package gov.lanl.yadas;

/**
 * Interface for specifying proposed moves to parameters as part of 
 * MultipleParameterUpdates.  
 * @see gov.lanl.yadas.MultipleParameterUpdate
 */
public interface Perturber {

    /**
     * This method defines the proposed move.  The current value of 
     * the unknown parameters will be stored in candarray, and this 
     * method modifies the elements of that two-dimensional array in 
     * place.  The modifications can depend on the value of "whoseTurn", 
     * so that each Perturber can in fact define several moves.  
     */
    public void perturb (double[][] candarray, int whoseTurn);    

    /**
     * This method returns the number of different moves defined by 
     * this perturber.   
     */
    public int numTurns ();

    /**
     * If the old value of the parameters is denoted by x and the new 
     * value is denoted by x', and the probability density of the 
     * perturber move is denoted by T(.|.), this method should return 
     * T(x|x')/T(x'|x).  
     */
    public double jacobian ();
}

