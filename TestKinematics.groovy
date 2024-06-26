import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR
import com.neuronrobotics.sdk.common.DeviceManager

// Load the robot from the device manager
MobileBase base =(MobileBase) DeviceManager.getSpecificDevice("SimpleRobot")
if(base==null)
	throw new RuntimeException("Please run the SimpleRobot.xml before running this script!")
// Load the arm from the robot
DHParameterKinematics arm = base.getAllDHChains().get(0)

for (int i=0;i<150;i++){
	// Create a tip target of 150,200
	TransformNR target = new TransformNR(120-i,100,0,new RotationNR())
	// send the target to the arm with 0 seconds for transition
	arm.setDesiredTaskSpaceTransform(target, 0)
	// wait for the  arm to arrive at the location specified
	Thread.sleep(16)
	// Read the location of the tip after it finished moving
	TransformNR result = arm.getCurrentTaskSpaceTransform()
	
	// compute the tip error by subtracting result from the intended target
	double xError = target.getX() - result.getX()
	double yError = target.getY() - result.getY()
	
	// print the results
	println "Result (should be 0,0)= "+xError+","+yError
	
	if(Math.abs(xError)>0.001 || Math.abs(yError) > 0.001) {
		println "FAIL, the  error is outside of the bounds"
		println result
	}else {
		println "Success!"
	}

}
