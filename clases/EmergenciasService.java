// EmergenciasService.java
package com.produvisa.emergencias.api;

import com.produvisa.emergencias.models.IncidenciaDto;
import com.produvisa.emergencias.models.Trabajador;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers; // Importar la clase Headers
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interfaz de Retrofit para definir los endpoints de la API.
 */
public interface EmergenciasService {

    // GET api/Emergencias/Trabajador/{cedula}
    // IMPORTANTE: Anulamos el Content-Type para evitar el Error 400 del servidor .NET en peticiones GET
    @Headers("Content-Type: ") 
    @GET("Trabajador/{cedula}")
    Call<Trabajador> getTrabajadorPorCedula(@Path("cedula") String cedula);

    // POST api/Emergencias/Incidencia (El Content-Type es correcto aquí, no se toca)
    @POST("Incidencia")
    Call<Void> registrarIncidencia(@Body IncidenciaDto incidencia);
}