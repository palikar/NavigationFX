/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BurningFX;

/**
 *
 * @author s_stanis
 */
import javax.annotation.PostConstruct;

/**
 *
 * @author s_stanis
 */
public abstract class Mediator<T extends CommandProvider> {

    private final CommandProvider commandProvider;

    public Mediator(CommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    @PostConstruct
    protected abstract void init();

    public T getCommandProvider() {
        return (T) commandProvider;
    }

}
