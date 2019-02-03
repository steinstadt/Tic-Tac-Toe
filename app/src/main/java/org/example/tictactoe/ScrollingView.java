package org.example.tictactoe;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 無限にスクロールする背景画像を描くカスタムビュー
 */

public class ScrollingView extends View {
    private Drawable mBackground;
    private int mScrollPos;

    public ScrollingView(Context context){
        this(context, null);
    }

    public ScrollingView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public ScrollingView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr){
        // カスタムビューの属性をロード
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ScrollingView, defStyleAttr, 0
        );
        // 背景を取得
        if (a.hasValue(R.styleable.ScrollingView_scrollingDrawable)){
            mBackground = a.getDrawable(R.styleable.ScrollingView_scrollingDrawable);
            mBackground.setCallback(this);
        }
        // ロードした属性を解放
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // ビューの大きさを取得(パティングは無視)
        int contentWidth = getWidth();
        int contentHeight = getHeight();

        // 背景を描画
        if (mBackground != null){
            // 背景をビューの大きさより大きくする
            int max = Math.max(mBackground.getIntrinsicHeight(),
                                mBackground.getIntrinsicWidth());
            mBackground.setBounds(0, 0, contentWidth*4, contentHeight*4);

            // 画像が表示される位置をずらす
            mScrollPos += 2;
            if (mScrollPos >= max) mScrollPos -= max;
            setTranslationX(-mScrollPos);
            setTranslationY(-mScrollPos);

            // 描画するとともに、次回も描画するように指示
            mBackground.draw(canvas);
            this.invalidate();
        }
    }
}
