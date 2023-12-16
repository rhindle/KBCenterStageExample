package org.firstinspires.ftc.teamcode.common.commandbase.autocommand;

import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.common.centerstage.ClawSide;
import org.firstinspires.ftc.teamcode.common.hardware.Globals;
import org.firstinspires.ftc.teamcode.common.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.common.subsystem.ExtensionSubsystem;
import org.firstinspires.ftc.teamcode.common.subsystem.IntakeSubsystem;

public class AutoDepositRetractCommand extends SequentialCommandGroup {
    public AutoDepositRetractCommand(RobotHardware robot, ExtensionSubsystem extension, IntakeSubsystem intake) {
        super(
                new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(0)),
                new WaitCommand(50),
                new InstantCommand(() -> robot.armActuator.setMotionProfileTargetPosition(3.3)),
                new InstantCommand(() -> Globals.retract()),
                new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.FLAT)),
                new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.35)),
                new WaitCommand(500),
                new InstantCommand(() -> intake.updateState(IntakeSubsystem.ClawState.CLOSED, ClawSide.LEFT)),
                new WaitCommand(500)
        );
    }
}
