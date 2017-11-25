package njdsoftware.app_functional.CustomWidgets;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import njdsoftware.app_functional.R;

/**
 * Created by Nathan on 10/08/2016.
 */
public class DropdownBox extends FrameLayout implements View.OnClickListener{
    public DropdownBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widgets_dropdown_box, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RelativeLayout thisItem = (RelativeLayout) this
                .findViewById(R.id.relativeLayout49);
        thisItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        PopupMenu popup = new PopupMenu(getContext(), this);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getContext(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.setGravity(GravityCompat.START);
        popup.show();//showing popup menu
    }
}
