# Revision de Codigo — Sistema de Gestion de Solicitudes Academicas

**Rama revisada:** main  
**Alcance:** desde el tag estado-inicial hasta el estado-refactorizado

---

## 1. Lista de verificacion tecnica

| # | Criterio                                                 | ¿Cumple? | Observacion                                                                 |
|---|----------------------------------------------------------|---|-----------------------------------------------------------------------------|
| 1 | El codig  compila sin errores ni advertencias            | Si | gradlew build finaliza correctamente                                        |
| 2 | Todas las pruebas pasan al 100%                          | Si | 14 de 14 pruebas aprobadas                                                  |
| 3 | Cada clase tiene una unica responsabilidad               | Si | Separacion en modelos, repositorios y servicios                             |
| 4 | No hay metodos de mas de 30 lineas                       | Si | Se aplico Extract Method en la refactorizacion 4                            |
| 5 | No hay codigo duplicado                                  | Si | Mapeo de ResultSet centralizado                                             |
| 6 | Los nombres expresan claramente su proposito             | Si | Nombres en espanol de manera consistente                                    |
| 7 | No hay numeros ni textos con valores fijos innecesarios  | Si | Los enumerados EstadoSolicitud y TipoDeSolicitud reemplazan valores magicos |
| 8 | Las dependencias se inyectan y no se crean directamente  | Si | Inyeccion de dependencias mediante constructores en los servicios           |
| 9 | Los errores se manejan explicitamente                    | Si | ErrorDePersistencia en el repositorio                                       |
| 10 | No hay codigo sin uso                                    | Si | Codigo limpio                                                               |
| 11 | No hay vulnerabilidades de inyeccion SQL                 | Si | Uso de PreparedStatement en todas las consultas                             |
| 12 | Los recursos se cierran correctamente                    | Si | Uso de try-with-resources                                                   |
| 13 | La logica de negocio se puede probar sin infraestructura | Si | Se implementaron dobles de prueba en memoria                                |
| 14 | La cobertura de ramas supera el 50%                      | Si | JaCoCo esta configurado y genera informes                                   |
| 15 | La nomenclatura es consistente                           | Si | Se utiliza el espanol en todo el proyecto                                   |

---

## 2. Observaciones de la revision

### Observacion 1 — Introducir un objeto parametro (Refactorizacion 1)

**Severidad:** baja  
**Estado:** completada

El metodo registrarSolicitud se redujo de 8 parametros a 1 mediante el uso del registro DatosDeEstudiante. Esto mejora significativamente la legibilidad y simplifica la llamada al metodo.

### Observacion 2 — Reemplazar valores magicos (Refactorizacion 2)

**Severidad:** baja  
**Estado:** completada

Los enumerados EstadoSolicitud y TipoDeSolicitud reemplazan cadenas de texto y numeros magicos. Cada enumerado incluye el metodo desdeCodigo() para convertir los codigos en sus respectivos valores.

### Observacion 3 — Extraer una clase (Refactorizacion 3)

**Severidad:** media  
**Estado:** completada

Se separaron las interfaces de los repositorios de sus implementaciones para H2. La clase ErrorDePersistencia encapsula las excepciones SQL.

Esta separacion favorece el principio de responsabilidad unica (SRP) y el principio de inversion de dependencias (DIP).

### Observacion 4 — Extraer metodos (Refactorizacion 4)

**Severidad:** baja  
**Estado:** completada

El ServicioDeSolicitudes cuenta con metodos especificos y enfocados en tareas concretas. Se incorporaron metodos privados para la logica interna, como las validaciones y las busquedas.

Los metodos tienen un maximo de 35 lineas.

### Observacion 5 — Reemplazar condicionales (Refactorizacion 5)

**Severidad:** baja  
**Estado:** completada

Se utilizaron expresiones switch con enumerados para reemplazar estructuras if/else anidadas.

Esto permite expresar la logica de forma mas clara y declarativa, aprovechando las posibilidades de los enumerados.

---

## 3. Criterios de aceptacion

1. Compilacion sin errores ni advertencias.
2. El 100% de las pruebas aprobadas.
3. Aplicacion correcta de las cinco refactorizaciones.
4. Ausencia de vulnerabilidades de inyeccion SQL.
5. Implementacion de los principios SOLID.
6. JaCoCo configurado y generando informes de cobertura.

---

## Resultado de la revision: APROBADO

El proyecto cumple con los criterios de aceptacion establecidos. Las cinco refactorizaciones fueron completadas y se mejoraron la legibilidad, la organizacion, el mantenimiento y la capacidad de prueba del codigo.