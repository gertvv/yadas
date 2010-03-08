package gov.lanl.yadas;

import java.util.*;

/** 
 * Class used for defining an array of univariate binomial distributions 
 * in system reliability problems.  
 */
public class SystemBinomialBonds {

    public SystemBinomialBonds (ReliableSystem system, MCMCParameter[] params,
				double[] x, double[] n) {
	this.system = system;
	this.x = x;
	this.n = n;
	int veclen = 2;
	if (params.length > 2) {
	    veclen = 3;
	}
	paramvec = new MCMCParameter[veclen];
	this.p = paramvec[0] = params[0];
	this.pi = paramvec[1] = params[1];
	if (veclen == 3) {
	    paramvec[2] = params[2];
	}
	double[] piv = pi.getValue();	
	for (int i = 0; i < system.getSize(); i++) {
	    // loop over nodes. If 
	    if (n[i] > 0) {
		if (((piv[i] > 0) && (piv[i] < 1)) || (!pi.areConstant(i))) {
		    bondlist.add
			( new MixtureBond		
			    ( new MCMCParameter[][] 
				{ paramvec,
				  paramvec },
			      new ArgumentMaker[][] 
				{ new ArgumentMaker[] 
				  { new ShortConstantArgument (x, i),
				    new ShortConstantArgument (n, i),
				    new ShortIdentityArgument (0, i), },
				  new ArgumentMaker[] 
				  { new ShortConstantArgument (x, i),
				    new ShortConstantArgument (n, i),
				    new SystemProbArgument (system, i) },
				},
			      new Likelihood[] 
				{ new Binomial(), new Binomial() },
			      new ProbConverterArgument (1, i)));
		} else if (piv[i] == 1) {
		    // only derived probability
		    bondlist.add(new BasicMCMCBond 
			(paramvec,
			 new ArgumentMaker[] {new ShortConstantArgument(x, i),
					      new ShortConstantArgument(n, i),
					      new SystemProbArgument
						  (system, i)},
			 new Binomial()));
		} else if (piv[i] == 0) {
		    // only native probability
		    bondlist.add(new BasicMCMCBond
			(paramvec,
			 new ArgumentMaker[] {new ShortConstantArgument(x, i),
					      new ShortConstantArgument(n, i),
					      new ShortIdentityArgument(0, i)},
			 new Binomial()));
		}
		((MCMCBond)bondlist.get(bondlist.size()-1)).setName("SystemBinomialBond." + i); 
	    }
	}
    }

    public ArrayList getList () {
	return bondlist;
    }

    ReliableSystem system;
    MCMCParameter p, pi;
    double[] x;
    double[] n;
    ArrayList bondlist = new ArrayList();
    MCMCParameter[] paramvec;
}

