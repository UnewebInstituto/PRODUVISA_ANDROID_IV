package com.produvisa.emergencias;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.produvisa.emergencias.models.Familiar;
import com.produvisa.emergencias.models.Trabajador;

public class SeleccionActivity extends AppCompatActivity {

    private Trabajador trabajador;
    private RadioGroup radioGroupSeleccion;
    private int selectedId = -1; // ID de la persona seleccionada (Trabajador o Familiar)
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
            Toast.makeText(this, "Error al cargar datos del trabajador.", Toast.LENGTH_LONG).show();
            finish();
        }

        // 2. Manejar la selección del RadioGroup
        radioGroupSeleccion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // El checkedId es el ID de la RadioButton (que seteamos como el ID del Trabajador/Familiar)
                RadioButton checkedRadioButton = findViewById(checkedId);
                selectedId = (int) checkedRadioButton.getTag(R.id.tag_trabajador_id);
                selectedFamiliarId = (Integer) checkedRadioButton.getTag(R.id.tag_familiar_id);
            }
        });

        // 3. Botón Continuar (pasa al cuestionario)
        buttonContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedId == -1) {
                    Toast.makeText(SeleccionActivity.this, "Debe seleccionar una persona.", Toast.LENGTH_SHORT).show();
                    return;
                }
                abrirCuestionarioActivity();
            }
        });
    }

    private void mostrarOpciones() {
        // ID para almacenar en el Tag de la RadioButton
        final int TRABAJADOR_TAG = trabajador.getId();

        // 1. Agregar RadioButton para el Trabajador
        RadioButton rbTrabajador = crearRadioButton(trabajador.getNombreCompleto());
        rbTrabajador.setTag(R.id.tag_trabajador_id, TRABAJADOR_TAG);
        rbTrabajador.setTag(R.id.tag_familiar_id, null); // Indicar que no es familiar
        radioGroupSeleccion.addView(rbTrabajador);

        // 2. Agregar RadioButtons para los Familiares
        if (trabajador.getFamiliares() != null) {
            for (Familiar familiar : trabajador.getFamiliares()) {
                RadioButton rbFamiliar = crearRadioButton(familiar.getNombreCompleto());
                rbFamiliar.setTag(R.id.tag_trabajador_id, trabajador.getId()); // Usar el ID del trabajador como referencia
                rbFamiliar.setTag(R.id.tag_familiar_id, familiar.getId());    // Usar el ID del familiar para el POST
                radioGroupSeleccion.addView(rbFamiliar);
            }
        }

        // Seleccionar por defecto al Trabajador
        if (radioGroupSeleccion.getChildCount() > 0) {
            ((RadioButton) radioGroupSeleccion.getChildAt(0)).setChecked(true);
            selectedId = trabajador.getId();
            selectedFamiliarId = null;
        }
    }

    private RadioButton crearRadioButton(String texto) {
        RadioButton rb = new RadioButton(this);
        rb.setText(texto);
        rb.setTextSize(18);
        rb.setPadding(0, 15, 0, 15);
        return rb;
    }

    private void abrirCuestionarioActivity() {
        Intent intent = new Intent(SeleccionActivity.this, CuestionarioActivity.class);
        intent.putExtra("trabajador_id", trabajador.getId());
        // Nullable(Of Integer) se pasa como Integer o null (0 en este caso si es null)
        intent.putExtra("familiar_id", selectedFamiliarId != null ? selectedFamiliarId : 0);
        // Extra para mostrar el nombre en la siguiente actividad
        String nombreSeleccionado = ((RadioButton) findViewById(radioGroupSeleccion.getCheckedRadioButtonId())).getText().toString();
        intent.putExtra("nombre_seleccionado", nombreSeleccionado);
        startActivity(intent);
    }
}