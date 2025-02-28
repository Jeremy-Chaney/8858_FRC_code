package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.WristSubsystem;

public class MoveWrist extends Command {
    private final WristSubsystem wristSubsystem;
    private final double speed;

    public MoveWrist(WristSubsystem wristSubsystem, double speed) {
        this.wristSubsystem = wristSubsystem;
        this.speed = speed; // motor speed
        addRequirements(wristSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void initialize(){ // runs when the command starts
        wristSubsystem.resetPID();
    }

    @Override
    public void execute(){ // runs periodically while the command is scheduled
        wristSubsystem.MoveWrist(speed);
    }

    @Override
    public boolean isFinished(){ // check if the command should stop running
        return false;
    }

    @Override
    public void end(boolean interrupted){ // runs when the command ends
        wristSubsystem.MoveWrist(0);
    }
}