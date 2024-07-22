import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Window extends JPanel implements ActionListener {
  private static final int WIDTH = 1200;
  private static final int HEIGHT = 675;
  private final BufferedImage bufferedImage;
  private final JLabel jLabel = new JLabel();
  private final Timer timer  = new Timer(10, this);
  private double[][] rotationMatrix = {
    {1, 0, 0, 0},
    {0, 1, 0, 0},
    {0, 0, 1, 0},
    {0, 0, 0, 1}
  };
  private double[][] translationMatrix = {
    {1, 0, 0, 0},
    {0, 1, 0, 0},
    {0, 0, 1, 0},
    {0, 0, 0, 1}
  };
  private double[][] cameraMatrix = matrixMultiply(rotationMatrix, translationMatrix);
  private double[][] viewportMatrix = {
    {WIDTH / 2, 0, 0, (WIDTH - 1) / 2},
    {0, HEIGHT / 2, 0, (HEIGHT - 1) / 2},
    {0, 0, 0.5, 0.5}
  };
  private double left = -3;
  private double right = 3;
  private double bottom = -3;
  private double top = 3;
  private double near = -1;
  private double far = 1;
  // Perspective
  private double[][] projectionMatrix = {
    {(2 * near) / (right - left), 0, (right + left) / (right - left), 0},
    {0, (2 * near) / (top - bottom), (top + bottom) / (top - bottom), 0},
    {0, 0, -(far + near) / (far - near), -(2 * far * near) / (far - near)},
    {0, 0, -1, 0}
  };
  // Orthographic
  // private double[][] projectionMatrix = {
  //   {2 / (right - left), 0, 0, -(right + left) / (right - left)},
  //   {0, 2 / (top - bottom), 0, -(top + bottom) / (top - bottom)},
  //   {0, 0, -2 / (far - near), -(far + near) / (far - near)},
  //   {0, 0, 0, 1}
  // };
  private double[][] cubeVertices = {
    {-1, -1, -11, 1},
    {1, -1, -11, 1},
    {1, 1, -11, 1},
    {-1, 1, -11, 1},
    {-1, -1, -9, 1},
    {1, -1, -9, 1},
    {1, 1, -9, 1},
    {-1, 1, -9, 1}
  };
  private int[][] cubeEdges = {
    {0, 1}, {1, 2}, {2, 3}, {3, 0},
    {4, 5}, {5, 6}, {6, 7}, {7, 4},
    {0, 4}, {1, 5}, {2, 6}, {3, 7}
  };

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

  private void createSquare() {
    Graphics2D g = bufferedImage.createGraphics();
    g.setColor(Color.blue);
    double[][] cameraMatrixCube = matrixMultiply(cameraMatrix, matrixTranspose(cubeVertices));
    double[][] projectionMatrixCube = matrixMultiply(projectionMatrix, cameraMatrixCube);
    projectionMatrixCube = matrixDivideArray(projectionMatrixCube, projectionMatrixCube[3]);
    double[][] viewportMatrixCube = matrixMultiply(viewportMatrix, projectionMatrixCube);

    for (int i = 0; i < cubeEdges.length; i++) {
      g.drawLine((int)viewportMatrixCube[0][cubeEdges[i][0]], (int)viewportMatrixCube[1][cubeEdges[i][0]], (int)viewportMatrixCube[0][cubeEdges[i][1]], (int)viewportMatrixCube[1][cubeEdges[i][1]]);
    }
    g.dispose();
  }

  private double[][] matrixMultiply(double[][] matrix1, double[][] matrix2) {
    double result[][] = new double[matrix1.length][matrix2[0].length];
    for (int i = 0; i < matrix1.length; i++) {
      for (int j = 0; j < matrix2[0].length; j++) {
        for (int k = 0; k < matrix1[0].length; k++) {
          result[i][j] += matrix1[i][k] * matrix2[k][j];
        }
      }
    }
    return result;
  }

  private double[][] matrixTranspose(double[][] matrix) {
    double[][] transpose = new double[matrix[0].length][matrix.length];
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        transpose[j][i] = matrix[i][j];
      }
    }
    return transpose;
  }

  private double[][] matrixDivideArray(double[][] matrix, double[] array) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        matrix[i][j] /= array[j];
      }
    }
    return matrix;
  }
}