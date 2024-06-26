// code here

import com.neuronrobotics.bowlerstudio.vitamins.Vitamins
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR
import com.neuronrobotics.bowlerstudio.physics.TransformFactory

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.Hexagon
import eu.mihosoft.vrl.v3d.Transform
import eu.mihosoft.vrl.v3d.parametrics.LengthParameter

class NamedCadGenerator{
	LengthParameter nameTagHeightParam = new LengthParameter("Nametag Height", 40, [60, 20])
	//LengthParameter dhParametersLength = new LengthParameter("dh Parameters Length", 40, [60, 20])

	LengthParameter tailLength		= new LengthParameter("Cable Cut Out Length",30,[500, 0.01])

	double moveTagFromCenter = 5

	//double baseX = dhParametersLength.getMM() - moveTagFromCenter
	double baseZ = 8
	double baseY =nameTagHeightParam.getMM()
	double textHeight =2
	double textToEdgeSpacing =1
	double ringDiameter = 20
	double holeDiameter=15
	CSG nameLocal = null
	double servoMaxZ =0;

	CSG getName(double baseX) {
		println "Creating name of size "+baseX
		//if(nameLocal==null) {
			nameLocal = CSG.text("Mr. Harrington", textHeight)
					.movez(baseZ)
			double xscale = (baseX-(textToEdgeSpacing*2))/nameLocal.getTotalX()

			double yScale = (baseY-(textToEdgeSpacing*2))/nameLocal.getTotalY()
			nameLocal=nameLocal
					.toXMin()
					.toYMin()
					.scalex(xscale)
					.scaley(yScale)
					.movex(textToEdgeSpacing)
					.movey(textToEdgeSpacing)
		//}

		return nameLocal
	}
	ArrayList<CSG> makeBase(String motorType, String motorSize){
		tailLength.setMM(130)
		
		double baseX = 40;
		CSG servo = Vitamins.get(motorType,motorSize)
		servoMaxZ = servo.getMaxZ();
		
		double servoy = servo.getTotalY()
		double servox = servo.getTotalX()
		double basex = servox + baseX + moveTagFromCenter
		double basey = servoy + moveTagFromCenter
		HashMap<String,Object> servoConfig = Vitamins.getConfiguration( motorType, motorSize)
		double servoHeight = servoConfig.get("servoShaftSideHeight")

		CSG base = new Cube(basex,basey,servoHeight).toCSG()
				.toZMax()
				.difference(servo)
		baseY =servoHeight
		CSG sideName = getName(baseX).rotx(-90)
				.toYMax()
				.movey(base.getMinY())
				.toXMin()
				.movex(base.getMinX()+textToEdgeSpacing)
				.toZMin()
				.movez(base.getMinZ())
		base=base.union(sideName)

		base.setName("servoBase")


		base.setManufacturing({ toMfg ->
			return toMfg.toZMin()//move it down to the flat surface
		})

		servo.addAssemblyStep(1, new Transform().movez(tailLength.getMM()+servoHeight+20))
		return [servo, base]
	}
	ArrayList<CSG> makeLinks(TransformNR linkDim,String shaftType, String shaftSize,String motorType, String motorSize){
		baseY =nameTagHeightParam.getMM()
		
		CSG servo;
		double endOfLinkCutout = 20
		if(motorType!=null) {
			servo = Vitamins.get(motorType,motorSize)
			servoMaxZ = servo.getMaxZ();
		}else {
			servo = new Hexagon(6.7, 100).toCSG().movez(-50);
		}
		servo = servo.transformed(TransformFactory.nrToCSG(linkDim))
		// HW 2 Set baseX here before it is used from the information in the  linkDim object
		double baseX = linkDim.getX() - moveTagFromCenter -endOfLinkCutout
		
		CSG nametagBase = new Cube(baseX,baseY,baseZ).toCSG()
		double distanceToBottom = nametagBase.getMinZ()
		
		CSG endEffectorUpright = new Cube(baseZ,baseY,servoMaxZ+baseZ).toCSG()
										.toZMin()
										.movez(-baseZ)
										.toXMax()
										.movex(-endOfLinkCutout)
									
		CSG endEffectorHolder  = new Cube(endOfLinkCutout*2,baseY,baseZ).toCSG()
									.toZMax()
									.union(endEffectorUpright)
									.transformed(TransformFactory.nrToCSG(linkDim))
									
		nametagBase=nametagBase.movez(-distanceToBottom)
				.toXMin()
				.toYMin()
		double distancetoTop = nametagBase.getMaxZ()
		CSG loop = new Cylinder(ringDiameter/2,baseZ).toCSG()
				.toXMax()
				.movey(baseY/2)
		CSG tag = nametagBase
				.union(loop)
				.hull()
				.union(getName(baseX))
		tag.setParameter(nameTagHeightParam)
		//tag.setParameter(dhParametersLength)
		CSG horn = Vitamins.get(shaftType, shaftSize)
				.movez(servoMaxZ)
		tag =tag.moveToCenterY()
				.movez(servoMaxZ)
				.movex(moveTagFromCenter)
				.difference(horn)
				.union(endEffectorHolder)
				.difference(servo)
		tag.setName("MrHarringtonNametag")
		tag.setManufacturing({ toMfg ->
			return toMfg.toZMin()//move it down to the flat surface
		})
		horn.addAssemblyStep(2, new Transform().movey(40))
		tag.addAssemblyStep(2, new Transform().movey(40))

		horn.addAssemblyStep(3, new Transform().movez(20))
		horn.addAssemblyStep(4, new Transform().movez(40))
		tag.addAssemblyStep(4, new Transform().movez(40))
		return [horn, tag]
	}

	ArrayList<CSG> generate(){
		ArrayList<CSG> links = makeLinks(new TransformNR(100,0,0,new RotationNR()),"hobbyServoHorn","standardMicro1","hobbyServo","mg92b")
		links.addAll(makeBase("hobbyServo","mg92b"))
		return links
	}
}

NamedCadGenerator gen = new NamedCadGenerator()

if(args==null)
	return gen.generate()
else
	return gen



