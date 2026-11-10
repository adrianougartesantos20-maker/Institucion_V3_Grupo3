package com.app.institucion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
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

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// HU-05
public class registrar_asistencia extends AppCompatActivity {

    Spinner spEstado;
    Button btnEscanear, btnManual;
    EditText edCodigo;
    TextView tvResultado;
    ActivityResultLauncher<ScanOptions> lectorQR = registerForActivityResult(new ScanContract(),
            new ActivityResultCallback<ScanIntentResult>() {
                @Override
                public void onActivityResult(ScanIntentResult result) {
                    if (result.getContents() == null) {
                        Toast.makeText(registrar_asistencia.this, "Lectura cancelada", Toast.LENGTH_SHORT).show();
                    } else {
                        registrar(result.getContents().trim());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_asistencia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spEstado = (Spinner) findViewById(R.id.spEstado);
        btnEscanear = (Button) findViewById(R.id.btnEscanear);
        btnManual = (Button) findViewById(R.id.btnManual);
        edCodigo = (EditText) findViewById(R.id.edCodigo);
        tvResultado = (TextView) findViewById(R.id.tvResultado);

        btnEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt(getString(R.string.prompt_qr));
                options.setBeepEnabled(true);
                options.setOrientationLocked(false);
                lectorQR.launch(options);
            }
        });

        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = edCodigo.getText().toString().trim();
                if (codigo.isEmpty()) {
                    Toast.makeText(registrar_asistencia.this, "Ingrese el código del alumno", Toast.LENGTH_SHORT).show();
                } else {
                    registrar(codigo);
                }
            }
        });
    }

    public void registrar(final String codigo) {
        final String estado = spEstado.getSelectedItem().toString();
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "registrar_asistencia.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String mensaje = jsonObject.getString("mensaje");
                            tvResultado.setText(mensaje);
                            Toast.makeText(registrar_asistencia.this, mensaje, Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("exito").equals("1")) edCodigo.setText("");
                        } catch (JSONException e) {
                            Toast.makeText(registrar_asistencia.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
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
                params.put("codigo", codigo);
                params.put("estado", estado);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(registrar_asistencia.this);
        requestQueue.add(request);
    }
}
