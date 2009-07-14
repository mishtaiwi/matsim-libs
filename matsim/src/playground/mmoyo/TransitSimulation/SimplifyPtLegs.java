package playground.mmoyo.TransitSimulation;

import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.PlanImpl;
import org.matsim.api.basic.v01.population.PlanElement;
import org.matsim.core.population.LegImpl;
import org.matsim.api.basic.v01.TransportMode
;/**
 *Deletes all pt activities in a plan
 */
public class SimplifyPtLegs {
	
	public SimplifyPtLegs(){
		
	}
	
	public void run(PlanImpl plan){
		for (int i= plan.getPlanElements().size()-1; i>=0; i--) {
			PlanElement pe = plan.getPlanElements().get(i);
			if (pe instanceof ActivityImpl) {
				String t = ((ActivityImpl)pe).getType();		
				if (t.equals("transf") || t.equals("transf on") || t.equals("transf off") || t.equals("wait pt") || t.equals("exit pt veh")){
					plan.removeActivity(i);
				}
			}else{
				LegImpl leg = ((LegImpl)pe);
				leg.setMode(TransportMode.pt);  //-> improve this, some of these legs are deleted
			}
				
		}
	}

}