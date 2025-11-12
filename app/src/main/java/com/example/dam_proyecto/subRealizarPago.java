package com.example.dam_proyecto; // Ajusta el paquete

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class subRealizarPago extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_realizar_pago);

        // ⚠️ Configurar para que parezca un diálogo flotante
        // Si quieres que ocupe menos espacio que match_parent, usa un tema de diálogo en el Manifest.

        Button btnFinalizarPago = findViewById(R.id.btnFinalizarPago);
        ImageButton btnCerrar = findViewById(R.id.cerrar);

        // Lógica: Finalizar Pago -> Abrir SubRealizarPago2
        btnFinalizarPago.setOnClickListener(v -> {
            // Reemplaza .class con el nombre correcto si es diferente
            Intent intent = new Intent(subRealizarPago.this, subRealizarPago.class);
            startActivity(intent);
            // Opcional: Cerrar esta ventana para que la 2da quede directamente sobre JuntaActivity
            // finish();
        });

        // Lógica: Cerrar -> Volver a JuntaActivity
        btnCerrar.setOnClickListener(v -> finish());
    }
}