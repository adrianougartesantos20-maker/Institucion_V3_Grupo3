package com.app.institucion;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// HU-06
public class historial_asistencia extends AppCompatActivity {

    Spinner spAlumno;
    TextView tvResumen;
    ListView lst1;

    ArrayList<Usuario> alumnos = new ArrayList<>();
    ArrayAdapter<Usuario> adapterAlumnos;
    ArrayList<String> asistencias_ = new ArrayList<String>();
    ArrayList<Asistencia> asistencias = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial_asistencia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spAlumno = (Spinner) findViewById(R.id.spAlumno);
        tvResumen = (TextView) findViewById(R.id.tvResumen);
        lst1 = (ListView) findViewById(R.id.lst1);

        adapterAlumnos = new ArrayAdapter<>(this, R.layout.item_spinner, alumnos);
        adapterAlumnos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlumno.setAdapter(adapterAlumnos);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, asistencias_);
        lst1.setAdapter(arrayAdapter);

        cargarAlumnos();
        spAlumno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ListarDatos(alumnos.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void cargarAlumnos() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_usuarios.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        alumnos.clear();
                        alumnos.add(new Usuario("0", "", "Todos los alumnos", "", "", "", "4", "Alumno"));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    alumnos.add(new Usuario(
                                            object.getString("id"),
                                            object.getString("usuario"),
                                            object.getString("nombres"),
                                            object.getString("apellidos"),
                                            object.getString("correo"),
                                            object.getString("telefono"),
                                            object.getString("id_rol"),
                                            object.getString("rol")));
                                }
                            } else {
                                Toast.makeText(historial_asistencia.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapterAlumnos.notifyDataSetChanged();
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
                params.put("rol", "Alumno");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(historial_asistencia.this);
        requestQueue.add(request);
    }

    public void ListarDatos(final String id_alumno) {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_asistencias.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        asistencias_.clear();
                        asistencias.clear();
                        int presentes = 0, tardanzas = 0, faltas = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Asistencia a = new Asistencia(
                                            object.getString("id"),
                                            object.getString("id_alumno"),
                                            object.getString("alumno"),
                                            object.getString("fecha"),
                                            object.getString("hora"),
                                            object.getString("estado"));
                                    asistencias.add(a);
                                    asistencias_.add(a.getFecha() + "  " + a.getHora() + "  -  " + a.getEstado() + "\n" + a.getAlumno());
                                    if (a.getEstado().equals("Presente")) presentes++;
                                    else if (a.getEstado().equals("Tardanza")) tardanzas++;
                                    else faltas++;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvResumen.setText("Presentes: " + presentes + "   Tardanzas: " + tardanzas + "   Faltas: " + faltas);
                        if (asistencias.isEmpty()) asistencias_.add("Sin registros de asistencia");
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
                params.put("id_alumno", id_alumno);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(historial_asistencia.this);
        requestQueue.add(request);
    }
}
