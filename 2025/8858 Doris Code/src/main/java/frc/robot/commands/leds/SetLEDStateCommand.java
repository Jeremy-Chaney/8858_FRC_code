package frc.robot.commands.leds;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.LEDSubsystem;
import frc.robot.subsystems.swervedrive.LEDSubsystem.Mode;

public class SetLEDStateCommand extends Command {
    private final Mode mode;
    private final LEDSubsystem subsystem;

    public SetLEDStateCommand(Mode mode, LEDSubsystem subsystem) {
        // addRequirements(subsystem);
        this.mode = mode;
        this.subsystem = subsystem;
    }

    @Override
    public void execute() {
        this.subsystem.setMode(mode);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
