package com.produvisa.emergencias;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.produvisa.emergencias.api.EmergenciasService;
import com.produvisa.emergencias.models.IncidenciaDto;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CuestionarioActivity extends AppCompatActivity {

    private static final int PERMISSION_CALL_REQUEST = 1;
    private int trabajadorId;
    private Integer familiarId;
    private String personaSeleccionadaNombre;
    private RadioGroup[] radioGroups;
    private EmergenciasService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuestionario);

        // Obtener datos de la persona seleccionada
        trabajadorId = getIntent().getIntExtra("trabajador_id", -1);
        int tempFamiliarId = getIntent().getIntExtra("familiar_id", 0);
        familiarId = (tempFamiliarId != 0) ? tempFamiliarId : null;
        personaSeleccionadaNombre = getIntent().getStringExtra("nombre_seleccionado");

        // Mostrar nombre de la persona en la interfaz
        TextView tvNombre = findViewById(R.id.textViewNombrePersona);
        tvNombre.setText("Cuestionario para: " + personaSeleccionadaNombre);

        // Inicializar RadioGroups
        radioGroups = new RadioGroup[]{
                findViewById(R.id.rg_p1),
                findViewById(R.id.rg_p2),
                findViewById(R.id.rg_p3),
                findViewById(R.id.rg_p4),
                findViewById(R.id.rg_p5)
        };

        // Inicializar Retrofit (con timeout para reintentos en llamadas)
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(EmergenciasService.class);

        Button buttonEnviar = findViewById(R.id.buttonEnviarCuestionario);
        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarRespuestas();
            }
        });
    }

    private void enviarRespuestas() {
        boolean[] respuestas = obtenerRespuestas();
        boolean emergenciaDetectada = false;

        // Verificar si alguna respuesta es 'SÍ'
        for (boolean respuesta : respuestas) {
            if (respuesta) {
                emergenciaDetectada = true;
                break;
            }
        }

        // Crear DTO para el POST
        IncidenciaDto dto = new IncidenciaDto(
                trabajadorId,
                familiarId,
                respuestas[0],
                respuestas[1],
                respuestas[2],
                respuestas[3],
                respuestas[4]
        );

        // Enviar datos al API
        registrarIncidencia(dto, emergenciaDetectada);
    }

    private boolean[] obtenerRespuestas() {
        boolean[] respuestas = new boolean[5];
        for (int i = 0; i < 5; i++) {
            // El ID del RadioButton de "SI" se asume como rb_pX_si
            int selectedId = radioGroups[i].getCheckedRadioButtonId();
            if (selectedId == getResources().getIdentifier("rb_p" + (i + 1) + "_si", "id", getPackageName())) {
                respuestas[i] = true;
            } else {
                respuestas[i] = false;
            }
        }
        return respuestas;
    }

    private void registrarIncidencia(final IncidenciaDto dto, final boolean emergenciaDetectada) {
        apiService.registrarIncidencia(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Registro POST exitoso
                    if (emergenciaDetectada) {
                        // Si hay emergencia, intentar la llamada
                        iniciarLlamadaEmergencia();
                    } else {
                        // Si no hay emergencia, mostrar mensaje de chequeo
                        mostrarMensajeDespedida("Servicio médico, se pondrá en contacto con usted, a fin de concertar chequeo de rutina");
                    }
                } else {
                    // Error en el servidor al registrar la incidencia
                    Toast.makeText(CuestionarioActivity.this, "Error al registrar la incidencia en el servidor. Código: " + response.code(), Toast.LENGTH_LONG).show();
                    // Opcionalmente, reintentar POST
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Fallo de conexión o certificado
                Toast.makeText(CuestionarioActivity.this, "Fallo de red al registrar incidencia: " + t.getMessage(), Toast.LENGTH_LONG).show();
                // Opcionalmente, reintentar POST
            }
        });
    }

    private void iniciarLlamadaEmergencia() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar el permiso si no está concedido
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL_REQUEST);
        } else {
            // Permiso ya concedido, iniciar llamada
            realizarLlamada();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALL_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, llamar
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
