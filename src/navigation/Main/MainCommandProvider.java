/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.Main;

import BurningFX.CommandProvider;
import UIElements.CityUI;
import edu.kit.informatik.RouteGraph.RouteGraph;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 *
 * @author s_stanis
 */
public class MainCommandProvider extends CommandProvider {

    RouteGraph rg;

    public MainCommandProvider() {
        rg = new RouteGraph();
        taskExecutonar = Executors.newSingleThreadExecutor();
    }

    public void addCityToTheGraph(CityUI city, Consumer<Boolean> callback) {
        newConcurentCommand(() -> {
            rg.addVertex(city.name);
        }, callback);
    }

}
