package Server;

import java.awt.*;

public class AccelerationImp {
    private float[] Acceleration = new float[2];
    //private float[] CaliAcceleration = new float[2];

    public Point ComputeMouseMove(float Acceleration_x, float Acceleration_y, long DeltaTime, ImpControl IC) {
        Acceleration[0] = Acceleration_x * 10;
        Acceleration[1] = -Acceleration_y * 10;

        /*for(int i=0;i<2;i++) {

            if (IC.IsStopped[i] && Math.abs(Acceleration[i]) > 1) {
                if (Acceleration[i] > 0) {
                    IC.m_Sign[i] = 1;
                } else {
                    IC.m_Sign[i] = -1;
                }
            }

            if (Math.abs(Acceleration[i]) < 1) {
                IC.Stoptime[i] += DeltaTime;
            } else {
                IC.Stoptime[i] = 0;
            }

            if (IC.Stoptime[i] > 6) {
                IC.IsStopped[i] = true;
            } else {
                IC.IsStopped[i] = false;
            }

            CaliAcceleration[i] = IC.m_Sign[i] * Math.abs(Acceleration[i]);
        }

        if(Math.abs(CaliAcceleration[0]) < 3) {
            CaliAcceleration[0] = 0;
        }
        if(Math.abs(CaliAcceleration[1]) < 3) {
            CaliAcceleration[1] = 0;
        }*/
        return new Point((int)Acceleration[0], (int)Acceleration[1]);
    }
}
