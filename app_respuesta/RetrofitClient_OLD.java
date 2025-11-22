package com.produvisa.emergencias.api;

import com.produvisa.emergencias.AppConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente Singleton de Retrofit para gestionar la configuración de la conexión
 * y extender los timeouts.
 *
 * Utiliza OkHttpClient para configurar tiempos de espera más largos (60s)
 * para evitar el error 'unexpected end of stream'.
 */
public class RetrofitClient {

    private static Retrofit retrofit = null;

    /**
     * Retorna la instancia de Retrofit configurada con tiempos de espera extendidos.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {

            // 1. Configurar el cliente OkHttp con timeouts extendidos
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS) // Tiempo máximo para establecer la conexión
                    .readTimeout(60, TimeUnit.SECONDS)    // Tiempo máximo para leer la respuesta
                    .writeTimeout(60, TimeUnit.SECONDS)   // Tiempo máximo para enviar datos (si aplica)
                    .build();

            // 2. Construir la instancia de Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) // Usar el cliente OkHttp personalizado
                    .build();
        }
        return retrofit;
    }

    /**
     * Retorna una instancia del servicio API (EmergenciasService)
     * usando la configuración de Retrofit con timeouts extendidos.
     */
    public static EmergenciasService getEmergenciasService() {
        return getClient().create(EmergenciasService.class);
    }
}