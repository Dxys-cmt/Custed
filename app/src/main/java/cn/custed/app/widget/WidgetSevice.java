package cn.custed.app.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by dxys on 17/4/8.
 */

public class WidgetSevice extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Log.e("widgetSevice","this1");
        return new WidgetFactory(this.getApplicationContext(),intent);
    }

}
