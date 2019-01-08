package net.rodrigoamaral.dspsp;

import java.io.FileNotFoundException;

import net.rodrigoamaral.dspsp.adapters.JMetalDSPSPExtendedAdapter;
import net.rodrigoamaral.dspsp.project.DynamicProject;

/**
 *
 * Wraps MODPSP model as a jMetal DoubleProblem.
 *
 */
@SuppressWarnings("serial")
public class DSPSProblemExtended extends DSPSProblem {

    public DSPSProblemExtended(String projectPropertiesFileName) throws FileNotFoundException {
        super(projectPropertiesFileName);
    }
    
    protected void initDSPSPAdapterFromFile(String projectPropertiesFileName) throws FileNotFoundException {
    	dspsp = new JMetalDSPSPExtendedAdapter(projectPropertiesFileName);
    }

    public DSPSProblemExtended(DynamicProject project) {
        super(project);
    }

	protected void initDSPSPAdapterFromProject(DynamicProject project) {
		dspsp = new JMetalDSPSPExtendedAdapter(project);
	}

}
