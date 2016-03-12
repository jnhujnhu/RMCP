package Server;


public class ImpControl {
    public long[] Stoptime;
    public boolean[] IsStopped;
    public int[] m_Sign;
    public float[] Velocity;
    public boolean IsTouched_y, IsTouched_x;
    public boolean IsMoved;
    public boolean IsDraggingEnd;

    public ImpControl() {
        Stoptime = new long[]{0, 0};
        IsStopped = new boolean[]{true, true};
        m_Sign = new int[]{1, 1};
        Velocity = new float[]{0, 0};
        IsTouched_x = false;
        IsTouched_y = false;
        IsMoved = false;
        IsDraggingEnd = false;
    }
}
