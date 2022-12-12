package raf.dsw.gerumap.gui.swing.state.concrate;

import raf.dsw.gerumap.core.ApplicationFramework;
import raf.dsw.gerumap.gui.swing.error.ErrorType;
import raf.dsw.gerumap.gui.swing.error.ProblemType;
import raf.dsw.gerumap.gui.swing.view.ElementPainter;
import raf.dsw.gerumap.gui.swing.view.MindMapView;
import raf.dsw.gerumap.gui.swing.view.PojamPainter;
import raf.dsw.gerumap.mapRepository.implementation.Element;
import raf.dsw.gerumap.mapRepository.implementation.Pojam;
import raf.dsw.gerumap.mapRepository.implementation.Veza;
import raf.dsw.gerumap.gui.swing.state.State;

import java.awt.*;

public class MoveState extends State {
    //Originalne koordinate gde je prvi put pritisnut kursor
    static int x;
    static int y;
    //Cuva prethodne koordinate
    static int lastX;
    static int lastY;
    @Override
    public void pressed(int x, int y, MindMapView m) {
        //Pocetne koordinate
        MoveState.x = MoveState.lastX = x;
        MoveState.y = MoveState.lastY = y;
    }

    //vraca na originalnu poziciju ako je trenutna zauzeta ili je van panela
    @Override
    public void released(int x, int y, MindMapView m) {
        for (Element element : m.getMapSelectionModel().getSelectedElements()) {
            //Veze ne treba da uporedjuje jer oni mogu jedna preko druge
            if (element instanceof Veza)
                continue;

            for (ElementPainter elementPainter : m.getElementPainterList()) {
                Pojam pojam = (Pojam) element;

                //Ne treba pojam sam sa sobom da uporedjuje
                if (elementPainter.getElement().equals(pojam))
                    continue;

                //Mora da se vrati na staru poziciju jer je nova zauzeta
                if (elementPainter instanceof PojamPainter && elementPainter.elementAt(pojam, pojam.getPosition())) {
                    int px = pojam.getPosition().x - (lastX - MoveState.x);
                    int py = pojam.getPosition().y - (lastY - MoveState.y);
                    pojam.setPosition(new Point(px, py));

                    ApplicationFramework.getInstance().getMessageGenerator().generateMessage(ErrorType.ERROR, ProblemType.POSITION_TAKEN);
                }
            }
        }
    }

    @Override
    public void dragged(int x, int y, MindMapView m) {
        //Ako je zumirano i prazan selekt onda se ponasa kao move za sve komponente da lazira pregled panela
        if (m.getMapSelectionModel().getSelectedElements().isEmpty() && m.getZoom() > 1) {
            for (ElementPainter elementPainter : m.getElementPainterList()) {
                if (elementPainter.getElement() instanceof Veza)
                    continue;
                Pojam pojam = (Pojam) elementPainter.getElement();

                int px = pojam.getPosition().x + (x - MoveState.lastX);
                int py = pojam.getPosition().y + (y - MoveState.lastY);

                pojam.setPosition(new Point(px, py));
            }

            MoveState.lastX = x;
            MoveState.lastY = y;
            return;
        }

        /*//Baci error ako nije zumirano
        if (m.getMapSelectionModel().getSelectedElements().isEmpty() && m.getZoom() <= 1) {
            ApplicationFramework.getInstance().getMessageGenerator().generateMessage(ErrorType.ERROR, ProblemType.HAS_TO_BE_ZOOMED);
        }
         */

        //Ako su selektovani da ih pomera
        for (Element element : m.getMapSelectionModel().getSelectedElements()) {
            if (element instanceof Veza)
                continue;
            Pojam pojam = (Pojam) element;

            int px = pojam.getPosition().x + (x - MoveState.lastX);
            int py = pojam.getPosition().y + (y - MoveState.lastY);

            /*
            System.out.println("x: " + pojam.getPosition().x + ", px: " + px);
            System.out.println("y: " + pojam.getPosition().y + ", py " + py);
            System.out.println("----------------------------------------------");
             */
            pojam.setPosition(new Point(px, py));
        }

        MoveState.lastX = x;
        MoveState.lastY = y;
    }
}
