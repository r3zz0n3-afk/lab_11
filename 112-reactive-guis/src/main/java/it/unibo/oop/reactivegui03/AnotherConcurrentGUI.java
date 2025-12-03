package it.unibo.oop.reactivegui03;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static volatile  boolean up = true;
    private static volatile  boolean stop;
    private static long SEC_SPLEEP = TimeUnit.SECONDS.toMillis(10);
    private final JLabel display = new JLabel();
    private final JButton stopButton = new JButton("stop");
    private final JButton upButton = new JButton("up");
    private final JButton downButton = new JButton("down");


    public AnotherConcurrentGUI() {

        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(upButton);
        panel.add(downButton);
        panel.add(stopButton);
        this.getContentPane().add(panel);
        this.setVisible(true);

        new Thread( new Runnable() {
             @Override
            public void run() {
                int counter = 0; 
                while (!stop) {
                    try {
                        // The EDT doesn't access `counter` anymore, it doesn't need to be volatile
                        final var nextText = Integer.toString(counter);
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                        if(up == true){
                            counter++;
                        } else {
                            counter--;
                        }
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SEC_SPLEEP);
                    stopApplication();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        upButton.addActionListener(e -> up = true);
        downButton.addActionListener(e -> up = false);
        stopButton.addActionListener(e -> this.stopApplication());
    }

    private void stopApplication() {
            stop = true;
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            stopButton.setEnabled(false);
    }
}
