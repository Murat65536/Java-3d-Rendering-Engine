package vectors;

public class Vec3 {
  double x, y, z;
  double r, g, b;
  double[] v = new double[3];

  public Vec3(double param1, double param2, double param3) {
    x = param1;
    y = param2;
    z = param3;
    r = param1;
    g = param2;
    b = param3;
    v[0] = param1; 
    v[1] = param2;
    v[2] = param3;
  }
}
