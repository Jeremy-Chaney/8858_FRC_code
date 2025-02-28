package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.ClimberSubsystem;

public class MoveClimber extends Command {
    private final ClimberSubsystem climberSubsystem;
    private final double speed;

    public MoveClimber(ClimberSubsystem climberSubsystem, double speed) {
        this.climberSubsystem = climberSubsystem;
        this.speed = speed; // motor speed
        addRequirements(climberSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() { // runs periodically while the command is scheduled
        climberSubsystem.move(speed);
    }

    @Override
    public boolean isFinished() { // check if the command should stop running
        return false; // never finish
    }

    @Override
    public void end(boolean interrupted) { // runs when the command ends
        climberSubsystem.move(0);
    }
}