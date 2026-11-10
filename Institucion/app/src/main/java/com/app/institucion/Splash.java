package com.app.institucion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        rotarImagen();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarIniciar();
            }
        }, 3000);//3 segundos
    }

    private void rotarImagen() {
        image = findViewById(R.id.imageView);
        RotateAnimation animation = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(3000); // Tiempo en rotar imagen
        image.startAnimation(animation);
    }

    private void mostrarIniciar() {
        Intent intent = new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        finish(); // Para cerrar este activity
    }
}
