package com.app.institucion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

// HU-02
public class registrar_usuario extends AppCompatActivity {

    EditText edUsuario, edClave, edNombres, edApellidos, edCorreo, edTelefono;
    Spinner spRol;
    Button b1, b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edUsuario = (EditText) findViewById(R.id.edUsuario);
        edClave = (EditText) findViewById(R.id.edClave);
        edNombres = (EditText) findViewById(R.id.edNombres);
        edApellidos = (EditText) findViewById(R.id.edApellidos);
        edCorreo = (EditText) findViewById(R.id.edCorreo);
        edTelefono = (EditText) findViewById(R.id.edTelefono);
        spRol = (Spinner) findViewById(R.id.spRol);
        b1 = (Button) findViewById(R.id.bt1);
        b2 = (Button) findViewById(R.id.bt2);
        spRol.setSelection(3);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), listar_usuarios.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void insert() {
        final String p_usuario = edUsuario.getText().toString().trim();
        final String p_clave = edClave.getText().toString();
        final String p_nombres = edNombres.getText().toString().trim();
        final String p_apellidos = edApellidos.getText().toString().trim();
        final String p_correo = edCorreo.getText().toString().trim();
        final String p_telefono = edTelefono.getText().toString().trim();
        final String p_id_rol = String.valueOf(spRol.getSelectedItemPosition() + 1);

        if (p_usuario.isEmpty()) {
            Toast.makeText(this, "Ingrese el usuario (DNI)", Toast.LENGTH_SHORT).show();
        } else if (p_clave.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
        } else if (p_nombres.isEmpty()) {
            Toast.makeText(this, "Ingrese los nombres", Toast.LENGTH_SHORT).show();
        } else if (p_apellidos.isEmpty()) {
            Toast.makeText(this, "Ingrese los apellidos", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Conexion.URL + "insertar_usuario.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Toast.makeText(registrar_usuario.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                                if (jsonObject.getString("exito").equals("1")) {
                                    edUsuario.setText("");
                                    edClave.setText("");
                                    edNombres.setText("");
                                    edApellidos.setText("");
                                    edCorreo.setText("");
                                    edTelefono.setText("");
                                    edUsuario.requestFocus();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(registrar_usuario.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(registrar_usuario.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", Sesion.token);
                    params.put("usuario", p_usuario);
                    params.put("clave", p_clave);
                    params.put("nombres", p_nombres);
                    params.put("apellidos", p_apellidos);
                    params.put("correo", p_correo);
                    params.put("telefono", p_telefono);
                    params.put("id_rol", p_id_rol);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(registrar_usuario.this);
            requestQueue.add(request);
        }
    }
}
