package Server;

import java.awt.*;


public class TouchScreenImp {

    private float[] Displacement = new float[]{0, 0};

    public Point ComputeMouseMove(float Displacement_x, float Displacement_y) {
        Displacement[0] = Displacement_x;
        Displacement[1] = Displacement_y;
        return new Point((int) Displacement[0], (int) Displacement[1]);
    }
}
