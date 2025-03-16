package frc.robot.commands.camera;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.CameraSubsystem;
import frc.robot.subsystems.swervedrive.ElevatorSubsystem;

public class SwitchCamera extends Command {
    private final CameraSubsystem cameraSubsystem;
    private final int camera_select;

    public SwitchCamera(CameraSubsystem cameraSubsystem, int camera_select) {
        this.cameraSubsystem = cameraSubsystem;
        this.camera_select = camera_select;
        addRequirements(cameraSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void execute(){
        cameraSubsystem.SwitchCamera(camera_select); // move elevator to target position
    }

    @Override
    public boolean isFinished(){
        return true; // run once
    }

    @Override
    public void end(boolean interrupted){
    }
}
