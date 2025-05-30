package teamcode.Teleop;

import static teamcode.Teleop.Singletons.VARS.*;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.gamepad.ButtonReader;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Objects;

import teamcode.Autonomous.RoadRunner.PinpointDrive;
import teamcode.Robot;
import teamcode.Teleop.Singletons.MotorWeights;


@Config
@TeleOp
public class Drive_V4 extends LinearOpMode {

    //VARIABLES:

    Robot robot;
    private MultipleTelemetry TELE;

    public static String MODE = "SAM";
    
    public double offset = 0;

    public double p = 0.0003, i = 0, d = 0.00001;
    public static int target = 30000;

    public PIDController controller;
    public double linearSlideZeroPosition = 0;
    public double slidesZeroPower = 0.08;

    public boolean linearAutomation = false;
    public boolean PID_MODE = false;
    public boolean pickupSample = true;
    public boolean magneticSwitch = false;


    public boolean dpadDownServoLock = false;

    public double linearSlidePosition;
    public boolean magneticSwitchHang = false;


    // Gamepads
    GamepadEx g1, g2;

    // Declare ButtonReaders as instance variables
    ButtonReader G1_START, G1_B, G1_DPAD_UP, G1_DPAD_DOWN, G1_LEFT_BUMPER, G1_RIGHT_BUMPER, G1_A;
    ButtonReader G2_B, G2_Y, G2_X, G2_RIGHT_BUMPER, G2_START, G2_BACK, G2_DPAD_DOWN, G2_DPAD_UP;

    // Corresponding booleans for press states
    public boolean G1_START_PRESSED = false, G1_B_PRESSED = false, G1_DPAD_UP_PRESSED = false,
            G1_DPAD_DOWN_PRESSED = false, G1_LEFT_BUMPER_PRESSED = false, G2_BACK_RELEASED = false,
            G2_B_PRESSED = false, G2_Y_PRESSED = false, G2_X_PRESSED = false,
            G2_RIGHT_BUMPER_PRESSED = false, G2_START_PRESSED = false, G2_BACK_PRESSED = false,
            G2_DPAD_DOWN_PRESSED = false, G2_DPAD_UP_PRESSED = false;

    // Corresponding doubles for press times, initialized to 0.0
    public double G1_START_PRESSED_TIME = 0.0, G1_B_PRESSED_TIME = 0.0, G1_DPAD_UP_PRESSED_TIME = 0.0,
            G1_DPAD_DOWN_PRESSED_TIME = 0.0, G1_LEFT_BUMPER_PRESSED_TIME = 0.0, G1_RIGHT_BUMPER_PRESSED_TIME = 0.0,
            G2_B_PRESSED_TIME = 0.0, G2_Y_PRESSED_TIME = 0.0, G2_X_PRESSED_TIME = 0.0,
            G2_RIGHT_BUMPER_PRESSED_TIME = 0.0, G2_START_PRESSED_TIME = 0.0, G2_BACK_PRESSED_TIME = 0.0,
            G2_DPAD_DOWN_PRESSED_TIME = 0.0, G2_DPAD_UP_PRESSED_TIME = 0.0;

    public boolean extendoOut = false;
    public boolean extendoHoldIn = false;
    public boolean extendoHoldOut = false;
    public boolean extendoIn = false;
    public boolean maximumExtension = false;

    public double extendoZeroPosition = 0;

    public double colorSensorTimer = 0.0;

    public boolean HANG_1_TARGET = true;
    public boolean HANG_2_TARGET = true;
    public boolean HANG_3_TARGET = true;
    public boolean HANG_4_TARGET = true;
    public boolean EXTENDO_HANG_TARGET = true;




    public void initialize() {
        
        robot = new Robot(hardwareMap);

        TELE = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        robot.drive = new PinpointDrive(hardwareMap, new Pose2d(0,0,0));

        robot.frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        robot.extendo.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.centerSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        robot.rightSpringHook.setPosition(RIGHT_SPRING_OFF);
        robot.leftSpringHook.setPosition(LEFT_SPRING_OFF);

        robot.rightStabilizer.setPosition(RIGHT_HOLD_OFF);
        robot.leftStabilizer.setPosition(LEFT_HOLD_OFF);

        controller = new PIDController(p, i, d);

        GamepadEx g2 = new GamepadEx(gamepad2);
        GamepadEx g1 = new GamepadEx(gamepad1);

        G1_START = new ButtonReader(
                g1, GamepadKeys.Button.START
        );


        G1_B = new ButtonReader(
                g1, GamepadKeys.Button.B
        );


        G1_DPAD_UP = new ButtonReader(
                g1, GamepadKeys.Button.DPAD_UP
        );

        G1_DPAD_DOWN = new ButtonReader(
                g1, GamepadKeys.Button.DPAD_DOWN
        );

        G1_LEFT_BUMPER = new ButtonReader(
                g1, GamepadKeys.Button.LEFT_BUMPER
        );

        G1_RIGHT_BUMPER  = new ButtonReader(
                g1, GamepadKeys.Button.RIGHT_BUMPER
        );

        G1_A = new ButtonReader(
                g1, GamepadKeys.Button.A
        );




        G2_B = new ButtonReader(
                g2, GamepadKeys.Button.B
        );

        G2_Y = new ButtonReader(
                g2, GamepadKeys.Button.Y
        );

        G2_X = new ButtonReader(
                g2, GamepadKeys.Button.X
        );


        G2_RIGHT_BUMPER = new ButtonReader(
                g2, GamepadKeys.Button.RIGHT_BUMPER
        );

        G2_START = new ButtonReader(
                g2, GamepadKeys.Button.START
        );

        G2_BACK = new ButtonReader(
                g2, GamepadKeys.Button.BACK
        );

        G2_DPAD_DOWN = new ButtonReader(
                g2, GamepadKeys.Button.DPAD_DOWN
        );

        G2_DPAD_UP = new ButtonReader(
                g2, GamepadKeys.Button.DPAD_UP
        );




    }
    
    
    
    @Override
    public void runOpMode() throws InterruptedException {
        
        initialize();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()){


            drivetrain(MODE);

            linearSlides();

            clawControl();

            horizontalSlides();

            limitSwitch();

            toggles();

            displayTelemetry();




        }
        
        
        
        
    }

    public void drivetrain (String mode){

        double rx = 0;
        double x = 0;
        double y = 0;

        double rotX;
        double rotY;


        double botHeading = robot.drive.pose.heading.toDouble() - offset;


        telemetry.addData("botHeading", Math.toDegrees(botHeading));

        telemetry.addData("offset", Math.toDegrees(offset));


        if (Objects.equals(mode, "SAM")) {

            rx = gamepad1.left_stick_x;
            x = -gamepad1.right_stick_x;
            y = gamepad1.right_stick_y;

        } else if (Objects.equals(mode, "!SAM")) {
            rx = gamepad1.left_stick_x;
            x = gamepad1.right_stick_x;
            y = -gamepad1.right_stick_y;
        } else if (Objects.equals(mode, "STEVE")) {
            rx = gamepad1.right_stick_x;
            x = gamepad1.left_stick_x;
            y = -gamepad1.left_stick_y;
        } else if (Objects.equals(mode, "FC")) {
            rx = gamepad1.left_stick_x;
            x = gamepad1.right_stick_x;
            y = -gamepad1.right_stick_y;


            rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            x = rotX;
            y = rotY;


        } else if (Objects.equals(mode, "FC2")) {
            rx = gamepad1.right_stick_x;
            x = gamepad1.left_stick_x;
            y = -gamepad1.left_stick_y;


            rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            x = rotX;
            y = rotY;


        }


        double slowFactor = 1 - gamepad1.left_trigger * SLOWDOWN_SPEED;

        rx *= slowFactor;
        x *= slowFactor;
        y *= slowFactor;


        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        frontLeftPower *= MotorWeights.FRONT_LEFT;
        frontRightPower *= MotorWeights.FRONT_RIGHT;
        backLeftPower *= MotorWeights.BACK_LEFT;
        backRightPower *= MotorWeights.BACK_RIGHT;


        robot.frontLeftMotor.setPower(frontLeftPower);
        robot.backLeftMotor.setPower(backLeftPower);
        robot.frontRightMotor.setPower(frontRightPower);
        robot.backRightMotor.setPower(backRightPower);

        robot.drive.updatePoseEstimate();

    }
    public void linearSlides () {

        linearSlidePosition = -robot.linearSlideEncoder.getCurrentPosition();


        double linearSlidePower = 0;

        if (gamepad2.right_trigger > 0.5) {
            linearSlidePower += 1;
        } else {
            linearSlidePower += gamepad2.right_trigger * 2;
        }

        if ((linearSlidePosition > linearSlideZeroPosition + 50)) {
            if (gamepad2.left_trigger > 0.5) {
                linearSlidePower -= 1;
            } else {
                linearSlidePower -= gamepad2.left_trigger * 2;
            }
        }

        if (!linearAutomation && !PID_MODE) {
            if ((linearSlidePosition > linearSlideZeroPosition + 50) && gamepad2.left_trigger == 0 && gamepad2.right_trigger == 0) {
                robot.leftSlide.setPower(slidesZeroPower);
                robot.rightSlide.setPower(slidesZeroPower);
            } else {

                robot.leftSlide.setPower(linearSlidePower);
                robot.rightSlide.setPower(linearSlidePower);
                if (linearSlidePower < 0.12) {
                    robot.centerSlide.setPower(0);
                } else {
                    robot.centerSlide.setPower(linearSlidePower);

                }


            }
        }


        if (!linearAutomation && PID_MODE) {
            controller.setPID(p, i, d);
            double PID = controller.calculate(linearSlidePosition, target);
            PID += 0.08;

            robot.rightSlide.setPower(PID);
            robot.leftSlide.setPower(PID);
            robot.centerSlide.setPower(PID);

        }

        if (gamepad2.left_trigger != 0 || gamepad2.right_trigger != 0) {
            PID_MODE = false;
        }

        if (-gamepad2.left_stick_y < -0.5) {
            target = (int) linearSlideZeroPosition;
            PID_MODE = true;
        } else if (-gamepad2.left_stick_y > 0.5) {
            target = (int) linearSlideZeroPosition + HIGH_SAMPLE_POS;
            robot.clawPivot.setPosition(PIVOT_OUTTAKE);
            robot.clawMove.setPosition(MOVE_OUTTAKE);

            PID_MODE = true;

        }


    }
    public void clawControl () {

        if (gamepad2.a) robot.claw.setPosition(CLAW_CLOSED);

        if (gamepad2.right_stick_x > 0.10 || gamepad2.right_stick_x < -0.10) {
            robot.clawRotate.setPosition(robot.clawRotate.getPosition() + (10 * gamepad2.right_stick_x / 180));
        }

    }
    public void horizontalSlides () {

        if ((robot.extendo.getCurrentPosition() - extendoZeroPosition) <= EXTENDO_MAX_TELE) {

            if (gamepad2.right_bumper && (robot.extendo.getCurrentPosition() - extendoZeroPosition <= EXTENDO_SOFTMAX_TELE)) {
                robot.extendo.setPower(1);
            } else if (extendoOut) {
                robot.extendo.setPower(1);
            } else if (extendoHoldOut) {
                robot.extendo.setPower(0.2);
            } else if ((gamepad2.left_bumper || extendoIn)) {
                robot.extendo.setPower(-1);
            } else if (extendoHoldIn) {
                robot.extendo.setPower(-0.2);
            } else {
                robot.extendo.setPower(0);
            }
        } else {

            if (maximumExtension && gamepad2.right_bumper && (robot.extendo.getCurrentPosition() - extendoZeroPosition <= EXTENDO_SOFTMAX_TELE)) {
                robot.extendo.setPower(1);
            } else if ((gamepad2.left_bumper || extendoIn)) {
                robot.extendo.setPower(-1);
            } else if (extendoHoldIn) {
                robot.extendo.setPower(-0.2);
            } else {
                robot.extendo.setPower(0);
            }

        }

        if (gamepad2.left_bumper || gamepad2.back || gamepad2.dpad_up || gamepad2.y || gamepad2.x) {
            extendoOut = false;
            extendoHoldOut = false;
        }

        if (gamepad2.right_bumper || gamepad2.start || gamepad2.dpad_down || gamepad2.x || gamepad2.y) {
            extendoIn = false;
            extendoHoldIn = false;
        }



        if (maximumExtension && !gamepad2.right_bumper) {
            maximumExtension = false;
        }
    }
    public void limitSwitch () {


        magneticSwitch = !robot.pin10.getState() || !robot.pin11.getState();
    }
    public void specimenScoreUp () {

        double automationTime = getRuntime() - G2_START_PRESSED_TIME;
        if (automationTime < 0.25) {
            robot.claw.setPosition(CLAW_LOOSE_GRAB);
        } else {
            robot.clawMove.setPosition(MOVE_SPECIMEN_SCORE);
            robot.clawPivot.setPosition(PIVOT_SPECIMEN_SCORE);
            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            if (automationTime > 0.8){
                robot.claw.setPosition(CLAW_CLOSED);
                G2_START_PRESSED = false;
            }
            if (G2_BACK_PRESSED){
                G2_START_PRESSED = false;
            }
        }



    }
    public void specimenGrabDown () {
        double automationTime = getRuntime() - G2_BACK_PRESSED_TIME;

        if (automationTime < 0.2) {
            robot.claw.setPosition(CLAW_OPEN);
        } else if (automationTime < 0.65) {
            extendoIn = true;
            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            robot.clawPivot.setPosition(PIVOT_ALL_OUT);

        } else if (automationTime < 1) {
            extendoIn = true;
            robot.claw.setPosition(CLAW_CLOSED);
        } else if (automationTime < 1.5) {

            robot.claw.setPosition(CLAW_CLOSED);

            robot.clawRotate.setPosition(ROTATE_FLIP);
            robot.clawMove.setPosition(MOVE_WALL_INTAKE);
            robot.clawPivot.setPosition(PIVOT_WALL_INTAKE);
            extendoIn = true;
        } else {
            extendoIn = false;
            extendoHoldIn = true;

            robot.clawRotate.setPosition(ROTATE_FLIP);
            robot.claw.setPosition(CLAW_OPEN);

            G2_BACK_PRESSED = false;

        }
    }
    public void slideReset () {

        double automationTime = getRuntime() - G1_START_PRESSED_TIME;
        linearAutomation = true;

        if (automationTime < 0.25) {

            robot.leftSlide.setPower(1);
            robot.rightSlide.setPower(1);
            robot.centerSlide.setPower(1);

            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            robot.clawMove.setPosition(MOVE_OUTTAKE);
            robot.clawPivot.setPosition(PIVOT_OUTTAKE);

        } else if (automationTime < 0.85) {

            robot.leftSlide.setPower(0.1);
            robot.rightSlide.setPower(0.1);
            robot.centerSlide.setPower(0.1);
            extendoIn = true;

            robot.claw.setPosition(CLAW_CLOSED);

            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            robot.clawMove.setPosition(MOVE_OUTTAKE - 0.1);
            robot.clawPivot.setPosition(PIVOT_SPECIMEN_SCORE);
        } else if (automationTime < 2.15) {
            extendoIn = false;
            robot.leftSlide.setPower(-1);
            robot.rightSlide.setPower(-1);
            robot.centerSlide.setPower(-1);

            robot.claw.setPosition(CLAW_CLOSED);

            robot.clawRotate.setPosition(ROTATE_NEUTRAL);

            robot.claw.setPosition(CLAW_CLOSED);
            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            robot.clawMove.setPosition(MOVE_OUTTAKE);
            robot.clawPivot.setPosition(PIVOT_OUTTAKE);

        } else if (automationTime < 2.65) {
            extendoIn = false;


            robot.leftSlide.setPower(0);
            robot.rightSlide.setPower(0);
            robot.centerSlide.setPower(0);

            robot.claw.setPosition(CLAW_CLOSED);
            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            robot.clawMove.setPosition(MOVE_OUTTAKE);
            robot.clawPivot.setPosition(PIVOT_OUTTAKE);
            linearSlideZeroPosition = robot.linearSlideEncoder.getCurrentPosition();
            extendoZeroPosition = robot.extendoEncoder.getCurrentPosition();
        } else {

            extendoIn = false;


            robot.leftSlide.setPower(0);
            robot.rightSlide.setPower(0);
            robot.centerSlide.setPower(0);

            robot.claw.setPosition(CLAW_CLOSED);
            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            robot.clawMove.setPosition(MOVE_OUTTAKE);
            robot.clawPivot.setPosition(PIVOT_OUTTAKE);

            linearSlideZeroPosition = robot.linearSlideEncoder.getCurrentPosition();


            extendoZeroPosition = robot.extendoEncoder.getCurrentPosition();

            linearAutomation = false;
            G1_START_PRESSED = false;
        }
    }
    public void submerisbleHover() {
        double automationTime = getRuntime() - G2_DPAD_DOWN_PRESSED_TIME;

        extendoIn = false;
        extendoHoldIn = false;

        if (automationTime < 0.6) {


            robot.claw.setPosition(CLAW_CLOSED);

            if (!dpadDownServoLock) {
                robot.clawPivot.setPosition(PIVOT_ALL_OUT);
                robot.clawMove.setPosition(MOVE_ALL_OUT);
                robot.clawRotate.setPosition(ROTATE_90);
                robot.claw.setPosition(CLAW_CLOSED);
            } else {
                robot.clawMove.setPosition(MOVE_HOVER_SAMPLE);
                robot.clawPivot.setPosition(PIVOT_SAMPLE_PICKUP);
                robot.clawRotate.setPosition(ROTATE_90);
                robot.claw.setPosition(CLAW_CLOSED);
            }

        } else {


            if (!dpadDownServoLock) {
                robot.clawPivot.setPosition(PIVOT_ALL_OUT);
                robot.clawMove.setPosition(MOVE_ALL_OUT);
                robot.clawRotate.setPosition(ROTATE_90);
                robot.claw.setPosition(CLAW_CLOSED);
            } else {
                robot.clawMove.setPosition(MOVE_HOVER_SAMPLE);
                robot.clawPivot.setPosition(PIVOT_SAMPLE_PICKUP);
                robot.clawRotate.setPosition(ROTATE_90);
                robot.claw.setPosition(CLAW_OPEN);
            }


            G2_DPAD_DOWN_PRESSED = false;
        }

    }
    public void samplePickupToDrop() {
        double automationTime = getRuntime() - G2_Y_PRESSED_TIME;

        if (automationTime < 0.3) {
            robot.claw.setPosition(CLAW_CLOSED);
        } else if (automationTime < 0.95) {

            robot.claw.setPosition(CLAW_CLOSED);

            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            robot.clawMove.setPosition(MOVE_ALL_OUT);
            robot.clawPivot.setPosition(PIVOT_ALL_OUT);
            extendoIn = true;
        } else if (automationTime < 1.05 && pickupSample) {

            robot.claw.setPosition(CLAW_CLOSED);

            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            robot.clawMove.setPosition(MOVE_WALL_INTAKE);
            robot.clawPivot.setPosition(PIVOT_WALL_INTAKE);
            extendoIn = true;
        } else {

            extendoIn = false;
            extendoHoldIn = true;


            G2_Y_PRESSED = false;
        }
    }
    public void samplePickup() {
        double automationTime = getRuntime() - G2_DPAD_UP_PRESSED_TIME;


        if (automationTime < 0.3) {

            robot.claw.setPosition(CLAW_CLOSED);
        } else if (automationTime < 1) {

            robot.claw.setPosition(CLAW_CLOSED);

            robot.clawRotate.setPosition(ROTATE_NEUTRAL);
            robot.clawMove.setPosition(MOVE_ALL_OUT);
            robot.clawPivot.setPosition(PIVOT_ALL_OUT);
            extendoIn = true;
        } else {
            extendoIn = false;
            extendoHoldIn = true;


            G2_DPAD_UP_PRESSED = false;
        }
    }
    public void hang() {
        double time = getRuntime() - G1_B_PRESSED_TIME;


        if (linearSlidePosition + linearSlideZeroPosition < HANG_1 + linearSlideZeroPosition && HANG_1_TARGET){
            robot.leftStabilizer.setPosition(LEFT_HOLD_ON);

            robot.rightStabilizer.setPosition(RIGHT_HOLD_ON);

            robot.clawMove.setPosition(MOVE_WALL_INTAKE);
            robot.clawPivot.setPosition(PIVOT_WALL_INTAKE);

            target = (int) (HANG_1 + linearSlideZeroPosition);
            PID_MODE = true;
            if (linearSlidePosition + linearSlideZeroPosition > HANG_1 + linearSlideZeroPosition - 1000){
                HANG_1_TARGET = false;
            }
        } else if (linearSlidePosition + linearSlideZeroPosition > HANG_2 + linearSlideZeroPosition && HANG_2_TARGET){
            HANG_1_TARGET = false;
            target = (int) (HANG_2 + linearSlideZeroPosition);
            PID_MODE = true;
            G1_B_PRESSED_TIME = getRuntime();
            if (linearSlidePosition + linearSlideZeroPosition < HANG_2 + linearSlideZeroPosition + 1000){
                HANG_2_TARGET = false;
            }
        } else if (time < 0.5){
            HANG_2_TARGET = false;
            PID_MODE = false;
            robot.rightSpringHook.setPosition(RIGHT_SPRING_ON);
            robot.leftSpringHook.setPosition(LEFT_SPRING_ON);
        }
        else if (linearSlidePosition + linearSlideZeroPosition < HANG_3 + linearSlideZeroPosition && HANG_3_TARGET){
            target = (int) (HANG_3 + linearSlideZeroPosition);
            robot.leftSpringHook.setPosition(LEFT_SPRING_IN);
            robot.rightSpringHook.setPosition(RIGHT_SPRING_IN);
            PID_MODE = true;
            if (linearSlidePosition + linearSlideZeroPosition > HANG_3 + linearSlideZeroPosition - 1000){
                HANG_3_TARGET = false;
            }
        }
        else if (robot.extendo.getCurrentPosition() < EXTENDO_HANG && EXTENDO_HANG_TARGET){
            HANG_3_TARGET = false;
            robot.clawMove.setPosition(MOVE_RAISED);
            extendoOut = true;
            if (robot.extendo.getCurrentPosition() > EXTENDO_HANG - 1000){
                extendoOut = false;
                robot.extendo.setPower(0);
                EXTENDO_HANG_TARGET = false;
            }
        }
        else if (linearSlidePosition + linearSlideZeroPosition > HANG_4 + linearSlideZeroPosition && HANG_4_TARGET){
            extendoOut = false;
            target = (int) (HANG_4 + linearSlideZeroPosition);
            PID_MODE = true;
            G1_B_PRESSED_TIME = getRuntime();
            if (linearSlidePosition + linearSlideZeroPosition < HANG_4 + linearSlideZeroPosition + 1000){
                HANG_4_TARGET = false;
            }
        }
        else if (!magneticSwitch && !magneticSwitchHang){
            HANG_4_TARGET = false;
            extendoHoldIn = false;
            extendoHoldOut = false;
            extendoOut = false;
            PID_MODE = false;
            linearAutomation = true;
            robot.leftSlide.setPower(0);
            robot.centerSlide.setPower(0);
            robot.rightSlide.setPower(0);
            if (time > 0.5){
                robot.extendo.setPower(-1);
                extendoIn = true;
            }
        }

        else {
            magneticSwitchHang = true;
            extendoIn = false;
            robot.extendo.setPower(0);
            robot.leftStabilizer.setPosition(LEFT_HOLD_OFF);

            robot.rightStabilizer.setPosition(RIGHT_HOLD_OFF);


            PID_MODE = false;

            linearAutomation = true;

            robot.leftSlide.setPower(-1);
            robot.rightSlide.setPower(-1);

            robot.centerSlide.setPower(-1);


            G1_B_PRESSED = false;
        }

    }
    public void toggles () {

        //HANG

        if (G1_B.wasJustPressed()) {
            G1_B_PRESSED = true;

            G1_B_PRESSED_TIME = getRuntime();
        }

        G1_B.readValue();

        if (G1_B_PRESSED){
            hang();
        }

        //OFFSET CORRECTION IN FC DRIVE

        if (G1_A.wasJustPressed()) {
            offset = robot.drive.pose.heading.toDouble();
        }

        G1_A.readValue();

        //CORRECT FOR OVER OR UNDER AIM IN SCORING

        if (G1_DPAD_UP.wasJustPressed()) {
            HIGH_SPECIMEN_POS_TELE += 100;
            target = (int) (HIGH_SPECIMEN_POS_TELE + linearSlideZeroPosition);
            PID_MODE = true;
        }


        if (G1_DPAD_DOWN.wasJustPressed()) {
            HIGH_SPECIMEN_POS_TELE -= 100;
            target = (int) (HIGH_SPECIMEN_POS_TELE + linearSlideZeroPosition);
            PID_MODE = true;
        }

        G1_DPAD_UP.readValue();

        G1_DPAD_DOWN.readValue();

        // DOUBLE-B CLAW


        if (G2_B.wasJustPressed()) {

            if ((getRuntime() - G2_B_PRESSED_TIME) < 0.5) {
                robot.claw.setPosition(CLAW_LESS_OPEN);

            } else {
                robot.claw.setPosition(CLAW_OPEN);

            }
            G2_B_PRESSED_TIME = getRuntime();
        }

        G2_B.readValue();

        // EXTENDO MAX

        G2_RIGHT_BUMPER.readValue();

        if (G2_RIGHT_BUMPER.wasJustPressed()) {

            if ((getRuntime() - G2_RIGHT_BUMPER_PRESSED_TIME) < 0.5) {
                maximumExtension = true;

            }
            G2_RIGHT_BUMPER_PRESSED_TIME = getRuntime();
        }

        // PICKUP FROM WALL AND GO TO SCORE SPECIMEN

        if (G1_LEFT_BUMPER.wasJustPressed() || G2_START.wasJustPressed()) {
            G2_START_PRESSED_TIME = getRuntime();
            G2_START_PRESSED = true;

        }

        if (G1_LEFT_BUMPER.wasJustReleased() || G2_START.wasJustReleased()) {

            target = (int) (HIGH_SPECIMEN_POS_TELE + linearSlideZeroPosition);
            PID_MODE = true;

        }

        G1_LEFT_BUMPER.readValue();
        G2_START.readValue();

        if (G2_START_PRESSED){
            specimenScoreUp();
        }

        // BACK UP AUTOMATION FOR MISS

        if (G2_X.wasJustPressed()){
            G2_X_PRESSED = true;
            G2_X_PRESSED_TIME = getRuntime();
        }

        if (G2_X_PRESSED){
            double time = getRuntime() - G2_X_PRESSED_TIME;
            PID_MODE = true;
            target = LOWER_SLIDES_MISS;
            if (time > 0.4){
                G2_X_PRESSED = false;
            }
        }

        //AUTOMATION FOR BACK --> RETRACT TO WALL

        if (G2_BACK.wasJustPressed() || G1_RIGHT_BUMPER.wasJustPressed()) {
            G2_BACK_PRESSED = true;
            G2_BACK_PRESSED_TIME = getRuntime();
        }


        if (G2_BACK.wasJustReleased() || G1_RIGHT_BUMPER.wasJustReleased()) {
            G2_BACK_RELEASED = true;
        }
        if (G2_BACK_RELEASED && getRuntime() - G2_BACK_PRESSED_TIME > WAIT_BACK_BUTTON){
            target = (int) linearSlideZeroPosition;
            G2_BACK_PRESSED = false;
            PID_MODE = true;
        }

        G1_RIGHT_BUMPER.readValue();

        G2_BACK.readValue();

        if (G2_BACK_PRESSED){
            specimenGrabDown();
        }

        // SLIDE RESET

        if (G1_START.wasJustPressed()) {

            G1_START_PRESSED = true;
            G2_START_PRESSED_TIME = getRuntime();

        }

        G1_START.readValue();

        if (G1_START_PRESSED){
            slideReset();
        }

        //DPAD DOWN

        if (G2_DPAD_DOWN.wasJustPressed()) {

            target = (int) (0 + linearSlideZeroPosition);
            PID_MODE = true;

            G1_DPAD_DOWN_PRESSED = true;


            G2_DPAD_DOWN_PRESSED_TIME = getRuntime();
            dpadDownServoLock = false;
        }

        if (G2_DPAD_DOWN.wasJustReleased()) {

            dpadDownServoLock = true;

            robot.claw.setPosition(CLAW_OPEN);
            robot.clawMove.setPosition(MOVE_HOVER_SAMPLE);
            robot.clawPivot.setPosition(PIVOT_SAMPLE_PICKUP);
            robot.clawRotate.setPosition(ROTATE_90);

            if (!extendoOut) {
                extendoOut = true;
            }

        }

        G2_DPAD_DOWN.readValue();

        if (G2_DPAD_DOWN_PRESSED) {
            submerisbleHover();
        }


        //AUTOMATION FOR Y --> GRAB AND GO TO RELEASE POSITION


        if (G2_Y.wasJustPressed()) {

            colorSensorTimer = getRuntime();


            if (robot.claw.getPosition() < CLAW_CLOSED + 0.04) {

                robot.clawRotate.setPosition(ROTATE_NEUTRAL);
                robot.clawMove.setPosition(MOVE_WALL_INTAKE);
                robot.clawPivot.setPosition(PIVOT_WALL_INTAKE);

                extendoIn = false;

                G2_DPAD_UP_PRESSED = false;
                extendoHoldIn = true;


            } else if (robot.clawPivot.getPosition() <= PIVOT_SAMPLE_PICKUP + 0.03 && robot.clawPivot.getPosition() >= PIVOT_SAMPLE_PICKUP - 0.03) {

                robot.clawMove.setPosition(MOVE_PICKUP_SAMPLE);
                robot.clawPivot.setPosition(PIVOT_SAMPLE_PICKUP);
            }


        }


        if (G2_Y.wasJustReleased()) {

            if (robot.claw.getPosition() > CLAW_CLOSED + 0.04 && (pickupSample || (getRuntime() - colorSensorTimer > 0.25))) {


                G2_Y_PRESSED = true;


                G2_Y_PRESSED_TIME = getRuntime();
            } else if (robot.clawPivot.getPosition() > (PIVOT_SAMPLE_PICKUP - 0.15)) {
                robot.clawMove.setPosition(MOVE_HOVER_SAMPLE);
                robot.clawPivot.setPosition(PIVOT_SAMPLE_PICKUP);

            }

        }


        G2_Y.readValue();

        if (G2_Y_PRESSED) {

            samplePickupToDrop();


        }

        //AUTOMATION FOR DPAD UP --> GRAB SPECIMEN

        if (G2_DPAD_UP.wasJustPressed()) {

            colorSensorTimer = getRuntime();

            robot.clawMove.setPosition(MOVE_PICKUP_SAMPLE);
            robot.clawPivot.setPosition(PIVOT_SAMPLE_PICKUP);




        }

        if (G2_DPAD_UP.wasJustReleased()) {

            if (pickupSample || getRuntime() - colorSensorTimer > 0.25) {

                G2_DPAD_UP_PRESSED = true;

                G2_DPAD_UP_PRESSED_TIME = getRuntime();
            } else {
                robot.clawMove.setPosition(MOVE_HOVER_SAMPLE);
            }

        }


        G2_DPAD_UP.readValue();

        if (G2_DPAD_UP_PRESSED) {

            samplePickup();
        }







    }
    public void displayTelemetry () {

        TELE.addData("Linear Slide Position", linearSlidePosition);
        TELE.addData("Linear Slide Zero Position", linearSlideZeroPosition);


        TELE.addData("Extendo Position", robot.extendo.getCurrentPosition());
        TELE.addData("Extendo Zero Position", extendoZeroPosition);

        TELE.addData("x", robot.drive.pose.position.x);
        TELE.addData("y", robot.drive.pose.position.y);

        TELE.addData("heading", Math.toDegrees(robot.drive.pose.heading.toDouble()));


        TELE.addData("pin10:", robot.pin10.getState());
        TELE.addData("pin11:", robot.pin11.getState());

        TELE.update();

    }






}
