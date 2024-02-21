import com.neuronrobotics.bowlerstudio.creature.ICadGenerator
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.sdk.addons.kinematics.DHLink
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.parametrics.LengthParameter

// code here

return new ICadGenerator(){
	LengthParameter dhParametersLength = new LengthParameter("dh Parameters Length", 40, [60, 20])
	
	@Override
	public ArrayList<CSG> generateCad(DHParameterKinematics arg0, int arg1) {
		dhParametersLength.setMM(arg0.getDH_R(arg1))
		
		DHLink link = arg0.getDhChain().getLinks().get(arg1)
		
		TransformNR aStep = new TransformNR(link.DhStep(0))
		
		ArrayList<CSG> cadParts = ScriptingEngine.gitScriptRun("https://github.com/BancroftKineticSystemsClass/KineticSystems2024Group09.git",
																 "Nametag.groovy")
 
		
		CSG horn = cadParts.get(0).movex(-dhParametersLength.getMM())
		CSG tag= cadParts.get(1).movex(-dhParametersLength.getMM())
		
		ArrayList<CSG> back =[]
		back.add(horn)
		back.add(tag)
		for(CSG c:back)
			c.setManipulator(arg0.getLinkObjectManipulator(arg1))
		return back;
	}

	@Override
	public ArrayList<CSG> generateBody(MobileBase arg0) {
		ArrayList<CSG> cadParts = ScriptingEngine.gitScriptRun("https://github.com/BancroftKineticSystemsClass/KineticSystems2024Group09.git",
			"Nametag.groovy")
		CSG servo =  cadParts.get(3)
		CSG base = cadParts.get(2)
		// TODO Auto-generated method stub
		ArrayList<CSG> back =[]
		back.add(servo)
		back.add(base)
		for(CSG c:back)
			c.setManipulator(arg0.getRootListener())
		for(DHParameterKinematics kin:arg0.getAllDHChains()) {
			CSG limbRoot =new Cube(1).toCSG()
			limbRoot.setManipulator(kin.getRootListener())
			back.add(limbRoot)

		}
		return back;
	}
	
	
}
