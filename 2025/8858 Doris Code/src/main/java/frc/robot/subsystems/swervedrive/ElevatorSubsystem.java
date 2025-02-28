package frc.robot.subsystems.swervedrive;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ElevatorSubsystem extends SubsystemBase {

    // public static SparkMax leftElevatorMotor = new SparkMax(16, MotorType.kBrushless);
    // public static SparkMax rightElevatorMotor = new SparkMax(15, MotorType.kBrushless);
    // public static RelativeEncoder leftElevatorEncoder = leftElevatorMotor.getEncoder();
    // public static RelativeEncoder rightElevatorEncoder = rightElevatorMotor.getEncoder();

    private final SparkMax leftElevatorMotor;
    private final SparkMax rightElevatorMotor;
    private final RelativeEncoder leftElevatorEncoder;
    private final RelativeEncoder rightElevatorEncoder;
    private final PIDController pidController;

    // PID Coefficients
    private final double kP = 0.1;
    private final double kI = 0.0;
    private final double kD = 0.0;
    private final double kFF = 0.0;
    private final double kMaxOutput = 0.3;
    private final double kMinOutput = -0.3;

    // pre-defined positions for the elevator
    public double l1_pos = 0.0;
    public double l2_pos = 0.0;
    public double l3_pos = 0.0;
    public double l4_pos = 0.0;
    public double elevator_top = 68.0;
    private double lastTargetPosition = 0.0;

    // left is increasing as elevator raises
    // right is decreasing as elevator raises
    public static ElevatorSubsystem elevatorinstance;

    public ElevatorSubsystem() {
        // initialize motors
        leftElevatorMotor = new SparkMax(16, MotorType.kBrushless);
        rightElevatorMotor = new SparkMax(15, MotorType.kBrushless);
        // initialize encoders
        leftElevatorEncoder = leftElevatorMotor.getEncoder();
        rightElevatorEncoder = rightElevatorMotor.getEncoder();
        // initialize PID controller
        pidController = new PIDController(kP, kI, kD);
        elevatorinstance = this; // save subsystem so it can be accessed anywhere
        // verify this works before uncommenting
        // setDefaultCommand(new Command() { // run this command when the subsystem isn't being used by another command
        //     @Override
        //     public void initialize() {
        //         elevatorinstance.resetPID(); // reset the PID controller
        //     }
        //     @Override
        //     public void execute() {
        //         elevatorinstance.HoldPosition(); // hold the elevator at the last set position
        //     }
        // });
    }

    /** Move the elevator to a certain position */
    public void MoveElevatorToPosition(double targetPosition){ // move the elevator to a certain position
        this.lastTargetPosition = targetPosition; // save the target position
        double currentPosition = leftElevatorEncoder.getPosition();
        double output = pidController.calculate(currentPosition, targetPosition);

        output = Math.max(kMinOutput, Math.min(kMaxOutput, output));

        leftElevatorMotor.set(output);
        rightElevatorMotor.set(-output);
    }
    /** Hold the elevator at the last target position */
    public void HoldPosition(){ // hold the elevator at the last target position
        this.MoveElevatorToPosition(lastTargetPosition);
    }
    /** Move the elevator at a certain speed */
    public void MoveElevator(double speed){
        leftElevatorMotor.set(speed); // run motors in opposite directions to move elevator up and down
        rightElevatorMotor.set(-speed);
        this.lastTargetPosition = this.getEncoderPosition(); // update the last target position so that the elevator doesn't go back to the last set position after the command ends
    }
    /** Get the height of the elevator */
    public double getEncoderPosition(){
        return leftElevatorEncoder.getPosition();
    }
    /** Reset the PID controller */
    public void resetPID(){
        pidController.reset();
    }
}