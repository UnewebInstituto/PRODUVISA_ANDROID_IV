package com.produvisa.emergencias;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.produvisa.emergencias.api.EmergenciasService;
import com.produvisa.emergencias.models.Trabajador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BienvenidaActivity extends AppCompatActivity {

    private EditText editTextCedula;
    private EmergenciasService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        editTextCedula = findViewById(R.id.editTextCedula);
        Button buttonEnviar = findViewById(R.id.buttonEnviarCedula);

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                // La URL base termina en 'Emergencias/'
                .baseUrl(AppConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(EmergenciasService.class);

        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cedula = editTextCedula.getText().toString().trim();
                if (cedula.isEmpty()) {
                    Toast.makeText(BienvenidaActivity.this, "Por favor, ingrese la cédula.", Toast.LENGTH_SHORT).show();
                    return;
                }
                buscarTrabajador(cedula);
            }
        });
    }

    private void buscarTrabajador(String cedula) {
        // Ejecutar llamada GET: api/Emergencias/Trabajador/{cedula}
        apiService.getTrabajadorPorCedula(cedula).enqueue(new Callback<Trabajador>() {
            @Override
            public void onResponse(Call<Trabajador> call, Response<Trabajador> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cédula encontrada: Iniciar la siguiente actividad con los datos
                    Trabajador trabajador = response.body();
                    abrirSeleccionActivity(trabajador);

                } else if (response.code() == 404) {
                    // Cédula NO encontrada: Mostrar mensaje de error del API
                    Toast.makeText(BienvenidaActivity.this,
                            "Trabajador con cédula " + cedula + " no encontrado.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // Otros errores (ej. 500 Internal Server Error)
                    Toast.makeText(BienvenidaActivity.this,
                            "Error en la API. Código: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Trabajador> call, Throwable t) {
                // Error de conexión, timeout, o problema de certificado (muy común en localhost HTTPS)
                Toast.makeText(BienvenidaActivity.this,
                        "Fallo de conexión o certificado: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                t.printStackTrace(); // Imprimir error en la consola para depuración
            }
        });
    }

    private void abrirSeleccionActivity(Trabajador trabajador) {
        Intent intent = new Intent(BienvenidaActivity.this, SeleccionActivity.class);
        // Usamos Gson para serializar el objeto Trabajador complejo
        String trabajadorJson = new com.google.gson.Gson().toJson(trabajador);
        intent.putExtra("trabajador_json", trabajadorJson);
        startActivity(intent);
    }
}