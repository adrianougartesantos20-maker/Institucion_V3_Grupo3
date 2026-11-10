package com.app.institucion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// HU-04
public class asignar_seccion extends AppCompatActivity {

    Spinner spAlumno, spGrado, spSeccion;
    EditText edPeriodo;
    Button btnAsignar;
    ListView lst1;

    ArrayList<Usuario> alumnos = new ArrayList<>();
    ArrayAdapter<Usuario> adapterAlumnos;
    ArrayList<String> matriculas_ = new ArrayList<String>();
    ArrayList<Matricula> matriculas = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asignar_seccion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spAlumno = (Spinner) findViewById(R.id.spAlumno);
        spGrado = (Spinner) findViewById(R.id.spGrado);
        spSeccion = (Spinner) findViewById(R.id.spSeccion);
        edPeriodo = (EditText) findViewById(R.id.edPeriodo);
        btnAsignar = (Button) findViewById(R.id.btnAsignar);
        lst1 = (ListView) findViewById(R.id.lst1);

        adapterAlumnos = new ArrayAdapter<>(this, R.layout.item_spinner, alumnos);
        adapterAlumnos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlumno.setAdapter(adapterAlumnos);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, matriculas_);
        lst1.setAdapter(arrayAdapter);

        cargarAlumnos();
        ListarDatos();

        btnAsignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignar();
            }
        });

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Matricula m = matriculas.get(position);
                new AlertDialog.Builder(asignar_seccion.this)
                        .setTitle("Quitar de la sección")
                        .setMessage("¿Quitar a " + m.getAlumno() + " de " + m.getGrado() + " " + m.getSeccion() + "?")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Quitar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Eliminar(m.getId());
                            }
                        })
                        .show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(asignar_seccion.this);
        requestQueue.add(request);
    }

    public void asignar() {
        if (spAlumno.getSelectedItem() == null) {
            Toast.makeText(this, "No hay alumnos registrados", Toast.LENGTH_SHORT).show();
            return;
        }
        final Usuario alumno = (Usuario) spAlumno.getSelectedItem();
        final String grado = spGrado.getSelectedItem().toString();
        final String seccion = spSeccion.getSelectedItem().toString();
        final String periodo = edPeriodo.getText().toString().trim();
        if (periodo.isEmpty()) {
            Toast.makeText(this, "Ingrese el periodo", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "insertar_matricula.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(asignar_seccion.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("exito").equals("1")) ListarDatos();
                        } catch (JSONException e) {
                            Toast.makeText(asignar_seccion.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
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
                params.put("id_alumno", alumno.getId());
                params.put("grado", grado);
                params.put("seccion", seccion);
                params.put("periodo", periodo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(asignar_seccion.this);
        requestQueue.add(request);
    }

    public void ListarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_matriculas.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        matriculas_.clear();
                        matriculas.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Matricula m = new Matricula(
                                            object.getString("id"),
                                            object.getString("id_alumno"),
                                            object.getString("alumno"),
                                            object.getString("usuario"),
                                            object.getString("grado"),
                                            object.getString("seccion"),
                                            object.getString("periodo"));
                                    matriculas.add(m);
                                    matriculas_.add(m.getGrado() + " " + m.getSeccion() + " (" + m.getPeriodo() + ")  -  " + m.getAlumno());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        RequestQueue requestQueue = Volley.newRequestQueue(asignar_seccion.this);
        requestQueue.add(request);
    }

    public void Eliminar(final String id) {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "eliminar_matricula.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                Toast.makeText(asignar_seccion.this, "Se quitó al alumno de la sección", Toast.LENGTH_SHORT).show();
                                ListarDatos();
                            } else {
                                Toast.makeText(asignar_seccion.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(asignar_seccion.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
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
                params.put("id", id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(asignar_seccion.this);
        requestQueue.add(request);
    }
}
