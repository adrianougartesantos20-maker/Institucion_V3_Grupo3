package com.app.institucion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

// HU-08
public class tareas_asignadas extends AppCompatActivity {

    ListView lst1;
    ArrayList<String> asignaciones_ = new ArrayList<String>();
    ArrayList<Asignacion> asignaciones = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tareas_asignadas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lst1 = (ListView) findViewById(R.id.lst1);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, asignaciones_);
        lst1.setAdapter(arrayAdapter);
        ListarDatos();

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (asignaciones.isEmpty()) return;
                Intent i = new Intent(getApplicationContext(), registrar_notas.class);
                i.putExtra("asignacion", asignaciones.get(position));
                startActivity(i);
            }
        });
    }

    public void ListarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_asignaciones.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        asignaciones_.clear();
                        asignaciones.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Asignacion a = new Asignacion(
                                            object.getString("id"),
                                            object.getString("id_docente"),
                                            object.getString("docente"),
                                            object.getString("id_curso"),
                                            object.getString("curso"),
                                            object.getString("grado"),
                                            object.getString("seccion"),
                                            object.getString("periodo"));
                                    asignaciones.add(a);
                                    asignaciones_.add(a.getCurso() + "\n" + a.getGrado() + " " + a.getSeccion() + " - Periodo " + a.getPeriodo());
                                }
                            } else {
                                Toast.makeText(tareas_asignadas.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (asignaciones.isEmpty()) asignaciones_.add("Aún no tiene cursos asignados");
                        arrayAdapter.notifyDataSetChanged();
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(tareas_asignadas.this);
        requestQueue.add(request);
    }
}
