/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.Main;

import BurningFX.CommandProvider;
import UIElements.CityUI;
import edu.kit.informatik.BasicGraphs.GraphOperations;
import edu.kit.informatik.Exceptions.VertexDoesNotExistException;
import edu.kit.informatik.Exceptions.WeigthStrategyDoesNotExist;
import edu.kit.informatik.RouteGraph.RouteGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void removeCityToTheGraph(CityUI city, Consumer<Boolean> callback) {
        newConcurentCommand(() -> {
            rg.removeVertex(city.name);
        }, callback);
    }

    public void search(CityUI city1, CityUI city2, String crit, Consumer<Integer> callback) {
        try {
            Object[] path = GraphOperations.getOptimalPathDijkstra(rg, city1.name, city2.name, rg.getWeigthStrategy(crit));
            int weight = GraphOperations.getPathLenth(path, rg.getWeigthStrategy(crit));
            callback.accept(weight);
            return;

        } catch (VertexDoesNotExistException ex) {
            callback.accept(-1);
            return;
        } catch (WeigthStrategyDoesNotExist ex) {
            callback.accept(-1);
            return;
        }

    }

    public void route(CityUI city1, CityUI city2, String crit, Consumer< Object[]> callback) {
        try {
            Object[] path = GraphOperations.getOptimalPathDijkstra(rg, city1.name, city2.name, rg.getWeigthStrategy(crit));

            callback.accept(path);
            return;
        } catch (VertexDoesNotExistException ex) {
            callback.accept(null);
            return;
        } catch (WeigthStrategyDoesNotExist ex) {
            callback.accept(null);
            return;
        }

    }

    public void setConnectionDist(CityUI city1, CityUI city2, int dist, Consumer<Boolean> callback) {
        newConcurentCommand(() -> {
            System.out.println(dist);
            rg.getDistanceStrategy().setWeight(city1.name, city2.name, dist);

            int timeSq = (int) rg.getTimeStrategy().getWeigth(city1.name, city2.name);
            timeSq *= timeSq;
            rg.getOptimalStrategy().setWeight(city1.name, city2.name, dist * dist + timeSq);
        }, callback);
    }

    public void setConnectionTime(CityUI city1, CityUI city2, int time, Consumer<Boolean> callback) {
        newConcurentCommand(() -> {

            rg.getTimeStrategy().setWeight(city1.name, city2.name, time);

            int distSq = (int) rg.getDistanceStrategy().getWeigth(city1.name, city2.name);
            distSq *= distSq;
            rg.getOptimalStrategy().setWeight(city1.name, city2.name, distSq);
        }, callback);
    }

    public void addConnection(CityUI city1, CityUI city2, Consumer<Boolean> callback) {
        newConcurentCommand(() -> {
            rg.addEdge(city1.name, city2.name);
        }, callback);
    }

    public void removeConnection(CityUI city1, CityUI city2, Consumer<Boolean> callback) {
        newConcurentCommand(() -> {
            rg.removeEdge(city1.name, city2.name);
        }, callback);
    }

}
