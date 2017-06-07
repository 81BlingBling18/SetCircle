import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by wqm on 6/8/17.
 */
public class GraphicsDemo extends JFrame {

    ArrayList<SELab01.Circle> list;
    public GraphicsDemo(ArrayList<SELab01.Circle> list) {
        setSize(1100, 1100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.list = list;
    }

    @Override
    public void paint(Graphics g) {

        // 画椭圆形
        for (SELab01.Circle c : list) {
            // System.out.println((int)(c.p.x*500+500) + "  " +(int)(c.p.y*500+500) + "  " +(int)(c.radius*500*2)+" "+ (int)(c.radius*500*2));
            g.drawOval((int)(c.p.x*500+500 -c.radius*500)+50, (int)(c.p.y*500+500 -c.radius*500)+50, (int)(c.radius*500*2), (int)(c.radius*500*2));


        }

    }
}
