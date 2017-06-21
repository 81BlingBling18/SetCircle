import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wqm on 6/16/17.
 */
public class SELab04 extends Application {

    private double up = 1;
    private double right = 1;
    private double down = -1;
    private double left = -1;
    private double front = 1;
    private double back = -1;
    private volatile double incD = 0.01;
    private int blockNumber = 0;
    private volatile ArrayList<Circle> maxList;
    private volatile LinkedList<Point> points;
    private int a;
    private int b;
    private int c;
    private int d;

    private volatile int compareCount = 0;
    private volatile double maxRadius = 0;
    private volatile Point maxRadiusPoint = null;
    private int ballNumber;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    static Long start;
    static Long end;

    //用四个线程来跑
    private Runnable first = new Runnable() {
        @Override
        public void run() {
            Point point = null;
            double r = 0;
            Point p;
            int size = points.size() / 4;
            for (a = 0; a < size; a++) {
                // System.out.println("fo");
                p = points.get(a);
                double maxRadius = maxRadius(p.x, p.y, p.z);
                if (r < maxRadius) {
                    r = maxRadius;
                    point = p;
                }
                // System.out.println("first number is " + f + "size is" + size);
            }
            compare(point, r);
            a = 0;
        }
    };
    private Runnable second = new Runnable() {
        @Override
        public void run() {
            Point point = null;
            double r = 0;
            Point p;
            int size = points.size() / 2;
            for (b = points.size() / 4; b < size; b++) {
                //System.out.println("ba");
                p = points.get(b);
                double maxRadius = maxRadius(p.x, p.y, p.z);
                if (r < maxRadius) {
                    r = maxRadius;
                    point = p;
                }
                // System.out.println("first number is " + f + "size is" + size);
            }
            compare(point, r);
            b = 0;
        }
    };
    private Runnable third = new Runnable() {
        @Override
        public void run() {
            Point point = null;
            double r = 0;
            Point p;
            int size = points.size() / 4 * 3;
            for (c = points.size() / 2; c < size; c++) {
                // System.out.println("fo");
                p = points.get(c);
                double maxRadius = maxRadius(p.x, p.y, p.z);
                if (r < maxRadius) {
                    r = maxRadius;
                    point = p;
                }
                // System.out.println("first number is " + f + "size is" + size);
            }
            compare(point, r);
            c = 0;
        }
    };
    private Runnable fourth = new Runnable() {
        @Override
        public void run() {
            Point point = null;
            double r = 0;
            Point p;
            int size = points.size();
            for (d = points.size() / 4 * 3; d < size; d++) {
                // System.out.println("fo");
                p = points.get(d);
                double maxRadius = maxRadius(p.x, p.y, p.z);
                if (r < maxRadius) {
                    r = maxRadius;
                    point = p;
                }
                // System.out.println("first number is " + f + "size is" + size);
            }
            compare(point, r);
            d = 0;
        }
    };


    public SELab04() {
        maxList = BallList.maxList;
        points = new LinkedList<>();

    }

    //开始计算，n是要计算的数量，step是步长，blocklist是小方块
    public void start(int n, double step, ArrayList<SELab04.Circle> blockList) {
        maxList.addAll(blockList);
        start(n, step);
    }
    //不加小方块的计算
    public void start(int n, double step) {
        ballNumber = n;
        incD = step;
        for (double k = back + incD; k < front; k += incD) {
            for (double i = left + incD; i < right; i += incD) {
                for (double j = down + incD; j < up; j += incD) {
                    points.add(new SELab04.Point(k, i, j));
                }
            }
        }
        threadPool.execute(first);
        threadPool.execute(second);
        threadPool.execute(third);
        threadPool.execute(fourth);
    }
    //每次计算完成之后用compare来从四个线程的结果中选出一个最大的
    private synchronized void compare(SELab04.Point point, double radius) {
        System.out.println(Thread.currentThread().getId());
        compareCount++;
        if (maxRadius < radius) {
            maxRadius = radius;
            maxRadiusPoint = point;
            // System.out.println("compare finished" + compareCount + " " + ballNumber + (point == null));

        }

        if (compareCount % 4 == 0) {
            SELab04.Circle c = new SELab04.Circle();
            c.p = maxRadiusPoint;
            c.radius = maxRadius;
            maxList.add(c);

            // System.out.println(  "maxR " + c.radius + "x= " + c.p.x + "y= " + c.p.y + "z= " + c.p.z + "list size:" + points.size() + (maxRadiusPoint == null));

            Iterator<Point> iterator = points.iterator();
            System.out.println(compareCount);
            while (iterator.hasNext()) {
                SELab04.Point p = iterator.next();
                SELab04.Circle circle = new SELab04.Circle();
                circle.p = p;
                if (distance(circle, c) < c.radius) {
                    iterator.remove();
                }
            }
            System.out.println("find one");
            maxRadius = 0;
            //  maxRadiusPoint = null;

            if (compareCount == 4 * ballNumber) {
                compareCount = 0;
                ballNumber = 0;
                // System.out.println("circle size " + maxList.size());
                launch();
            } else {
                threadPool.execute(first);
                threadPool.execute(second);
                threadPool.execute(third);
                threadPool.execute(fourth);

            }
        }

    }

    //找出现有条件下的最大半径，方法是求当前点与四条边，所有已经确定的圆的距离，取最小值
    private double maxRadius(double x, double y, double z) {
        ArrayList<Double> rList = new ArrayList<>(6);
        rList.add(Math.abs(x - front));
        rList.add(Math.abs(x - back));
        rList.add(Math.abs(y - right));
        rList.add(Math.abs(y - left));
        rList.add(Math.abs(z - up));
        rList.add(Math.abs(z - down));
        SELab04.Circle circle = new SELab04.Circle();
        circle.p.x = x;
        circle.p.y = y;
        circle.p.z = z;
        double result = rList.get(0);
        for (Double d : rList) {
            if (result > d) {
                result = d;
            }
        }
        for (SELab04.Circle c : maxList) {
            double tmp = distance(c, circle) - c.radius;
            if (tmp < result) {
                result = tmp;
            }
        }


        return result;
    }

    //计算两个圆心的距离
    private double distance(SELab04.Circle c1, SELab04.Circle c2) {
        return Math.sqrt((c1.p.x - c2.p.x) * (c1.p.x - c2.p.x) + (c1.p.y - c2.p.y) * (c1.p.y - c2.p.y) + (c1.p.z - c2.p.z) * (c1.p.z - c2.p.z));
    }

    //重写start方法，用来展示球体
    @Override
    public void start(Stage stage) {
        ArrayList<Circle> maxList = BallList.maxList;
        //  System.out.println("circle size from start" + BallList.maxList.size());

        Shape3D[] circles = new Shape3D[maxList.size()];

        // Create and position camera
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.getTransforms().addAll(
                new Rotate(-20, Rotate.Y_AXIS),
                new Rotate(-20, Rotate.X_AXIS));
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(0);

        for (int i = 0; i < maxList.size(); i++) {
            System.out.printf("ball sum %d,r:%f,x:%.3f,y:%.3f,z:%.3f\n",maxList.size(),maxList.get(i).radius,maxList.get(i).p.x,maxList.get(i).p.y,maxList.get(i).p.z);

            circles[i] = new Sphere(maxList.get(i).radius * 200);
            circles[i].setCullFace(CullFace.BACK);
            circles[i].setTranslateX(maxList.get(i).p.x * 200 + 400);
            circles[i].setTranslateY(maxList.get(i).p.y * 200 + 400);
            circles[i].setTranslateZ(maxList.get(i).p.z * 200 + 400);
            circles[i].setTranslateZ(maxList.get(i).p.z * 200 + 400);
        }

        // Build the Scene Graph
        Group root = new Group(circles);

        PointLight pointLight = new PointLight(Color.ANTIQUEWHITE);
        pointLight.setTranslateX(800);
        pointLight.setTranslateY(-100);
        pointLight.setTranslateZ(-1000);

        root.getChildren().add(pointLight);


        Scene scene = new Scene(root, 800, 800, true);
        scene.setFill(Color.WHITE);

        scene.setCamera(camera);
        stage.setScene(scene);
        end = System.currentTimeMillis();
        System.out.println("计算时间为" + (end-start)/1000);
        //Displaying the contents of the stage
        stage.show();
    }

    //若不添加钉子，可以直接运行程序后在命令行输入步长和球体个数
    //若添加钉子，则需要在list中添加钉子的坐标
    public static void main(String[] args) {
        start  = System.currentTimeMillis();
        Scanner scanner = new Scanner(System.in);

        String num = "hell";
        String step = "-0.0";
        while (!num.matches("^[1-9][0-9]*$") || num.contains("-")) {
            System.out.println("请输入要生成的球的个数(大于0的整数)：");
            num = scanner.nextLine();
        }

        while (!step.matches("^[0-1].[0-9]*$") || step.contains("-")) {
            System.out.println("请输入计算步长（(0,2)区间内，常用值有0.05、0.025、0.01、0.001,越小运算时间越长）：");
            step = scanner.nextLine();
        }
        System.out.println("个数：" + Integer.valueOf(num) + "步长：" + Double.valueOf(step) + "现在开始计算。。。。");

        //计算圆的半径和位置
        SELab04 lab = new SELab04();
        //在List中添加小方块
        ArrayList<SELab04.Circle> list = new ArrayList<>();
        SELab04.Circle c1 = new SELab04().new Circle();
        c1.p.x = 0;
        c1.p.y = 0;
        c1.p.z = 0;
        list.add(c1);
        SELab04.Circle c2 = new SELab04().new Circle();
        c2.p.x = 0.5;
        c2.p.y = 0.5;
        c2.p.z = 0.5;
        list.add(c2);
        SELab04.Circle c3 = new SELab04().new Circle();
        c3.p.x = -0.5;
        c3.p.y = -0.5;
        c3.p.z = -0.5;
        list.add(c3);
        lab.start(Integer.valueOf(num), Double.valueOf(step),list);
    }

    //内部类用于定义圆和点
    class Circle {
        Point p = new Point(0, 0, 0);
        double radius;

    }

    class Point {
        public Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        double x;
        double y;
        double z;

    }
}
