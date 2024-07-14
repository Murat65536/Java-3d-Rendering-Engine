import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class Window extends JPanel implements ActionListener {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private final BufferedImage bufferedImage;
    private final JLabel jLabel = new JLabel();
    private final Timer timer  = new Timer(10, this);
    private double[] projection = mat4Ortho(0, WIDTH, HEIGHT, 0, -1, 1);

    public Window() {
        super(true);
        this.setLayout(new GridLayout());
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        jLabel.setIcon(new ImageIcon(bufferedImage));
        this.add(jLabel);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        clear();
        createSquare();
        jLabel.repaint();
    }

    private void clear() {
      Graphics2D g = bufferedImage.createGraphics();
      g.setColor(Color.gray);
      g.fillRect(0, 0, WIDTH, HEIGHT);
      g.dispose();
    }

    private void createLine(int[] coord1, int[] coord2, int width, Color color) {
      int x = coord2[0] - coord1[0];
      int y = coord2[1] - coord1[1];
      double r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
      double angle = Math.atan2(y, x);
      double[] translate = mat4Translate((coord1[0] + coord2[0]) / 2, (coord1[1] + coord2[1]) / 2, 0);
      double[] scale = mat4Scale(r, width, 1);
      double[] rotate = mat4Rotate(0, 0, 1, angle);
      double[] model = mat4Mul(mat4Mul(scale, rotate), translate);
      
      Graphics2D g = bufferedImage.createGraphics();
      g.setStroke(new BasicStroke(width));
      // g.drawLine();
    }

    private void createSquare() {
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.blue);
        g.dispose();
    }

    private double[] mat4Ortho(double right, double left, double bottom, double top, double near, double far) {
      double[] mat = {
        2 / (right - left),
        0,
        0,
        0,
        0,
        2 / (top - bottom),
        0,
        0,
        0,
        0,
        -2 / (far - near),
        0,
        -(right + left) / (right - left),
        -(top + bottom) / (top - bottom),
        -(far + near) / (far - near),
        1
      };
      return mat;
    }

    private double[] mat4Translate(double x, double y, double z) {
      double[] mat = {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        x, y, z, 1
      };

      return mat;
    }

      private double[] mat4Scale(double x, double y, double z) {
        double[] mat = {
          x, 0, 0, 0,
          0, y, 0, 0,
          0, 0, z, 0,
          0, 0, 0, 1
        };

      return mat;
    }

    private double[] mat4Rotate(double x, double y, double z, double angle) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double tan = 1 - cos;

      double l = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

      double normalizedX = x / l;
      double normalizedY = y / l;
      double normalizedZ = z / l;

      double[] result = {
        tan * normalizedX * normalizedX + cos,
        tan * normalizedX * normalizedY - sin * normalizedZ,
        tan * normalizedX * normalizedZ + sin * normalizedY,
        0,
        tan * normalizedX * normalizedY + sin * normalizedZ,
        tan * normalizedY * normalizedY + cos,
        tan * normalizedY * normalizedZ - sin * normalizedX,
        0,
        tan * normalizedX * normalizedZ - sin * normalizedY,
        tan * normalizedY * normalizedZ + sin * normalizedX,
        tan * normalizedZ * normalizedZ + cos,
        0,
        0,
        0,
        0,
        1
      };

      return result;
    }

    private double[] mat4Mul(double[] m1, double[] m2) {
      double[] result = {
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0
      };

      for (int i = 0; i < 16; i++) {
        result[i] = m1[i] * m2[i] + m1[i + 1] * m2[i + 1] + m1[i + 2] * m2[i + 2] + m1[i + 3] * m2[i + 3];
      }

      return result;
    }
}