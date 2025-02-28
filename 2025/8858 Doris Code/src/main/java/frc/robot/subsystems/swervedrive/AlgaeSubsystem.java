package frc.robot.subsystems.swervedrive;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class AlgaeSubsystem extends SubsystemBase {

    private final SparkMax leftAlgaeIntakeMotor;
    private final SparkMax rightAlgaeIntakeMotor;
    public static AlgaeSubsystem algae_intake_instance;

    public AlgaeSubsystem() {
        // initialize motors
        leftAlgaeIntakeMotor = new SparkMax(18, MotorType.kBrushless);
        rightAlgaeIntakeMotor = new SparkMax(19, MotorType.kBrushless);
        algae_intake_instance = this; // save subsystem so it can be accessed anywhere
    }

    /** move intake at speed */
    public void algaeIntake(double speed) {
        leftAlgaeIntakeMotor.set(speed);
        rightAlgaeIntakeMotor.set(-speed);
    }
}