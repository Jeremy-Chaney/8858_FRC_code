package frc.robot.commands.intake;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.swervedrive.AlgaeSubsystem;

public class AutoScoreAlgae extends Command {
    private final AlgaeSubsystem algaeSubsystem;

    // Used to determine how long to 'blind run' coral motor after limit switches can't see the coral anymore
    Timer algaeIntakeTimer = new Timer();
    boolean algaeTimerActive = false;
    double algaeIntakeEndTime;
    double algaeBlindRunTime = 0.5;

    public AutoScoreAlgae(AlgaeSubsystem algaeSubsystem) {
        this.algaeSubsystem = algaeSubsystem;
        addRequirements(algaeSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void execute() { // runs periodically while the command is scheduled
        algaeSubsystem.algaeIntake(Constants.ALG_M_SPEED);
        if(!algaeTimerActive){
            algaeTimerActive = true;
            algaeIntakeEndTime = algaeIntakeTimer.get() + algaeBlindRunTime;
        }
    }

    @Override
    public boolean isFinished() { // check if the command should stop running
        // return (!(algaeIntakeSubsystem.getTop() || algaeIntakeSubsystem.getBot()));
        SmartDashboard.putNumber("Algae Blind Run Timer", algaeIntakeTimer.get() - algaeIntakeEndTime);
        return (algaeTimerActive && (algaeIntakeTimer.get() > algaeIntakeEndTime));
    }

    @Override
    public void end(boolean interrupted) { // runs when the command ends
        algaeSubsystem.algaeIntake(0);
    }

    @Override
    public void initialize(){
        algaeIntakeTimer.start();
        algaeTimerActive = false;

        // shouldn't need this as it will be overwritten but good to have ~some~ value
        algaeIntakeEndTime = algaeIntakeTimer.get() + algaeBlindRunTime;
    }
}