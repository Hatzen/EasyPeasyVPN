package de.hartz.vpn.helper;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kaiha on 25.04.2018.
 */
public abstract class  LoadingFrame extends JFrame implements Runnable {

    private Color backgroundGray = new Color(200,200, 200, 200);
    private Color highlightDotBlue = new Color(0,50,200);
    private Color dotBlue = new Color(50,100,250);

    private boolean isLoading;
    private int highlightDot;

    private Timer timer;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (isLoading) {
            Graphics2D graphics = (Graphics2D) g;
            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHints(rh);
            graphics.setColor(backgroundGray);
            graphics.fillRect(0,0, getWidth(), getHeight());
            int circleRadius = 50;
            int offsetBetweenDots = 50;
            int verticalPosition = (getHeight() / 2) - (circleRadius / 2);
            int horizontalPosition = (getWidth() / 2) - (circleRadius / 2) - circleRadius - offsetBetweenDots;
            for (int i = 0; i < 3; ++i) {
                graphics.setColor(dotBlue);
                if (i == highlightDot) {
                    graphics.setColor(highlightDotBlue);
                }
                int x = horizontalPosition + i * (circleRadius + offsetBetweenDots);
                graphics.fillOval(x, verticalPosition, circleRadius, circleRadius);
            }
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            if ( timer == null ) {
                timer = new Timer();
                timer.scheduleAtFixedRate(getTimerTask(), 500, 500);
            }
        } else {
            timer.cancel();
            timer = null;
            repaint();
        }
        requestFocus();
        setEnabled(!isLoading);
    }

    @Override
    public void run() {
        setLoading(true);
        performTask();
        setLoading(false);
    }

    /**
     * Do all background tasks in here and call new Thread(this).start()
     */
    public abstract void performTask();

    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                highlightDot = ++highlightDot % 3;
                repaint();
            }
        };
    }
}
