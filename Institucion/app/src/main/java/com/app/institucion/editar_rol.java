package com.app.institucion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// HU-03
public class editar_rol extends AppCompatActivity {

    TextView tvNombre, tvRolActual;
    Spinner spRol;
    Button b1, b3;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_rol);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvRolActual = (TextView) findViewById(R.id.tvRolActual);
        spRol = (Spinner) findViewById(R.id.spRol);
        b1 = (Button) findViewById(R.id.bt1);
        b3 = (Button) findViewById(R.id.bt3);

        Intent i = getIntent();
        usuario = (Usuario) i.getSerializableExtra("usuario");
        tvNombre.setText(usuario.getNombres() + " " + usuario.getApellidos());
        tvRolActual.setText("Rol actual: " + usuario.getRol());
        spRol.setSelection(Integer.parseInt(usuario.getIdRol()) - 1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarRol();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void guardarRol() {
        final String id_rol = String.valueOf(spRol.getSelectedItemPosition() + 1);
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "asignar_rol.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(editar_rol.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("exito").equals("1")) finish();
                        } catch (JSONException e) {
                            Toast.makeText(editar_rol.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", Sesion.token);
                params.put("id_usuario", usuario.getId());
                params.put("id_rol", id_rol);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(editar_rol.this);
        requestQueue.add(request);
    }
}
