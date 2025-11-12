package com.example.dam_proyecto; // Ajusta el paquete

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class subRealizarPago2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_realizar_pago2);

        Button btnSubirEvidencias = findViewById(R.id.btnSubirEvidencias);
        ImageButton btnCerrar = findViewById(R.id.cerrar);

        // Lógica: Subir Evidencias -> Finaliza la secuencia de pago
        btnSubirEvidencias.setOnClickListener(v -> {
            Toast.makeText(this, "Evidencias enviadas. ¡Pago Registrado!", Toast.LENGTH_SHORT).show();

            // Cierra esta ventana y regresa a JuntaActivity
            finish();
        });

        // Lógica: Cerrar -> Regresa a la ventana anterior (SubRealizarPagoActivity, si no la cerraste antes)
        btnCerrar.setOnClickListener(v -> finish());
    }
}