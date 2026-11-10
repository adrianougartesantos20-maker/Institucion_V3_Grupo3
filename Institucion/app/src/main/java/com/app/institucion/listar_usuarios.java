package com.app.institucion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// HU-02
public class listar_usuarios extends AppCompatActivity {

    ImageButton boton_imagen;
    ListView lst1;
    ArrayList<String> usuarios_ = new ArrayList<String>();
    ArrayList<Usuario> usuarios = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar_usuarios);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boton_imagen = (ImageButton) findViewById(R.id.imageButton);
        lst1 = (ListView) findViewById(R.id.lst1);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, usuarios_);
        lst1.setAdapter(arrayAdapter);

        boton_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(listar_usuarios.this, "Registrar usuario", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), registrar_usuario.class));
            }
        });

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), editar_usuario.class);
                i.putExtra("usuario", usuarios.get(position));
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Sesion.usuario != null) ListarDatos();
    }

    public void ListarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_usuarios.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        usuarios_.clear();
                        usuarios.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String exito = jsonObject.getString("exito");
                            if (exito.equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Usuario u = new Usuario(
                                            object.getString("id"),
                                            object.getString("usuario"),
                                            object.getString("nombres"),
                                            object.getString("apellidos"),
                                            object.getString("correo"),
                                            object.getString("telefono"),
                                            object.getString("id_rol"),
                                            object.getString("rol"));
                                    usuarios.add(u);
                                    usuarios_.add(u.getApellidos() + ", " + u.getNombres()
                                            + "\n" + u.getRol() + " - " + u.getUsuario());
                                }
                            } else {
                                Toast.makeText(listar_usuarios.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(listar_usuarios.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", Sesion.token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(listar_usuarios.this);
        requestQueue.add(request);
    }
}
