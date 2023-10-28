package org.firstinspires.ftc.teamcode.opmode.testing.device;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;

import org.firstinspires.ftc.teamcode.common.drive.localizer.ThreeWheelLocalizer;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.Pose;
import org.firstinspires.ftc.teamcode.common.hardware.Globals;
import org.firstinspires.ftc.teamcode.common.hardware.RobotHardware;

public class LocalizationTest extends CommandOpMode {

    private final RobotHardware robot = RobotHardware.getInstance();
    private ThreeWheelLocalizer localizer;

    private boolean started = false;

    @Override
    public void initialize() {
        CommandScheduler.getInstance().reset();

        Globals.IS_AUTO = true;
        Globals.IS_USING_IMU = false;

        robot.init(hardwareMap, telemetry);
        localizer = new ThreeWheelLocalizer();

        robot.enabled = true;

        robot.read();
        while (!isStarted()) {
            telemetry.addLine("auto in init");
            telemetry.update();
        }
    }

    @Override
    public void run() {
        if (!started) {
            started = true;
            localizer.setPoseEstimate(new Pose2d(0, 0, 0));
        }

        super.run();

        Pose currentPose = localizer.getPos();
        telemetry.addData("poseX", currentPose.x);
        telemetry.addData("poseY", currentPose.y);
        telemetry.addData("heading", currentPose.heading);
        telemetry.update();
    }
}
