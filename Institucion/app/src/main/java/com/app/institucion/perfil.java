package com.app.institucion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

// HU-09
public class perfil extends AppCompatActivity {

    TextView tvNombre, tvDatos;
    EditText edCorreo, edTelefono, edClave;
    Button btnActualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvDatos = (TextView) findViewById(R.id.tvDatos);
        edCorreo = (EditText) findViewById(R.id.edCorreo);
        edTelefono = (EditText) findViewById(R.id.edTelefono);
        edClave = (EditText) findViewById(R.id.edClave);
        btnActualizar = (Button) findViewById(R.id.btnActualizar);
        Usuario u = Sesion.usuario;
        tvNombre.setText(u.getNombres() + " " + u.getApellidos());
        tvDatos.setText("Usuario: " + u.getUsuario() + "\nRol: " + u.getRol());
        edCorreo.setText(u.getCorreo());
        edTelefono.setText(u.getTelefono());

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });
    }

    public void Update() {
        final String correo = edCorreo.getText().toString().trim();
        final String telefono = edTelefono.getText().toString().trim();
        final String clave = edClave.getText().toString();

        if (!correo.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!clave.isEmpty() && clave.length() < 6) {
            Toast.makeText(this, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "actualizar_perfil.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(perfil.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("exito").equals("1")) {
                                Sesion.usuario.setCorreo(correo);
                                Sesion.usuario.setTelefono(telefono);
                                edClave.setText("");
                            }
                        } catch (JSONException e) {
                            Toast.makeText(perfil.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
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
                params.put("correo", correo);
                params.put("telefono", telefono);
                params.put("clave", clave);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(perfil.this);
        requestQueue.add(request);
    }
}
