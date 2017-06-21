import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import sun.net.www.protocol.file.Handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Created by wqm on 6/9/17.
 */
public class SELab03 extends Application{

    private double up=1;
    private double right=1;
    private double down = -1;
    private double left = -1;
    private double front = 1;
    private double back = -1;
    private double incD = 0.05;
    private int blockNumber = 0;
    private ArrayList<SELab03.Circle> maxList;
    private LinkedList<SELab03.Point> points;
    private volatile int f;
    private volatile int b;


    Runnable forward = new Runnable() {
        @Override
        public void run() {

        }
    };
    Runnable backward = new Runnable() {
        @Override
        public void run() {

        }
    };

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
        blockNumber = blockList.size();
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
        if (blockNumber != 0) {
            ArrayList<Circle> list = (ArrayList<Circle>) maxList.subList(blockNumber - 1, maxList.size());
            blockNumber = 0;
            return list;
        } else {
            return maxList;
        }
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
        System.out.println("find one");

        Iterator<SELab03.Point> iterator = points.iterator();
        //System.out.println(points.size());
        while (iterator.hasNext()) {
            SELab03.Point p = iterator.next();
            SELab03.Circle circle = new SELab03.Circle();
            circle.p = p;
            if (distance(circle, c) < c.radius) {
                iterator.remove();
            }
        }
        //System.out.println("maxR " + c.radius + "x= " + c.p.x + "y= " + c.p.y+ "z= " + c.p.z + "list size:" + points.size());
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

    //重写start方法，用来展示球体
    @Override
    public void start(Stage stage) {
        //计算圆的半径和位置
        SELab03 lab = new SELab03();
        ArrayList<Circle> list =lab.start(1000 );

        Shape3D[] circles = new Shape3D[list.size()];

        // Create and position camera
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.getTransforms().addAll (
                new Rotate(-20, Rotate.Y_AXIS),
                new Rotate(-20, Rotate.X_AXIS));
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(0);

        for (int i = 0;i<list.size();i++) {
            circles[i] = new Sphere(list.get(i).radius * 200);
            circles[i].setCullFace(CullFace.BACK);
            circles[i].setTranslateX(list.get(i).p.x * 200+400);
            circles[i].setTranslateY(list.get(i).p.y * 200+400);
            circles[i].setTranslateZ(list.get(i).p.z * 200+400);
            circles[i].setTranslateZ(list.get(i).p.z * 200+400);
           // System.out.println("x:" + (list.get(i).p.x * 200 + 500) + "y:" + (list.get(i).p.y * 500 + 500) + "z:" + (list.get(i).p.z * 500 + 500));
        }

        // Build the Scene Graph
        Group root = new Group(circles);

        PointLight pointLight = new PointLight(Color.ANTIQUEWHITE);
        pointLight.setTranslateX(800);
        pointLight.setTranslateY(-100);
        pointLight.setTranslateZ(-1000);

        root.getChildren().add(pointLight);


        Scene scene = new Scene(root,800,800,true);
        scene.setFill(Color.WHITE);

        scene.setCamera(camera);
        stage.setScene(scene) ;

        //Displaying the contents of the stage
        stage.show() ;
    }
    public static void main(String[] args) {
        launch(args);
    }

}
