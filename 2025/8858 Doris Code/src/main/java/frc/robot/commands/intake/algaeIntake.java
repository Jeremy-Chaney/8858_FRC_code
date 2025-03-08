package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.swervedrive.AlgaeSubsystem;

public class algaeIntake extends Command {
    private final AlgaeSubsystem algaeSubsystem;
    private final double speed;

    public algaeIntake(AlgaeSubsystem algaeSubsystem, double speed) {
        this.algaeSubsystem = algaeSubsystem;
        this.speed = speed; // motor speed
        addRequirements(algaeSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() { // runs periodically while the command is scheduled
        algaeSubsystem.algaeIntake(speed);
    }

    @Override
    public boolean isFinished() { // check if the command should stop running
        return false; // never finish
    }

    @Override
    public void end(boolean interrupted) { // runs when the command ends
        algaeSubsystem.algaeIntake(0);
    }
}