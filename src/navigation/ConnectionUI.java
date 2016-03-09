package navigation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author s_stanis
 */
public class ConnectionUI {

    int fromX, fromY, toX, toY;
    Color color = Color.BLACK;
    int fromCity, toCity;
    SimpleIntegerProperty time, dist;

    public ConnectionUI(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        time = new SimpleIntegerProperty(0);
        dist = new SimpleIntegerProperty(0);
    }

    public ConnectionUI(ConnectionUI con) {
        this(con.fromX, con.fromY, con.toX, con.toY);
        this.fromCity = con.fromCity;
        this.toCity = con.toCity;
    }

    public void render(GraphicsContext gc) {
        gc.setStroke(color);
        if (color.equals(Color.RED)) {
            gc.setLineWidth(3);
        } else {
            gc.setLineWidth(1);
        }
        gc.strokeLine(fromX, fromY, toX, toY);
    }

}
