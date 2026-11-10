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

// HU-07
public class asignar_curso extends AppCompatActivity {

    Spinner spDocente, spCurso, spGrado, spSeccion;
    EditText edPeriodo;
    Button btnAsignar;
    ListView lst1;

    ArrayList<Usuario> docentes = new ArrayList<>();
    ArrayAdapter<Usuario> adapterDocentes;
    ArrayList<Curso> cursos = new ArrayList<>();
    ArrayAdapter<Curso> adapterCursos;
    ArrayList<String> asignaciones_ = new ArrayList<String>();
    ArrayList<Asignacion> asignaciones = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asignar_curso);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spDocente = (Spinner) findViewById(R.id.spDocente);
        spCurso = (Spinner) findViewById(R.id.spCurso);
        spGrado = (Spinner) findViewById(R.id.spGrado);
        spSeccion = (Spinner) findViewById(R.id.spSeccion);
        edPeriodo = (EditText) findViewById(R.id.edPeriodo);
        btnAsignar = (Button) findViewById(R.id.btnAsignar);
        lst1 = (ListView) findViewById(R.id.lst1);

        adapterDocentes = new ArrayAdapter<>(this, R.layout.item_spinner, docentes);
        adapterDocentes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDocente.setAdapter(adapterDocentes);

        adapterCursos = new ArrayAdapter<>(this, R.layout.item_spinner, cursos);
        adapterCursos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurso.setAdapter(adapterCursos);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, asignaciones_);
        lst1.setAdapter(arrayAdapter);

        cargarDocentes();
        cargarCursos();
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
                final Asignacion a = asignaciones.get(position);
                new AlertDialog.Builder(asignar_curso.this)
                        .setTitle("Quitar asignación")
                        .setMessage("¿Quitar " + a.toString() + " al docente " + a.getDocente() + "?\nTambién se borrarán sus notas y horarios.")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Quitar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Eliminar(a.getId());
                            }
                        })
                        .show();
            }
        });
    }

    public void cargarDocentes() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_usuarios.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        docentes.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    docentes.add(new Usuario(
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
                        adapterDocentes.notifyDataSetChanged();
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
                params.put("rol", "Docente");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(asignar_curso.this);
        requestQueue.add(request);
    }

    public void cargarCursos() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_cursos.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cursos.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    cursos.add(new Curso(object.getString("id"), object.getString("nombre")));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapterCursos.notifyDataSetChanged();
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
        RequestQueue requestQueue = Volley.newRequestQueue(asignar_curso.this);
        requestQueue.add(request);
    }

    public void asignar() {
        if (spDocente.getSelectedItem() == null) {
            Toast.makeText(this, "No hay docentes registrados", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spCurso.getSelectedItem() == null) {
            Toast.makeText(this, "No hay cursos registrados", Toast.LENGTH_SHORT).show();
            return;
        }
        final Usuario docente = (Usuario) spDocente.getSelectedItem();
        final Curso curso = (Curso) spCurso.getSelectedItem();
        final String grado = spGrado.getSelectedItem().toString();
        final String seccion = spSeccion.getSelectedItem().toString();
        final String periodo = edPeriodo.getText().toString().trim();
        if (periodo.isEmpty()) {
            Toast.makeText(this, "Ingrese el periodo", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "insertar_asignacion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(asignar_curso.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("exito").equals("1")) ListarDatos();
                        } catch (JSONException e) {
                            Toast.makeText(asignar_curso.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
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
                params.put("id_docente", docente.getId());
                params.put("id_curso", curso.getId());
                params.put("grado", grado);
                params.put("seccion", seccion);
                params.put("periodo", periodo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(asignar_curso.this);
        requestQueue.add(request);
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
                                    asignaciones_.add(a.toString() + "\nDocente: " + a.getDocente());
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
        RequestQueue requestQueue = Volley.newRequestQueue(asignar_curso.this);
        requestQueue.add(request);
    }

    public void Eliminar(final String id) {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "eliminar_asignacion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                Toast.makeText(asignar_curso.this, "Asignación eliminada", Toast.LENGTH_SHORT).show();
                                ListarDatos();
                            } else {
                                Toast.makeText(asignar_curso.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(asignar_curso.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(asignar_curso.this);
        requestQueue.add(request);
    }
}
