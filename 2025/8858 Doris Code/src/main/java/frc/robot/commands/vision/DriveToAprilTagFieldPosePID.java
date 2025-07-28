package frc.robot.commands.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.util.MovingAverage;

public class DriveToAprilTagFieldPosePID extends Command {
    private final SwerveSubsystem swerve;
    private final Pose2d targetPose;

    MovingAverage currentX_Average = new MovingAverage(20);
    MovingAverage currentY_Average = new MovingAverage(20);
    MovingAverage forwardAvg = new MovingAverage(20);
    MovingAverage strafeAvg = new MovingAverage(20);
    private final PIDController forward_drive_pidController;
    private final PIDController strafe_drive_pidController;
    private final PIDController angle_pidController;

    private final double drive_kp = 0.9;
    private final double drive_ki = 0.0;
    private final double drive_kd = 0.1;

    private final double angle_kp = 0.1;
    private final double angle_ki = 0.0;
    private final double angle_kd = 0.0;

    private final double kTranslationGain = 1.5;
    private final double kRotationGain = 1.0;

    private final double maxRotationSpeed = 1.0;
    private final double maxMovementSpeed = 0.5;

    private final double kPoseTolerance = 0.07;
    private final double kAngleTolerance = 1.0;

    public DriveToAprilTagFieldPosePID(SwerveSubsystem swerve, AprilTagFieldLayout layout, int tagID, double xOffset, double yOffset, int rotation_deg){
        this.swerve = swerve;
        forward_drive_pidController = new PIDController(drive_kp, drive_ki, drive_kd);
        strafe_drive_pidController = new PIDController(drive_kp, drive_ki, drive_kd);
        angle_pidController = new PIDController(angle_kp, angle_ki, angle_kd);
        Pose3d pose3d = layout.getTagPose(tagID).orElse(null);
        double x = pose3d.getX();
        double y = pose3d.getY();
        Rotation3d rot3d = pose3d.getRotation();
        Rotation2d rot2d = Rotation2d.fromDegrees(rot3d.getZ());
        Transform2d offset = new Transform2d(
            new Translation2d(xOffset, yOffset),
            Rotation2d.fromDegrees(rotation_deg)
        );
        this.targetPose = new Pose2d(x, y, rot2d).transformBy(offset);

        if(targetPose == null){
            throw new IllegalArgumentException("AprilTag ID " + tagID + " not found in field layout.");
        }

        addRequirements(swerve);
    }

    @Override
    public void initialize(){
        forward_drive_pidController.reset();
        strafe_drive_pidController.reset();
        angle_pidController.reset();
    }

    @Override
    public void execute() {
        Pose2d currentPose = swerve.getPose();

        double distanceError = targetPose.getTranslation().getDistance(currentPose.getTranslation());
        double speed = MathUtil.clamp(drive_kp, 0, maxMovementSpeed);

        // double dx = targetPose.getX() - currentPose.getX();
        // double dy = targetPose.getY() - currentPose.getY();
        Rotation2d dTheta = targetPose.getRotation().minus(currentPose.getRotation());

        currentX_Average.add(currentPose.getX());
        currentY_Average.add(currentPose.getY());

        double smoothedX = currentX_Average.getAverage();
        double smoothedY = currentY_Average.getAverage();

        double forward = forward_drive_pidController.calculate(targetPose.getX(), smoothedX);
        double strafe = strafe_drive_pidController.calculate(targetPose.getY(), smoothedY);
        // double turn = angle_pidController.calculate(targetPose.getRotation().getDegrees(), currentPose.getRotation().getDegrees());

        forward = MathUtil.clamp(forward, -maxMovementSpeed, maxMovementSpeed);
        strafe = MathUtil.clamp(strafe, -maxMovementSpeed, maxMovementSpeed);
        double turn = MathUtil.clamp(dTheta.getDegrees(), -maxRotationSpeed, maxRotationSpeed);

        forwardAvg.add(forward);
        strafeAvg.add(strafe);

        forward = forwardAvg.getAverage();
        strafe = strafeAvg.getAverage();

        swerve.drive(new Translation2d(forward, strafe), turn, true);

        SmartDashboard.putNumber("LL Current X", smoothedX);
        SmartDashboard.putNumber("LL Current Y", smoothedY);
        SmartDashboard.putNumber("LL Current Degrees", currentPose.getRotation().getDegrees());

        SmartDashboard.putNumber("LL Target X", targetPose.getX());
        SmartDashboard.putNumber("LL Target Y", targetPose.getY());
        SmartDashboard.putNumber("LL Target Degrees", targetPose.getRotation().getDegrees());

        SmartDashboard.putNumber("LL Forward", forward);
        SmartDashboard.putNumber("LL Strafe", strafe);
        SmartDashboard.putNumber("LL Turn", turn);
    }

    @Override
    public boolean isFinished() {
        Pose2d currentPose = swerve.getPose();

        double dx = targetPose.getX() - currentPose.getX();
        double dy = targetPose.getY() - currentPose.getY();
        double dTheta = targetPose.getRotation().getDegrees() - currentPose.getRotation().getDegrees();

        return (Math.hypot(dx, dy) < kPoseTolerance) && (Math.abs(dTheta) < kAngleTolerance);
    }

    @Override
    public void end(boolean interrupted){
        swerve.drive(new Translation2d(), 0, true);
    }
}
