import com.neuronrobotics.bowlerstudio.creature.ICadGenerator
import com.neuronrobotics.bowlerstudio.physics.TransformFactory
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.sdk.addons.kinematics.DHLink
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

import eu.mihosoft.vrl.v3d.*
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.parametrics.LengthParameter

// code here

return new ICadGenerator(){
	//LengthParameter dhParametersLength = new LengthParameter("dh Parameters Length", 40, [60, 20])
	
	def cadGen = ScriptingEngine.gitScriptRun("https://github.com/BancroftKineticSystemsClass/KineticSystems2024Group09.git",
															 "Nametag.groovy",[])
	@Override
	public ArrayList<CSG> generateCad(DHParameterKinematics arm, int linkIndex) {
		//dhParametersLength.setMM(arg0.getDH_R(arg1))
		
		DHLink link = arm.getDhChain().getLinks().get(linkIndex)
		
		TransformNR aStep = new TransformNR(link.DhStep(0))
		
		
		String motorType=null;
		String motorSize=null;
		if(arm.getNumberOfLinks()-1!=linkIndex) {
			motorType= arm.getElectroMechanicalType(linkIndex+1)
			motorSize = arm.getElectroMechanicalSize(linkIndex+1)
		}
		String shaftType= arm.getShaftType(linkIndex)
		String shaftSize = arm.getShaftSize(linkIndex)
		
		println cadGen
		ArrayList<CSG> cadParts=  cadGen.makeLinks(aStep,shaftType,shaftSize,motorType,motorSize)
		
		Transform nrToCSG = TransformFactory.nrToCSG(aStep).inverse()
		
		CSG horn = cadParts.get(0).transformed(nrToCSG)
		CSG tag= cadParts.get(1).transformed(nrToCSG)
		
		ArrayList<CSG> back =[]
		back.add(horn)
		back.add(tag)
		for(CSG c:back)
			c.setManipulator(arm.getLinkObjectManipulator(linkIndex))
		return back;
	}

	@Override
	public ArrayList<CSG> generateBody(MobileBase arg0) {
		String motorType = arg0.getAppendages().get(0).getElectroMechanicalType(0)
		String motorSize = arg0.getAppendages().get(0).getElectroMechanicalSize(0)

		ArrayList<CSG> cadParts = cadGen.makeBase(motorType,motorSize)
		CSG servo =  cadParts.get(0)
		CSG base = cadParts.get(1)
		// TODO Auto-generated method stub
		ArrayList<CSG> back =[]
		back.add(servo)
		back.add(base)
		for(CSG c:back)
			c.setManipulator(arg0.getRootListener())

		return back;
	}
	
	
}
