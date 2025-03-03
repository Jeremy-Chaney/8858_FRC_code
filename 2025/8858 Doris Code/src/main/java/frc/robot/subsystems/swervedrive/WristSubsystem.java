package frc.robot.subsystems.swervedrive;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WristSubsystem extends SubsystemBase {

    private final SparkMax wristMotor;
    private final double kP = 0.5;
    private final double kI = 0.0;
    private final double kD = 0.0;
    private final double kFF = 0.0;
    private final double kMaxOutput = 0.3;
    private final double kMinOutput = -0.3;

    public final AbsoluteEncoder wristEncoder;
    private final PIDController wrist_PID;
    public static WristSubsystem wrist_instance;
    private double lastPosition;

    public WristSubsystem() {
        // initialize motor
        wristMotor = new SparkMax(22, MotorType.kBrushless);
        // initialize throughbore encoder
        wristEncoder = wristMotor.getAbsoluteEncoder();
        // initialize PID controller
        wrist_PID = new PIDController(kP, kI, kD);
        wrist_instance = this; // save subsystem so it can be accessed anywhere
        // verify this works before uncommenting
        setDefaultCommand(new Command() { // run this command when the subsystem isn't being used by another command
            {
                addRequirements(wrist_instance);
            }
            @Override
            public void initialize() {
                wrist_instance.resetPID(); // reset the PID controller
            }
            @Override
            public void execute() {
                wrist_instance.MoveWristToPosition(0.5); // hold the wrist at the last set position
            }
        });
    }

    /** move wrist to a position */
    public void MoveWristToPosition(double targetPosition) {
        this.lastPosition = targetPosition; // save the target position
        double currentPosition = wristEncoder.getPosition(); // get the current position
        double output = wrist_PID.calculate(currentPosition, targetPosition); // calulate the motor speed with a pid controller

        output = Math.max(kMinOutput, Math.min(kMaxOutput, output)); // limit the output to the min and max output

        wristMotor.set(output); // set the motor speed
    }

    /** hold the wrist at the last set position */
    public void holdPosition() {
        MoveWristToPosition(lastPosition);
    }

    /** move wrist at a speed */
    public void MoveWrist(double speed) {
        wristMotor.set(speed);
        lastPosition = getEncoderPosition(); // update the last target position so that the wrist doesn't go back to the last set position after the command ends
    }

    /** get wrist position */
    public double getEncoderPosition() {
        return wristEncoder.getPosition();
    }

    /** reset pid controller */
    public void resetPID() {
        wrist_PID.reset();
    }
}