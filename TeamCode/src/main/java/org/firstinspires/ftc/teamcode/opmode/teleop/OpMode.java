package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.ConditionalCommand;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.centerstage.ClawSide;
import org.firstinspires.ftc.teamcode.common.commandbase.state.ClawCommand;
import org.firstinspires.ftc.teamcode.common.drive.drivetrain.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.common.drive.localizer.ThreeWheelLocalizer;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.Pose;
import org.firstinspires.ftc.teamcode.common.hardware.Globals;
import org.firstinspires.ftc.teamcode.common.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.common.subsystem.ExtensionSubsystem;
import org.firstinspires.ftc.teamcode.common.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.common.util.MathUtils;

@Config
@TeleOp(name = "OpMode")
public class OpMode extends CommandOpMode {

    private final RobotHardware robot = RobotHardware.getInstance();
    private MecanumDrivetrain drivetrain;
    private ExtensionSubsystem extension;
    private IntakeSubsystem intake;

    private GamepadEx gamepadEx;
    private GamepadEx gamepadEx2;

    private double loopTime = 0.0;

    private final boolean rightStickGreat = false;
    private boolean lastRightStickGreat = false;

    private boolean lastJoystickUp = false;
    private boolean lastJoystickDown = false;

    public static double targetpos = 0.0475;

    public static double targetServo = 0.455;

    @Override
    public void initialize() {
        CommandScheduler.getInstance().reset();

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());
        Globals.IS_AUTO = false;
        Globals.IS_USING_IMU = true;

        gamepadEx = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);

        robot.init(hardwareMap, telemetry);
        drivetrain = new MecanumDrivetrain();
        extension = new ExtensionSubsystem();
        intake = new IntakeSubsystem();
        robot.addSubsystem(drivetrain, extension, intake);

        robot.intakePivotActuator.setTargetPosition(targetpos);
        robot.intakePivotActuator.write();

        gamepadEx.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                        .whenPressed(new ConditionalCommand(
                                new ClawCommand(intake, IntakeSubsystem.ClawState.INTERMEDIATE, ClawSide.LEFT),
                                new ClawCommand(intake, IntakeSubsystem.ClawState.OPEN, ClawSide.LEFT),
                                () -> (intake.leftClaw == (IntakeSubsystem.ClawState.CLOSED))
                        ));

        gamepadEx.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(new ConditionalCommand(
                        new ClawCommand(intake, IntakeSubsystem.ClawState.INTERMEDIATE, ClawSide.RIGHT),
                        new ClawCommand(intake, IntakeSubsystem.ClawState.OPEN, ClawSide.RIGHT),
                        () -> (intake.rightClaw == (IntakeSubsystem.ClawState.CLOSED))
                ));

        gamepadEx.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(new ConditionalCommand(
                        new ClawCommand(intake, IntakeSubsystem.ClawState.INTERMEDIATE, ClawSide.BOTH),
                        new ClawCommand(intake, IntakeSubsystem.ClawState.OPEN, ClawSide.BOTH),
                        () -> (intake.rightClaw == (IntakeSubsystem.ClawState.CLOSED) || (intake.leftClaw == IntakeSubsystem.ClawState.CLOSED))
                ));



        gamepadEx2.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(new SequentialCommandGroup(
                        new InstantCommand(() -> extension.setScoring(false)),
                        new InstantCommand(() -> extension.setFlip(false)),
                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.01)),
                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(350)),
                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.FLAT)),
                        new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.515)),
                        new WaitCommand(250),
                        new ClawCommand(intake, IntakeSubsystem.ClawState.OPEN, ClawSide.BOTH)
                ))
                        .whenPressed(new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(-0.05))
                                .alongWith(new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(300))
                                        .alongWith(new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.495)))));
        gamepadEx.getGamepadButton(GamepadKeys.Button.A)
                        .whenPressed(
                                new ConditionalCommand(
                                        new SequentialCommandGroup(
                                                new InstantCommand(() -> extension.setScoring(false)),
                                                new InstantCommand(() -> extension.setFlip(false)),
                                                new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.0)),
                                                new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(0)),
                                                new WaitCommand(250),
                                                new ClawCommand(intake, IntakeSubsystem.ClawState.CLOSED, ClawSide.BOTH),
                                                new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.STORED)),
                                                new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.0475))),
                                        new SequentialCommandGroup(
                                                new InstantCommand(() -> extension.setScoring(false)),
                                                new InstantCommand(() -> extension.setFlip(false)),
                                                new ClawCommand(intake, IntakeSubsystem.ClawState.CLOSED, ClawSide.BOTH),
                                                new WaitCommand(250),
                                                new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.0)),
                                                new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(0)),
                                                new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.STORED)),
                                                new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.0475))),
                                        () -> extension.getScoring())


                                );
        gamepadEx2.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(
                        new ConditionalCommand(
                                new SequentialCommandGroup(
                                        new InstantCommand(() -> extension.setScoring(false)),
                                        new InstantCommand(() -> extension.setFlip(false)),
                                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.0)),
                                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(0)),
                                        new WaitCommand(250),
                                        new ClawCommand(intake, IntakeSubsystem.ClawState.CLOSED, ClawSide.BOTH),
                                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.STORED)),
                                        new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.0475))),
                                new WaitCommand(1),
                                () -> extension.getScoring())


                );

//        gamepadEx2.getGamepadButton(GamepadKeys.Button.X)
//                .whenPressed(new SequentialCommandGroup(
//                        new InstantCommand(() -> extension.setScoring(true)),
//                        new InstantCommand(() -> extension.setUpdated(false)),
//                        new WaitCommand(200),
//                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.SCORING)),
//                        new WaitCommand(500),
//                        new InstantCommand(() -> extension.setFlip(true))
//                ));
        gamepadEx2.getGamepadButton(GamepadKeys.Button.Y)
                        .whenPressed(new SequentialCommandGroup(
                                new InstantCommand(() -> extension.setScoring(true)),
                                new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(((Double) extension.getPair().first).doubleValue())),
                                new WaitCommand(200),
                                new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.SCORING)),
                                new WaitCommand(400),
                                new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(((Integer) extension.getPair().second).doubleValue())))
                        );
        gamepadEx2.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(new SequentialCommandGroup(
                        new InstantCommand(() -> extension.incrementBackdropHeight(1)),
                        new ConditionalCommand(
                                new SequentialCommandGroup(
                                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(((Double) extension.getPair().first).doubleValue())),
                                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.SCORING)),
                                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(((Integer) extension.getPair().second).doubleValue()))
                                ),
                                new WaitCommand(1),
                                () -> extension.getScoring()
                        )));
        gamepadEx2.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(new SequentialCommandGroup(
                        new InstantCommand(() -> extension.incrementBackdropHeight(-1)),
                        new ConditionalCommand(
                                new SequentialCommandGroup(
                                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(((Double) extension.getPair().first).doubleValue())),
                                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.SCORING)),
                                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(((Integer) extension.getPair().second).doubleValue()))
                                ),
                                new WaitCommand(1),
                                () -> extension.getScoring()
                        )));



        // combination of angle and extension amount, get minimums, get maximums, math.map
        robot.read();
        while (opModeInInit()) {
            telemetry.addLine("Robot Initialized. Mason is very cool and he is the best perosn to ever exist in the owrld and java ois the owrst progmraming kanguage nad ih ate it so so os much LLL + Ratio + cope + cget out of my game L");
            telemetry.update();
        }
    }

    @Override
    public void run() {
        robot.read();

        drivetrain.set(new Pose(gamepad1.left_stick_x, -gamepad1.left_stick_y, MathUtils.joystickScalar(-gamepad1.left_trigger + gamepad1.right_trigger, 0.01)), 0);

        boolean currentJoystickUp = gamepad2.right_stick_y < -0.5;
        boolean currentJoystickDown = gamepad2.right_stick_y > 0.5;
        if (currentJoystickUp && !lastJoystickUp) {
            // height go upp
            extension.incrementBackdropHeight(1);
            CommandScheduler.getInstance().schedule(
                    new SequentialCommandGroup(
                            new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(((Double) extension.getPair().first).doubleValue())),
                            new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.SCORING)),
                            new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(((Integer) extension.getPair().second).doubleValue())))
            );
        }

        if (currentJoystickDown && !lastJoystickDown) {
            // gheight go dwodn
            extension.incrementBackdropHeight(-1);
            CommandScheduler.getInstance().schedule(
                    new SequentialCommandGroup(
                            new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(((Double) extension.getPair().first).doubleValue())),
                            new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.SCORING)),
                            new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(((Integer) extension.getPair().second).doubleValue())))
            );
        }
        lastJoystickUp = currentJoystickUp;
        lastJoystickDown = currentJoystickDown;

        // input
        super.run();
        robot.periodic();

        lastRightStickGreat = rightStickGreat;

//        telemetry.addData("extension", robot.extensionActuator.getPosition());
//        telemetry.addData("angle", robot.pitchActuator.getPosition());
        telemetry.addData("LEVEL", extension.getBackdropHeight());
//        telemetry.addData("targetAngle", extension.t_angle);
        telemetry.addData("targetExtension", robot.extensionActuator.getTargetPosition());
//        telemetry.addData("diffX", extension.diff_x);
//        telemetry.addData("diffy", extension.diff_y);
//        telemetry.addData("velocity", localizer.getNewPoseVelocity());
        double loop = System.nanoTime();
        telemetry.addData("hz ", 1000000000 / (loop - loopTime));
        loopTime = loop;
        telemetry.update();
        robot.write();
        robot.clearBulkCache();
    }
}
