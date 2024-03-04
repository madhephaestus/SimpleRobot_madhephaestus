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
		arg1[0]=100
		arg1[1]=40
		return arg1;
	}
	
}