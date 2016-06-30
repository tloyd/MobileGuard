package cc.springwind.mobileguard.override;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by HeFan on 2016/6/25 0025.
 */
public class FocusTextView extends TextView{
    public FocusTextView(Context context) {
        super(context);
    }

    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}
