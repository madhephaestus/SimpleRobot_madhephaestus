// code here

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube

CSG nametagBase = new Cube(70,30,3).toCSG()

double distanceToBottom = nametagBase.getMinZ()

nametagBase=nametagBase.movez(-distanceToBottom)
			.toXMin()
			.toYMin()

double distancetoTop = nametagBase.getMaxZ()




CSG name = CSG.text("Kevin yay!", 2)
				.movez(distancetoTop)

double hackyScale = (nametagBase.getTotalX()-2)/name.getTotalX()  // FIXME this is a hack and needs to be calculated

double yScale = (nametagBase.getTotalY()-2)/name.getTotalY()

name=name		
		.toXMin()	
		.toYMin()	
		.scalex(hackyScale)
		.scaley(yScale)
		.movex(1)
		.movey(1)



return [nametagBase, name]