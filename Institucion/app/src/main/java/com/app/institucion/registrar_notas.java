package com.app.institucion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

// HU-08
public class registrar_notas extends AppCompatActivity {

    TextView tvCurso;
    Spinner spAlumno;
    EditText edEvaluacion, edNota;
    Button btnGuardar;
    ListView lst1;
    Asignacion asignacion;

    ArrayList<Matricula> alumnos = new ArrayList<>();
    ArrayAdapter<Matricula> adapterAlumnos;
    ArrayList<String> notas_ = new ArrayList<String>();
    ArrayList<Nota> notas = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_notas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvCurso = (TextView) findViewById(R.id.tvCurso);
        spAlumno = (Spinner) findViewById(R.id.spAlumno);
        edEvaluacion = (EditText) findViewById(R.id.edEvaluacion);
        edNota = (EditText) findViewById(R.id.edNota);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        lst1 = (ListView) findViewById(R.id.lst1);

        Intent i = getIntent();
        asignacion = (Asignacion) i.getSerializableExtra("asignacion");
        tvCurso.setText(asignacion.toString());

        adapterAlumnos = new ArrayAdapter<>(this, R.layout.item_spinner, alumnos);
        adapterAlumnos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlumno.setAdapter(adapterAlumnos);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, notas_);
        lst1.setAdapter(arrayAdapter);

        cargarAlumnos();
        ListarDatos();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (notas.isEmpty()) return;
                final Nota n = notas.get(position);
                String[] opciones = {"Editar nota", "Eliminar nota"};
                new AlertDialog.Builder(registrar_notas.this)
                        .setTitle(n.getAlumno() + " - " + n.getEvaluacion())
                        .setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) cargarEnFormulario(n);
                                else Eliminar(n.getId());
                            }
                        })
                        .show();
            }
        });
    }
    private void cargarEnFormulario(Nota n) {
        for (int i = 0; i < alumnos.size(); i++) {
            if (alumnos.get(i).getIdAlumno().equals(n.getIdAlumno())) spAlumno.setSelection(i);
        }
        edEvaluacion.setText(n.getEvaluacion());
        edNota.setText(n.getNota());
        edNota.requestFocus();
    }
    public void cargarAlumnos() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_matriculas.php",
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
                                    alumnos.add(new Matricula(
                                            object.getString("id"),
                                            object.getString("id_alumno"),
                                            object.getString("alumno"),
                                            object.getString("usuario"),
                                            object.getString("grado"),
                                            object.getString("seccion"),
                                            object.getString("periodo")));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (alumnos.isEmpty()) {
                            Toast.makeText(registrar_notas.this, "No hay alumnos en esta sección", Toast.LENGTH_SHORT).show();
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
                params.put("grado", asignacion.getGrado());
                params.put("seccion", asignacion.getSeccion());
                params.put("periodo", asignacion.getPeriodo());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(registrar_notas.this);
        requestQueue.add(request);
    }

    public void guardar() {
        if (spAlumno.getSelectedItem() == null) {
            Toast.makeText(this, "No hay alumnos en esta sección", Toast.LENGTH_SHORT).show();
            return;
        }
        final Matricula alumno = (Matricula) spAlumno.getSelectedItem();
        final String evaluacion = edEvaluacion.getText().toString().trim();
        final String nota = edNota.getText().toString().trim();

        if (evaluacion.isEmpty()) {
            Toast.makeText(this, "Ingrese la evaluación (ej. Práctica 1)", Toast.LENGTH_SHORT).show();
            return;
        }
        double valor;
        try {
            valor = Double.parseDouble(nota);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese una nota válida", Toast.LENGTH_SHORT).show();
            return;
        }
        if (valor < 0 || valor > 20) {
            Toast.makeText(this, "La nota debe estar entre 0 y 20", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "guardar_nota.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(registrar_notas.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("exito").equals("1")) {
                                edNota.setText("");
                                ListarDatos();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(registrar_notas.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
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
                params.put("id_asignacion", asignacion.getId());
                params.put("id_alumno", alumno.getIdAlumno());
                params.put("evaluacion", evaluacion);
                params.put("nota", nota);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(registrar_notas.this);
        requestQueue.add(request);
    }

    public void ListarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_notas.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        notas_.clear();
                        notas.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Nota n = new Nota(
                                            object.getString("id"),
                                            object.getString("id_asignacion"),
                                            object.getString("curso"),
                                            object.getString("id_alumno"),
                                            object.getString("alumno"),
                                            object.getString("evaluacion"),
                                            object.getString("nota"),
                                            object.getString("fecha"));
                                    notas.add(n);
                                    notas_.add(n.getAlumno() + "\n" + n.getEvaluacion() + ":  " + n.getNota());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (notas.isEmpty()) notas_.add("Aún no hay notas registradas");
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
                params.put("id_asignacion", asignacion.getId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(registrar_notas.this);
        requestQueue.add(request);
    }

    public void Eliminar(final String id) {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "eliminar_nota.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                Toast.makeText(registrar_notas.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                                ListarDatos();
                            } else {
                                Toast.makeText(registrar_notas.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(registrar_notas.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(registrar_notas.this);
        requestQueue.add(request);
    }
}
