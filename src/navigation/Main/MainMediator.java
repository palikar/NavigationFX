/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.Main;

import BurningFX.CommandProvider;
import BurningFX.Mediator;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import UIElements.CityUI;

/**
 *
 * @author s_stanis
 */
public class MainMediator extends Mediator<MainCommandProvider> {

    public MainMediator(CommandProvider commandProvider) {
        super(commandProvider);
    }

    @Override
    protected void init() {

    }

}
