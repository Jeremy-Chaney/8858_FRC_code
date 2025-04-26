// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkAbsoluteEncoder;
import com.revrobotics.CANSparkBase.IdleMode;

import javax.naming.ldap.ManageReferralControl;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkBase;

import java.text.BreakIterator;
import java.time.Clock;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PneumaticHub;

// Phoenix 6 is in the com.ctre.phoenix6.* packages
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.sim.TalonFXSimState;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.RgbFadeAnimation;
import com.ctre.phoenix.led.TwinkleAnimation;
import com.ctre.phoenix.led.LarsonAnimation.BounceMode;
import com.ctre.phoenix.led.TwinkleAnimation.TwinklePercent;
import com.ctre.phoenix.led.TwinkleOffAnimation.TwinkleOffPercent;
import com.ctre.phoenix.led.FireAnimation;
import com.ctre.phoenix.led.LarsonAnimation;
import com.ctre.phoenix.led.ColorFlowAnimation;

// All hardware classes already have WPILib integration
// final TalonFX m_talonFX = new TalonFX(0);
// final CANcoder m_cancoder = new CANcoder(0);

// final TalonFXSimState m_talonFXSim = m_talonFX.getSimState();

// final DutyCycleOut m_talonFXOut = new DutyCycleOut(0);

// final TalonFXConfiguration m_talonFXConfig = new TalonFXConfiguration();
// final CANcoderConfiguration m_cancoderConfig = new CANcoderConfiguration();

// InvertedValue m_talonFXInverted = InvertedValue.CounterClockwise_Positive;

// m_talonFX.setControl(m_talonFXOut);/* */

//import edu.wpi.first.wpilibj.XboxController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  public CANSparkMax leftFrontMotor = new CANSparkMax(8, CANSparkLowLevel.MotorType.kBrushed);
  public CANSparkMax leftBackMotor = new CANSparkMax(3, CANSparkLowLevel.MotorType.kBrushed);
  public CANSparkMax rightFrontMotor = new CANSparkMax(2, CANSparkLowLevel.MotorType.kBrushed);
  public CANSparkMax rightBackMotor = new CANSparkMax(4, CANSparkLowLevel.MotorType.kBrushed);
  public CANSparkMax armMotor = new CANSparkMax(5, CANSparkLowLevel.MotorType.kBrushless);
  public Boolean armMotionRequested = false;
  public double targetPosition = 0;
  public double targetSpeed = 0;
  public static Servo shooter = new Servo(0);
  public static final double MANUALARMSPEED = 0.1;
  public static final int MANUALDOWNBUTTON = 3;
  public static final int MANUALUPBUTTON = 5;
  public static final double AUTOARMSPEED = 0.05;
  public static final int AUTODOWNBUTTON = 4;
  public static final int AUTOUPBUTTON = 6;
  public static final double ENCODERTODEGREES = 15.0;
  public static final double ENCODERTOLERANCE = 1.0 / ENCODERTODEGREES;
  public static final long SHORTSLEEP = 50;
  // public static final double NOTLAUNCH = 0;
  public static final double LAUNCH = 30;
  public Compressor compressor = new Compressor(PneumaticsModuleType.REVPH);
  public Servo compressorSwitch = new Servo(1);
  public PWM lights = new PWM(2);
  public Solenoid m_Solenoid = new Solenoid(PneumaticsModuleType.REVPH, 1);
  public Boolean solenoid = true;
  public Joystick joystick = new Joystick(0);
  public static double driveLimiter = 0.6;
  final TalonFX m_talonFX = new TalonFX(0);
  final CANcoder m_cancoder = new CANcoder(0);
  final PneumaticHub PH = new PneumaticHub();

  final TalonFXSimState m_talonFXSim = m_talonFX.getSimState();

  final TalonFXConfiguration m_talonFXConfig = new TalonFXConfiguration();
  final CANcoderConfiguration m_cancoderConfig = new CANcoderConfiguration();

  InvertedValue m_talonFXInverted = InvertedValue.CounterClockwise_Positive;

  public CANdle candle = new CANdle(11);

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    armMotor.setIdleMode(IdleMode.kBrake);
    leftBackMotor.setIdleMode(IdleMode.kCoast);
    leftFrontMotor.setIdleMode(IdleMode.kCoast);
    rightBackMotor.setIdleMode(IdleMode.kCoast);
    rightFrontMotor.setIdleMode(IdleMode.kCoast);
    leftFrontMotor.setInverted(false);
    leftBackMotor.setInverted(false);
    rightFrontMotor.setInverted(false);
    rightBackMotor.setInverted(false);
    //compressor.enableDigital();
    compressor.enableAnalog(80,100);
    LarsonAnimation larson_anim = new LarsonAnimation(50, 150, 25, 0, .2, 8, BounceMode.Back, 6);
    // candle.animate(anim);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // 0.80 = Red
    // 0.83 = Orange
    // 0.87 = Yellow
    // 0.90 = Green
    // 0.93 = Blue
    // 0.95 = Purple
    // Red = 255, 0, 0
    // Orange = 200, 10, 0
    // Yellow = 75, 50, 0
    // Green = 0, 255, 0
    // Sky Blue = 0, 255, 255
    // Blue = 0, 0, 255
    // Purple = 200, 0, 255
    // Pink = 255, 80, 60
    lights.setPosition(0.80);
    RainbowAnimation rainbow_anim = new RainbowAnimation(0.2, 0.5, 8);
    FireAnimation fire_anim = new FireAnimation(0.2, .25, 8, 0.7, 0.1);
    RgbFadeAnimation fde_anim = new RgbFadeAnimation(.2, 1, 105);
    TwinkleAnimation twinkle_anim = new TwinkleAnimation(0, 0, 255, 0, 1, 105, TwinklePercent.Percent18);
    LarsonAnimation larson_anim = new LarsonAnimation(50, 150, 25, 0, .2, 10, BounceMode.Back, 6);
    LarsonAnimation larson_anim2 = new LarsonAnimation(0, 0, 255, 0, .25, 10, BounceMode.Back, 6);
    ColorFlowAnimation flow_anim = new ColorFlowAnimation(50, 150, 25);
    // candle.setLEDs(0, 0, 0, 0, 0, 8);
    // if(joystick.getRawButton(8)){
    //   candle.setLEDs(0, 0, 0, 0, 0, 0);
    // }
    // if (joystick.getRawButton(1)) {
    // shooter.set(1);
    // candle.setLEDs(255, 0, 0, 0, 0, 20);
    // }
    // else {
    // shooter.set(0);
    // lights.setPosition(0.93);
    // }

    if (joystick.getRawButton(1)) {
      try {
        charge();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    else if (!joystick.getRawButton(1)){
    }

    if (joystick.getRawButton(2)) {
      compressor.enableDigital();
      // compressor.enableAnalog(80, 100);
    }
    else{
      compressor.enableDigital();
      // compressor.enableAnalog(80, 100);
    }

    if (joystick.getRawButton(7)){
      candle.setLEDs(0, 0, 0, 0, 0, 107);
    }

    while (joystick.getRawButtonPressed(2)) {
      // candle.animate(larson_anim2);

      }

      // Mr Gibson said to not mess to much with the solenoid too much for now.
      // if (joystick.getRawButton(7)) {
      //   solenoid = !solenoid;
      //   m_Solenoid.set(solenoid);
      // }
      double shooterPosition = shooter.getPosition();
      SmartDashboard.putNumber("Shooter Value", shooterPosition);
      // This section controls how the arm moves
      if (armMotionRequested) {
        // Automatic controls of the arm
        double currentPosition = armMotor.getEncoder().getPosition();
        SmartDashboard.putNumber("Arm value", currentPosition);
        // If the arm is close to the target position within tolerance, stop moving
        if (Math.abs(currentPosition - targetPosition) < ENCODERTOLERANCE) {
          armMotionRequested = false;
          targetSpeed = 0;
        } else {
          armMotor.set(targetSpeed);
        }
        // Disable automatic movement
        if (joystick.getRawButton(MANUALUPBUTTON)) {
          armMotionRequested = false;
        }
        // Disable automatic movement
        if (joystick.getRawButton(MANUALDOWNBUTTON)) {
          armMotionRequested = false;
        }
      } else { // Manual controls of the arm
        // Press button 5 to move the arm up
        if (joystick.getRawButton(MANUALUPBUTTON)) {
          armMotor.set(MANUALARMSPEED);
        }
        // Press button 3 to move the arm down
        else if (joystick.getRawButton(MANUALDOWNBUTTON)) {
          armMotor.set(-MANUALARMSPEED);
        } else {
          armMotor.set(0.0);
        }

        // Press button 6 to move the arm up 15 degrees
        if (joystick.getRawButton(AUTOUPBUTTON)) {
          // Get the starting position
          double startPosition = armMotor.getEncoder().getPosition();
          SmartDashboard.putNumber("Arm value", startPosition);
          // Check to see if the arm is within tolerance below the highest position
          if (startPosition < (60.0 / ENCODERTODEGREES - ENCODERTOLERANCE)) {
            armMotionRequested = true;
            // Calculate the target position
            targetPosition = startPosition + 15.0 / ENCODERTODEGREES;
            if (targetPosition > 60.0 / ENCODERTODEGREES) { // Prevent the arm from going too high
              targetPosition = 60.0 / ENCODERTODEGREES;
            }
            targetSpeed = AUTOARMSPEED;
            armMotor.set(targetSpeed);
          }
        }

        // Press button 4 to move the arm down 15 degrees
        else if (joystick.getRawButton(AUTODOWNBUTTON)) {
          // Get the starting position
          double startPosition = armMotor.getEncoder().getPosition();
          SmartDashboard.putNumber("Arm value", startPosition);
          // Check to see if the arm is within tolerance above the horizontal position
          if (startPosition > ENCODERTOLERANCE) {
            armMotionRequested = true;
            // Calculate the target position
            targetPosition = startPosition - 15.0 / ENCODERTODEGREES;
            if (targetPosition < 0.0) { // Prevent the arm from going too low
              targetPosition = 0.0;
            }
            targetSpeed = -AUTOARMSPEED;
            armMotor.set(targetSpeed);
          }
        }
      }

      double y = joystick.getRawAxis(1);
      double x = joystick.getRawAxis(0);

      leftBackMotor.set((y - x) * driveLimiter);
      leftFrontMotor.set((y - x) * driveLimiter);
      rightBackMotor.set((-y - x) * driveLimiter);
      rightFrontMotor.set((-y - x) * driveLimiter);
    }

    public void notLaunch() throws InterruptedException{
      Thread.sleep(500);
    }

    public void launch() throws InterruptedException{
      shooter.setPosition(20);
      Thread.sleep(250);
      shooter.setPosition(0);
      candle.setLEDs(0, 0, 255, 0, 0, 8);
      candle.setLEDs(0, 255, 0, 0, 8, 97);
      int lightnum = 9;
      int lightnub = 0;

      // while (lightnum < 105) {
      //   candle.setLEDs(0, 210, 0, 50, lightnum, 1);
      //   candle.setLEDs(0, 0, 210, 50, 1, 1);
      //   candle.setLEDs(0, 0, 210, 50, 3, 1);
      //   candle.setLEDs(0, 0, 210, 50, 5, 1);
      //   candle.setLEDs(0, 0, 210, 50, 7, 1);
      //   lightnum = lightnum + 2;
      // }
      // while (lightnub < 105) {
      //   candle.setLEDs(211, 211, 211, 50, lightnub, 1);
      //   lightnub = lightnub + 2;
      // }
    }

    public void stop() throws InterruptedException{
      Thread.sleep(1000);
      candle.setLEDs(0, 0, 0, 0, 0, 105);
    }

    public void charge() throws InterruptedException{
      candle.setLEDs(255, 0, 0, 0, 104, 2);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 101, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 98, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 95, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 92, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 89, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 86, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 83, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 80, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 77, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 74, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 71, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 68, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 65, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 62, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 59, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 56, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 53, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 50, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 47, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 44, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 41, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 38, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 35, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 32, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 29, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 26, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 23, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 20, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 17, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 14, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 11, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 8, 3);
      Thread.sleep(SHORTSLEEP);
      candle.setLEDs(255, 0, 0, 0, 0, 1);
      candle.setLEDs(255, 0, 0, 0, 7, 1);
      Thread.sleep(500);
      candle.setLEDs(255, 0, 0, 0, 1, 1);
      candle.setLEDs(255, 0, 0, 0, 6, 1);
      Thread.sleep(500);
      candle.setLEDs(255, 0, 0, 0, 2, 1);
      candle.setLEDs(255, 0, 0, 0, 5, 1);
      Thread.sleep(500);
      candle.setLEDs(255, 0, 0, 0, 3, 1);
      candle.setLEDs(255, 0, 0, 0, 4, 1);
      Thread.sleep(500);
      launch();
    }
  

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }
}
