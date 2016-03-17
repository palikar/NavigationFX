/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.Main;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author s_stanis
 */
public class MainView extends FXMLView {
    
    public MainPresenter getRealPresenter() {
        return (MainPresenter) super.getPresenter();
    }
    
   
    
}
