package Server;
import java.awt.*;
import java.awt.event.InputEvent;

public class MouseControl{

    private Robot m_Robot;
    private static Dimension ScreenDim;
    public boolean Mouseclick;

    public MouseControl() {
        try{
            Mouseclick = false;
            m_Robot = new Robot();
            ScreenDim = Toolkit.getDefaultToolkit().getScreenSize();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void MouseSingleClick(int LoRButton) {
        if(LoRButton == 0) {
            m_Robot.mousePress(InputEvent.BUTTON1_MASK);
            m_Robot.delay(100);
            m_Robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }
        else if(LoRButton == 1){
            m_Robot.mousePress(InputEvent.BUTTON3_MASK);
            m_Robot.delay(100);
            m_Robot.mouseRelease(InputEvent.BUTTON3_MASK);
        }
        else {
            m_Robot.mousePress(InputEvent.BUTTON2_MASK);
            m_Robot.delay(100);
            m_Robot.mouseRelease(InputEvent.BUTTON2_MASK);
        }
    }

    public void LeftMouseDoubleClick() {
        m_Robot.mousePress(InputEvent.BUTTON1_MASK);
        m_Robot.delay(100);
        m_Robot.mouseRelease(InputEvent.BUTTON1_MASK);
        m_Robot.delay(100);
        m_Robot.mousePress(InputEvent.BUTTON1_MASK);
        m_Robot.delay(100);
        m_Robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }


    public void MouseDragging(Point PosChange, ImpControl IC) {
        if(!IC.IsMoved) {
            m_Robot.mousePress(InputEvent.BUTTON1_MASK);
            System.out.println("1");
        }
        IC.IsMoved = true;
        m_Robot.delay(10);
        MouseMove(PosChange, IC);
        System.out.println("2");
        if(IC.IsDraggingEnd)
        {
            m_Robot.delay(10);
            m_Robot.mouseRelease(InputEvent.BUTTON1_MASK);
            System.out.println("3");
            IC.IsMoved = false;
            IC.IsDraggingEnd = false;
        }
    }

    public void MouseScroll(int wheelAmt) {
        m_Robot.mouseWheel(- wheelAmt);
    }
    
    public void MouseMove(Point PosChange, ImpControl IC) {
        Point MousePoint = MouseInfo.getPointerInfo().getLocation();
        Point axis = new Point(MousePoint.x + PosChange.x, MousePoint.y + PosChange.y);
        if(axis.x >= ScreenDim.width) {
            axis.x = ScreenDim.width - 1;
            IC.IsTouched_x = true;
        }
        else if(axis.x < 0) {
            axis.x = 0;
            IC.IsTouched_x = true;
        }
        else {
            IC.IsTouched_x = false;
        }
        if(axis.y >= ScreenDim.height) {
            axis.y = ScreenDim.height - 1;
            IC.IsTouched_y = true;
        }
        else if(axis.y < 0) {
            axis.y = 0;
            IC.IsTouched_y = true;
        }
        else {
            IC.IsTouched_y = false;
        }

        m_Robot.mouseMove(axis.x, axis.y);
    }
}
