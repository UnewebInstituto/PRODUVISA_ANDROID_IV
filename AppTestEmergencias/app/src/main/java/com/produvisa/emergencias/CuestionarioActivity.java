package com.produvisa.emergencias;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.produvisa.emergencias.api.EmergenciasService;
import com.produvisa.emergencias.api.RetrofitClient; // Importar RetrofitClient
import com.produvisa.emergencias.models.IncidenciaDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CuestionarioActivity extends AppCompatActivity {

    private static final int PERMISSION_CALL_REQUEST = 1;
    private static final String TAG = "CuestionarioActivity"; // Etiqueta para Logcat

    private int trabajadorId;
    private Integer familiarId; // Puede ser null
    private String personaSeleccionadaNombre;
    private RadioGroup[] radioGroups;
    private EmergenciasService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuestionario);

        // 1. Obtener datos del Intent
        trabajadorId = getIntent().getIntExtra("trabajador_id", 0);
        int familiarIdInt = getIntent().getIntExtra("familiar_id", 0);
        // Si el valor es 0, significa que es el trabajador, por lo que familiarId es null
        familiarId = (familiarIdInt > 0) ? familiarIdInt : null;
        personaSeleccionadaNombre = getIntent().getStringExtra("nombre_seleccionado");

        // 2. Inicializar Retrofit Service usando el Singleton
        apiService = RetrofitClient.getEmergenciasService();

        // 3. Inicializar las vistas y RadioGroups

        // CORRECCIÓN: El ID correcto del TextView en activity_cuestionario.xml es textViewNombrePersona.
        TextView tvPersona = findViewById(R.id.textViewNombrePersona);
        Button buttonEnviar = findViewById(R.id.buttonEnviarCuestionario);

        // Mostrar el nombre de la persona en el TextView
        if (personaSeleccionadaNombre != null) {
            tvPersona.setText("Cuestionario para: " + personaSeleccionadaNombre);
        }

        // Inicializar RadioGroups
        radioGroups = new RadioGroup[5];
        radioGroups[0] = findViewById(R.id.rg_p1);
        radioGroups[1] = findViewById(R.id.rg_p2);
        radioGroups[2] = findViewById(R.id.rg_p3);
        radioGroups[3] = findViewById(R.id.rg_p4);
        radioGroups[4] = findViewById(R.id.rg_p5);

        // 4. Configurar el Listener del Botón
        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarIncidencia();
            }
        });
    }

    /**
     * Extrae las respuestas del cuestionario y envía el DTO al API.
     */
    private void enviarIncidencia() {
        // Extraer respuestas booleanas (true = SI, false = NO)
        boolean p1 = obtenerRespuesta(radioGroups[0], R.id.rb_p1_si);
        boolean p2 = obtenerRespuesta(radioGroups[1], R.id.rb_p2_si);
        boolean p3 = obtenerRespuesta(radioGroups[2], R.id.rb_p3_si);
        boolean p4 = obtenerRespuesta(radioGroups[3], R.id.rb_p4_si);
        boolean p5 = obtenerRespuesta(radioGroups[4], R.id.rb_p5_si);

        // Crear el objeto DTO (Data Transfer Object)
        IncidenciaDto incidencia = new IncidenciaDto(
                trabajadorId,
                familiarId, // Será null si es el trabajador
                p1, p2, p3, p4, p5
        );

        // Llamada al API
        Call<Void> call = apiService.registrarIncidencia(incidencia);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CuestionarioActivity.this, "Incidencia registrada con éxito.", Toast.LENGTH_SHORT).show();
                    // Una vez registrada, procede a la llamada de emergencia
                    solicitarPermisoLlamada();
                } else {
                    String errorMsg = "Error al registrar incidencia. Código: " + response.code();
                    Toast.makeText(CuestionarioActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMsg + ". Respuesta: " + response.errorBody().toString());
                    // Si falla el registro, simplemente mostramos el mensaje de despedida.
                    mostrarMensajeDespedida("Fallo en el registro de la incidencia. Vuelva a intentar.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMsg = "Fallo de conexión al registrar incidencia: " + t.getMessage();
                Toast.makeText(CuestionarioActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, errorMsg, t);
                // Si falla la conexión, mostramos el mensaje de despedida.
                mostrarMensajeDespedida("Fallo de conexión. Por favor, revise su conexión a internet.");
            }
        });
    }

    /**
     * Método auxiliar para obtener la respuesta booleana de un RadioGroup.
     * @param rg El RadioGroup a evaluar.
     * @param siRadioButtonId El ID del RadioButton que representa el 'SI'.
     * @return true si el botón 'SI' está seleccionado, false en caso contrario.
     */
    private boolean obtenerRespuesta(RadioGroup rg, int siRadioButtonId) {
        return rg.getCheckedRadioButtonId() == siRadioButtonId;
    }


    /**
     * Verifica y solicita el permiso de llamada si es necesario.
     */
    private void solicitarPermisoLlamada() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // El permiso no ha sido otorgado, se solicita al usuario.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL_REQUEST);
        } else {
            // El permiso ya está otorgado.
            realizarLlamada();
        }
    }

    /**
     * Maneja el resultado de la solicitud de permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALL_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado
                realizarLlamada();
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso de llamada denegado. No se puede realizar la llamada de emergencia.", Toast.LENGTH_LONG).show();
                // Mostrar mensaje de despedida aunque no se haya llamado
                mostrarMensajeDespedida("Gracias por usar la aplicación de Emergencias de PRODUVISA, estamos para contribuir a tu bienestar y el de tu familia");
            }
        }
    }

    private void realizarLlamada() {
        // Se usa `tel:` para la llamada directa (ACTION_CALL)
        String numero = "tel:" + AppConfig.NUMERO_EMERGENCIA;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(numero));
        try {
            startActivity(callIntent);
            // La llamada ha sido disparada. Mostrar mensaje de despedida inmediatamente.
            mostrarMensajeDespedida("Gracias por usar la aplicación de Emergencias de PRODUVISA, estamos para contribuir a tu bienestar y el de tu familia");
        } catch (SecurityException e) {
            Toast.makeText(this, "Error de seguridad al intentar la llamada. Verifique permisos.", Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarMensajeDespedida(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        // Cierra la actividad para volver a la pantalla de bienvenida o salir
        finish();
    }
}