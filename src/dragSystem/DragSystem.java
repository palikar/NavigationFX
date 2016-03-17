/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragSystem;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import java.util.function.Consumer;

/**
 *
 * @author s_stanis
 */
public class DragSystem {

    ArrayList<Dragable> items;
    double initialX = 0, initialY = 0;
    Dragable dragged = null;
    GraphicsContext gc;
    Consumer<Object> reRender;
    boolean enable;

    public DragSystem(GraphicsContext gc, Consumer<Object> reRender) {
        items = new ArrayList<>();
        this.reRender = reRender;
        this.gc = gc;
    }

    public void addItem(Dragable item) {
        items.add(item);
    }

    public void removeItem(Dragable item) {
        items.remove(item);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void pressEvent(double x, double y) {
        if (dragged != null) {
            return;
        }
        for (Dragable item : items) {
            if (x > item.getX() && x < item.getX() + item.getWidth()
                    && y > item.getY() && y < item.getY() + item.getHeight()) {
                initialX = (int) x - item.getX();
                initialY = (int) y - item.getY();
                dragged = item;

                item.dragged();
                reRender.accept(null);
                break;
            }
        }
    }

    public void releaseEvent(double x, double y) {
        if (dragged != null) {
            dragged.unDragged();
            dragged.render(gc);
            dragged = null;

            reRender.accept(null);
        }
    }

    public void dragEvent(double x, double y) {
        if (dragged != null) {
            dragged.setX(x - initialX);
            dragged.setY(y - initialY);
            reRender.accept(null);
        }

    }

    public boolean isDragging() {
        return dragged != null;
    }

    public Dragable getDragged() {
        return dragged;
    }

}
