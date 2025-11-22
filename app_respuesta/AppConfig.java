package com.produvisa.emergencias;

/**
 * Clase de configuración global de la aplicación.
 * Contiene constantes clave como la URL base del API y el número de emergencia.
 */
public class AppConfig {

    /**
     * URL base del API.
     * Importante: Usar la IP del loopback del emulador (10.0.2.2) y el puerto HTTP (5000).
     * Si usa un dispositivo físico, deberá reemplazar 10.0.2.2 por la IP de su máquina en la red local.
     */
    public static final String API_BASE_URL = "http://10.0.2.2:5000/api/Emergencias/";

    /**
     * Número de teléfono de emergencia al que se llamará.
     * Debe incluir el código de país.
     */
    public static final String NUMERO_EMERGENCIA = "+584141234567";
}