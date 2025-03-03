// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.File;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.climber.MoveClimberToPosition;
import frc.robot.commands.elevator.MoveElevatorToPosition;
import frc.robot.commands.intake.MoveWrist;
import frc.robot.commands.intake.MoveWristToPosition;
import frc.robot.commands.intake.algaeIntake;
import frc.robot.commands.intake.coralIntake;
import frc.robot.subsystems.swervedrive.AlgaeSubsystem;
import frc.robot.subsystems.swervedrive.ClimberSubsystem;
import frc.robot.subsystems.swervedrive.CoralIntakeSubsystem;
import frc.robot.subsystems.swervedrive.ElevatorSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.subsystems.swervedrive.WristSubsystem;
import swervelib.SwerveInputStream;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic
 * methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and
 * trigger mappings) should be declared here.
 */
public class RobotContainer {

    // Replace with CommandPS4Controller or CommandJoystick if needed
    public final CommandXboxController driverXbox = new CommandXboxController(0);
    // The robot's subsystems and commands are defined here...
    public final SwerveSubsystem drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
            "swerve/neo"));

    private final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    private final AlgaeSubsystem algaeSubsystem = new AlgaeSubsystem();
    private final CoralIntakeSubsystem coralSubsystem = new CoralIntakeSubsystem();
    private final WristSubsystem wristSubsystem = new WristSubsystem();
    private final ClimberSubsystem climberSubsystem = new ClimberSubsystem();
    private int preset = 0;

    /**
     * Converts driver input into a field-relative ChassisSpeeds that is controlled
     * by angular velocity.
     */
    SwerveInputStream driveAngularVelocity = SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> driverXbox.getLeftY() * -1,
            () -> driverXbox.getLeftX() * -1)
            .withControllerRotationAxis(driverXbox::getRightX)
            .deadband(OperatorConstants.DEADBAND)
            .scaleTranslation(0.8)
            .allianceRelativeControl(true);

    /**
     * Clone's the angular velocity input stream and converts it to a fieldRelative
     * input stream.
     */
    SwerveInputStream driveDirectAngle = driveAngularVelocity.copy().withControllerHeadingAxis(driverXbox::getRightX,
            driverXbox::getRightY)
            .headingWhile(true);

    /**
     * Clone's the angular velocity input stream and converts it to a robotRelative
     * input stream.
     */
    SwerveInputStream driveRobotOriented = driveAngularVelocity.copy().robotRelative(true)
            .allianceRelativeControl(false);

    SwerveInputStream driveAngularVelocityKeyboard = SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> -driverXbox.getLeftY(),
            () -> -driverXbox.getLeftX())
            .withControllerRotationAxis(() -> driverXbox.getRawAxis(
                    2))
            .deadband(OperatorConstants.DEADBAND)
            .scaleTranslation(0.8)
            .allianceRelativeControl(true);
    // Derive the heading axis with math!
    SwerveInputStream driveDirectAngleKeyboard = driveAngularVelocityKeyboard.copy()
            .withControllerHeadingAxis(() -> Math.sin(driverXbox.getRawAxis(2) * Math.PI) * (Math.PI * 2),
                    () -> Math.cos(driverXbox.getRawAxis(2) * Math.PI) * (Math.PI * 2))
            .headingWhile(true);

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        // Configure the trigger bindings
        configureBindings();
        // configureButtonBindings();
        DriverStation.silenceJoystickConnectionWarning(true);
        NamedCommands.registerCommand("test", Commands.print("I EXIST"));
    }

    // private void configureButtonBindings(){
    // new JoystickButton(driverXbox, XboxController.Button.kB.value).onTrue(new
    // MoveElevatorToPosition(elevatorSubsystem, 20));
    // }

    /**
     * Use this method to define your trigger->command mappings. Triggers can be
     * created via the
     * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
     * an arbitrary predicate, or via the
     * named factories in
     * {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses
     * for
     * {@link CommandXboxController
     * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
     * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick
     * Flight joysticks}.
     */
    private void configureBindings() {
        Command driveFieldOrientedDirectAngle = drivebase.driveFieldOriented(driveDirectAngle);
        Command driveFieldOrientedAnglularVelocity = drivebase.driveFieldOriented(SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> driverXbox.getLeftY(),
            () -> driverXbox.getLeftX())
            .withControllerRotationAxis(() -> driverXbox.getRightX() * -0.85)
            .deadband(OperatorConstants.DEADBAND)
            .scaleTranslation(0.8)
            .allianceRelativeControl(true));
        Command driveRobotOrientedAngularVelocity = drivebase.driveFieldOriented(driveRobotOriented);
        Command driveSetpointGen = drivebase.driveWithSetpointGeneratorFieldRelative(
                driveDirectAngle);
        Command driveFieldOrientedDirectAngleKeyboard = drivebase.driveFieldOriented(driveDirectAngleKeyboard);
        Command driveFieldOrientedAnglularVelocityKeyboard = drivebase.driveFieldOriented(driveAngularVelocityKeyboard);
        Command driveSetpointGenKeyboard = drivebase.driveWithSetpointGeneratorFieldRelative(
                driveDirectAngleKeyboard);

        if (RobotBase.isSimulation()) {
            drivebase.setDefaultCommand(driveFieldOrientedDirectAngleKeyboard);
        } else {
            drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity);
            
        }

        if (Robot.isSimulation()) {
            driverXbox.start()
                    .onTrue(Commands.runOnce(() -> drivebase.resetOdometry(new Pose2d(3, 3, new Rotation2d()))));
            driverXbox.button(1).whileTrue(drivebase.sysIdDriveMotorCommand());

        }
        if (DriverStation.isTest()) {
            drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity); // Overrides drive command above!

            driverXbox.x().whileTrue(Commands.runOnce(drivebase::lock, drivebase).repeatedly());
            driverXbox.y().whileTrue(drivebase.driveToDistanceCommand(1.0, 0.2));
            driverXbox.start().onTrue((Commands.runOnce(drivebase::zeroGyro)));
            driverXbox.back().whileTrue(drivebase.centerModulesCommand());
            // driverXbox.leftBumper().onTrue(Commands.none());
            // driverXbox.rightBumper().onTrue(Commands.none());
        } else { // configure controls for teleop
            /*
            Notes for controls:

            .onTrue() will run a command the button is initially pressed
            .onFalse() will run a command when the button is released
            
            .whileTrue() will run a command when the button is pressed and cancel it when the button is released
            .whileFalse() will run a command when the button is released and cancel it when the button is pressed

            a ParallelCommandGroup will run multiple commands at the same time until the last command in the list finishes

            general control format: driverXbox.<button name>().<when it should run>(command);
            */

            // Level 1 coral (the bin thing at the bottom of the reef)
            driverXbox.povDown().onTrue(new ParallelCommandGroup(
                    new MoveElevatorToPosition(elevatorSubsystem,Constants.ELE_L1),
                    new MoveWristToPosition(wristSubsystem, Constants.WR_L1)));
            // driverXbox.povDown().onFalse());

            // Level 2 coral (lowest arm on the reef)
            driverXbox.povLeft().onTrue(new ParallelCommandGroup(
                    new MoveElevatorToPosition(elevatorSubsystem, Constants.ELE_L2),
                    new MoveWristToPosition(wristSubsystem, Constants.WR_L2)){{
                        setName("L2");
                    }});

            // // low algae
            // driverXbox.povUpLeft().onTrue(new ParallelCommandGroup(
            //         new MoveElevatorToPosition(elevatorSubsystem, 32),
            //         new MoveWristToPosition(wristSubsystem, .5)));

            // Level 3 coral (middle arm on the reef)
            driverXbox.povUp().onTrue(new ParallelCommandGroup(
                    new MoveElevatorToPosition(elevatorSubsystem, Constants.ELE_L3),
                    new MoveWristToPosition(wristSubsystem, Constants.WR_L3)));

            // // high algae
            // driverXbox.povUpRight().onTrue(new ParallelCommandGroup(
            //         new MoveElevatorToPosition(elevatorSubsystem, 49.833),
            //         new MoveWristToPosition(wristSubsystem, .5)));

            // high algae
            driverXbox.back().onTrue(new ParallelCommandGroup(
                    new MoveElevatorToPosition(elevatorSubsystem, 45.833),
                    new MoveWristToPosition(wristSubsystem, .5)));

            // Level 4 coral (top arm on the reef)
            driverXbox.povRight().onTrue(new ParallelCommandGroup(
                    new MoveElevatorToPosition(elevatorSubsystem, Constants.ELE_L4),
                    new MoveWristToPosition(wristSubsystem, Constants.WR_L4)));

            // // List Down
            // driverXbox.povDown().whileTrue(new ParallelCommandGroup(
            //         new MoveElevatorToPositionList(elevatorSubsystem, -1),
            //         new MoveWristToPositionList(wristSubsystem, -1)));
            // // driverXbox.povDown().onFalse());

            // // List up
            // driverXbox.povUp().whileTrue(new ParallelCommandGroup(
                    // new MoveElevatorToPositionList(elevatorSubsystem, 1),
            //         new MoveWristToPositionList(wristSubsystem, 1)));

            // move coral intake. left bumper is intake, right bumper is outtake
            driverXbox.leftBumper().whileTrue(new ParallelCommandGroup(
                new coralIntake(coralSubsystem, -Constants.COR_M_SPEED),
                new MoveElevatorToPosition(elevatorSubsystem, Constants.ELE_COR_IN),
                new MoveWristToPosition(wristSubsystem, Constants.WR_COR_IN)
            ));

            // move algae intake
            driverXbox.a().whileTrue(new algaeIntake(algaeSubsystem, -Constants.ALG_M_SPEED));
            driverXbox.y().whileTrue(new algaeIntake(algaeSubsystem, Constants.ALG_M_SPEED));


            /*
             * Coral Station preset
             * Sets elevator/wrist position
             * intakes coral constantly
             */
            driverXbox.rightBumper().whileTrue(new ParallelCommandGroup(
                new coralIntake(coralSubsystem, Constants.COR_M_SPEED),
                new algaeIntake(algaeSubsystem, -Constants.ALG_M_SPEED)
            ));
            // drivebase.driveToPose(
            // new Pose2d(new Translation2d(4, 4), Rotation2d.fromDegrees(0)))
            // );
            // driverXbox.start().whileTrue(Commands.none());
            // driverXbox.back().whileTrue(Commands.none());
            // driverXbox.leftBumper().whileTrue(Commands.runOnce(drivebase::lock,
            // drivebase).repeatedly());
            // driverXbox.rightBumper().onTrue(Commands.none());

            // move wrist at speed
            driverXbox.leftTrigger(0.2)
                    .whileTrue(new MoveWrist(wristSubsystem, -0.2));
            driverXbox.rightTrigger(0.2)
                    .whileTrue(new MoveWrist(wristSubsystem, 0.2));

            // move climber at speed. this will need to be changed to MoveClimberToPosition at some point
            // driverXbox.x().whileTrue(new MoveClimber(climberSubsystem, 0.15));
            // driverXbox.b().whileTrue(new MoveClimber(climberSubsystem, -0.15));
            //driverXbox.x().onTrue(new MoveClimberToPosition(climberSubsystem, 0.8, 0.1));
            driverXbox.b().onTrue(new MoveClimberToPosition(climberSubsystem, 0.41, 0.4));


            // Reset the elevator, wrist and gyro
            driverXbox.start().onTrue(new ParallelCommandGroup(
                // new MoveElevatorToPositionList(elevatorSubsystem, -100),
                // new MoveWristToPositionList(wristSubsystem, -100),
                new MoveElevatorToPosition(elevatorSubsystem, 0),
                new MoveWristToPosition(wristSubsystem, 0.500),
                new algaeIntake(algaeSubsystem, 0),
                new coralIntake(coralSubsystem, 0)
            ));
            driverXbox.start().onTrue(new ParallelCommandGroup(
                // new MoveElevatorToPositionList(elevatorSubsystem, -100),
                // new MoveWristToPositionList(wristSubsystem, -100),
                (Commands.runOnce(drivebase::zeroGyro))
            ));
        }

    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An example command will be run in autonomous
        return drivebase.getAutonomousCommand("Step 1 Auto");
    }

    public void setMotorBrake(boolean brake) {
        drivebase.setMotorBrake(brake);
    }
}
