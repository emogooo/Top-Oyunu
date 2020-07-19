package com.example.finalodevideneme1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import java.util.Random;

public class EmirhanAydin_Activity extends AppCompatActivity implements SensorEventListener {
    private Random random = new Random();
    private int topX, topY;
    private int topYaricap = 27;
    private EmirhanAydin_View canvas;
    private SensorManager sM;
    private Sensor emoSensor;
    private int sensorX, sensorY;
    private long lastSensorUpdateTime;
    private int oyunHizi = 2;
    private int width;
    private int height;
    private int kenarKontrolX;
    private int kenarKontrolY;
    private int[] puanToplariX;
    private int[] puanToplariY;
    private int puanToplariYaricap = 27;
    private int oyunPuani;
    private int eksiTopKontrol;
    private boolean dur;
    private int artiSayaci;
    private boolean[] artilar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        emoSensor = sM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sM.registerListener(this, emoSensor, SensorManager.SENSOR_DELAY_GAME);
        lastSensorUpdateTime = 0;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        topX = width / 2 - topYaricap;
        topY = height / 2 - topYaricap;

        puanToplariX = new int[15];
        puanToplariY = new int[15];
        for (int i = 0; i < 15; i++) {
            puanToplariX[i] = random.nextInt(width - 350) + 100;
            puanToplariY[i] = random.nextInt(height - 350) + 100;
        }

        eksiTopKontrol = -1;
        dur = true;
        artiSayaci = 0;
        artilar = new boolean[5];
        for (int i = 0; i < 5; i++) {
            artilar[i] = true;
        }

        oyunPuani = 10;

        canvas = new EmirhanAydin_View(EmirhanAydin_Activity.this);
        setContentView(canvas);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (dur) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && (System.currentTimeMillis() - lastSensorUpdateTime) > 100) {
                lastSensorUpdateTime = System.currentTimeMillis();
                sensorX = (int) event.values[0];
                sensorY = (int) event.values[1];
            }

            kenarDegmeVeSensorIleHareketKontrol();
            nesnelereCarpismaKontrol();
            canvas.invalidate();
        }
    }

    private void kenarDegmeVeSensorIleHareketKontrol() {
        kenarKontrolX = 0;
        kenarKontrolY = 0;

        if ((topX + topYaricap) > width) {
            kenarKontrolX = 2;
        } else if ((topX - topYaricap) < 0) {
            kenarKontrolX = 1;
        }
        if ((topY + topYaricap) > height - 210) {
            kenarKontrolY = 4;
        } else if ((topY - topYaricap) < 0) {
            kenarKontrolY = 3;
        }

        if (kenarKontrolX != 1 && sensorX > 0) {
            topX -= oyunHizi;
        } else if (kenarKontrolX != 2 && sensorX < 0) {
            topX += oyunHizi;
        }
        if (kenarKontrolY != 3 && sensorY < 0) {
            topY -= oyunHizi;
        } else if (kenarKontrolY != 4 && sensorY > 0) {
            topY += oyunHizi;
        }
    }

    private void nesnelereCarpismaKontrol() {
        for (int i = 0; i < 10; i++) {
            if (i != eksiTopKontrol && Math.sqrt((topX - puanToplariX[i]) * (topX - puanToplariX[i]) + (topY - puanToplariY[i]) * (topY - puanToplariY[i])) < (topYaricap + puanToplariYaricap)) {
                oyunPuani -= 2;
                eksiTopKontrol = i;
                if (oyunPuani <= 0) {
                    kaybettinizMesaji();
                    dur = false;
                }
            }
        }
        for (int i = 10; i < 15; i++) {
            if (artilar[i - 10] && Math.sqrt((topX - puanToplariX[i]) * (topX - puanToplariX[i]) + (topY - puanToplariY[i]) * (topY - puanToplariY[i])) < (topYaricap + puanToplariYaricap)) {
                oyunPuani++;
                artilar[i - 10] = false;
                artiSayaci++;
                if (oyunPuani == 50) {
                    kazandinizMesaji();
                    dur = false;
                }

                if (artiSayaci == 5) {
                    parkurSifirla();
                }
            }
        }
    }

    private void parkurSifirla() {
        artiSayaci = 0;
        for (int i = 0; i < 5; i++) {
            artilar[i] = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void kazandinizMesaji() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmirhanAydin_Activity.this);
        builder.setTitle("Oyunu kazandınız!");
        builder.setMessage("Yeni oyuna başlamak ister misiniz?");
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EmirhanAydin_Activity.this.finish();
            }
        });
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EmirhanAydin_Activity.this.recreate();
            }
        });
        builder.show();
    }

    private void kaybettinizMesaji() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmirhanAydin_Activity.this);
        builder.setTitle("Oyunu kaybettiniz.");
        builder.setMessage("Yeni oyuna başlamak ister misiniz?");
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EmirhanAydin_Activity.this.finish();
            }
        });

        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EmirhanAydin_Activity.this.recreate();
            }
        });
        builder.show();
    }

    private class EmirhanAydin_View extends View {
        private Paint tPaint, textPuanPaint;
        private Paint[] paints;

        public EmirhanAydin_View(Context con) {
            super(con);

            tPaint = new Paint();
            paints = new Paint[15];
            textPuanPaint = new Paint();

            for (int i = 0; i < 10; i++) {
                paints[i] = new Paint();
                paints[i].setColor(Color.BLACK);
            }
            for (int i = 10; i < 15; i++) {
                paints[i] = new Paint();
                paints[i].setColor(Color.rgb(255, 127, 0));
            }
            textPuanPaint.setColor(Color.BLUE);
            tPaint.setColor(Color.GREEN);

            textPuanPaint.setTextSize(50f);
        }

        @Override
        public void onDraw(Canvas can) {
            can.drawText("Puanınız: " + oyunPuani, 50, 50, textPuanPaint);
            can.drawCircle(topX, topY, topYaricap, tPaint);
            for (int i = 0; i < 15; i++) {
                can.drawCircle(puanToplariX[i], puanToplariY[i], puanToplariYaricap, paints[i]);
            }
        }
    }
}