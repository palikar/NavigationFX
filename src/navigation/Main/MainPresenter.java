/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.Main;

import connectionSystem.Connectable;
import connectionSystem.ConnectionSystem;
import dragSystem.DragSystem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import javax.inject.Inject;
import UIElements.CityUI;
import UIElements.ConnectionUI;

/**
 *
 * @author s_stanis
 */
public class MainPresenter implements Initializable {

    private ResourceBundle resources = null;

    @FXML
    private Button addCity;
    @FXML
    private Button removeCity;
    @FXML
    private Button removeCon;
    @FXML
    private TextField dist;
    @FXML
    private TextField time;
    @FXML
    public ListView<CityUI> citiesList;
    @FXML
    private TableView<ConnectionUI> connectionsTable;
    @FXML
    private TableColumn<ConnectionUI, String> fromColumn;
    @FXML
    private TableColumn<ConnectionUI, String> toColumn;
    @FXML
    private Canvas canvas;
    @Inject
    MainMediator mediator;
    DragSystem dragSystem;
    ConnectionSystem conSystem;

    private GraphicsContext gc;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resources = rb;
        this.gc = canvas.getGraphicsContext2D();
        dist.setEditable(false);
        time.setEditable(false);
        dragSystem = new DragSystem(gc, object -> render());
        conSystem = new ConnectionSystem(gc, o -> render(), (id1, id2) -> {
            addConnection(id1, id2);
        });
        canvas.setOnMousePressed(me -> {
            if (me.isPrimaryButtonDown()) {
                dragSystem.pressEvent(me.getX(), me.getY());
            } else if (me.isSecondaryButtonDown()) {
                conSystem.pressEvent(me.getX(), me.getY());
            }
        });
        canvas.setOnMouseReleased(me -> {

            dragSystem.releaseEvent(me.getX(), me.getY());
            conSystem.releaseEvent(me.getX(), me.getY());

        });
        canvas.setOnMouseDragged(me -> {

            dragSystem.dragEvent(me.getX(), me.getY());
            conSystem.dragEvent(me.getX(), me.getY());
            if (dragSystem.isDragging()) {
                updateConnections((CityUI) dragSystem.getDragged());
            }

        });
        citiesList.setCellFactory((ListView<CityUI> city) -> {
            return new ListCell<CityUI>() {
                @Override
                protected void updateItem(CityUI t, boolean bln) {
                    super.updateItem(t, bln);
                    if (t != null) {
                        setGraphic(new Label(t.name));
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });
        citiesList.getItems().addListener(this::newCityAdded);
        setUpCanvas();
        connectionsTable.getItems().addListener(this::newConnectionAdded);

        fromColumn.setCellValueFactory((TableColumn.CellDataFeatures<ConnectionUI, String> param) -> {
            return new SimpleStringProperty(citiesList.getItems().stream().filter(city -> city.id == param.getValue().fromCity).findFirst().get().name + "");
        });
        toColumn.setCellValueFactory((TableColumn.CellDataFeatures<ConnectionUI, String> param) -> {
            return new SimpleStringProperty(citiesList.getItems().stream().filter(city -> city.id == param.getValue().toCity).findFirst().get().name + "");
        });
        connectionsTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ConnectionUI> observable, ConnectionUI oldValue, ConnectionUI newValue) -> {
            if (newValue != null) {
                if (oldValue != null) {
                    Bindings.unbindBidirectional(dist.textProperty(), oldValue.getDist());
                    Bindings.unbindBidirectional(time.textProperty(), oldValue.getTime());
                    oldValue.setColor(Color.BLACK);
                }
                dist.setEditable(true);
                time.setEditable(true);
                Bindings.bindBidirectional(dist.textProperty(), newValue.getDist(), new NumberStringConverter());
                Bindings.bindBidirectional(time.textProperty(), newValue.getTime(), new NumberStringConverter());
                newValue.setColor(Color.RED);

            } else {
                dist.setEditable(false);
                time.setEditable(false);
                Bindings.unbindBidirectional(dist.textProperty(), oldValue.getDist());
                Bindings.unbindBidirectional(time.textProperty(), oldValue.getTime());
                oldValue.setColor(Color.BLACK);
                dist.setText("");
                time.setText("");
            }
            render();
        });

        connectionsTable.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue.equals(Boolean.TRUE) && connectionsTable.getSelectionModel().getSelectedIndex() != -1) {
                connectionsTable.getSelectionModel().getSelectedItem().setColor(Color.RED);
                render();
            }
            if (newValue.equals(Boolean.FALSE) && !connectionsTable.getItems().isEmpty()) {
                connectionsTable.getSelectionModel().getSelectedItem().setColor(Color.BLACK);
                render();
            }
        });

    }

    private void addConnection(Connectable id1, Connectable id2) {
        if (!connectionsTable.getItems().stream().anyMatch((ConnectionUI con) -> {
            return id1.getId() == con.toCity
                    && id2.getId() == con.fromCity
                    || id1.getId() == con.fromCity
                    && id2.getId() == con.toCity;
        })) {
            connectionsTable.getItems().add(
                    new ConnectionUI(id1.getId(), id2.getId(),
                            (int) id1.getX() + (int) id1.getWidth() / 2,
                            (int) id1.getY() + (int) id1.getHeight() / 2,
                            (int) id2.getX() + (int) id2.getWidth() / 2,
                            (int) id2.getY() + (int) id2.getHeight() / 2));

        }

    }

    private void setUpCanvas() {

        render();

    }

    private void newCityAdded(ListChangeListener.Change<? extends CityUI> change) {
        change.next();
        change.getAddedSubList().forEach(city -> {
            dragSystem.addItem(city);
            conSystem.addItem(city);
        });
        render();

    }

    private void newConnectionAdded(ListChangeListener.Change<? extends ConnectionUI> change) {

    }

    private void updateConnections(CityUI draggedCity) {
        connectionsTable.getItems().stream().
                filter((ConnectionUI con)
                        -> con.toCity == draggedCity.id || con.fromCity == draggedCity.id)
                .forEach((ConnectionUI con) -> {
                    if (con.toCity == draggedCity.id) {
                        con.toX = (int) draggedCity.getX() + (int) draggedCity.getWidth() / 2;
                        con.toY = (int) draggedCity.getY() + (int) draggedCity.getHeight() / 2;
                    } else if (con.fromCity == draggedCity.id) {
                        con.fromX = (int) draggedCity.getX() + (int) draggedCity.getWidth() / 2;
                        con.fromY = (int) draggedCity.getY() + (int) draggedCity.getHeight() / 2;
                    }
                });
    }

    public void removeCity(ActionEvent e) {
        CityUI selected = citiesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            connectionsTable.getItems().removeAll(
                    connectionsTable.getItems().
                    filtered((ConnectionUI con) -> con.toCity == selected.id || con.fromCity == selected.id));
            citiesList.getItems().remove(selected);

        }

    }

    public void addCity(ActionEvent e) {
        citiesList.getItems().add(new CityUI(50, 50, "Sliven"));

    }

    public void removeConnection(ActionEvent e) {
        ConnectionUI selectedCon = connectionsTable.getSelectionModel().getSelectedItem();
        if (selectedCon != null) {
            connectionsTable.getItems().removeAll(
                    connectionsTable.getItems().
                    filtered((ConnectionUI con) -> selectedCon.toCity == con.toCity && selectedCon.fromCity == con.fromCity));

        }
    }

    private void render() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        connectionsTable.getItems().forEach((ConnectionUI con) -> con.render(gc));
        conSystem.renderCurrentConnection();
        citiesList.getItems().forEach((CityUI city) -> city.render(gc));
    }

}
