package com.produvisa.emergencias;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // IMPORTANTE: Importar la clase Log
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.produvisa.emergencias.api.EmergenciasService;
import com.produvisa.emergencias.models.Trabajador;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

public class BienvenidaActivity extends AppCompatActivity {

    // Tag para identificar los mensajes de Log en Logcat
    private static final String TAG = "EmergenciasApp"; 
    private EmergenciasService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        Button btnIngresar = findViewById(R.id.buttonEnviarCedula);
        
        // 1. Inicializar Retrofit
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

        // 2. Listener del botón
        btnIngresar.setOnClickListener(v -> {
            EditText editTextCedula = findViewById(R.id.editTextCedula);
            // IMPORTANTE: Sanear la entrada de espacios en blanco
            String cedula = editTextCedula.getText().toString().trim(); 

            if (cedula.isEmpty()) {
                Toast.makeText(this, "Debe ingresar la cédula.", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- PASO DE DEBUGGING ---
            // 1. Imprimir la cédula saneada
            Log.d(TAG, "Cédula ingresada (saneada): " + cedula); 
            
            // 2. Imprimir la URL completa que se construirá
            String urlCompleta = AppConfig.API_BASE_URL + "Trabajador/" + cedula;
            Log.d(TAG, "URL completa del llamado a la API: " + urlCompleta);
            // --------------------------

            // Llamar a la API con la cédula saneada
            obtenerTrabajador(cedula);
        });
    }
    
    // Método para llamar a la API
    private void obtenerTrabajador(final String cedula) {
        // La URL completa ya fue impresa antes de esta llamada
        
        apiService.getTrabajadorPorCedula(cedula).enqueue(new Callback<Trabajador>() {
            @Override
            public void onResponse(Call<Trabajador> call, Response<Trabajador> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Éxito: Se encontró el trabajador
                    Trabajador trabajador = response.body();
                    abrirSeleccionActivity(trabajador);
                } else if (response.code() == 404) {
                    // No encontrado: La cédula no existe
                    Toast.makeText(BienvenidaActivity.this, "Cédula no encontrada. Intente de nuevo.", Toast.LENGTH_LONG).show();
                } else {
                    // Error 400, 500, u otros
                    String errorMsg = "Error en la Api. Código: " + response.code();
                    Toast.makeText(BienvenidaActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    // --- PASO DE DEBUGGING ---
                    Log.e(TAG, errorMsg + ". URL usada: " + call.request().url().toString());
                    // --------------------------
                }
            }

            @Override
            public void onFailure(Call<Trabajador> call, Throwable t) {
                // Fallo de conexión de red
                String errorMsg = "Fallo de conexión: " + t.getMessage();
                Toast.makeText(BienvenidaActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                // --- PASO DE DEBUGGING ---
                Log.e(TAG, errorMsg + ". URL usada: " + call.request().url().toString());
                Log.e(TAG, "Causa del fallo: ", t);
                // --------------------------
            }
        });
    }
    
    // Método auxiliar para iniciar la siguiente actividad
    private void abrirSeleccionActivity(Trabajador trabajador) {
        Intent intent = new Intent(BienvenidaActivity.this, SeleccionActivity.class);
        // Usar Gson para pasar el objeto completo a la siguiente actividad
        String trabajadorJson = new Gson().toJson(trabajador);
        intent.putExtra("trabajador_json", trabajadorJson);
        startActivity(intent);
    }
}