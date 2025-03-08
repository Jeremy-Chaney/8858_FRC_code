package frc.robot.subsystems.swervedrive;

import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.cameraserver.CameraServer;

public class CameraSubsystem extends SubsystemBase{
    private UsbCamera camera0, camera1;
    private VideoSink server;

    private int camera_sel = 0;

    public CameraSubsystem(){
        // Cameras
        camera0 = CameraServer.startAutomaticCapture(0);
        camera0.setResolution(160, 120);
        camera0.setFPS(10);
        camera1 = CameraServer.startAutomaticCapture(1);
        camera1.setResolution(160, 120);
        camera1.setFPS(10);
        server = CameraServer.getServer();
        server.setSource(camera0);
        camera_sel = 0;
    }

    public void SwitchCamera(){
        if(camera_sel == 0){
            server.setSource(camera1);
        } else if(camera_sel == 1){
            server.setSource(camera0);
        } else {
            server.setSource(camera0);
        }
    }
}
