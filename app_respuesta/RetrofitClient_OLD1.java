package com.produvisa.emergencias.api;

import com.produvisa.emergencias.AppConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente Singleton de Retrofit para gestionar la configuración de la conexión.
 * Incluye configuración avanzada para evitar errores de conexión en entornos de desarrollo:
 * 1. Timeouts de 60 segundos.
 * 2. Interceptor 'Connection: close' para evitar 'unexpected end of stream' (común con IIS Express).
 */
public class RetrofitClient {

    private static Retrofit retrofit = null;

    /**
     * Retorna la instancia única de Retrofit, inicializándola si es necesario.
     * Implementa el patrón Singleton.
     * @return Instancia única de Retrofit.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {

            // Configurar el cliente OkHttp con lógica para manejo de conexión
            OkHttpClient client = new OkHttpClient.Builder()
                    // 1. Timeouts extendidos (60s) para evitar cortes por latencia en la red/servidor
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    // 2. Interceptor para forzar 'Connection: close'
                    // Esto soluciona el error 'unexpected end of stream' que ocurre con IIS Express
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Connection", "close") // Fuerza al servidor a no usar keep-alive
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .retryOnConnectionFailure(true) // Permite reintentar si hay un fallo inicial en la conexión
                    .build();

            // Construir Retrofit con el cliente OkHttp configurado
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    /**
     * Método factoría conveniente para obtener el servicio de la API listo para usar.
     * @return Instancia del servicio API lista para usar (EmergenciasService).
     */
    public static EmergenciasService getEmergenciasService() {
        return getClient().create(EmergenciasService.class);
    }
}