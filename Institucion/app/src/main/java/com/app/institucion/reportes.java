package com.app.institucion;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// HU-14
public class reportes extends AppCompatActivity {

    Spinner spTipo;
    Button btnGenerar;
    TextView tvFecha;
    ListView lst1;
    ArrayList<String> lineas = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reportes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spTipo = (Spinner) findViewById(R.id.spTipo);
        btnGenerar = (Button) findViewById(R.id.btnGenerar);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        lst1 = (ListView) findViewById(R.id.lst1);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, lineas);
        lst1.setAdapter(arrayAdapter);

        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generar();
            }
        });
    }

    public void generar() {
        final String tipo = String.valueOf(spTipo.getSelectedItemPosition() + 1);
        final String titulo = spTipo.getSelectedItem().toString();

        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "reportes.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lineas.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    lineas.add(object.getString("descripcion") + ":   " + object.getString("valor"));
                                }
                                if (lineas.isEmpty()) lineas.add("No hay información para este reporte");
                                String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
                                tvFecha.setText(titulo + " - generado el " + fecha);
                            } else {
                                Toast.makeText(reportes.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(reportes.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
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
                params.put("tipo", tipo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(reportes.this);
        requestQueue.add(request);
    }
}
