package gov.lanl.yadas;
//
//

/**
 * Interface that must be satisfied by MCMCParameter and any potential enhancements 
 * or replacements to it.  For example, MCMCParameter does not store old values, 
 * writes output to a file, and does not handle streaming data; potentially new 
 * classes implementing this interface could generalize these behaviors.
 */
public interface MCMCNode {

    public void finish ();
		
}
