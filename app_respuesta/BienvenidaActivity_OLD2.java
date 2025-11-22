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
import com.produvisa.emergencias.api.RetrofitClient;
import com.produvisa.emergencias.models.Trabajador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BienvenidaActivity extends AppCompatActivity {

    private static final String TAG = "EmergenciasApp";
    private EmergenciasService apiService;
    private EditText editTextCedula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        editTextCedula = findViewById(R.id.editTextCedula);
        Button btnIngresar = findViewById(R.id.buttonEnviarCedula);

        // 1. Inicializar Retrofit usando el cliente Singleton con timeouts extendidos
        apiService = RetrofitClient.getEmergenciasService();

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cedula = editTextCedula.getText().toString().trim();
                if (!cedula.isEmpty()) {
                    buscarTrabajador(cedula);
                } else {
                    Toast.makeText(BienvenidaActivity.this, "Por favor, ingrese su número de cédula.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Realiza la llamada a la API para obtener los datos del trabajador.
     * @param cedula El número de cédula a buscar.
     */
    private void buscarTrabajador(String cedula) {
        // La llamada se realiza al endpoint: GET Trabajador/{cedula}
        Call<Trabajador> call = apiService.getTrabajadorPorCedula(cedula);

        call.enqueue(new Callback<Trabajador>() {
            @Override
            public void onResponse(Call<Trabajador> call, Response<Trabajador> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Éxito: Trabajador encontrado
                    Trabajador trabajador = response.body();
                    abrirSeleccionActivity(trabajador);
                } else if (response.code() == 404) {
                    // No encontrado
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
                // Fallo de conexión de red o timeout
                String errorMsg = "Fallo de conexión. Verifique su red o la API: " + t.getMessage();
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