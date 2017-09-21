package com.teachwithapps.weconomyexperience.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.Constants;
import com.teachwithapps.weconomyexperience.R;
import com.teachwithapps.weconomyexperience.firebase.FireData;
import com.teachwithapps.weconomyexperience.util.Log;

import org.parceler.Parcel;

/**
 * Created by mint on 9-8-17.
 */
@IgnoreExtraProperties
@Parcel
public class PlayerData extends FireData {

    @Exclude
    private static final String TAG = PlayerData.class.getName();

    @Exclude
    private Bitmap playerBitmap;

    private String name;
    private String photoUrl;
    private String color;

    public PlayerData() {
    }

    public PlayerData(String uuid, String name, String color, String photoUrl) {
        this.setId("player_id_" + uuid);
        this.name = name;
        this.color = color;
        this.photoUrl = photoUrl;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("photo_url")
    public String getPhotoUrl() {
        return photoUrl;
    }

    @PropertyName("photo_url")
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @PropertyName("color")
    public void setColor(String color) {
        this.color = color;
    }

    @PropertyName("color")
    public String getColor() {
        return color;
    }

    @Exclude
    public Bitmap getBitmap() {
        if(playerBitmap != null) {
            return playerBitmap;

        } else {
            if (name == null) {
                name = "Visitor #" + getId().substring(0, 5);
            }

            String color = getColor();
            if (color == null) {
                color = Constants.getRandomColor();
            }

            if(color.charAt(0) != '#') {
                color = "#" + color;
            }

            Log.d(TAG, color);

            Bitmap bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.parseColor(color));

            Paint textPaint = new TextPaint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(64);
            textPaint.setFakeBoldText(true);

            float[] textWidths = new float[1];
            textPaint.getTextWidths(name, 0, 1, textWidths);

            int xPos = (int)(canvas.getWidth() / 2 - (textWidths[0] * .5f));
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
            canvas.drawText("" + name.charAt(0), xPos, yPos, textPaint);
            playerBitmap = bitmap;
            return playerBitmap;
        }
    }
}
