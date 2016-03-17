/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BurningFX;

import com.airhacks.afterburner.injection.Injector;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s_stanis
 */
public class BurningFX {

    static CommandProvider comProvider;

    public static void setUp(CommandProvider comProvider) {
        BurningFX.comProvider = comProvider;

        Injector.setInstanceSupplier((Class<?> t) -> {

            try {
                if (t.getSuperclass().equals(Mediator.class)) {
                    return t.getConstructor(CommandProvider.class).newInstance(comProvider);
                }

                return t.newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(BurningFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        });

    }

}
