package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.ElevatorSubsystem;

public class MoveElevatorToPositionList extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private int positionList = 0;
    private final int listDirection;
    private Double[] positions = {
        0.0, //L1
        10.0, //coral intake
        11.286, //L2
        32.0, //L3 + low algae
        51.833, //high algae
        70.5 //L4
    };

    public MoveElevatorToPositionList(ElevatorSubsystem elevatorSubsystem, int direction) {
        this.elevatorSubsystem = elevatorSubsystem;
        listDirection = direction;
        addRequirements(elevatorSubsystem); // add requirement so that multiple commands using the same subsystem don't run at the same time
    }

    @Override
    public void initialize(){
        elevatorSubsystem.resetPID(); // reset PID controller
        positionList = listDirection + positionList;
        if(positionList >= positions.length) {
            positionList = positions.length - 1;
        }
        if(positionList < 0) {
            positionList = 0;
        }
    }

    @Override
    public void execute(){
        elevatorSubsystem.MoveElevatorToPosition(positions[positionList]); // move elevator to target position
    }

    @Override
    public boolean isFinished(){
        return false; // never finish
    }

    @Override
    public void end(boolean interrupted){
        // elevatorSubsystem.MoveElevatorToPosition(elevatorSubsystem.getEncoderPosition());
    }
}