package com.produvisa.emergencias.api;

import com.produvisa.emergencias.models.IncidenciaDto;
import com.produvisa.emergencias.models.Trabajador;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interfaz de Retrofit para definir los endpoints de la API.
 */
public interface EmergenciasService {

    // GET api/Emergencias/Trabajador/{cedula}
    @GET("Trabajador/{cedula}")
    Call<Trabajador> getTrabajadorPorCedula(@Path("cedula") String cedula);

    // POST api/Emergencias/Incidencia
    @POST("Incidencia")
    Call<Void> registrarIncidencia(@Body IncidenciaDto incidencia);
}