package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.ElevatorSubsystem;

public class MoveElevatorToPosition extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final double targetPosition;

    public MoveElevatorToPosition(ElevatorSubsystem elevatorSubsystem, double targetPosition) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.targetPosition = targetPosition; // set target position
        addRequirements(elevatorSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void initialize(){
        elevatorSubsystem.resetPID(); // reset PID controller
    }

    @Override
    public void execute(){
        elevatorSubsystem.MoveElevatorToPosition(targetPosition); // move elevator to target position
    }

    @Override
    public boolean isFinished(){
        return false; // never finish
    }

    @Override
    public void end(boolean interrupted){
        elevatorSubsystem.MoveElevatorToPosition(elevatorSubsystem.getEncoderPosition());
    }
}