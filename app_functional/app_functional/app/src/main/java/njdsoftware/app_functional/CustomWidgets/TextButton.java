package njdsoftware.app_functional.CustomWidgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Nathan on 28/07/2016.
 */
public class TextButton extends AppCompatButton {
    public TextButton(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),
                "fonts/RussoOne-Regular.ttf"));
    }
}
