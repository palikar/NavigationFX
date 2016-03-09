/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author s_stanis
 */
public class CityUI {

    private static int currentId = 0;
    public int x, y;
    public int id;
    public String name;
    public Color secondaryColor = Color.WHITE;

    public CityUI(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.id = currentId++;
        this.name = name;
    }

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

}
