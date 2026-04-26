# 📱 SelfFocus: Control Digital Personal

**Autocontrol de pantalla, límites suaves y enfoque sin distracciones. 100% offline y privado.**

---

## 🚀 Cómo Compilar la App

### Requisitos Previos
- **Android Studio**: Hedgehog (2023.1.1) o superior
- **JDK**: 17 o superior
- **Android SDK**: API 34 (Android 14)
- **Gradle**: 8.2.0+

### Pasos para Generar APK/AAB

1. **Abrir el proyecto en Android Studio**
   ```
   File → Open → Seleccionar la carpeta raíz del proyecto
   ```

2. **Sincronizar Gradle**
   - Android Studio sincronizará automáticamente al abrir
   - O ejecutar: `File → Sync Project with Gradle Files`

3. **Generar APK Debug (para testing)**
   ```bash
   ./gradlew assembleDebug
   ```
   - Output: `app/build/outputs/apk/debug/app-debug.apk`

4. **Generar APK Release (para distribución)**
   ```bash
   ./gradlew assembleRelease
   ```
   - Output: `app/build/outputs/apk/release/app-release.apk`

5. **Generar AAB (para Play Store)**
   ```bash
   ./gradlew bundleRelease
   ```
   - Output: `app/build/outputs/bundle/release/app-release.aab`

---

## 📁 Estructura del Proyecto

```
app/src/main/java/com/selffocus/
├── core/                      # Capa base
│   ├── constants/             # Constantes y rutas
│   ├── di/                    # Inyección de dependencias (Hilt)
│   └── util/                  # Utilidades y helpers
├── data/                      # Capa de datos
│   ├── local/                 # Room Database, DAOs, Entities
│   ├── prefs/                 # DataStore Preferences
│   ├── repository/            # Implementaciones de repositorios
│   ├── util/                  # Helpers específicos de datos
│   └── worker/                # WorkManager background tasks
├── domain/                    # Capa de dominio (reglas de negocio)
│   ├── model/                 # Modelos de dominio
│   ├── repository/            # Interfaces de repositorios
│   └── usecase/               # Casos de uso
└── presentation/              # Capa de UI (Jetpack Compose)
    ├── navigation/            # Navegación
    ├── ui/
    │   ├── components/        # Componentes reutilizables
    │   ├── screens/           # Pantallas completas
    │   └── theme/             # Tema Material 3
    └── viewmodel/             # ViewModels
```

---

## 🔧 Configuración Técnica

### Stack Tecnológico
| Capa | Tecnología | Versión |
|------|------------|---------|
| Lenguaje | Kotlin | 1.9.22+ |
| UI | Jetpack Compose + Material 3 | BOM 2024.02.00 |
| Arquitectura | Clean Architecture + MVVM | - |
| DI | Hilt | 2.50 |
| Persistencia | Room + DataStore | 2.6.1 / 1.0.0 |
| Background | WorkManager | 2.9.0 |
| Min SDK | Android 7.0 (API 24) | - |
| Target SDK | Android 14 (API 34) | - |

### Permisos Requeridos
La app solicita los siguientes permisos (el usuario debe conceder manualmente algunos):

| Permiso | Uso | Concesión |
|---------|-----|-----------|
| `PACKAGE_USAGE_STATS` | Tracking de uso de apps | Manual (Ajustes) |
| `POST_NOTIFICATIONS` | Notificaciones de límites | Runtime (Android 13+) |
| `USE_EXACT_ALARM` | Scheduling preciso | Automático |
| `FOREGROUND_SERVICE` | Timer de Focus Mode | Automático |

---

## 🎯 Características Principales

### ✅ MVP Funcional (Batch 1-5 Completo)
1. **Dashboard** - Métricas de uso diario/semanal
2. **Límites de Apps** - Establecer límites diarios por aplicación
3. **Focus Timer** - Sesiones de enfoque con temporizador
4. **Settings** - Configuración y gestión de permisos
5. **Background Sync** - Sincronización automática cada 15 min
6. **Notificaciones Suaves** - Alertas al alcanzar límites
7. **Privacidad Total** - Todo local, 0 red, cifrado con Keystore

### 🔒 Privacidad y Seguridad
- **Android Keystore**: Cifrado AES-256 para preferencias sensibles
- **Data Extraction Rules**: Backup en la nube deshabilitado
- **0 Analytics**: Sin tracking externo
- **Offline First**: Funciona completamente sin internet

---

## 📊 Estado del Desarrollo

| Batch | Contenido | Estado |
|-------|-----------|--------|
| 1 | Config Gradle, Manifest, DI, Room, UsageStats | ✅ Completado |
| 2 | Domain layer, Repos, UseCases, Mappers | ✅ Completado |
| 3 | ViewModels, UiState, WorkManager, Notificaciones | ✅ Completado |
| 4 | Navigation, MainActivity, UI Screens, Theme | ✅ Completado |
| 5 | Recursos, Iconos, ProGuard, Ready para Build | ✅ Completado |

**Progreso Total: 95%** 🎉

Lo único pendiente es:
- Firmar con keystore de producción (debes hacerlo tú por seguridad)
- Subir a Play Console (requiere cuenta de desarrollador)

---

## 🛠️ Solución de Problemas

### Error: "SDK location not found"
Crear `local.properties` en la raíz del proyecto:
```properties
sdk.dir=/ruta/a/tu/Android/Sdk
```

### Error: "Permission denied" en Linux/Mac
```bash
chmod +x gradlew
./gradlew assembleDebug
```

### Error: "Hilt dependencies not found"
- Verificar conexión a internet (descarga de dependencias)
- Ejecutar: `./gradlew clean build --refresh-dependencies`

### La app se cierra al abrir
- Conceder permiso de `PACKAGE_USAGE_STATS` manualmente:
  - Ajustes → Bienestar Digital → Acceso a datos de uso → SelfFocus

---

## 📝 Próximos Pasos (Post-MVP)

### Features Pro (Futuras)
- [ ] Sonidos ambiente para Focus Mode
- [ ] Exportar reportes en PDF
- [ ] Backup cifrado automático a Google Drive
- [ ] Temas avanzados personalizables
- [ ] Widgets de escritorio
- [ ] Estadísticas de rachas mensuales

### Publicación en Play Store
1. Crear cuenta de desarrollador ($25 USD único)
2. Generar AAB firmado: `./gradlew bundleRelease`
3. Preparar assets:
   - Icono 512x512 px
   - Screenshots (móvil y tablet)
   - Descripción optimizada para ASO
4. Completar cuestionario de seguridad (Data Safety)
5. Enviar a revisión (2-7 días hábiles)

---

## 📄 Licencia

**SelfFocus** - Código abierto para uso personal y educativo.
Prohibida la redistribución comercial sin autorización.

---

## 🤝 Soporte

Para issues técnicos o sugerencias, revisar la documentación oficial de:
- [Android Developers](https://developer.android.com/)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

**¡Gracias por usar SelfFocus! 🎯**
*Recupera el control de tu atención digital.*