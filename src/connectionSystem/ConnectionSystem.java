/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectionSystem;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author s_stanis
 */
public class ConnectionSystem {

    ArrayList<Connectable> items;
    Connectable first, second;
    GraphicsContext gc;
    Consumer<Object> reRender;
    BiConsumer<Connectable, Connectable> newConnection;
    double lastX, lastY;

    public ConnectionSystem(GraphicsContext gc, Consumer<Object> reRender, BiConsumer<Connectable, Connectable> newConnection) {
        items = new ArrayList<>();
        this.reRender = reRender;
        this.newConnection = newConnection;
        this.gc = gc;
    }

    public void addItem(Connectable item) {
        items.add(item);
    }

    public void removeItem(Connectable item) {
        items.remove(item);
    }

    public void pressEvent(double x, double y) {
        if (first != null) {
            return;
        }
        for (Connectable item : items) {
            if (x > item.getX() && x < item.getX() + item.getWidth()
                    && y > item.getY() && y < item.getY() + item.getHeight()) {
                first = item;
                first.selected();
                lastX = x;
                lastY = y;
                reRender.accept(null);
                break;
            }

        }
    }

    public void releaseEvent(double x, double y) {
        if (first != null) {
            if (second != null) {
                second.unselect();
                newConnection.accept(first, second);
            }

            first.unselect();
            first = null;
            reRender.accept(null);

        }

    }

    public void dragEvent(double x, double y) {
        if (first != null) {
            lastX = x;
            lastY = y;

            for (Connectable item : items) {
                if (item.equals(first)) {
                    continue;
                }
                if (x > item.getX() && x < item.getX() + item.getWidth()
                        && y > item.getY() && y < item.getY() + item.getHeight()) {
                    second = item;
                    second.selected();
                    lastX = second.getX() + second.getWidth() / 2;
                    lastY = second.getY() + second.getHeight() / 2;
                    break;
                }
                if (second != null) {
                    second.unselect();
                    second = null;
                }
            }

            reRender.accept(null);
        }
    }

    public void renderCurrentConnection() {
        if (first != null) {
            gc.strokeLine(first.getX() + first.getWidth() / 2, first.getY() + first.getHeight() / 2, lastX, lastY);
        }

    }

}
