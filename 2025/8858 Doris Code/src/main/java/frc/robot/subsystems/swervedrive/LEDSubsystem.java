package frc.robot.subsystems.swervedrive;

import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.TwinkleAnimation;
import com.ctre.phoenix.led.TwinkleAnimation.TwinklePercent;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDSubsystem extends SubsystemBase {
    private static LEDSubsystem m_instance = null;
    private CANdle candle = null;
    private static final int LED_COUNT = 38;
    private Timer m_timer = null;
    private Mode mode = null;
    public static final Mode default_state = Mode.BLINK_GREEN;
    private static final TwinkleAnimation blue_twinkle_anim = new TwinkleAnimation(0, 255, 0, 0, 0.2, LED_COUNT,
            TwinklePercent.Percent18);

    public static enum Mode {
        OFF,
        SOLID_RED,
        SOLID_GREEN,
        SOLID_BLUE,
        RED_GODZILLA,
        BLUE_GODZILLA,
        BLINK_RED,
        BLINK_BLUE,
        BLINK_GREEN,
        TWINKLE_BLUE,
    }

    private LEDSubsystem() {
        candle = new CANdle(17);
        m_timer = new Timer();
        setMode(default_state);
        m_instance = this;
        setDefaultCommand(new Command() {
            {
                addRequirements(m_instance);
            }

            @Override
            public void execute() {
                m_instance.holdState();
            }
        });
    }

    public static LEDSubsystem getInstance() {
        if (m_instance == null) {
            m_instance = new LEDSubsystem();
        }
        return m_instance;
    }

    public void holdState() {
        double time = m_timer.get();
        SmartDashboard.putNumber("LED Animation Timer", time);
        switch (mode) {
            case OFF:
                setColor(0, 0, 0);
                break;
            case SOLID_RED:
                setColor(255, 0, 0);
                break;
            case SOLID_GREEN:
                setColor(0, 255, 0);
                break;
            case SOLID_BLUE:
                setColor(0, 0, 255);
                break;
            case RED_GODZILLA:
                runGodzilla(time, 255, 0, 0);
                break;
            case BLUE_GODZILLA:
                runGodzilla(time, 0, 0, 255);
                break;
            case BLINK_RED:
                blink(1, time, 255, 0, 0);
                break;
            case BLINK_GREEN:
                blink(1, time, 0, 255, 0);
                break;
            case BLINK_BLUE:
                blink(1, time, 0, 0, 255);
                break;
            case TWINKLE_BLUE:
                candle.animate(blue_twinkle_anim);
                break;
        }
    }

    public void setMode(Mode mode) {
        if (this.mode == Mode.RED_GODZILLA) {
            return;
        }
        if (this.mode == Mode.BLUE_GODZILLA) {
            return;
        }
        SmartDashboard.putString("LED Mode", mode.toString());
        candle.clearAnimation(0);
        this.mode = mode;
        m_timer.reset(); // reset animation timer
        m_timer.start(); // start animation timer
        last_blink = 0; // reset blink timer
        blink_state = true; // reset blink state
    }
    
    public void manualOverride(Mode mode) {
        this.mode = mode;
        setMode(mode);
    }

    /**
     * 
     * @param rate seconds per blink
     * @param time the led animation timer
     * @param r    amount of red
     * @param g    amount of green
     * @param b    amount of blue
     */
    private void blink(double rate, double time, int r, int g, int b) {
        if (time % rate < (rate / 2)) {
            setColor(r, g, b);
        } else {
            setColor(0, 0, 0);
        }
    }

    private void setColor(int r, int g, int b) {
        Color col = new Color(r, g, b);
        SmartDashboard.putString("LED Color", col.toHexString());
        candle.setLEDs(r, b, g, 0, 0, LED_COUNT);
    }

    private double last_blink = 0;
    private boolean blink_state = true;
    private final double godzilla_blink_rate = 17;

    /**
     * blink the leds starting slowly, but speeding up over time, with the max blink
     * speed occuring at 15 seconds
     * blink rate is number of blinks per second, and should max out at 8
     * 
     * @param time
     */
    private void runGodzilla(double time, int red, int green, int blue) {
        double blink_rate = time > godzilla_blink_rate ? 8 : 1 + (time / godzilla_blink_rate) * 7; // max out at 8
                                                                                                   // blinks per second
        if (time - last_blink > 1.0 / blink_rate) {
            last_blink = time;
            if (blink_state) {
                setColor(red, green, blue);
            } else {
                setColor(0, 0, 0);
            }
            blink_state = !blink_state;
        }
    }
}
