package org.firstinspires.ftc.teamcode.common.commandbase.autocommand;

import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.common.centerstage.ClawSide;
import org.firstinspires.ftc.teamcode.common.commandbase.subsytemcommand.ArmCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.subsytemcommand.ArmFloatCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.subsytemcommand.ArmLiftCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.subsytemcommand.ClawCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.subsytemcommand.ExtensionCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.subsytemcommand.PivotStateCommand;
import org.firstinspires.ftc.teamcode.common.subsystem.IntakeSubsystem;

public class ThirdStackGrabCommand extends SequentialCommandGroup {
    public ThirdStackGrabCommand() {
        super(
                new ArmFloatCommand(true),
                new ClawCommand(IntakeSubsystem.ClawState.OPEN, ClawSide.RIGHT),
                new WaitCommand(250),
                new ExtensionCommand(600),
                new WaitCommand(500),
                new ClawCommand(IntakeSubsystem.ClawState.CLOSED, ClawSide.RIGHT),
                new WaitCommand(250),
                new ArmFloatCommand(false),
                new ArmCommand(0),
                new PivotStateCommand(IntakeSubsystem.PivotState.STORED),
                new ExtensionCommand(0)
        );
    }
}
