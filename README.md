# Metodologia-II-Trabajo-Integrador

**Alumnos**

Austin Alex Nahuel  
Leonel Jesus Aballay

**Comision**
6

# Sistema de Gestion de Solicitudes Academicas

Trabajo Practico Integrador — Metodologia de Sistemas II (UTN)

Modulo para registrar solicitudes de estudiantes, clasificarlas por tipo, prioridad y estado, asignar responsables, consultar y generar reportes.

El objetivo del trabajo no es la funcionalidad en si, sino documentar la mejora de su calidad interna: diagnostico de code smells, aplicacion de SOLID y Clean Code, refactorings verificados y revision tecnica.

## Tecnologias

- Java 21
- Gradle
- H2 (base de datos embebida)
- JUnit 5 + JaCoCo

## Instalacion y uso

cd Sistema-Academico
./gradlew run        # ejecuta la demostracion
./gradlew test       # corre la suite de pruebas

Reporte de cobertura:

./gradlew test jacocoTestReport.

# build/reports/jacoco/test/html/index.html

## Funcionalidades

- Registrar solicitud (crea el estudiante si no existe)
- Cambiar estado respetando las transiciones validas
- Asignar responsable con control de carga maxima
- Buscar por estado, tipo o responsable
- Generar reporte con totales, demora promedio y vencidas

## Estructura

Sistema-Academico/src/main/java/ar/utn/solicitudes/
├── models/        entidades, enums y objetos de valor
├── repository/    puertos de persistencia y adaptadores H2
├── services/      reglas de negocio y generacion de reportes
└── Main.java      ensamblado de dependencias

## Etapas del trabajo

| Tag | Que contiene                                            |
|---|---------------------------------------------------------|
| estado-inicial | Sistema base con los problemas de diseño a diagnosticar |
| etapa-diagnostico | Documento de calidad interna/externa y code smells      |
| etapa-refactorizada | Los cinco refactorings aplicados                        |
| etapa-pruebas | Suite de 14 pruebas y reporte de cobertura              |
| estado-refactorizado | Estado final                                            |

## Refactorings aplicados

| # | Refactoring | Smell que resuelve | Principio |
|---|---|---|---|
| 1 | Introduce Parameter Object | 8 parametros en registrarSolicitud | — |
| 2 | Replace Magic Number with Constant | Numeros y strings magicos | — |
| 3 | Extract Class | God Object, duplicacion, excepciones tragadas | SRP + DIP |
| 4 | Extract Method | Metodos de 70 lineas | SRP |
| 5 | Replace Conditional with Polymorphism | Condicionales anidados de estado | OCP |

## Documentacion

- docs/01-diagnostico.md — calidad interna vs externa y code smells
- docs/02-code-review.md — checklist tecnico y observaciones



