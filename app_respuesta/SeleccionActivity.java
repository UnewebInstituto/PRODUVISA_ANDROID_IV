package com.produvisa.emergencias;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.produvisa.emergencias.models.Familiar;
import com.produvisa.emergencias.models.Trabajador;

public class SeleccionActivity extends AppCompatActivity {

    private Trabajador trabajador;
    private RadioGroup radioGroupSeleccion;
    // No necesitamos selectedId aquí, solo el ID del familiar si aplica
    private Integer selectedFamiliarId = null; // ID del Familiar (null si es el trabajador)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion);

        radioGroupSeleccion = findViewById(R.id.radioGroupSeleccion);
        Button buttonContinuar = findViewById(R.id.buttonEnviarSeleccion);

        // 1. Obtener datos del Intent
        String trabajadorJson = getIntent().getStringExtra("trabajador_json");
        if (trabajadorJson != null) {
            trabajador = new Gson().fromJson(trabajadorJson, Trabajador.class);
            mostrarOpciones();
        } else {
            Toast.makeText(this, "Error: Datos del trabajador no recibidos.", Toast.LENGTH_LONG).show();
            finish(); // Cierra la actividad si no hay datos
            return;
        }

        // 2. Listener para la selección de la persona
        radioGroupSeleccion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = findViewById(checkedId);
                // Si la persona seleccionada es el trabajador
                if (checkedRadioButton.getTag(R.id.tag_es_trabajador) != null) {
                    selectedFamiliarId = null;
                } else {
                    // Si es un familiar, obtener su ID del Tag
                    selectedFamiliarId = (Integer) checkedRadioButton.getTag(R.id.tag_familiar_id);
                }
            }
        });

        // 3. Botón Continuar
        buttonContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroupSeleccion.getCheckedRadioButtonId() != -1) {
                    abrirCuestionarioActivity();
                } else {
                    Toast.makeText(SeleccionActivity.this, "Por favor, seleccione una persona.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Muestra el trabajador y sus familiares como opciones de RadioButton.
     */
    private void mostrarOpciones() {
        // Opción 1: El trabajador (siempre el primero)
        RadioButton rbTrabajador = crearRadioButton(trabajador.getNombreCompleto());
        rbTrabajador.setId(View.generateViewId());
        // Usar un Tag para marcar que es el trabajador y facilitar el manejo del listener
        rbTrabajador.setTag(R.id.tag_es_trabajador, true);
        radioGroupSeleccion.addView(rbTrabajador);

        // Opciones 2+: Familiares
        if (trabajador.getFamiliares() != null) {
            for (Familiar familiar : trabajador.getFamiliares()) {
                RadioButton rbFamiliar = crearRadioButton(familiar.getNombreCompleto());
                rbFamiliar.setId(View.generateViewId());
                // Usar Tags para almacenar el ID del trabajador y el ID del familiar
                // El Tag con clave R.id.tag_familiar_id almacenará el ID que se enviará en el POST
                rbFamiliar.setTag(R.id.tag_familiar_id, familiar.getId());
                radioGroupSeleccion.addView(rbFamiliar);
            }
        }

        // Seleccionar por defecto al Trabajador (el primer elemento)
        if (radioGroupSeleccion.getChildCount() > 0) {
            ((RadioButton) radioGroupSeleccion.getChildAt(0)).setChecked(true);
            selectedFamiliarId = null; // Reiniciar
        }
    }

    /**
     * Crea un RadioButton con estilo uniforme.
     * @param texto El texto a mostrar.
     * @return El RadioButton creado.
     */
    private RadioButton crearRadioButton(String texto) {
        RadioButton rb = new RadioButton(this);
        rb.setText(texto);
        rb.setTextSize(18);
        // Usar LayoutParams para asegurar que ocupen todo el ancho
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rb.setLayoutParams(layoutParams);
        rb.setPadding(0, 15, 0, 15);
        return rb;
    }

    /**
     * Inicia la CuestionarioActivity.
     */
    private void abrirCuestionarioActivity() {
        Intent intent = new Intent(SeleccionActivity.this, CuestionarioActivity.class);
        intent.putExtra("trabajador_id", trabajador.getId());
        // Pasar el ID del familiar (null si es el trabajador)
        intent.putExtra("familiar_id", selectedFamiliarId != null ? selectedFamiliarId : 0);

        // Extra para mostrar el nombre de la persona seleccionada en la siguiente actividad
        RadioButton selectedRb = findViewById(radioGroupSeleccion.getCheckedRadioButtonId());
        String nombreSeleccionado = selectedRb != null ? selectedRb.getText().toString() : trabajador.getNombreCompleto(); // Fallback
        intent.putExtra("persona_seleccionada_nombre", nombreSeleccionado);

        startActivity(intent);
    }
}