package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.CoralIntakeSubsystem;

public class preScoreAutoCoralIntake extends Command {
    private final CoralIntakeSubsystem coralIntakeSubsystem;

    public preScoreAutoCoralIntake(CoralIntakeSubsystem coralIntakeSubsystem) {
        this.coralIntakeSubsystem = coralIntakeSubsystem;
        addRequirements(coralIntakeSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void execute() { // runs periodically while the command is scheduled
        coralIntakeSubsystem.preScoreAutoCoralIntake();
    }

    @Override
    public boolean isFinished() { // check if the command should stop running
        return (!coralIntakeSubsystem.getTop());
        // return false;
    }

    @Override
    public void end(boolean interrupted) { // runs when the command ends
        coralIntakeSubsystem.coralIntake(0);
    }
}