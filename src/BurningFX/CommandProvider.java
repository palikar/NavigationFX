/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BurningFX;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import javafx.concurrent.Task;

/**
 *
 * @author s_stanis
 */
public abstract class CommandProvider {

    protected ExecutorService taskExecutonar;

    protected void newConcurentCommand(Action code, Consumer<Boolean> callback) {
        Task adding = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                code.run();
                return 0;
            }
        };
        adding.setOnFailed(e -> {
            System.out.println(adding.getException());
            callback.accept(Boolean.FALSE);
        });
        adding.setOnSucceeded(e -> {
            callback.accept(Boolean.TRUE);
        });
        taskExecutonar.submit(adding);
    }

    void shutdown() {
        if (taskExecutonar != null) {
            taskExecutonar.shutdownNow();
        }
    }

}
