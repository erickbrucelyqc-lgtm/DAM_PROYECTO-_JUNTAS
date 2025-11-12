package com.example.dam_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class subUnirse extends AppCompatActivity {
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sub_unirse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    Button btncerrar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_unirse);

        // Vinculamos el botón con su ID del XML
        btncerrar2 = findViewById(R.id.btncerrar2);

        // Le añadimos un listener (acción al hacer clic)
        btncerrar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos el Intent para ir al otro layout -------------> (subUnirse a subUnirse2)
                Intent intent = new Intent(subUnirse.this, subUnirse2.class);
                startActivity(intent); // Inicia la nueva actividad
            }
        });
    }
}