// code here

import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.Transform
import eu.mihosoft.vrl.v3d.parametrics.LengthParameter

class NamedCadGenerator{
	LengthParameter nameTagHeightParam = new LengthParameter("Nametag Height", 30, [60, 20])
	LengthParameter dhParametersLength = new LengthParameter("dh Parameters Length", 40, [60, 20])

	LengthParameter tailLength		= new LengthParameter("Cable Cut Out Length",30,[500, 0.01])

	double moveTagFromCenter = 5

	double baseX = dhParametersLength.getMM() - moveTagFromCenter
	double baseZ = 8
	double baseY =nameTagHeightParam.getMM()
	double textHeight =2
	double textToEdgeSpacing =1
	double ringDiameter = 20
	double holeDiameter=15
	CSG servo = Vitamins.get("hobbyServo", "mg92b")
	CSG name = CSG.text("Mr. Harrington", textHeight)
	.movez(baseZ/2)
	ArrayList<CSG> makeBase(){
	}
	ArrayList<CSG> makeLinks(){
		CSG nametagBase = new Cube(baseX,baseY,baseZ).toCSG()
		double distanceToBottom = nametagBase.getMinZ()

		nametagBase=nametagBase.movez(-distanceToBottom)
				.toXMin()
				.toYMin()

		double distancetoTop = nametagBase.getMaxZ()

		double xscale = (nametagBase.getTotalX()-(textToEdgeSpacing*2))/name.getTotalX()

		double yScale = (nametagBase.getTotalY()-(textToEdgeSpacing*2))/name.getTotalY()

		name=name
				.toXMin()
				.toYMin()
				.scalex(xscale)
				.scaley(yScale)
				.movex(textToEdgeSpacing)
				.movey(textToEdgeSpacing)
		CSG loop = new Cylinder(ringDiameter/2,baseZ).toCSG()
				.toXMax()
				.movey(baseY/2)

		CSG hole = new Cylinder(holeDiameter/2,baseZ).toCSG()
				.movex(-ringDiameter/2)
				.movey(baseY/2)

		CSG tag = nametagBase
				.union(loop)
				.hull()
				//.difference(hole)
				.union(name)


		tag.setParameter(nameTagHeightParam)
		tag.setParameter(dhParametersLength)
		CSG horn = Vitamins.get("hobbyServoHorn", "standardMicro1")
				.movez(servo.getMaxZ())
		tag =tag.moveToCenterY()
				.movez(servo.getMaxZ())
				.movex(moveTagFromCenter)
				.difference(horn)
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


		tailLength.setMM(130)

		double servoy = servo.getTotalY()
		double servox = servo.getTotalX()
		double basex = servox + baseX + moveTagFromCenter
		double basey = servoy + moveTagFromCenter
		HashMap<String,Object> servoConfig = Vitamins.getConfiguration( "hobbyServo", "mg92b")
		double servoHeight = servoConfig.get("servoShaftSideHeight")

		CSG base = new Cube(basex,basey,servoHeight).toCSG()
				.toZMax()
				.difference(servo)

		CSG sideName = name.rotx(-90)
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


		return [ makeLinks(),base, servo]
	}
}

NamedCadGenerator gen = new NamedCadGenerator()

if(args==null)
	return gen.generate()
else
	return gen



