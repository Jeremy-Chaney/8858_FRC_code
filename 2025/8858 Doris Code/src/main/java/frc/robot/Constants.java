// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import swervelib.math.Matter;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean constants. This
 * class should not be used for any other purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants
{

  public static final double ROBOT_MASS = (148 - 20.3) * 0.453592; // 32lbs * kg per pound
  public static final Matter CHASSIS    = new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS);
  public static final double LOOP_TIME  = 0.13; //s, 20ms + 110ms sprk max velocity lag
  public static final double MAX_SPEED  = Units.feetToMeters(14.5);

  //Positions for coral elevator height for levels 1-4
  public static final double ELE_L1 =(0.0);
  public static final double ELE_L2 =(11.286);
  public static final double ELE_L3 =(32.0);
  public static final double ELE_L4 =(70.5);

  //Positions for coral wrist angle for levels 1-4
  public static final double WR_L1 =(0.667);
  public static final double WR_L2 =(0.66);
  public static final double WR_L3 =(0.66);
  public static final double WR_L4 =(0.691);

  //Speed for Algae and Coral intake Motors
  public static final double ALG_M_SPEED =(0.4);
  public static final double COR_M_SPEED =(0.5);
//   public static final double ALG_M_MIN_SPEED = (-0.5);

  //Positions for Wrist and elevator for coral intake
  public static final double ELE_COR_IN =(10.0);
  public static final double WR_COR_IN =(0.6);

  //Climber speed stuff
  public static final double CL_M_SPEED = (0.4);


  // Maximum speed of the robot in meters per second, used to limit acceleration.

//  public static final class AutonConstants
//  {
//
//    public static final PIDConstants TRANSLATION_PID = new PIDConstants(0.7, 0, 0);
//    public static final PIDConstants ANGLE_PID       = new PIDConstants(0.4, 0, 0.01);
//  }

  public static final class DrivebaseConstants
  {

    // Hold time on motor brakes when disabled
    public static final double WHEEL_LOCK_TIME = 10; // seconds
  }

  public static class OperatorConstants
  {

    // Joystick Deadband
    public static final double DEADBAND        = 0.1;
    public static final double LEFT_Y_DEADBAND = 0.1;
    public static final double RIGHT_X_DEADBAND = 0.1;
    public static final double TURN_CONSTANT    = 6;

    //CAN IDs
    public static final int FL_ANG = 1;
    public static final int FL_DR = 2;
    public static final int FR_ANG = 3;
    public static final int FR_DR = 4;
    public static final int BL_DR = 5;
    public static final int BL_ANG = 6;
    public static final int BR_DR = 7;
    public static final int BR_ANG = 8;
    public static final int LC_MOTOR = 9;
    public static final int RC_MOTOR = 10;
    public static final int BL_CANCODER = 11;
    public static final int BR_CANCODER = 12;
    public static final int FR_CANCODER = 13;
    public static final int FL_CANCODER = 14;
    public static final int ELE_R = 15;
    public static final int ELE_L = 16;
    public static final int ALG_L = 18;
    public static final int ALG_R = 19;
    public static final int COR_MOTOR = 21;
    public static final int WRIST_MOTOR = 22;
    public static final int ELE_MOTOR = 23;

  }
}
