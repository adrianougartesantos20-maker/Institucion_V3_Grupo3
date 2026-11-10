package com.app.institucion;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

// HU-05
public class mi_codigo_qr extends AppCompatActivity {

    TextView tvNombre, tvCodigo;
    ImageView imgQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mi_codigo_qr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvCodigo = (TextView) findViewById(R.id.tvCodigo);
        imgQR = (ImageView) findViewById(R.id.imgQR);

        String codigo = Sesion.usuario.getUsuario();
        tvNombre.setText(Sesion.usuario.getNombres() + " " + Sesion.usuario.getApellidos());
        tvCodigo.setText("Código: " + codigo);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(codigo, BarcodeFormat.QR_CODE, 600, 600);
            imgQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "No se pudo generar el código QR", Toast.LENGTH_SHORT).show();
        }
    }
}
