/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragSystem;

import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author s_stanis
 */
public interface Dragable {

    void setX(double x);

    void setY(double y);

    int getId();

    double getX();

    double getY();

    double getHeight();

    double getWidth();

    void dragged();

    void unDragged();

    void render(GraphicsContext gc);

}
