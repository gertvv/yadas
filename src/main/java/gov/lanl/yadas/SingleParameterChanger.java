package gov.lanl.yadas;

/**
 * Basic class implementing ParameterChanger interface; should be ignored by users.  
 */ 
public class SingleParameterChanger implements ParameterChanger{

    public void change (double[][] preargs) {
	preargs[whatami][which] = cand;
    }

    public void setAll (int w, double c, int w2) {
	whatami = w;
	cand = c;
	which = w2;
    }

    int whatami = 0, which = 0;
    double cand = 1.0;
}
