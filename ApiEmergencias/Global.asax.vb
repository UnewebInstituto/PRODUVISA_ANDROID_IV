Imports System.Web.Optimization
Imports System.Web.Http
Public Class MvcApplication
    Inherits System.Web.HttpApplication

    Sub Application_Start()
        AreaRegistration.RegisterAllAreas()

        ' LLAMAR A LA CONFIGURACIÓN DEL API WEB
        GlobalConfiguration.Configure(AddressOf WebApiConfig.Register)


        FilterConfig.RegisterGlobalFilters(GlobalFilters.Filters)
        RouteConfig.RegisterRoutes(RouteTable.Routes)
        BundleConfig.RegisterBundles(BundleTable.Bundles)
    End Sub
End Class
