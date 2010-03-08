package gov.lanl.yadas;

/**
 * Class implementing MCMCUpdate that does nothing.  
 */
public class NullUpdate implements MCMCUpdate
{
    public void update () {}

    public String accepted () { return "Null";} 

    public void updateoutput () {};

    public void finish () {};

}
