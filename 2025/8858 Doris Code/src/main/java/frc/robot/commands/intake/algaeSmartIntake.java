package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.swervedrive.AlgaeSubsystem;

public class algaeSmartIntake extends Command {
    private final AlgaeSubsystem algaeSubsystem;
    public algaeSmartIntake (AlgaeSubsystem algaeSubsystem){
        this.algaeSubsystem = algaeSubsystem;


        addRequirements(algaeSubsystem);
    }

    @Override
    public void initialize() {
    }


    @Override
    public void execute() {
        algaeSubsystem.algaeIntake(-Constants.ALG_M_SPEED);
    }

    @Override
    public boolean isFinished() {
        return (algaeSubsystem.getAlgaeCurrent() > 14);
    }

    @Override
    public void end(boolean interrupted) {
        algaeSubsystem.algaeIntake(-Constants.ALG_M_HOLD_SPEED);
    }

}
