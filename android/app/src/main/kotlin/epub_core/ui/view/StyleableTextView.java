package epub_core.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.epub_demo.R;
import epub_core.util.UiUtil;

public class StyleableTextView extends AppCompatTextView {

    public StyleableTextView(Context context) {
        super(context);
    }

    public StyleableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        UiUtil.setCustomFont(this, context, attrs,
                R.styleable.StyleableTextView,
                R.styleable.StyleableTextView_folio_font);
    }

    public StyleableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        UiUtil.setCustomFont(this, context, attrs,
                R.styleable.StyleableTextView,
                R.styleable.StyleableTextView_folio_font);
    }

}
