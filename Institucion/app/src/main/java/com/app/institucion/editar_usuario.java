package com.app.institucion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class editar_usuario extends AppCompatActivity {

    EditText edId, edUsuario, edNombres, edApellidos, edCorreo, edTelefono, edClave;
    Button b1, b2, b3;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edId = (EditText) findViewById(R.id.edId);
        edUsuario = (EditText) findViewById(R.id.edUsuario);
        edNombres = (EditText) findViewById(R.id.edNombres);
        edApellidos = (EditText) findViewById(R.id.edApellidos);
        edCorreo = (EditText) findViewById(R.id.edCorreo);
        edTelefono = (EditText) findViewById(R.id.edTelefono);
        edClave = (EditText) findViewById(R.id.edClave);
        b1 = (Button) findViewById(R.id.bt1);
        b2 = (Button) findViewById(R.id.bt2);
        b3 = (Button) findViewById(R.id.bt3);
        Intent i = getIntent();
        usuario = (Usuario) i.getSerializableExtra("usuario");
        edId.setText(usuario.getId());
        edUsuario.setText(usuario.getUsuario());
        edNombres.setText(usuario.getNombres());
        edApellidos.setText(usuario.getApellidos());
        edCorreo.setText(usuario.getCorreo());
        edTelefono.setText(usuario.getTelefono());

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarEliminar();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Eliminar a " + usuario.getNombres() + " " + usuario.getApellidos() + "?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Eliminar(edId.getText().toString());
                    }
                })
                .show();
    }

    public void Eliminar(final String id) {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "eliminar_usuario.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                Toast.makeText(editar_usuario.this, "Eliminó correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(editar_usuario.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(editar_usuario.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(editar_usuario.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", Sesion.token);
                params.put("id", id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(editar_usuario.this);
        requestQueue.add(request);
    }

    public void Update() {
        final String id = edId.getText().toString().trim();
        final String p_usuario = edUsuario.getText().toString().trim();
        final String nombres = edNombres.getText().toString().trim();
        final String apellidos = edApellidos.getText().toString().trim();
        final String correo = edCorreo.getText().toString().trim();
        final String telefono = edTelefono.getText().toString().trim();
        final String clave = edClave.getText().toString();

        if (p_usuario.isEmpty()) {
            Toast.makeText(this, "Ingrese el usuario", Toast.LENGTH_SHORT).show();
        } else if (nombres.isEmpty()) {
            Toast.makeText(this, "Ingrese los nombres", Toast.LENGTH_SHORT).show();
        } else if (apellidos.isEmpty()) {
            Toast.makeText(this, "Ingrese los apellidos", Toast.LENGTH_SHORT).show();
        } else if (!clave.isEmpty() && clave.length() < 6) {
            Toast.makeText(this, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Conexion.URL + "actualizar_usuario.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Toast.makeText(editar_usuario.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                                if (jsonObject.getString("exito").equals("1")) finish();
                            } catch (JSONException e) {
                                Toast.makeText(editar_usuario.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(editar_usuario.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", Sesion.token);
                    params.put("id", id);
                    params.put("usuario", p_usuario);
                    params.put("nombres", nombres);
                    params.put("apellidos", apellidos);
                    params.put("correo", correo);
                    params.put("telefono", telefono);
                    params.put("clave", clave);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(editar_usuario.this);
            requestQueue.add(request);
        }
    }
}
