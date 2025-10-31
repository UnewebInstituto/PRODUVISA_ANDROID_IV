package com.curso.ejempl01hacerllamada;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CALL_PHONE = 1;
    // El número destino
    private final String NUMERO_DESTINO = "+584142886735";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button callButton = findViewById(R.id.btn_call);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // La condición previa es el clic en el botón.
                // Dispara la verificación de permisos.
                checkAndRequestCallPermission();
            }
        });

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

    }
    /**
     * Verifica si el permiso CALL_PHONE está concedido y, si no lo está, lo solicita.
     */
    private void checkAndRequestCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permiso NO concedido: Solicitarlo.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PERMISSION_REQUEST_CALL_PHONE);

        } else {
            // Permiso CONCEDIDO: Proceder directamente a la llamada.
            makePhoneCall();
        }
    }

    /**
     * Maneja la respuesta del usuario a la solicitud de permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario concedió el permiso.
                makePhoneCall();
            } else {
                // El usuario denegó el permiso.
                Toast.makeText(this, "Permiso de llamada denegado. No se puede realizar la acción.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Ejecuta la llamada telefónica usando un Intent.ACTION_CALL.
     */
    private void makePhoneCall() {
        // Asegúrate de usar el esquema 'tel:' para números de teléfono
        String dialUri = "tel:" + NUMERO_DESTINO;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(dialUri));

        try {
            // Dado que ya comprobamos el permiso, esta llamada debería ser segura.
            startActivity(callIntent);
        } catch (SecurityException e) {
            // Esto solo debería ocurrir si la comprobación previa falló o el SO lo bloqueó.
            Toast.makeText(this, "Error al iniciar la llamada: Faltan permisos.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}