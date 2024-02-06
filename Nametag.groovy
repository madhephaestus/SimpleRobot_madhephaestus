// code here

import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.parametrics.LengthParameter

LengthParameter nameTagHeightParam = new LengthParameter("Nametag Height", 30, [60, 20])
LengthParameter tailLength		= new LengthParameter("Cable Cut Out Length",30,[500, 0.01])
tailLength.setMM(130)

double baseX = 100
double baseZ = 8
double baseY =nameTagHeightParam.getMM()
double textHeight =2
double textToEdgeSpacing =1
double ringDiameter = 20
double holeDiameter=15
double moveTagFromCenter = 5

CSG nametagBase = new Cube(baseX,baseY,baseZ).toCSG()

double distanceToBottom = nametagBase.getMinZ()

nametagBase=nametagBase.movez(-distanceToBottom)
		.toXMin()
		.toYMin()

double distancetoTop = nametagBase.getMaxZ()




CSG name = CSG.text("Mr. Harrington", textHeight)
		.movez(distancetoTop)

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
		.setName("MrHarringtonNametag")

tag.setParameter(nameTagHeightParam)

CSG servo = Vitamins.get("hobbyServo", "mg92b")
CSG horn = Vitamins.get("hobbyServoHorn", "standardMicro1")
		.movez(servo.getMaxZ())
tag =tag.moveToCenterY()
		.movez(servo.getMaxZ())
		.movex(moveTagFromCenter)
		.difference(horn)
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

tag.setManufacturing({ toMfg ->
	return toMfg.toZMin()//move it down to the flat surface
})	

base.setManufacturing({ toMfg ->
	return toMfg.toZMin()//move it down to the flat surface
})	

return [horn, tag, base,servo]

