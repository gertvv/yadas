package gov.lanl.yadas;

/** 
 * Interface used in reversible jump MCMC.  Requires the software writer 
 * to tell YADAS how to compute the density function of the proposal 
 * distribution as a function of the old value.  
 * @see gov.lanl.yadas.ReversibleJumpUpdate
 */
public interface JumpPerturber extends Perturber {

    public double density (double[][] oldarr, double[][] newarr, 
			   int whoseTurn);

}

