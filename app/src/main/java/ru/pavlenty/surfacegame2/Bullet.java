package ru.pavlenty.surfacegame2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bullet {
    private int x;
    private int y;
    private int maxX;
    private int speed;
    private Bitmap bitmap;

    Bullet (Context context, int X, int Y, int playerSpeed, int mxX) {
        x = X;
        y = Y;
        speed = playerSpeed + 10;
        maxX = mxX;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);
        bitmap.setWidth(10);
    }

    int update () {
        x += speed;
        if (x > maxX) {
            return 1;
        }
        return 0;
    }

    int getX () {
        return x;
    }

    int getY () {
        return y;
    }

    Bitmap getBitmap () {
        return bitmap;
    }

}
