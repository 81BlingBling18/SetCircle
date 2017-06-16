import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

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
    private double incD = 0.01;
    private int blockNumber = 0;
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

    //重写start方法，用来展示球体
    @Override
    public void start(Stage stage) {
        //计算圆的半径和位置
        SELab03 lab = new SELab03();
        ArrayList<Circle> list =lab.start(10 );

        Shape3D[] circles = new Sphere[list.size()];


        // Create and position camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll (
                new Rotate(-20, Rotate.Y_AXIS),
                new Rotate(-20, Rotate.X_AXIS),
                new Translate(0, 0, -15));

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().add(camera);

        for (int i = 0;i<list.size();i++) {
            Sphere sphere = new Sphere();
            circles[i] = sphere;
            sphere.setRadius(list.get(i).radius * 2);
            sphere.setCullFace(CullFace.BACK);

            circles[i].setTranslateX(list.get(i).p.x * 2);
            circles[i].setTranslateY(list.get(i).p.y * 2);
            circles[i].setTranslateZ(list.get(i).p.z * 2);

            System.out.println("x:" + (list.get(i).p.x * 200 + 500) + "y:" + (list.get(i).p.y * 500 + 500) + "z:" + (list.get(i).p.z * 500 + 500));
        }
        root.getChildren().addAll(circles);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 500,500);
        subScene.setFill(Color.ALICEBLUE);
        subScene.setCamera(camera);
        Group group = new Group();
        group.getChildren().add(subScene);


        //Adding scene to the stage
        stage.setScene(new Scene(group)) ;

        //Displaying the contents of the stage
        stage.show() ;
    }
    public static void main(String[] args) {
        launch(args);
    }

}
