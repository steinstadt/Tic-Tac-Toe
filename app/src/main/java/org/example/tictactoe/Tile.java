package org.example.tictactoe;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;

public class Tile {

    public enum Owner{
        X, O, NEITHER, BOTH
    }

    // これらのレベルは、Drawableの定義で指定されている
    private static final int LEVEL_X = 0;
    private static final int LEVEL_O = 1;
    private static final int LEVEL_BLANK = 2;
    private static final int LEVEL_AVAILABLE = 3;
    private static final int LEVEL_TIE = 3;

    private final GameFragment mGame;
    private Owner mOwner = Owner.NEITHER;
    private View mView;
    private Tile mSubTiles[];

    public Tile(GameFragment game){
        this.mGame = game;
    }

    public View getView(){
        return mView;
    }

    public void setView(View view){
        this.mView = view;
    }

    public Owner getOwner(){
        return mOwner;
    }

    public void setOwner(Owner owner){
        this.mOwner = owner;
    }

    public Tile[] getSubTiles(){
        return mSubTiles;
    }

    public void setSubTiles(Tile[] subTiles){
        this.mSubTiles = subTiles;
    }

    // Drawableの状態を管理するコード
    public void updateDrawableState(){
        if (mView == null){
            return;
        }
        int level = getLevel();
        if (mView.getBackground() != null){
            mView.getBackground().setLevel(level);
        }
        if (mView instanceof ImageButton){
            Drawable drawable = ((ImageButton) mView).getDrawable();
            drawable.setLevel(level);
        }
    }

    private int getLevel(){
        int level = LEVEL_BLANK;
        switch (mOwner){
            case X:
                level = LEVEL_X;
                break;
            case O:
                level = LEVEL_O;
                break;
            case BOTH:
                level = LEVEL_TIE;
                break;
            case NEITHER:
                level = mGame.isAvailable(this) ? LEVEL_AVAILABLE : LEVEL_BLANK;
                break;
        }
        return level;
    }

    public Owner findWinner(){
        // オーナーがすでにわかっている場合はそれを返す
        if (getOwner() != Owner.NEITHER){
            return getOwner();
        }

        int totalX[] = new int[4];
        int totalO[] = new int[4];
        countCaptures(totalX, totalO);
        if (totalX[3] > 0) return Owner.X;
        if (totalO[3] > 0) return Owner.O;

        // 描画の状態をチェックする
        // 引き分け
        int total = 0;
        for (int row = 0; row < 3; row++){
            for (int col = 0; col < 3; col++){
                Owner owner = mSubTiles[3 * row + col].getOwner();
                if (owner != Owner.NEITHER){
                    total++;
                }
            }
            if (total == 9) return Owner.BOTH;
        }
        // まだどちらもこのマス目のオーナーになっていない
        return Owner.NEITHER;
    }


}
