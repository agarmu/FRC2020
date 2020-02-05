package frc.robot;

import frc.robot.subsystems.*;
import frc.robot.Constants;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.*;
import java.util.List;
import edu.wpi.first.wpilibj.kinematics.*; 
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj2.command.button.*;
import frc.robot.commands.*;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;

public class RobotContainer{

    public TrajectoryFollowing pathfollower;
    public Joystick pad;
    public JoystickButton aPad;
    public JoystickButton xPad;
    public JoystickButton yPad;

    public RobotContainer(){
        pathfollower = new TrajectoryFollowing();
        pad = new Joystick(0);
        aPad = new JoystickButton(pad, 1);
        xPad = new JoystickButton(pad, 4);
        yPad = new JoystickButton(pad, 3);

    }

    public Command getAutonomousCommand(){

        pathfollower.resetOdometry(pathfollower.getPose());
        pathfollower.zeroHeading();

        var autoVoltageConstraint =
        new DifferentialDriveVoltageConstraint(
            new SimpleMotorFeedforward(Constants.kVolts,
                                       Constants.kVSPM,
                                       Constants.kVSSPM),
            pathfollower.getKinematics(),
            10);
        TrajectoryConfig trajectoryConfig = new TrajectoryConfig(Constants.maxVelocity, 3).setKinematics(pathfollower.getKinematics()).addConstraint(autoVoltageConstraint);

        Trajectory traj = TrajectoryGenerator.generateTrajectory(new Pose2d(0, 0, new Rotation2d(0)), List.of(), new Pose2d(-.25, -.25, new Rotation2d(-90)), trajectoryConfig);
        
        RamseteCommand ramseteCommand = new RamseteCommand(traj, pathfollower::getPose, new RamseteController(2, 0.7), new SimpleMotorFeedforward(Constants.kVolts, Constants.kVSPM, Constants.kVSSPM), 
        pathfollower.getKinematics(), pathfollower::getSpeeds, new PIDController(Constants.kPDriveVelocity, 0, 0), new PIDController(Constants.kPDriveVelocity, 0, 0), pathfollower::tankDriveVolts, pathfollower);//add ref to differential drive object in trajectoryfollowing);

        return ramseteCommand.andThen(() -> pathfollower.tankDriveVolts(0, 0));
    }



    public double leftSpeed(){
        return pad.getRawAxis(1);
    }
    public double rightSpeed(){
        return pad.getRawAxis(5);
    }
    public double climbRPower(){
        return pad.getRawAxis(3);
    }
    public double climbLPower(){
        return pad.getRawAxis(2);
    }
    public boolean bButton(){
        return pad.getRawButton(2);
    }

    public boolean lBumper(){
        return pad.getRawButton(5);
    }

    public boolean rBumper(){
        return pad.getRawButton(6);
    }

}

