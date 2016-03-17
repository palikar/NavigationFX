/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UIElements;

import connectionSystem.Connectable;
import dragSystem.Dragable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author s_stanis
 */
public class CityUI implements Dragable, Connectable {

    private static int currentId = 0;
    public double x, y;
    public int id;
    public String name;
    public Color secondaryColor = Color.WHITE;

    public CityUI(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.id = currentId++;
        this.name = name;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setLineWidth(1);
        gc.setFill(secondaryColor);
        gc.fillRect(x, y, 75, 25);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(14));
        gc.fillText(name, x, y + 20);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, 75, 25);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        CityUI city = (CityUI) obj;
        return city.id == this.id;

    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getHeight() {
        return 25;
    }

    @Override
    public double getWidth() {
        return 75;
    }

    @Override
    public void dragged() {
        secondaryColor = Color.RED;
    }

    @Override
    public void unDragged() {
        secondaryColor = Color.WHITE;

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void selected() {
        secondaryColor = Color.GREEN;
    }

    @Override
    public void unselect() {
        secondaryColor = Color.WHITE;
    }

}
