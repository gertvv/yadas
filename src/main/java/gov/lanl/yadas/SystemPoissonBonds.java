package gov.lanl.yadas;

import java.util.*;

/**
 * Defines an array of Poisson distributions, for use in system reliability problems.  
 */
public class SystemPoissonBonds {
    
    public SystemPoissonBonds (ReliableSystem system, MCMCParameter[] params,
			       double[] x, double[] timeOnTest) {
	this.system = system;
	this.x = x;
	this.timeOnTest = timeOnTest;
	this.lambda = params[0];
	this.pi = params[1];
	double[] piv = pi.getValue();	
	for (int i = 0; i < system.getSize(); i++) {
	    // loop over nodes. If 
	    if (timeOnTest[i] > 0) {
		if (((piv[i] > 0) && (piv[i] < 1)) || (!pi.areConstant(i))) {
		    bondlist.add
			( new MixtureBond		
			    ( new MCMCParameter[][] 
				{ new MCMCParameter[] {lambda, pi},
				  new MCMCParameter[] {lambda, pi} },
			      new ArgumentMaker[][] 
				{ new ArgumentMaker[] 
				  { new ShortConstantArgument (x, i),
				    new ShortScaledArgument (timeOnTest,0,i) },
				  new ArgumentMaker[] 
				  { new ShortConstantArgument (x, i),
				    new SystemRateArgument (system, timeOnTest,
							    i) },
				},
			      new Likelihood[] 
				{ new Poisson(), new Poisson() },
			      new ProbConverterArgument (1, i)));
		} else if (piv[i] == 1) {
		    // only derived probability
		    bondlist.add(new BasicMCMCBond 
			(new MCMCParameter[] {lambda, pi},
			 new ArgumentMaker[] {new ShortConstantArgument(x, i),
					      new SystemRateArgument
						  (system, timeOnTest, i),},
			 new Poisson() ));
		} else if (piv[i] == 0) {
		    // only native probability
		    bondlist.add(new BasicMCMCBond
			(new MCMCParameter[] {lambda, pi},
			 new ArgumentMaker[] {new ShortConstantArgument(x, i),
					      new ShortScaledArgument
						  (timeOnTest, 0, i), },
			 new Poisson() ));
		}	    
		((MCMCBond)bondlist.get(bondlist.size()-1)).setName("SystemPoissonBond." + i); 

	    }
	}
    }

    public ArrayList getList () {
	return bondlist;
    }

    ReliableSystem system;
    MCMCParameter lambda, pi;
    double[] x;
    double[] timeOnTest;
    ArrayList bondlist = new ArrayList();

}

