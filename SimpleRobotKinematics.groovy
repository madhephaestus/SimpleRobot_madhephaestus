import com.neuronrobotics.sdk.addons.kinematics.DHChain
import com.neuronrobotics.sdk.addons.kinematics.DhInverseSolver
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

return new DhInverseSolver() {

	@Override
	public double[] inverseKinematics(TransformNR arg0, double[] arg1, DHChain arg2) {
		double xTarget = arg0.getX()
		double yTarget = arg0.getY()
		double l1 = arg2.getLinks().get(0).getRadius()
		double l2 = arg2.getLinks().get(1).getRadius()
		println "Targeted to "+xTarget+" , "+yTarget
		double xSquared = Math.pow(xTarget, 2)
		double ySquared = Math.pow(yTarget, 2)
		double Hyp = Math.sqrt(xSquared+ySquared)
		if((l1+l2)<=Hyp) {
			println "Hyp check failed, exiting"
			throw new RuntimeException("FAIL! the hypotinuse must be less than the sum of the link lengths")
		}
		double theta1 = Math.atan2(yTarget, xTarget)
		
		
		arg1[0]=Math.toDegrees(theta1)
		arg1[1]=0
		return arg1;
	}
	
}