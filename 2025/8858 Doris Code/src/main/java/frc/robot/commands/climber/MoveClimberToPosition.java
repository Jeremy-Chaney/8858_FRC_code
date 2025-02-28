package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.ClimberSubsystem;

public class MoveClimberToPosition extends Command {
    private final ClimberSubsystem climberSubsystem;
    private final double speed;
    private final double position;

    public MoveClimberToPosition(ClimberSubsystem climberSubsystem, double position, double speed){
        this.climberSubsystem = climberSubsystem;
        this.position = position;
        this.speed = speed;
        addRequirements(climberSubsystem);
    }

    @Override
    public void initialize() {
        climberSubsystem.resetPID(); // reset PID controller
    }

    @Override
    public void execute() { // runs periodically while the command is scheduled
        climberSubsystem.MoveClimberToPosition(position, speed);
    }

    @Override
    public boolean isFinished() { // check if the command should stop running
        return false; // never finish
    }

    @Override
    public void end(boolean interrupted) { // runs when the command ends
        climberSubsystem.MoveClimberToPosition(climberSubsystem.getEncoder(), 0.4);
    }
}