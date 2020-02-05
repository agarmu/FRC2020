package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;


import frc.robot.Constants;


public class Limelight extends SubsystemBase {

    // Horizontal Offset variable
    double x;

    // Vertical Offset variable
    double y;

    // Area of the target
    double area;

    // Skew of the robot
    double s;

    // Correct values to move the robot to align with the target, basically the motor power that is sent when a target is seen
    double left_command;
    double right_command;

    // These are the motors on the robot
    TalonSRX driveMasterRight = new TalonSRX(Constants.masterRightMotor);
    TalonSRX driveMasterLeft = new TalonSRX(Constants.masterLeftMotor);
    VictorSPX driveSlaveRight = new VictorSPX(Constants.slaveRightMotor);
    VictorSPX driveSlaveLeft = new VictorSPX(Constants.slaveLeftMotor);
/*
    CANSparkMax driveMasterL = new CANSparkMax(Constants.masterLeftMotor, MotorType.kBrushless);
    CANSparkMax driveSlaveL = new CANSparkMax(Constants.slaveLeftMotor, MotorType.kBrushless);
    CANSparkMax driveMasterR = new CANSparkMax(Constants.masterRightMotor, MotorType.kBrushless);
    CANSparkMax driveSlaveR = new CANSparkMax(Constants.slaveRightMotor, MotorType.kBrushless);
*/
    // Whether target is visible or not
    double v;

    // Height of limelight in inches
    double h1;

    // Height of goal in inches
    double h2;
    
    // Desired distance from goal in inches
    double idealDistance;

    //Actual distance from goal in inches
    double d; 
    
    // Angle of limelight
    double a;

    double led;
    
    

    public Limelight(){

    // Will need to change, values to calculate robot stopping distance from target
    h1 = 6;
    h2 = 81.24;
    idealDistance = 12;
    a = 30;


    // Post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);

    // Assign motor ports to the motors



    }

    // Allows horizontal offset to be a dynamic value
    public void setX(NetworkTableEntry tx){
        x = tx.getDouble(0.0);
    }
    public void setY(NetworkTableEntry ty){
      y = ty.getDouble(0.0);
    }
    // Allows 
    public void setArea(NetworkTableEntry ta){
      area = ta.getDouble(0.0);
  
    }
    public void setV(NetworkTableEntry tv){
      v = tv.getDouble(0.0);
    }
    public double getV(){
      return v;
    }


    public void printX(){
      System.out.println(x);
    }

    // Program to allow the drivers to auto align based on the target
    public void autoAlign(){

      // Constants used to calculate motor power for alignment
      Double Kp = -Constants.Kp;
      Double KpDistance = -.01;
      //Double area_error = 3 - area;
      Double distance_adjust = Constants.distance_adjust;
      Double min_command = Constants.min_command;
      left_command = 0;
      right_command = 0;


      // Checks to see if button pressec



        // Set heading error and the steering adjust
        Double heading_error = -x;
        Double distance_error = -y;
        Double steering_adjust = 0.075;
        // Determine power based on the horizontal offset
        if (x > 1.0)
        {
                steering_adjust = Kp*heading_error - min_command;
        }
        else if (x < -1.0)
        {
                steering_adjust = Kp*heading_error + min_command;
        }
        
        distance_adjust = KpDistance * distance_error;

        left_command += -steering_adjust + distance_adjust;
        right_command += distance_adjust + steering_adjust;
  
        // Determine distance to stop based on area of image seen
        /*if (area > .25){
          driving_adjust = KpDistance * area_error + min_command;
        }
        else if (area < .25){
          driving_adjust = KpDistance * area_error - min_command;
  
        }*/
  
        // Run motors if the target is seen 
        if (v == 1){

          driveMasterLeft.set(ControlMode.PercentOutput, left_command);
          driveMasterRight.set(ControlMode.PercentOutput, -right_command);
          driveSlaveLeft.set(ControlMode.PercentOutput, left_command);
          driveSlaveRight.set(ControlMode.PercentOutput, -right_command);
  
        }
        else
        {
          driveMasterLeft.set(ControlMode.PercentOutput, 0);
          driveMasterRight.set(ControlMode.PercentOutput, 0);
          driveSlaveLeft.set(ControlMode.PercentOutput, 0);
          driveSlaveRight.set(ControlMode.PercentOutput, 0);
        }
    }


    }


// atiksh is dumb/test