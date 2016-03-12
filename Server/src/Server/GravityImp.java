package Server;

import java.awt.*;


public class GravityImp {

    private float[] Gravity = new float[2];

    public Point ComputeMouseMove(float Gravity_x, float Gravity_y) {
        Gravity[0] = -Gravity_x;
        Gravity[1] = Gravity_y;
        return new Point((int) Gravity[0], (int)Gravity[1]);
    }
}
