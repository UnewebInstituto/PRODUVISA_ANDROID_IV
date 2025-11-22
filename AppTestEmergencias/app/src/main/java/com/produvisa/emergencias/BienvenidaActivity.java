package com.produvisa.emergencias;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.produvisa.emergencias.api.EmergenciasService;
import com.produvisa.emergencias.api.RetrofitClient; // Importar el cliente Singleton
import com.produvisa.emergencias.models.Trabajador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BienvenidaActivity extends AppCompatActivity {

    private static final String TAG = "EmergenciasApp";
    private EditText editTextCedula;
    // Usar el servicio obtenido del cliente singleton
    private EmergenciasService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        editTextCedula = findViewById(R.id.editTextCedula);
        Button btnIngresar = findViewById(R.id.buttonEnviarCedula);

        // 1. Inicializar Retrofit Service usando el Singleton
        // ESTO es crucial para usar la configuración de OkHttp con el interceptor 'Connection: close'
        apiService = RetrofitClient.getEmergenciasService();

        btnIngresar.setOnClickListener(v -> ingresar());
    }

    private void ingresar() {
        // Obtenemos la cédula tal cual la ingresa el usuario (ej: V12345678)
        String cedulaInput = editTextCedula.getText().toString().trim();

        if (cedulaInput.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su número de cédula.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Usamos la cédula original (cedulaInput), ya que la API la acepta.
        Call<Trabajador> call = apiService.getTrabajadorPorCedula(cedulaInput);

        call.enqueue(new Callback<Trabajador>() {
            @Override
            public void onResponse(Call<Trabajador> call, Response<Trabajador> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Éxito: Trabajador encontrado
                    Trabajador trabajador = response.body();
                    Toast.makeText(BienvenidaActivity.this, "Bienvenido, " + trabajador.getNombres(), Toast.LENGTH_SHORT).show();
                    abrirSeleccionActivity(trabajador);
                } else {
                    // Error 400, 500, u otros
                    String errorMsg = "Error en la Api. Código: " + response.code() + ". El trabajador no fue encontrado o la cédula es incorrecta.";
                    Toast.makeText(BienvenidaActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    // --- PASO DE DEBUGGING: Mostrar exactamente qué URL falló ---
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