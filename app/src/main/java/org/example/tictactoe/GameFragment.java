package org.example.tictactoe;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.HashSet;
import java.util.Set;

public class GameFragment extends Fragment {

    // データ構造が入る
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8, R.id.large9};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8, R.id.small9};
    private Tile mEntireBoard = new Tile(this);
    private Tile mLargeTiles[] = new Tile[9];
    private Tile mSmallTiles[][] = new Tile[9][9];
    private Tile.Owner mPlayer = Tile.Owner.X;
    private Set<Tile> mAvailable = new HashSet<Tile>();
    private int mLastLarge;
    private int mLastSmall;



    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        // コンフィギュレーションの変更を跨いでこのフラグメントを維持する
        setRetainInstance(true);
        initGame();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView =
                inflater.inflate(R.layout.large_board, container, false);
        initViews(rootView);
        updateAllTiles();
        return rootView;
    }

    // 全てのデータ構造を初期化する
    public void initGame(){
        Log.d("UT3", "init game");
        mEntireBoard = new Tile(this);
        // 全てのマス目を作る
        for (int large=0; large<9; large++){
            mLargeTiles[large] = new Tile(this);
            for(int small=0; small<9; small++){
                mSmallTiles[large][small] = new Tile(this);
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);

        // プレイヤーが先に指す場合、指せる場所を設定する
        mLastSmall = -1;
        mLastLarge = -1;
        setAvailableFromLastMove(mLastSmall);
    }

    // ビューの初期化
    private void initViews(View rootView){
        mEntireBoard.setView(rootView);
        for (int large=0; large<9; large++){
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);

            for (int small=0; small<9; small++){
                ImageButton inner = (ImageButton) outer.findViewById(mSmallIds[small]);
                final int fLarge = large;
                final int fSmall = small;
                final Tile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isAvailable(smallTile)){
                            makeMove(fLarge, fSmall);
                            switchTurns();
                        }
                    }
                });
            }
        }
    }

    // 指し手の処理
    private void makeMove(int large, int small){
        mLastLarge = large;
        mLastSmall = small;
        Tile smallTile = mSmallTiles[large][small];
        Tile largeTile = mLargeTiles[large];
        smallTile.setOwner(mPlayer);
        setAvailableFromLastMove(small);
        Tile.Owner oldWinner = largeTile.getOwner();
        Tile.Owner winner = largeTile.findWinner();
        if (winner != oldWinner){
            largeTile.setOwner(winner);
        }
        winner = mEntireBoard.findWinner();
        mEntireBoard.setOwner(winner);
        updateAllTiles();
        if (winner != Tile.Owner.NEITHER){
            ((GameActivity)getActivity()).reportWinner(winner);
        }
    }

    // プレイヤーの切り替え
    private void switchTurns(){
        mPlayer = mPlayer == Tile.Owner.X ? Tile.Owner.O : Tile.Owner.X;
    }

    // ゲームのリスタート
    public void restartGame(){
        initGame();
        initViews(getView());
        updateAllTiles();
    }

    // 有効な指し手の処理
    private void clearAvailable(){
        mAvailable.clear();
    }

    private void addAvailable(Tile tile){
        mAvailable.add(tile);
    }

    public boolean isAvailable(Tile tile){
        return mAvailable.contains(tile);
    }

    private void setAvailableFromLastMove(int small){
        clearAvailable();
        // 前の指し手に対応する盤の空いているマス目を有効にする
        if (small != -1){
            for (int dest = 0; dest < 9; dest++){
                Tile tile = mSmallTiles[small][dest];
                if (tile.getOwner() == Tile.Owner.NEITHER){
                    addAvailable(tile);
                }
            }
        }
        // 有効なマス目がない場合には、空いているマス目をすべて有効にする
        if (mAvailable.isEmpty()){
            setAllAvailable();
        }
    }

    private void setAllAvailable(){
        for (int large = 0; large < 9; large++){
            for (int small = 0; small < 9; small++){
                Tile tile = mSmallTiles[large][small];
                if (tile.getOwner() == Tile.Owner.NEITHER){
                    addAvailable(tile);
                }
            }
        }
    }

    /**
     * ゲームの状態を表す文字列を作る
     */
    public String getState(){
        StringBuffer builder = new StringBuffer();
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < 9; large++){
            for (int small = 0; small < 9; small++){
                builder.append(mSmallTiles[large][small].getOwner().name());
                builder.append(',');
            }
        }
        return builder.toString();
    }

    /**
     * 与えられた文字列からゲームの状態を復元する
     */
    public void putState(String gameData){
        String[] fields = gameData.split(",");
        int index = 0;
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++){
            for (int small = 0; small < 9; small++){
                Tile.Owner owner = Tile.Owner.valueOf(fields[index++]);
                mSmallTiles[large][small].setOwner(owner);
            }
        }
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
    }

    private void updateAllTiles(){
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++){
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++){
                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }
}
