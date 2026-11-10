package com.app.institucion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menu_usuario extends AppCompatActivity {

    TextView tvBienvenida;
    Button btnAsistencia, btnHistorial, btnTareas, btnPerfil, btnMisNotas, btnMisHorarios, btnMiQR, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBienvenida = (TextView) findViewById(R.id.tvBienvenida);
        btnAsistencia = (Button) findViewById(R.id.btnAsistencia);
        btnHistorial = (Button) findViewById(R.id.btnHistorial);
        btnTareas = (Button) findViewById(R.id.btnTareas);
        btnPerfil = (Button) findViewById(R.id.btnPerfil);
        btnMisNotas = (Button) findViewById(R.id.btnMisNotas);
        btnMisHorarios = (Button) findViewById(R.id.btnMisHorarios);
        btnMiQR = (Button) findViewById(R.id.btnMiQR);
        btnCerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);

        String rol = Sesion.usuario.getRol();
        tvBienvenida.setText("Hola, " + Sesion.usuario.getNombres() + " (" + rol + ")");
        btnAsistencia.setVisibility(View.GONE);
        btnHistorial.setVisibility(View.GONE);
        btnTareas.setVisibility(View.GONE);
        btnPerfil.setVisibility(View.GONE);
        btnMisNotas.setVisibility(View.GONE);
        btnMisHorarios.setVisibility(View.GONE);
        btnMiQR.setVisibility(View.GONE);

        if (rol.equals("Auxiliar")) {
            btnAsistencia.setVisibility(View.VISIBLE);
            btnHistorial.setVisibility(View.VISIBLE);
        } else if (rol.equals("Docente")) {
            btnTareas.setVisibility(View.VISIBLE);
            btnPerfil.setVisibility(View.VISIBLE);
            btnMisHorarios.setVisibility(View.VISIBLE);
        } else if (rol.equals("Alumno")) {
            btnMisNotas.setVisibility(View.VISIBLE);
            btnMisHorarios.setVisibility(View.VISIBLE);
            btnMiQR.setVisibility(View.VISIBLE);
        }

        btnAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), registrar_asistencia.class));
            }
        });
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), historial_asistencia.class));
            }
        });
        btnTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), tareas_asignadas.class));
            }
        });
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), perfil.class));
            }
        });
        btnMisNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), mis_notas.class));
            }
        });
        btnMisHorarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), mis_horarios.class));
            }
        });
        btnMiQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), mi_codigo_qr.class));
            }
        });
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sesion.cerrar(menu_usuario.this);
            }
        });
    }
}
