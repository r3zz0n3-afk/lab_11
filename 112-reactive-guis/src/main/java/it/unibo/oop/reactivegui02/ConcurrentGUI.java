package it.unibo.oop.reactivegui02;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static volatile  boolean up = true;
    private static volatile  boolean stop;
    private final JLabel display = new JLabel();


    public ConcurrentGUI() {

        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        final JButton stopButton = new JButton("stop");
        final JButton upButton = new JButton("up");
        final JButton downButton = new JButton("down");
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
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                        if(up == true){
                            counter++;
                        } else {
                            counter--;
                        }
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace();;
                    }
                }
            }
        }).start();

        upButton.addActionListener(e -> up = true);
        downButton.addActionListener(e -> up = false);
        stopButton.addActionListener(e -> { 
            stop = true;
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            stopButton.setEnabled(false);
        });


    }
}
