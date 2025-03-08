package frc.robot.commands.camera;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.CameraSubsystem;
import frc.robot.subsystems.swervedrive.ElevatorSubsystem;

public class SwitchCamera extends Command {
    private final CameraSubsystem cameraSubsystem;

    public SwitchCamera(CameraSubsystem cameraSubsystem) {
        this.cameraSubsystem = cameraSubsystem;
        addRequirements(cameraSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void execute(){
        cameraSubsystem.SwitchCamera(); // move elevator to target position
    }

    @Override
    public boolean isFinished(){
        return true; // never finish
    }

    @Override
    public void end(boolean interrupted){
    }
}
