package com.example.kevin.mmcclient;


import java.io.IOException;

public class RMCPImp {

    public static final String Magic = "RemoteMouseControlProtocol1.0";
    public static final String Delimiter = " ";

    public String CreateRMCP(int ChooseMode, float ParameterX, float ParameterY
            , boolean LeftMouseSingleClick, boolean LeftMouseDoubleClick, boolean RightMouseSingleClick
            , boolean MidMouseSingleClick, boolean MouseScroll) throws IOException{

        String RMCPStr;

        RMCPStr = Magic + Delimiter + Integer.toString(ChooseMode) + Delimiter + Float.toString(ParameterX) + Delimiter + Float.toString(ParameterY)
                + Delimiter + (LeftMouseSingleClick? "1":"0") + Delimiter + (LeftMouseDoubleClick? "1":"0") + Delimiter
                + (RightMouseSingleClick? "1":"0") + Delimiter + (MidMouseSingleClick? "1":"0") + Delimiter
                + (MouseScroll? "1":"0");

        return RMCPStr;
    }

}
