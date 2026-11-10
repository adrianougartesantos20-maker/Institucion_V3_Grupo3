package com.app.institucion;

import android.app.ProgressDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// HU-01
public class MainActivity extends AppCompatActivity {

    EditText edUsuario, edClave;
    Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edUsuario = (EditText) findViewById(R.id.edUsuario);
        edClave = (EditText) findViewById(R.id.edClave);
        btnIngresar = (Button) findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresar();
            }
        });
    }

    public void ingresar() {
        final String p_usuario = edUsuario.getText().toString().trim();
        final String p_clave = edClave.getText().toString();

        if (p_usuario.isEmpty()) {
            Toast.makeText(this, "Ingrese su usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        if (p_clave.isEmpty()) {
            Toast.makeText(this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Validando...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String exito = jsonObject.getString("exito");
                            String mensaje = jsonObject.getString("mensaje");
                            if (exito.equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                JSONObject object = jsonArray.getJSONObject(0);
                                Usuario u = new Usuario(
                                        object.getString("id"),
                                        object.getString("usuario"),
                                        object.getString("nombres"),
                                        object.getString("apellidos"),
                                        object.getString("correo"),
                                        object.getString("telefono"),
                                        object.getString("id_rol"),
                                        object.getString("rol"));
                                Sesion.usuario = u;
                                Sesion.token = jsonObject.getString("token");
                                Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                                abrirMenuSegunRol();
                            } else {
                                Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                                edClave.setText("");
                            }
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", p_usuario);
                params.put("clave", p_clave);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }
    private void abrirMenuSegunRol() {
        Intent i;
        if (Sesion.usuario.getRol().equals("Administrador")) {
            i = new Intent(getApplicationContext(), panel_admin.class);
        } else {
            i = new Intent(getApplicationContext(), menu_usuario.class);
        }
        startActivity(i);
        finish();
    }
}
