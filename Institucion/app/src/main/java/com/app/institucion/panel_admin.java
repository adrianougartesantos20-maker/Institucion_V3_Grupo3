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

// HU-12
public class panel_admin extends AppCompatActivity {

    TextView tvBienvenida;
    Button btnUsuarios, btnRoles, btnSecciones, btnCursos, btnHorarios, btnReportes, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBienvenida = (TextView) findViewById(R.id.tvBienvenida);
        btnUsuarios = (Button) findViewById(R.id.btnUsuarios);
        btnRoles = (Button) findViewById(R.id.btnRoles);
        btnSecciones = (Button) findViewById(R.id.btnSecciones);
        btnCursos = (Button) findViewById(R.id.btnCursos);
        btnHorarios = (Button) findViewById(R.id.btnHorarios);
        btnReportes = (Button) findViewById(R.id.btnReportes);
        btnCerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);

        tvBienvenida.setText("Hola, " + Sesion.usuario.getNombres() + " (" + Sesion.usuario.getRol() + ")");

        btnUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), listar_usuarios.class));
            }
        });
        btnRoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), asignar_roles.class));
            }
        });
        btnSecciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), asignar_seccion.class));
            }
        });
        btnCursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), asignar_curso.class));
            }
        });
        btnHorarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), registrar_horario.class));
            }
        });
        btnReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), reportes.class));
            }
        });
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sesion.cerrar(panel_admin.this);
            }
        });
    }
}
