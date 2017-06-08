import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by wqm on 6/8/17.
 */
public class SELab01 {
    private double up=1;
    private double right=1;
    private double down = -1;
    private double left = -1;
    private double incD = 0.001;
    ArrayList<Circle> maxList;
    LinkedList<Point> points;

    public SELab01() {
        maxList = new ArrayList<>();
        points = new LinkedList<>();
        for (double i = left + incD; i < right; i += incD) {
            for (double j= down + incD;j<up;j+=incD) {
                points.add(new Point(i, j));
            }
        }
    }

    public SELab01(ArrayList<Circle> blockList) {
        maxList = blockList;
        points = new LinkedList<>();
        for (double i = left + incD; i < right; i += incD) {
            for (double j= down + incD;j<up;j+=incD) {
                points.add(new Point(i, j));
            }
        }
    }

    public ArrayList<Circle> start(int n) {
        for (int i = 0;i<n;i++) {
            calculate();
        }
        return maxList;
    }

    private void calculate() {
        Circle c = new Circle();
        c.radius = 0;
        for (Point p : points) {
            double r = maxRadius(p.x, p.y);
            //System.out.println("min r is " + r);
            if (r > c.radius) {
                c = new Circle();
                c.p = p;
                c.radius = r;
            }

        }
        maxList.add(c);
        System.out.println("maxR " + c.radius + "x= " + c.p.x + "y= " + c.p.y);
        Iterator<Point> iterator = points.iterator();
        while (iterator.hasNext()) {
            Point p = iterator.next();
            Circle circle = new Circle();
            circle.p = p;
            if (distance(circle, c) < c.radius) {
                iterator.remove();
            }
        }

    }


    private  double maxRadius(double x, double y) {
        ArrayList<Double> rList = new ArrayList<>();
        rList.add(Math.abs(x - left));
        rList.add(Math.abs(x - right));
        rList.add(Math.abs(y - up));
        rList.add(Math.abs(y - down));
        Circle circle = new Circle();
        circle.p.x = x;
        circle.p.y = y;
        for (Circle c : maxList) {
            rList.add(distance(c, circle) - c.radius);
        }
        double result = rList.get(0);
        for (Double d : rList) {
            if (result > d) {
                result = d;

            }
        }
        System.out.println(result);
        return result;
    }

    private double distance(Circle c1, Circle c2) {
        return Math.sqrt((c1.p.x - c2.p.x) * (c1.p.x - c2.p.x) + (c1.p.y - c2.p.y) * (c1.p.y - c2.p.y));
    }

    class Circle{
        Point p = new Point(0,0);
        double radius;

    }
    class Point{
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
        double x;
        double y;
    }

    public static void main(String[] args) {
        ArrayList<Circle> circles = new ArrayList<>();
        Circle circle = new SELab01().new Circle();
        circle.p.x = 0;
        circle.p.y = 0;
        circles.add(circle);
        SELab01 lab = new SELab01();
        //input your circle number here
        ArrayList<Circle> list = lab.start(1);
        GraphicsDemo demo = new GraphicsDemo(list);
        demo.setVisible(true);


    }
}
