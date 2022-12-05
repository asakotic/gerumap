package raf.dsw.gerumap.gui.swing.view;

import raf.dsw.gerumap.mapRepository.implementation.Element;
import raf.dsw.gerumap.mapRepository.implementation.Pojam;
import raf.dsw.gerumap.mapRepository.implementation.Veza;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class VezaPainter extends ElementPainter{


    public VezaPainter(Element element) {
        super(element);
    }

    //todo ne treba vise puta ista veza za dva pojma i ovaj uslov za draw ne treba ovde
    @Override
    public void draw(Graphics2D g, Element element) {
        Veza veza = (Veza) element;
        int x = veza.getFrom().getPosition().x;
        int y =veza.getFrom().getPosition().y;
        int x2 =veza.getTo().getPosition().x;
        int y2 =veza.getTo().getPosition().y;

        //From - pocetni pojam
        Point left = leftPosition(x, y, veza.getFrom()); // left
        Point up = upPosition(x, y, veza.getFrom()); // up
        Point right = rightPosition(x, y, veza.getFrom());// right
        Point down = downPosition(x, y, veza.getFrom());//down

        Point[] arrPoints = {left, up, right, down};
        double min = 9999;
        double intezitet;
        int xx = x;
        int yy = y;

        for (int i = 0; i < arrPoints.length; i++) {
            intezitet = Math.sqrt(Math.pow(arrPoints[i].x - x2, 2) + Math.pow(arrPoints[i].y - y2, 2));
            if (min > intezitet) {
                min = intezitet;
                xx = arrPoints[i].x;
                yy = arrPoints[i].y;
            }
        }

        //To - krajni pojam
        Point left2 = leftPosition(x2, y2, veza.getTo()); //left
        Point up2 = upPosition(x2, y2, veza.getTo()); //up
        Point right2 = rightPosition(x2, y2, veza.getTo()); // right
        Point down2 = downPosition(x2, y2, veza.getTo()); // down

        arrPoints = new Point[]{left2, up2, right2, down2};
        min = 9999;
        int xx2 = x2;
        int yy2 = y2;

        for (int i = 0; i < arrPoints.length; i++) {
            intezitet = Math.sqrt(Math.pow(arrPoints[i].x - xx, 2) + Math.pow(arrPoints[i].y - yy, 2));
            if (min > intezitet) {
                min = intezitet;
                xx2 = arrPoints[i].x;
                yy2 = arrPoints[i].y;
            }
        }

        //Cuva i kooridante delova elipse gde se zakljuca veza
        veza.setPocetak(new Point(xx, yy));
        veza.setKraj(new Point(xx2, yy2));

        shape = new Line2D.Float(xx, yy, xx2, yy2);
        g.setPaint(veza.getColor());
        g.setStroke(new BasicStroke(veza.getStroke()));
        g.draw(shape);
    }

    //todo crta levo desno gore dole ali koordinate elipse su gore levo
    //za brisanje pojma da li smo ga pogodili
    @Override
    public boolean elementAt(Element element, Point position) {
        if (element instanceof Veza)//Mora pojam da pogodi
            return false;

        //this
        Veza veza = (Veza) this.element;
        //A
        int x = veza.getPocetak().x;//x1
        int y = veza.getPocetak().y;//x1
        //B
        int right = veza.getKraj().x;//x2
        int down = veza.getKraj().y;//y2

        if (x > right) {
            int t = x;
            x = right;
            right = t;
        }

        //that
        Pojam that= (Pojam) element;
        int x2 = position.x;//x
        int y2 = position.y;//y

        //Ako se x2 hitbox pojma nalazi izmedju leve i desne koordinate veze
        if (x < x2 && x2 <= right) {//todo popravi brisanje veze

            //jednacina prave: y - y1 = ((y2-y1)/(x2-x1))*(x-x1)  A(x1, y1) B(x2, y2)
            //kod nas: y2 - y = ((down - y)/(right - x)) * (x2 - x)
            int k = (y - down) / (x - right);
            int result = position.y - y - k * position.x + k * x;
            System.out.println("result: " + result);

            if ( -5 <= result && result <= 5)//Moze sa 10 da promasi(kao hitbox)
                return true;
        }

        if (x == right)
            return true;

        return false;
    }

    //-0
    private Point leftPosition(int x, int y, Pojam pojam) {
        Point left = new Point();
        left.x = x;
        left.y = y + pojam.getDimension().height/2;

        return left;
    }

    // |
    // 0
    private Point upPosition(int x, int y, Pojam pojam) {
        Point up = new Point();
        up.x = x + pojam.getDimension().width/2;
        up.y = y;

        return up;
    }

    //0-
    private Point rightPosition(int x, int y, Pojam pojam) {
        Point right = new Point();
        right.x = x + pojam.getDimension().width;
        right.y = y + pojam.getDimension().height/2;

        return right;
    }

    //0
    //|
    private Point downPosition(int x, int y, Pojam pojam) {
        Point down = new Point();
        down.x = x + pojam.getDimension().width/2;
        down.y = y + pojam.getDimension().height;

        return down;
    }
}
