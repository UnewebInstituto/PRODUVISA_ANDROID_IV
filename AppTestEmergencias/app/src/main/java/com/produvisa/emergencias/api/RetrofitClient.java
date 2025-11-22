package com.produvisa.emergencias.api;

import com.produvisa.emergencias.AppConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor; // Importar para logging
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente Singleton de Retrofit para gestionar la configuración de la conexión
 * y extender los timeouts.
 *
 * Se ha añadido un Interceptor para forzar "Connection: close" y solucionar
 * el error común 'unexpected end of stream' en entornos de desarrollo.
 */
public class RetrofitClient {

    private static Retrofit retrofit = null;

    /**
     * Retorna la instancia de Retrofit configurada con tiempos de espera extendidos y estabilidad de conexión.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {

            // 1. Configurar Logging Interceptor
            // Esto imprimirá los detalles de la petición y la respuesta en Logcat
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Configurar el cliente OkHttp con timeouts extendidos y Interceptores
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS) // Tiempo máximo para establecer la conexión
                    .readTimeout(60, TimeUnit.SECONDS)    // Tiempo máximo para leer la respuesta
                    .writeTimeout(60, TimeUnit.SECONDS)   // Tiempo máximo para enviar datos (si aplica)

                    // Añadir el logger para depuración (Útil para ver el JSON recibido)
                    .addInterceptor(logging)

                    // --- SOLUCIÓN CLAVE para 'unexpected end of stream' ---
                    // Fuerza a que la conexión se cierre después de cada solicitud,
                    // evitando problemas de reutilización de conexión con servidores locales.
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request request = original.newBuilder()
                                .header("Connection", "close")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    // -------------------------------------------------------

                    .build();

            // 3. Construir la instancia de Retrofit
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
     * usando la configuración de Retrofit con estabilidad de conexión.
     */
    public static EmergenciasService getEmergenciasService() {
        return getClient().create(EmergenciasService.class);
    }
}