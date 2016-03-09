/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
//import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author s_stanis
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    public Button addCity;

    @FXML
    public Button removeCity;
    @FXML
    public Button removeCon;
    @FXML
    public TextField dist;
    @FXML
    public TextField time;

    @FXML
    public ListView<CityUI> citiesList;

    @FXML
    public TableView<ConnectionUI> connectionsTable;
    @FXML
    TableColumn<ConnectionUI, String> fromColumn;
    @FXML
    TableColumn<ConnectionUI, String> toColumn;

    @FXML
    public Canvas canvas;

    ObservableList<CityUI> cities = FXCollections.observableArrayList();
    ObservableList<ConnectionUI> connections = FXCollections.observableArrayList();

    private CityUI selectedCity = null;
    private CityUI cityCon1 = null;
    private CityUI cityCon2 = null;
    private ConnectionUI curConnection = null;

    private GraphicsContext gc;

    private int initialX, initialY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.gc = canvas.getGraphicsContext2D();
        dist.setEditable(false);
        time.setEditable(false);

        connectionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        connectionsTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ConnectionUI> observable, ConnectionUI oldValue, ConnectionUI newValue) -> {
            if (newValue != null) {
                if (oldValue != null) {
                    Bindings.unbindBidirectional(dist.textProperty(), oldValue.dist);
                    Bindings.unbindBidirectional(time.textProperty(), oldValue.time);
                    oldValue.color = Color.BLACK;
                }
                dist.setEditable(true);
                time.setEditable(true);
                Bindings.bindBidirectional(dist.textProperty(), newValue.dist, new NumberStringConverter());
                Bindings.bindBidirectional(time.textProperty(), newValue.time, new NumberStringConverter());
                newValue.color = Color.RED;

            } else {
                dist.setEditable(false);
                time.setEditable(false);
                Bindings.unbindBidirectional(dist.textProperty(), oldValue.dist);
                Bindings.unbindBidirectional(time.textProperty(), oldValue.time);
                oldValue.color = Color.BLACK;
                dist.setText("");
                time.setText("");
            }
            render();
        });

        connectionsTable.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue.equals(Boolean.TRUE) && connectionsTable.getSelectionModel().getSelectedIndex() != -1) {
                connectionsTable.getSelectionModel().getSelectedItem().color = Color.RED;
                render();
            }
            if (newValue.equals(Boolean.FALSE)) {
                connectionsTable.getSelectionModel().getSelectedItem().color = Color.BLACK;
                render();
            }
        });

        citiesList.setCellFactory(new Callback<ListView<CityUI>, ListCell<CityUI>>() {

            @Override
            public ListCell<CityUI> call(ListView<CityUI> p) {

                return new ListCell<CityUI>() {

                    @Override
                    protected void updateItem(CityUI t, boolean bln) {
                        super.updateItem(t, bln); //To change body of generated methods, choose Tools | Templates.
                        if (t != null) {
                            setGraphic(new Label(t.name));
                        } else {
                            setGraphic(null);
                        }
                    }

                };
            }
        });
        toColumn.setCellValueFactory(data -> {

            int from = data.getValue().toCity;
            String toCity = null;
            for (CityUI city : cities) {
                if (city.id == from) {
                    toCity = city.name;
                    break;
                }
            }

            return new SimpleStringProperty(toCity);
        });
        fromColumn.setCellValueFactory(data -> {

            int from = data.getValue().fromCity;
            String fromCity = null;
            for (CityUI city : cities) {
                if (city.id == from) {
                    fromCity = city.name;
                    break;
                }
            }

            return new SimpleStringProperty(fromCity);
        });
        connections.addListener((Change<? extends ConnectionUI> e) -> {
            e.next();
            if (e.wasAdded()) {
                e.getAddedSubList().forEach((ConnectionUI con) -> connectionsTable.getItems().add(con));
            } else if (e.wasRemoved()) {
                e.getRemoved().forEach((ConnectionUI con) -> connectionsTable.getItems().remove(con));
            }
            render();
        });
        cities.addListener((Change<? extends CityUI> e) -> {
            e.next();
            if (e.wasAdded()) {
                e.getAddedSubList().stream().forEach((CityUI city) -> citiesList.getItems().add(city));
            } else if (e.wasRemoved()) {
                e.getRemoved().forEach((CityUI city) -> citiesList.getItems().remove(city));
            }
            render();
        });

        setUpMouseEvents();
    }

    private void setUpMouseEvents() {

        canvas.setOnMouseClicked((MouseEvent me) -> {

            if (me.getClickCount() == 2) {
                cities.add(new CityUI((int) me.getX(), (int) me.getY(), "Random"));
            }

        });

        canvas.setOnMousePressed((MouseEvent me) -> {
            double x = me.getX();
            double y = me.getY();
            for (CityUI city : cities) {
                if (x > city.x && x < city.x + 75
                        && y > city.y && y < city.y + 25) {
                    if (me.isPrimaryButtonDown()) {
                        initialX = (int) x - city.x;
                        initialY = (int) y - city.y;
                        selectedCity = city;
                        selectedCity.secondaryColor = Color.RED;
                        selectedCity.render(gc);
                        break;
                    } else if (me.isSecondaryButtonDown()) {
                        cityCon1 = city;
                        cityCon1.secondaryColor = Color.GREEN;
                        curConnection = new ConnectionUI((int) city.x + 75 / 2, (int) city.y + 25 / 2, (int) city.x, (int) city.y);
                        curConnection.fromCity = city.id;
                        cityCon1.render(gc);
                        break;
                    }
                }
            }
        });

        canvas.setOnMouseReleased((MouseEvent me) -> {
            if (cityCon1 != null) {
                if (cityCon2 != null) {
                    cityCon2.secondaryColor = Color.WHITE;
                    cityCon2 = null;
                    addConnection();

                }
                cityCon1.secondaryColor = Color.WHITE;
                cityCon1 = null;
                curConnection = null;
                render();
            }

            if (selectedCity != null) {
                selectedCity.secondaryColor = Color.WHITE;
                selectedCity.render(gc);
                selectedCity = null;
            }

        });

        canvas.setOnMouseDragged((MouseEvent me) -> {
            double x = me.getX();
            double y = me.getY();
            if (cityCon1 != null) {
                curConnection.toX = (int) x;
                curConnection.toY = (int) y;
                for (CityUI city : cities) {
                    if (city.equals(cityCon1)) {
                        continue;
                    }
                    if (x > city.x && x < city.x + 75
                            && y > city.y && y < city.y + 25) {
                        cityCon2 = city;
                        cityCon2.secondaryColor = Color.GREEN;
                        curConnection.toX = cityCon2.x + 75 / 2;
                        curConnection.toY = cityCon2.y + 25 / 2;
                        curConnection.toCity = cityCon2.id;
                        break;
                    } else {
                        city.secondaryColor = Color.WHITE;
                        cityCon2 = null;
                    }
                };
                render();
            }

            if (selectedCity != null) {
                selectedCity.x = (int) me.getX() - initialX;
                selectedCity.y = (int) me.getY() - initialY;
                connections.stream().
                        filter((ConnectionUI con)
                                -> con.toCity == selectedCity.id || con.fromCity == selectedCity.id)
                        .forEach((ConnectionUI con) -> {
                            if (con.toCity == selectedCity.id) {
                                con.toX = selectedCity.x + 75 / 2;
                                con.toY = selectedCity.y + 25 / 2;
                            } else if (con.fromCity == selectedCity.id) {
                                con.fromX = selectedCity.x + 75 / 2;
                                con.fromY = selectedCity.y + 25 / 2;
                            }
                        });
                render();
            }
        });
        render();
    }

    private void addConnection() {

        if (!connections.stream().anyMatch((ConnectionUI con) -> {
            return curConnection.toCity == con.toCity
                    && curConnection.fromCity == con.fromCity
                    || curConnection.toCity == con.fromCity
                    && curConnection.fromCity == con.toCity;
        })) {
            connections.add(new ConnectionUI(curConnection));

        }
    }

    public void removeCity() {
        CityUI selected = citiesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            connections.removeAll(
                    connections.
                    filtered((ConnectionUI con) -> con.toCity == selected.id || con.fromCity == selected.id));
            cities.remove(selected);
        }

    }

    public void addCity(ActionEvent e) {

//        TextInputDialog dialog = new TextInputDialog("walter");
//        dialog.setTitle("Text Input Dialog");
//        dialog.setHeaderText("Look, a Text Input Dialog");
//        dialog.setContentText("Please enter your name:");
//        Optional<String> result = dialog.showAndWait();
//        result.ifPresent(name -> cities.add(new CityUI(50, 50, name)));
        cities.add(new CityUI(50, 50, "Sliven"));
    }

    public void removeConnection() {
        System.out.println("asas");
        ConnectionUI selectedCon = connectionsTable.getSelectionModel().getSelectedItem();
        if (selectedCon != null) {
            connections.removeAll(
                    connections.
                    filtered((ConnectionUI con) -> selectedCon.toCity == con.toCity && selectedCon.fromCity == con.fromCity));

        }
    }

    private void render() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (curConnection != null) {
            curConnection.render(gc);
        }
        connections.forEach((ConnectionUI con) -> con.render(gc));
        cities.forEach((CityUI city) -> city.render(gc));
    }

}
