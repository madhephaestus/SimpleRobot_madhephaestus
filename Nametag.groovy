// code here

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.parametrics.LengthParameter

LengthParameter nameTagHeightParam = new LengthParameter("Nametag Height", 30, [60,20])

double baseX = 100
double baseZ = 3
double baseY =nameTagHeightParam.getMM()
double textHeight =2
double textToEdgeSpacing =1
double ringDiameter = 20
double holeDiameter=15

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
			.difference(hole)
			.union(name)
			.setName("MrHarringtonNametag")

tag.setParameter(nameTagHeightParam)

return [tag]

