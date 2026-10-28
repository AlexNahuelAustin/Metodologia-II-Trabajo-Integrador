# Diagnostico de Calidad — Sistema de Gestion de Solicitudes Academicas

## 1. Calidad externa vs. calidad interna

### Calidad externa: que funciona

Desde la mirada del usuario, el sistema cumple con lo que promete:

- Registra solicitudes y devuelve un identificador.
- Rechaza datos invalidos (legajo vacio, email sin arroba, tipo inexistente) con un mensaje claro.
- Impide transiciones de estado invalidas: no se puede poner en proceso una solicitud sin responsable, ni reabrir una ya cerrada.
- Asigna responsables respetando el tope de carga.
- Persiste los datos en H2, de modo que sobreviven al reinicio.
- Genera el reporte con totales, demora promedio y vencidas.

La ejecucion del `Main` lo demuestra: todos los casos, tanto los exitosos como los de error, se comportan como se espera.

### Calidad interna: que esta mal por dentro

Nada de lo anterior se percibe desde el codigo. La clase `GestorDeSolicitudes` concentra **toda** la aplicacion: validaciones, reglas de negocio, acceso a la base de datos y presentacion por consola, en 300 lineas sin separacion alguna.

Consecuencias concretas:

| Dimension | Estado actual                                                                                                         |
|---|-----------------------------------------------------------------------------------------------------------------------|
| **Mantenibilidad** | Cambiar el tope de solicitudes abiertas exige buscar un `5` suelto en medio de un metodo de 70 lineas.                |
| **Testabilidad** | No se puede probar ninguna regla de negocio sin levantar la base de datos. No hay un solo punto de inyeccion.         |
| **Legibilidad** | Los nombres (`p`, `ep`, `res`, `c`, `lst`) obligan a leer el cuerpo entero para entender que hace cada variable.      |
| **Evolucion** | Agregar un tipo de solicitud o un estado nuevo obliga a tocar varios metodos distintos, con riesgo de olvidar alguno. |
| **Seguridad** |  El SQL se arma concatenando strings: un legajo con comilla rompe la consulta o permite inyeccion.                    |

### La conclusion

El sistema tiene **calidad externa aceptable y calidad interna pobre**. Esa combinacion es exactamente la definicion de deuda tecnica: funciona hoy, pero cada cambio futuro cuesta mas de lo que deberia. El riesgo no es que falle ahora, sino que el costo del cambio crezca hasta volver el modulo intocable.

---

## 2. Deuda tecnica y code smells

| # | Code smell | Evidencia                                                                                                                                      | Ubicacion | Impacto | Riesgo | Prioridad |
|---|---|------------------------------------------------------------------------------------------------------------------------------------------------|---|---|---|---|
| 1 | **God Object** | Una sola clase valida, aplica reglas de negocio, accede a la base e imprime por consola. 300 lineas, 9 metodos publicos.                       | `GestorDeSolicitudes` (clase completa) | Cuatro motivos de cambio distintos conviven. Viola SRP. | Alto: cualquier cambio puede romper algo no relacionado. | **Alta** |
| 2 | **Metodo largo** | `registrarSolicitud` tiene 70 lineas y hace 6 cosas: valida, busca el estudiante, lo da de alta, cuenta abiertas, calcula prioridad e inserta. | `registrarSolicitud` | Imposible de leer de una pasada; los comentarios internos marcan los metodos que faltan. | Alto: alta probabilidad de introducir bugs al modificarlo. | **Alta** |
| 3 | **Codigo duplicado** | `buscar                                                                                                                                        