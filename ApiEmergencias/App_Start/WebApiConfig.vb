' App_Start/WebApiConfig.vb
Imports System.Web.Http

Public Module WebApiConfig
    Public Sub Register(ByVal config As HttpConfiguration)
        ' HABILITAR EL ENRUTAMIENTO POR ATRIBUTOS
        config.MapHttpAttributeRoutes()

        ' Ruta predeterminada para el controlador de API (si es necesario)
        config.Routes.MapHttpRoute(
            name:="DefaultApi",
            routeTemplate:="api/{controller}/{id}",
            defaults:=New With {.id = RouteParameter.Optional}
        )
    End Sub
End Module
