/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectionSystem;

/**
 *
 * @author s_stanis
 */
public interface Connectable {

    double getX();

    double getY();

    int getId();

    double getHeight();

    double getWidth();

    public void selected();

    public void unselect();

}
