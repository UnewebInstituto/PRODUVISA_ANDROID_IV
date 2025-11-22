Public Class IncidenciaRegistroDto
    Public Property TrabajadorId As Integer
    ' FamiliarId es Nullable para manejar el campo opcional
    Public Property FamiliarId As Nullable(Of Integer)
    Public Property Pregunta1 As Boolean
    Public Property Pregunta2 As Boolean
    Public Property Pregunta3 As Boolean
    Public Property Pregunta4 As Boolean
    Public Property Pregunta5 As Boolean
End Class
