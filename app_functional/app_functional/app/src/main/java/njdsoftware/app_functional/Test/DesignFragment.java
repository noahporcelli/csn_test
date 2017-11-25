package njdsoftware.app_functional.Test;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import njdsoftware.app_functional.CustomWidgets.CustomFontTextView;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.ToolbarController;

/**
 * Created by Nathan on 12/08/2016.
 */
public class DesignFragment extends Fragment {
    View thisFragmentView;

    public DesignFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragmentView = inflater.inflate(R.layout.screens_notifications_challenge_complete_fragment, container, false);
        return thisFragmentView;
    }

    private void testpage1(){
        CustomFontTextView tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t1);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t2);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t3);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.BLACK);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t4);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.BLACK);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t5);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t6);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t7);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t8);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t9);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t10);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t11);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t12);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t13);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t14);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t15);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t16);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t17);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.WHITE);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t18);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.appBarText.setTextColor(Color.BLACK);
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t19);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.toolbar.invalidate();
            }
        });
        tv = (CustomFontTextView) thisFragmentView.findViewById(R.id.t20);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolbarController.toolbar.findViewById(R.id.tool_bar_top_row).setBackgroundColor(((TextView)v).getCurrentTextColor());
                ToolbarController.toolbar.invalidate();
            }
        });
    }

    private void testpage2(){

    }
}
