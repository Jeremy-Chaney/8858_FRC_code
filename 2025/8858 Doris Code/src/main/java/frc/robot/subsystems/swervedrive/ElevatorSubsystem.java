package frc.robot.subsystems.swervedrive;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;

import frc.robot.Constants;
import frc.robot.Constants.OperatorConstants;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ElevatorSubsystem extends SubsystemBase {

    // public static SparkMax leftElevatorMotor = new SparkMax(16, MotorType.kBrushless);
    // public static SparkMax rightElevatorMotor = new SparkMax(15, MotorType.kBrushless);
    // public static RelativeEncoder leftElevatorEncoder = leftElevatorMotor.getEncoder();
    // public static RelativeEncoder rightElevatorEncoder = rightElevatorMotor.getEncoder();

    // private final SparkMax leftElevatorMotor;
     private final SparkMax rightElevatorMotor;
    // private final SparkFlex elevatorMotor;
    // private final RelativeEncoder leftElevatorEncoder;
    private final RelativeEncoder rightElevatorEncoder;
    // private final RelativeEncoder elevatorEncoder;
    private final PIDController pidController;

    // PID Coefficients
    private final double kP = 0.1;
    private final double kI = 0.001;
    private final double kD = 0.00001;
    private final double kFF = 0.0;
    private final double kMaxOutput = 0.6;
    private final double kMinOutput = -0.45;

    // pre-defined positions for the elevator
    public double lastTargetPosition = 0.0;

    // left is increasing as elevator raises
    // right is decreasing as elevator raises
    public static ElevatorSubsystem elevatorinstance;

    public ElevatorSubsystem() {
        // initialize motors
        // leftElevatorMotor = new SparkMax(16, MotorType.kBrushless);
        rightElevatorMotor = new SparkMax(OperatorConstants.CAN_ELEVATOR_R, MotorType.kBrushless);
        // elevatorMotor = new SparkFlex(9, MotorType.kBrushless);
        // initialize encoders
        // leftElevatorEncoder = leftElevatorMotor.getEncoder();
        rightElevatorEncoder = rightElevatorMotor.getEncoder();
        // elevatorEncoder = rightElevatorEncoder.getEncoder();
        // initialize PID controller
        pidController = new PIDController(kP, kI, kD);
        elevatorinstance = this; // save subsystem so it can be accessed anywhere
        // verify this works before uncommenting
        setDefaultCommand(new Command() { // run this command when the subsystem isn't being used by another command
            {
                addRequirements(elevatorinstance);
            }
            @Override
            public void initialize() {
                elevatorinstance.resetPID(); // reset the PID controller
            }
            @Override
            public void execute() {
                elevatorinstance.HoldPosition(); // hold the elevator at the last set position
            }
        });
    }

    /** Move the elevator to a certain position */
    public void MoveElevatorToPosition(double targetPosition){ // move the elevator to a certain position
        this.lastTargetPosition = targetPosition; // save the target position
        // double currentPosition = leftElevatorEncoder.getPosition();
        double currentPosition = -rightElevatorEncoder.getPosition();
        double output = pidController.calculate(currentPosition, targetPosition);
        // output = (currentPosition < targetPosition ? 0.2 : -0.2);
        // if (Math.abs(currentPosition-targetPosition) < 1.0) {
        //     output = 0;
        // }

        output = Math.max(kMinOutput, Math.min(kMaxOutput, output));

        if(output < 0 && currentPosition < 7){
            output = output / 2.0;
        }
        if(output > 0 && currentPosition > 63){
            output = output / 1.0;
        }
        // leftElevatorMotor.set(output);
        rightElevatorMotor.set(-output);
        // elevatorMotor.set(-output);
        SmartDashboard.putNumber("Elevator Power", -output);
        SmartDashboard.putNumber("Elevator Target Position", lastTargetPosition);
        SmartDashboard.putBoolean("Elevator Is At Position", isAtTarget());
    }

    public boolean isAtTarget() {
        return Math.abs(getEncoderPosition() - lastTargetPosition) < Constants.ELE_TOL;
    }


    /** Hold the elevator at the last target position */
    public void HoldPosition(){ // hold the elevator at the last target position
        this.MoveElevatorToPosition(lastTargetPosition);
    }
    /** Move the elevator at a certain speed */
    public void MoveElevator(double speed){
        // leftElevatorMotor.set(speed); // run motors in opposite directions to move elevator up and down
        rightElevatorMotor.set(-speed);
        // elevatorMotor.set(-speed);
        this.lastTargetPosition = this.getEncoderPosition(); // update the last target position so that the elevator doesn't go back to the last set position after the command ends
    }
    /** Get the height of the elevator */
    public double getEncoderPosition(){
        // return leftElevatorEncoder.getPosition();
        return -rightElevatorEncoder.getPosition();
    }
    /** Reset the PID controller */
    public void resetPID(){
        pidController.reset();
    }
}