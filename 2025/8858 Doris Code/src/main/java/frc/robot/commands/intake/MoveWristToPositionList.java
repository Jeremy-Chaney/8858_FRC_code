package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.WristSubsystem;

public class MoveWristToPositionList extends Command {
    private final WristSubsystem wristSubsystem;
    private int positionList = 0;
    private final int listDirection;
    private Double[] positions = {
        0.677, //L1
        0.6, //coral intake
        0.66, //L2
        0.66, //L3 + low algae
        0.5, //high algae
        0.691 //L4
    };
    private boolean done=false;

    public MoveWristToPositionList(WristSubsystem wristSubsystem, int direction) {
        this.wristSubsystem = wristSubsystem;
        listDirection = direction;
        addRequirements(wristSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void initialize() { // runs when the command starts
        done=false;
        wristSubsystem.resetPID(); // reset the PID controller
        positionList = positionList + listDirection;
        if(positionList >= positions.length){
            positionList = positions.length - 1;
        }
        if(positionList < 0){
            positionList = 0;
        }
    }

    @Override
    public void execute() { // runs periodically while the command is scheduled
        wristSubsystem.MoveWristToPosition(positions[positionList]); // move the wrist to the target position
    }

    @Override
    public boolean isFinished() { // check if the command should stop running
        return false; // the command should never stop running
    }

    @Override
    public void end(boolean interrupted) { // runs when the command ends
        // wristSubsystem.MoveWrist(0); // set the motor speed to 0
    }
}