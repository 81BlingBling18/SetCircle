import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by wqm on 6/9/17.
 */
public class SELab03 {
    private double up=1;
    private double right=1;
    private double down = -1;
    private double left = -1;
    private double front = 1;
    private double back = -1;
    private double incD = 0.01;
    ArrayList<SELab03.Circle> maxList;
    LinkedList<SELab03.Point> points;
    //默认构造方法，用于构造不设置钉子的方法
    public SELab03() {
        maxList = new ArrayList<>();
        points = new LinkedList<>();
        for (double k = back + incD;k<front;k+=incD) {
            for (double i = left + incD; i < right; i += incD) {
                for (double j= down + incD;j<up;j+=incD) {
                    points.add(new SELab03.Point(k,i,j));
                }
            }
        }

    }
    //可以设置一定数量的钉子
    public SELab03(ArrayList<SELab03.Circle> blockList) {
        maxList = blockList;
        points = new LinkedList<>();
        for (double k = back + incD;k<front;k+=incD) {
            for (double i = left + incD; i < right; i += incD) {
                for (double j= down + incD;j<up;j+=incD) {
                    points.add(new SELab03.Point(k,i,j));
                }
            }
        }
    }
    //开始计算，n是要计算的数量
    public ArrayList<SELab03.Circle> start(int n) {
        for (int i = 0;i<n;i++) {
            calculate();
        }
        return maxList;
    }
    //产生一个圆
    private void calculate() {
        SELab03.Circle c = new SELab03.Circle();
        c.radius = 0;
        for (SELab03.Point p : points) {
            double r = maxRadius(p.x, p.y,p.z);
            //System.out.println("min r is " + r);
            if (r > c.radius) {
                c = new SELab03.Circle();
                c.p = p;
                c.radius = r;
            }

        }
        maxList.add(c);

        Iterator<SELab03.Point> iterator = points.iterator();
        System.out.println(points.size());
        while (iterator.hasNext()) {
            SELab03.Point p = iterator.next();
            SELab03.Circle circle = new SELab03.Circle();
            circle.p = p;
            if (distance(circle, c) < c.radius) {
                iterator.remove();
            }
        }
        System.out.println("maxR " + c.radius + "x= " + c.p.x + "y= " + c.p.y+ "z= " + c.p.z + "list size:" + points.size());
    }

    //找出现有条件下的最大半径，方法是求当前点与四条边，所有已经确定的圆的距离，取最小值
    private  double maxRadius(double x, double y,double z) {
        ArrayList<Double> rList = new ArrayList<>();
        rList.add(Math.abs(x - front));
        rList.add(Math.abs(x - back));
        rList.add(Math.abs(y -right));
        rList.add(Math.abs(y - left));
        rList.add(Math.abs(z - up));
        rList.add(Math.abs(z - down));
        SELab03.Circle circle = new SELab03.Circle();
        circle.p.x = x;
        circle.p.y = y;
        circle.p.z = z;
        for (SELab03.Circle c : maxList) {
            rList.add(distance(c, circle) - c.radius);
        }
        double result = rList.get(0);
        for (Double d : rList) {
            if (result > d) {
                result = d;
            }
        }

        return result;
    }
    //计算两个圆心的距离
    private double distance(SELab03.Circle c1, SELab03.Circle c2) {
        return Math.sqrt((c1.p.x - c2.p.x) * (c1.p.x - c2.p.x) + (c1.p.y - c2.p.y) * (c1.p.y - c2.p.y)+ (c1.p.z - c2.p.z) * (c1.p.z - c2.p.z));
    }

    class Circle{
        Point p = new Point(0,0,0);
        double radius;

    }
    class Point{
        public Point(double x, double y,double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        double x;
        double y;
        double z;
    }

    public static void main(String[] args) {
//        ArrayList<SELab01.Circle> circles = new ArrayList<>();
//        SELab01.Circle circle = new SELab01().new Circle();
//        circle.p.x = 0;
//        circle.p.y = 0;
//        circles.add(circle);
//        SELab01.Circle circle2 = new SELab01().new Circle();
//        circle2.p.x = -0.5;
//        circle2.p.y = -0.5;
//        circles.add(circle2);
//        SELab01.Circle circle3 = new SELab01().new Circle();
//        circle3.p.x = -0.5;
//        circle3.p.y = 0.5;
//        circles.add(circle3);
        SELab03 lab = new SELab03();
        //input your circle number here
        ArrayList<SELab03.Circle> list = lab.start(100);
       // GraphicsDemo demo = new GraphicsDemo(list);
        //demo.setVisible(true);

    }

    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex3f(0f,0.5f,0f);
        gl.glVertex3f(-0.5f,0.2f,0f);
        gl.glVertex3f(-0.5f,-0.2f,0f);
        gl.glVertex3f(0f,-0.5f,0f);
        gl.glVertex3f(0f,0.5f,0f);
        gl.glVertex3f(0.5f,0.2f,0f);
        gl.glVertex3f(0.5f,-0.2f,0f);
        gl.glVertex3f(0f,-0.5f,0f);
        gl.glEnd();
    }
}
