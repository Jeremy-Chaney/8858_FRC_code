package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.CoralIntakeSubsystem;

public class coralIntake extends Command {
    private final CoralIntakeSubsystem coralIntakeSubsystem;
    private final double speed;

    public coralIntake(CoralIntakeSubsystem coralIntakeSubsystem, double speed) {
        this.coralIntakeSubsystem = coralIntakeSubsystem;
        this.speed = speed; // motor speed
        addRequirements(coralIntakeSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void execute() { // runs periodically while the command is scheduled
        coralIntakeSubsystem.coralIntake(speed);
    }

    @Override
    public boolean isFinished() { // check if the command should stop running
        return false; // never finish
    }

    @Override
    public void end(boolean interrupted) { // runs when the command ends
        coralIntakeSubsystem.coralIntake(0);
    }
}