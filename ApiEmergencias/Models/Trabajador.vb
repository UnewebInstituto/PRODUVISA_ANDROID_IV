' Models/Trabajador.vb
Imports System.Collections.Generic
Public Class Trabajador
    Public Property Id As Integer
    Public Property Cedula As String
    Public Property Nombres As String
    Public Property Apellidos As String
    Public Property CorreoElectronico As String
    Public Property Telefono As String
    ' Para anidar los familiares en la respuesta JSON
    Public Property Familiares As List(Of Familiar)
End Class
