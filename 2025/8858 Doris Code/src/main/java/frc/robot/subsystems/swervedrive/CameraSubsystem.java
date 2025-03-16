package frc.robot.subsystems.swervedrive;

import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.cameraserver.CameraServer;
import frc.robot.Constants;

public class CameraSubsystem extends SubsystemBase{
    private UsbCamera camera_coral, camera_climb;
    private VideoSink server;

    private int camera_sel = 0;

    public CameraSubsystem(){
        // Cameras
        camera_coral = CameraServer.startAutomaticCapture(Constants.OperatorConstants.CAM_COR);
        camera_coral.setResolution(160, 120);
        camera_coral.setFPS(Constants.OperatorConstants.CAM_FPS);
        camera_climb = CameraServer.startAutomaticCapture(Constants.OperatorConstants.CAM_CLM);
        camera_climb.setResolution(160, 120);
        camera_climb.setFPS(Constants.OperatorConstants.CAM_FPS);
        server = CameraServer.getServer();
        server.setSource(camera_coral);
        camera_sel = Constants.OperatorConstants.CAM_CLM;
    }

    public void SwitchCamera(int camera_select){

        if(camera_select == Constants.OperatorConstants.CAM_COR){
            // CORAL CAMERA
            server.setSource(camera_coral);
            camera_sel = camera_select;

        } else if (camera_select == Constants.OperatorConstants.CAM_CLM) {
            // CLIMB CAMERA
            server.setSource(camera_climb);
            camera_sel = camera_select;

        } else {
            // DEFAULT CASE
            server.setSource(camera_climb);
            camera_select = Constants.OperatorConstants.CAM_CLM;
        }
    }
}
