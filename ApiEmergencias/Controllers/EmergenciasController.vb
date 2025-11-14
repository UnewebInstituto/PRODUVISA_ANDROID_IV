' Imports System.Web.Mvc
' Controllers/EmergenciasController.vb
Imports System.Web.Http
Imports System.Net
Imports System.Net.Http
' Asegúrate de que las referencias a los modelos y DataAccess estén correctas
Imports ApiEmergencias.Models
Imports ApiEmergencias.DataAccess ' Asume este Namespace para la clase Repository


Namespace Controllers
    Public Class EmergenciasController
        Inherits ApiController

        Private ReadOnly _repository As New EmergenciasRepository()

        ' *******************************************************************
        ' 1. Endpoint: Obtener Trabajador y Familiares por Cédula
        ' GET api/Emergencias/Trabajador/{cedula}
        ' *******************************************************************
        <HttpGet>
        <Route("api/Emergencias/Trabajador/{cedula}")>
        Public Function GetTrabajadorConFamiliares(ByVal cedula As String) As HttpResponseMessage
            Dim trabajador As Trabajador = _repository.GetTrabajadorConFamiliares(cedula)

            If trabajador Is Nothing Then
                ' 404 Not Found
                Return Request.CreateErrorResponse(HttpStatusCode.NotFound, "Trabajador con cédula " & cedula & " no encontrado.")
            End If

            ' 200 OK y devuelve el objeto Trabajador (que incluye Familiares) en formato JSON
            Return Request.CreateResponse(HttpStatusCode.OK, trabajador)
        End Function

        ' *******************************************************************
        ' 2. Endpoint: Registrar Incidencia
        ' POST api/Emergencias/Incidencia
        ' *******************************************************************
        <HttpPost>
        <Route("api/Emergencias/Incidencia")>
        Public Function RegistrarIncidencia(<FromBody()> ByVal dto As IncidenciaRegistroDto) As HttpResponseMessage
            If dto Is Nothing OrElse dto.TrabajadorId <= 0 Then
                Return Request.CreateErrorResponse(HttpStatusCode.BadRequest, "Datos de incidencia inválidos.")
            End If

            Try
                Dim nuevoId As Integer = _repository.RegistrarIncidencia(dto)

                ' Creación de objeto respuesta para mostrar el ID generado
                Dim respuesta As New With {
                .Id = nuevoId,
                .TrabajadorId = dto.TrabajadorId,
                .FamiliarId = dto.FamiliarId,
                .FechaHora = DateTime.Now.ToString() ' Fecha real de inserción
            }

                ' 201 Created
                Return Request.CreateResponse(HttpStatusCode.Created, respuesta)

            Catch ex As Exception
                ' 500 Internal Server Error
                ' En un entorno real, se debería loggear el error
                Return Request.CreateErrorResponse(HttpStatusCode.InternalServerError, "Error al registrar la incidencia: " & ex.Message)
            End Try
        End Function

    End Class
End Namespace