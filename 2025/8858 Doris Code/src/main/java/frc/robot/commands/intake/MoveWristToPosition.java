package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.WristSubsystem;

public class MoveWristToPosition extends Command {
    private final WristSubsystem wristSubsystem;
    private final double targetPosition;

    public MoveWristToPosition(WristSubsystem wristSubsystem, double targetPosition) {
        this.wristSubsystem = wristSubsystem;
        this.targetPosition = targetPosition; // target position
        addRequirements(wristSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void initialize() { // runs when the command starts
        wristSubsystem.resetPID(); // reset the PID controller
    }

    @Override
    public void execute() { // runs periodically while the command is scheduled
        wristSubsystem.MoveWristToPosition(targetPosition); // move the wrist to the target position
    }

    @Override
    public boolean isFinished() { // check if the command should stop running
        return false; // the command should never stop running
    }

    @Override
    public void end(boolean interrupted) { // runs when the command ends
        wristSubsystem.MoveWrist(0); // set the motor speed to 0
    }
}