import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new Window());
                frame.pack();
                frame.setTitle("Matrix Test");
                frame.setResizable(false);
                frame.setFocusable(true);
                frame.requestFocusInWindow();
                frame.setVisible(true);
            }
        });
    }
}