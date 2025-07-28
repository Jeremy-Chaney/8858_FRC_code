package frc.robot.commands.vision;

import static edu.wpi.first.units.Units.Rotation;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

public class DriveToAprilTagFieldPose extends Command {
    private final SwerveSubsystem swerve;
    private final AprilTagFieldLayout layout;
    private final int TargetTagID;
    private final Pose2d targetPose;

    private final double kTranslationGain = 2.5;
    private final double kRotationGain = 1.0;

    private final double maxRotationSpeed = 1.0;
    private final double maxMovementSpeed = 2.0;

    private final double kPoseTolerance = 0.07;
    private final double kAngleTolerance = 1.0;

    public DriveToAprilTagFieldPose(SwerveSubsystem swerve, AprilTagFieldLayout layout, int tagID, double xOffset, double yOffset, int rotation_deg){
        this.swerve = swerve;
        this.layout = layout;
        this.TargetTagID = tagID;
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
    public void execute() {
        Pose2d currentPose = swerve.getPose();

        double dx = targetPose.getX() - currentPose.getX();
        double dy = targetPose.getY() - currentPose.getY();
        Rotation2d dTheta = targetPose.getRotation().minus(currentPose.getRotation());

        double forward = MathUtil.clamp(dx * kTranslationGain, -maxMovementSpeed, maxMovementSpeed);
        double strafe = MathUtil.clamp(dy * kTranslationGain, -maxMovementSpeed, maxMovementSpeed);
        double turn = MathUtil.clamp(dTheta.getDegrees() * kRotationGain, -maxRotationSpeed, maxRotationSpeed);

        swerve.drive(new Translation2d(forward, strafe), turn, true);

        SmartDashboard.putNumber("Target X", targetPose.getX());
        SmartDashboard.putNumber("Target Y", targetPose.getY());
        SmartDashboard.putNumber("Target Degrees", targetPose.getRotation().getDegrees());
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
