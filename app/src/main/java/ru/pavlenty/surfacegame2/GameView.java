package ru.pavlenty.surfacegame2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private ArrayList<Star> stars = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();

    int screenX;
    int countMisses;

    int flag = 0;

    private boolean isGameOver;

    int score;

    int highScore[] = new int[4];


    SharedPreferences sharedPreferences;

    static MediaPlayer gameOnsound;
    final MediaPlayer killedEnemysound;
    final MediaPlayer gameOversound;

    Context context;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        player = new Player(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();

        int starNums = 200;
        for (int i = 0; i < starNums; i++) {
            Star s = new Star(screenX, screenY);
            stars.add(s);
        }

        this.screenX = screenX;
        countMisses = 0;
        isGameOver = false;

        score = 0;
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME", Context.MODE_PRIVATE);

        highScore[0] = sharedPreferences.getInt("score1", 0);
        highScore[1] = sharedPreferences.getInt("score2", 0);
        highScore[2] = sharedPreferences.getInt("score3", 0);
        highScore[3] = sharedPreferences.getInt("score4", 0);
        this.context = context;

        gameOnsound = MediaPlayer.create(context,R.raw.gameon);
        killedEnemysound = MediaPlayer.create(context,R.raw.killedenemy);
        gameOversound = MediaPlayer.create(context,R.raw.gameover);

        gameOnsound.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int cnt = motionEvent.getPointerCount();
        Log.e ("kek", ""+cnt);

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                cnt = 0;
                player.stopBoosting();
                flag = 0;
        }
        int flag_to_shoot = 0;
        int flag_to_move = 0;
        for (int i = 0; i < cnt; ++i) {
            float t = motionEvent.getX (i);
            if (t > (float)screenX / 2) ++flag_to_move;
            else ++flag_to_shoot;
        }
        if (flag_to_shoot == 0) flag = 0;
        else ++flag;
        if (flag_to_move == 0) player.stopBoosting();
        else player.setBoosting();

        if(isGameOver){
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                context.startActivity(new Intent(context,MainActivity.class));
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (playing) {
//            if (flag) Log.e("kek", "run");
            update();
//            if (flag) Log.e("kek", "upd");
            draw();
//            if (flag) Log.e("kek", "draw");
//            control();
        }
    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.WHITE);
            paint.setTextSize(20);

            for (Star s : stars) {
                paint.setStrokeWidth(s.getStarWidth());
                canvas.drawPoint(s.getX(), s.getY(), paint);
            }

            paint.setTextSize(30);
            canvas.drawText("Очки: "+score,100,50,paint);

            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);
            if (flag > 0) ++flag;
            if (flag > 0 && flag % 3 == 0) {
                bullets.add (new Bullet(this.context, player.getX() + 320, player.getY() + 50, player.getSpeed(), screenX));
            }

            if (!bullets.isEmpty())
                for (Bullet b: bullets) {
                    b.setSpeed(player.getSpeed() + 20);
                    canvas.drawBitmap(
                            b.getBitmap(),
                            b.getX(),
                            b.getY(),
                            paint);
                }

            if(isGameOver){
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);

                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("Конец игры",canvas.getWidth()/2,yPos,paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }


    public static void stopMusic(){
        gameOnsound.stop();
    }

    private void update() {
        score++;
        player.update();

        for (Star s : stars) {
            s.update(player.getSpeed());
        }

        for (int i = 0; i < bullets.size(); ++i) {
            int f = bullets.get(i).update();
            if (f == 1) {
                bullets.remove(i);
                --i;
            }
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e ("oops", "lol");
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


}