package org.firstinspires.ftc.teamcode.common.commandbase.auto;

import androidx.core.math.MathUtils;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.controller.PIDFController;

import org.firstinspires.ftc.teamcode.common.drive.drivetrain.Drivetrain;
import org.firstinspires.ftc.teamcode.common.drive.localizer.Localizer;
import org.firstinspires.ftc.teamcode.common.drive.localizer.ThreeWheelLocalizer;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.Pose;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.Vector2D;
import org.firstinspires.ftc.teamcode.common.drive.pathing.path.GVFPathFollower;
import org.firstinspires.ftc.teamcode.common.drive.pathing.path.HermitePath;

@Config
public class GVFCommand extends CommandBase {
    GVFPathFollower controller;
    Localizer localizer;
    HermitePath path;
    Drivetrain drivetrain;

    public static double xP = 0.04;
    public static double xD = 0.0;
    public static double xF = 0.0;

    public static double yP = 0.04;
    public static double yD = 0.0;
    public static double yF = 0.0;

    public static double hP = 0.0;
    public static double hD = 0.0;
    public static double hF = 0;

    public static PIDFController xController = new PIDFController(xP, 0.0, xD, xF);
    public static PIDFController yController = new PIDFController(yP, 0.0, yD, yF);
    public static PIDFController hController = new PIDFController(hP, 0.0, hD, hF);
    public static double max_power = 1.0;
    public static double max_heading = 0.5;

    public static double kN = 0.15;
    public static double kS = 0.5;
    public static double kC = 1.0;

    public static Pose gvf = new Pose(0, 0, 0);

    public static Pose powers2 = new Pose(0, 0, 0);

    public GVFCommand(Drivetrain drivetrain, Localizer localizer, HermitePath path) {
        this.drivetrain = drivetrain;
        this.localizer = localizer;
        this.path = path;
        this.controller = new GVFPathFollower(path, localizer.getPos(), kN, kS, kC);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        Pose robotPose = localizer.getPos();
        robotPose.heading = Math.toRadians(robotPose.heading);
        controller.setCurrentPose(robotPose);
        gvf = controller.calculateGVF();
        gvf.heading = gvf.toVec2D().deadzoneX(0.02).angle();
//        gvf.heading = gvf.toVec2D().angle();
//        System.out.println(gvf);
        Pose robotVelocity = ((ThreeWheelLocalizer) localizer).getNewPoseVelocity();
        Pose powers = getPowers(gvf, robotVelocity, robotPose);
        drivetrain.set(powers);
    }

    @Override
    public boolean isFinished() {
        return controller.isFinished();
    }

    @Override
    public void end(boolean interrupted) {
    }

    public Pose getPowers(Pose gvf, Pose velocityPose, Pose robotPose) {

//        Vector2D gvf2D = gvf.toVec2D();

//        double dir = Math.atan2(gvf2D.y, gvf2D.x);

//        Vector2D gvf2D = gvf.toVec2D();

//        Vector2D gvf2D = gvf.toVec2D().unit();

        Pose delta = gvf.subt(velocityPose);

        Pose powers = new Pose(
                xController.calculate(0, delta.x),
                yController.calculate(0, delta.y),
                hController.calculate(0, delta.heading)
        );


//        Pose powers = new Pose(
//                delta.x * xP,
//                delta.y * yP,
//                delta.heading * hP
//        );

        double x_rotated = powers.x * Math.cos(robotPose.heading) - powers.y * Math.sin(robotPose.heading);
        double y_rotated = powers.x * Math.sin(robotPose.heading) + powers.y * Math.cos(robotPose.heading);
////        double x_power = -x_rotated < -max_power ? -max_power :
//                Math.min(-x_rotated, max_power);
//        double y_power = -y_rotated < -max_power ? -max_power :
//                Math.min(-y_rotated, max_power);
//        double heading_power = MathUtils.clamp(powers.heading, -max_heading, max_heading);
//
//        if(Math.abs(x_power) < 0.01) x_power = 0;
//        if(Math.abs(y_power) < 0.01) y_power = 0;

        powers2 = new Pose(y_rotated / 12 * 12, x_rotated / 12 * 12, -powers.heading / 12 * 12);
        return powers2;
//        double heading_component = hController.calculate(robotPose.heading, gvf.heading);
//        double heading_power = MathUtils.clamp(heading_component, -max_heading, max_heading);


//        double power = xController.calculate(0, magnitude);
//
//        double y_component = Math.cos(dir) * power;
//        double x_component = Math.sin(dir) * power;
//
//        double x_power = Math.signum(x_component) * Math.min(Math.abs(x_component), max_power);
//        double y_power = Math.signum(y_component) * Math.min(Math.abs(y_component), max_power);
//        -heading_power / voltage * 12
//        return new Pose(-y_power, x_power, gvf.heading);

//        Pose powers = new Pose(
//                xController.calculate(0, gvf.x),
//                yController.calculate(0, gvf.y),
//                hController.calculate(0, gvf.heading)
//        );
//        double x_rotated = powers.x * Math.cos(robotPose.heading) - powers.y * Math.sin(robotPose.heading);
//        double y_rotated = powers.x * Math.sin(robotPose.heading) + powers.y * Math.cos(robotPose.heading);
//        double x_power = -x_rotated < -max_power ? -max_power :
//                Math.min(-x_rotated, max_power);
//        double y_power = -y_rotated < -max_power ? -max_power :
//                Math.min(-y_rotated, max_power);
//        double heading_power = MathUtils.clamp(powers.heading, -max_heading, max_heading);
//
//        if(Math.abs(x_power) < 0.01) x_power = 0;
//        if(Math.abs(y_power) < 0.01) y_power = 0;
//
//        return new Pose(-y_power / voltage * 12, x_power / voltage * 12, -heading_power / voltage * 12);
    }
}
