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
import javafx.util.converter.NumberStringConverter;
import javax.inject.Inject;
import UIElements.CityUI;
import UIElements.ConnectionUI;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.ComboBoxListCell;

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
    @FXML
    ToggleGroup crit;
    @FXML
    ComboBox<CityUI> fromCity;
    @FXML
    ComboBox<CityUI> toCity;
    @FXML
    Button search;
    @FXML
    Button route;
    @FXML
    Button allPaths;
    @FXML
    Toggle distRadio;
    @FXML
    Toggle timeRadio;
    @FXML
    Toggle optimalRadio;
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

        toCity.setCellFactory((ListView<CityUI> city) -> {
            return new ListCell<CityUI>() {
                @Override
                protected void updateItem(CityUI t, boolean bln) {
                    super.updateItem(t, bln);
                    if (t != null) {
                        setText(t.name);
                        // setGraphic(new Label(t.name));
                    } else {
                        setText("");
                    }
                }
            };
        });
        fromCity.setCellFactory((ListView<CityUI> city) -> {
            return new ListCell<CityUI>() {
                @Override
                protected void updateItem(CityUI t, boolean bln) {
                    super.updateItem(t, bln);
                    if (t != null) {
                        setText(t.name);
                    } else {
                        setText("");
                    }
                }
            };
        });

        toCity.setButtonCell(new ComboBoxListCell<CityUI>() {

            @Override
            public void updateItem(CityUI t, boolean bln) {
                super.updateItem(t, bln);
                if (t != null) {
                    setText(t.name);
                } else {
                    setText("");
                }
            }

        });

        fromCity.setButtonCell(new ComboBoxListCell<CityUI>() {

            @Override
            public void updateItem(CityUI t, boolean bln) {
                super.updateItem(t, bln);
                if (t != null) {
                    setText(t.name);
                } else {
                    setText("");
                }
            }

        });

        distRadio.setUserData("route");
        timeRadio.setUserData("time");
        optimalRadio.setUserData("optimal");

    }

    private void setUpCanvas() {
        render();
    }

    private void newCityAdded(ListChangeListener.Change<? extends CityUI> change) {
        change.next();
        change.getAddedSubList().forEach(city -> {
            dragSystem.addItem(city);
            conSystem.addItem(city);
            toCity.getItems().add(city);
            fromCity.getItems().add(city);
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
            mediator.getCommandProvider().removeCityToTheGraph(selected, (Boolean result) -> {
                if (result) {
                    citiesList.getItems().remove(selected);
                    connectionsTable.getItems().removeAll(
                            connectionsTable.getItems().
                            filtered((ConnectionUI con) -> con.toCity == selected.id || con.fromCity == selected.id));
                }
            });

        }

    }

    public void addCity(ActionEvent e) {
        CityUI newCity = new CityUI(50, 50, Math.random() * 100 + "");
        mediator.getCommandProvider().addCityToTheGraph(newCity, (Boolean success) -> {
            if (success) {
                citiesList.getItems().add(newCity);
            }
        });

    }

    public void removeConnection(ActionEvent e) {
        ConnectionUI selectedCon = connectionsTable.getSelectionModel().getSelectedItem();
        if (selectedCon != null) {

            CityUI city1 = citiesList.getItems().stream().filter(city -> city.id == selectedCon.fromCity).findFirst().get();
            CityUI city2 = citiesList.getItems().stream().filter(city -> city.id == selectedCon.toCity).findFirst().get();
            mediator.getCommandProvider().removeConnection(city1, city2, (Boolean result) -> {
                if (result) {
                    connectionsTable.getItems().removeAll(
                            connectionsTable.getItems().
                            filtered((ConnectionUI con) -> selectedCon.toCity == con.toCity && selectedCon.fromCity == con.fromCity));
                    render();
                }
            });
        }
    }

    private void addConnection(Connectable id1, Connectable id2) {
        if (!connectionsTable.getItems().stream().anyMatch((ConnectionUI con) -> {
            return id1.getId() == con.toCity
                    && id2.getId() == con.fromCity
                    || id1.getId() == con.fromCity
                    && id2.getId() == con.toCity;
        })) {
            ConnectionUI newCon = new ConnectionUI(id1.getId(), id2.getId(),
                    (int) id1.getX() + (int) id1.getWidth() / 2,
                    (int) id1.getY() + (int) id1.getHeight() / 2,
                    (int) id2.getX() + (int) id2.getWidth() / 2,
                    (int) id2.getY() + (int) id2.getHeight() / 2);

            CityUI city1 = citiesList.getItems().stream().filter(city -> city.id == newCon.fromCity).findFirst().get();
            CityUI city2 = citiesList.getItems().stream().filter(city -> city.id == newCon.toCity).findFirst().get();

            newCon.getDist().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                mediator.getCommandProvider().setConnectionDist(city1, city2, newValue.intValue(), (Boolean res) -> {
                });
            });
            newCon.getTime().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                mediator.getCommandProvider().setConnectionTime(city1, city2, newValue.intValue(), (Boolean res) -> {
                });
            });
            mediator.getCommandProvider().addConnection(city1, city2, (Boolean result) -> {
                if (result) {
                    connectionsTable.getItems().add(newCon);
                    render();
                }
            });

        }

    }

    public void searchClick() {

        if (toCity.getSelectionModel().isEmpty()) {
            return;
        }
        if (fromCity.getSelectionModel().isEmpty()) {
            return;
        }

        mediator.getCommandProvider().search(
                fromCity.getValue(),
                toCity.getValue(),
                crit.getSelectedToggle().getUserData().toString(),
                (Integer pathValue) -> {

                    System.out.println(pathValue);
                });

    }

    public void routeClick() {
        if (toCity.getSelectionModel().isEmpty()) {
            return;
        }
        if (fromCity.getSelectionModel().isEmpty()) {
            return;
        }

        mediator.getCommandProvider().route(
                fromCity.getValue(),
                toCity.getValue(),
                crit.getSelectedToggle().getUserData().toString(),
                (Object[] path) -> {
                    List<Object> pathList = Arrays.asList(path);
                    for (int i = 0; i < pathList.size() - 1; i++) {
                        colorConnection(pathList.get(i).toString(), pathList.get(i + 1).toString());
                    }
                });
    }

    public void allPathsClick() {

    }

    public void colorConnection(String city1, String city2) {
        int id1 = citiesList.getItems().stream().filter(city -> city.name.equals(city1)).findFirst().get().id;
        int id2 = citiesList.getItems().stream().filter(city -> city.name.equals(city2)).findFirst().get().id;
        ConnectionUI connection = connectionsTable.getItems().stream().filter(con -> (con.fromCity == id1 && con.toCity == id2)
                || (con.fromCity == id2 && con.toCity == id1)).findFirst().get();
        connection.setColor(Color.RED);

    }

    private void render() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        connectionsTable.getItems().forEach((ConnectionUI con) -> con.render(gc));
        conSystem.renderCurrentConnection();
        citiesList.getItems().forEach((CityUI city) -> city.render(gc));
    }

}
