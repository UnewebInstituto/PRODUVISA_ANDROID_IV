package com.produvisa.emergencias;

/**
 * Clase para almacenar configuraciones de la aplicación como la URL base de la API y el número de emergencia.
 */
public class AppConfig {
    // 1. URL Base de la API (Ajustar con tu puerto fijo, ej: 5000)
    // Usar la IP de la máquina local (10.0.2.2) para acceder a localhost de Windows desde el emulador Android.
    public static final String API_BASE_URL = "https://10.0.2.2:44338/api/Emergencias/";

    // 2. Número de teléfono de emergencia (formato que acepta la acción CALL_ACTION)
    public static final String NUMERO_EMERGENCIA = "+584142886735";
}