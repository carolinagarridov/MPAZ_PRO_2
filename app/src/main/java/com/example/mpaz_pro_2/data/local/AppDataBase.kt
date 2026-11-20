package com.example.mpaz_pro_2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CurricularOAEntity::class,
        QuestionEntity::class,
        EvaluationConfigEntity::class,
        StudentAttemptEntity::class,
        PedagogicalReportEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun curricularDao(): CurricularDao
    abstract fun questionDao(): QuestionDao
    abstract fun evaluationConfigDao(): EvaluationConfigDao
    abstract fun attemptDao(): AttemptDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpaz_database_v8"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(getDatabase(context))
                }
            }

            suspend fun populateInitialData(db: AppDatabase) {
                try {
                    // 1. Objetivos de Aprendizaje para las 4 asignaturas Mineduc
                    val oas = listOf(
                        CurricularOAEntity(
                            id = 1,
                            subject = "Lenguaje y Comunicación",
                            oaCode = "OA01",
                            oaTitle = "Comprensión de Textos y Sustantivos",
                            unit = "Unidad 1",
                            description = "Leer independientemente y reconocer tipos de palabras en textos cortos.",
                            status = "PUBLICADO"
                        ),
                        CurricularOAEntity(
                            id = 2,
                            subject = "Matemática",
                            oaCode = "OA02",
                            oaTitle = "Conteo y Secuencias hasta 100",
                            unit = "Unidad 1",
                            description = "Resolver problemas de secuencias numéricas y adiciones básicas.",
                            status = "PUBLICADO"
                        ),
                        CurricularOAEntity(
                            id = 3,
                            subject = "Ciencias Naturales",
                            oaCode = "OA03",
                            oaTitle = "Órganos del Cuerpo Humano",
                            unit = "Unidad 1",
                            description = "Identificar ubicación y función del corazón, pulmones y estómago.",
                            status = "PUBLICADO"
                        ),
                        CurricularOAEntity(
                            id = 4,
                            subject = "Historia, Geografía y C. Sociales",
                            oaCode = "OA04",
                            oaTitle = "Planos y Puntos Cardinales",
                            unit = "Unidad 1",
                            description = "Ubicación espacial básica en mapas y planos sencillos.",
                            status = "PUBLICADO"
                        )
                    )
                    oas.forEach { db.curricularDao().insertOA(it) }

                    // 2. Banco de Preguntas Únicas y Variadas (IDs Fijos)
                    val questions = listOf(
                        // LENGUAJE Y COMUNICACIÓN (OA01)
                        QuestionEntity(
                            id = 1,
                            oaCode = "OA01",
                            questionText = "¿Cuál de las siguientes palabras es un sustantivo común?",
                            optionA = "Perro", optionB = "Correr", optionC = "Alegre",
                            correctAnswer = "A",
                            explanation = "'Perro' nombra a un animal de forma general."
                        ),
                        QuestionEntity(
                            id = 2,
                            oaCode = "OA01",
                            questionText = "¿Qué tipo de palabra es 'Chile'?",
                            optionA = "Verbo", optionB = "Sustantivo Propio", optionC = "Adjetivo",
                            correctAnswer = "B",
                            explanation = "Los nombres de países son sustantivos propios y se escriben con mayúscula."
                        ),
                        QuestionEntity(
                            id = 3,
                            oaCode = "OA01",
                            questionText = "¿Qué signo se utiliza para realizar una pregunta escrita?",
                            optionA = "Signo de interrogación (¿ ?)", optionB = "Punto final (.)", optionC = "Coma (,)",
                            correctAnswer = "A",
                            explanation = "Los signos de interrogación ¿? se colocan al inicio y final de una pregunta."
                        ),
                        QuestionEntity(
                            id = 4,
                            oaCode = "OA01",
                            questionText = "¿Cuál es el opuesto (antónimo) de la palabra 'grande'?",
                            optionA = "Alto", optionB = "Pequeño", optionC = "Enorme",
                            correctAnswer = "B",
                            explanation = "Lo contrario de algo grande es algo pequeño."
                        ),

                        // MATEMÁTICA (OA02)
                        QuestionEntity(
                            id = 5,
                            oaCode = "OA02",
                            questionText = "¿Qué número sigue en la secuencia: 10, 20, 30, ___?",
                            optionA = "35", optionB = "40", optionC = "50",
                            correctAnswer = "B",
                            explanation = "La secuencia avanza de 10 en 10."
                        ),
                        QuestionEntity(
                            id = 6,
                            oaCode = "OA02",
                            questionText = "¿Cuánto es 15 + 10?",
                            optionA = "25", optionB = "20", optionC = "30",
                            correctAnswer = "A",
                            explanation = "15 más una decena es igual a 25."
                        ),
                        QuestionEntity(
                            id = 7,
                            oaCode = "OA02",
                            questionText = "Si tienes 8 lápices y te regalan 2 más, ¿cuántos lápices tienes en total?",
                            optionA = "9", optionB = "10", optionC = "12",
                            correctAnswer = "B",
                            explanation = "8 + 2 suma 10 en total."
                        ),
                        QuestionEntity(
                            id = 8,
                            oaCode = "OA02",
                            questionText = "¿Cuál de los siguientes números es el MAYOR?",
                            optionA = "45", optionB = "54", optionC = "39",
                            correctAnswer = "B",
                            explanation = "54 tiene 5 decenas, siendo superior a 45 y 39."
                        ),

                        // CIENCIAS NATURALES (OA03)
                        QuestionEntity(
                            id = 9,
                            oaCode = "OA03",
                            questionText = "¿Qué órgano se encarga de bombear la sangre por todo el cuerpo?",
                            optionA = "Estómago", optionB = "Pulmones", optionC = "Corazón",
                            correctAnswer = "C",
                            explanation = "El corazón es el órgano muscular impulsor de la sangre."
                        ),
                        QuestionEntity(
                            id = 10,
                            oaCode = "OA03",
                            questionText = "¿Qué órgano utilizas principalmente para respirar aire?",
                            optionA = "Pulmones", optionB = "Corazón", optionC = "Estómago",
                            correctAnswer = "A",
                            explanation = "Los pulmones absorben el oxígeno del aire."
                        ),
                        QuestionEntity(
                            id = 11,
                            oaCode = "OA03",
                            questionText = "¿A qué lugar llega la comida después de tragarla?",
                            optionA = "Cerebro", optionB = "Estómago", optionC = "Pulmones",
                            correctAnswer = "B",
                            explanation = "El estómago procesa los alimentos ingeridos."
                        ),

                        // HISTORIA, GEOGRAFÍA Y C. SOCIALES (OA04)
                        QuestionEntity(
                            id = 12,
                            oaCode = "OA04",
                            questionText = "¿Por dónde sale el Sol por las mañanas en Chile?",
                            optionA = "Este (Cordillera de los Andes)", optionB = "Oeste (Océano Pacífico)", optionC = "Sur",
                            correctAnswer = "A",
                            explanation = "El Sol amanece por el Este, sobre la Cordillera de los Andes."
                        ),
                        QuestionEntity(
                            id = 13,
                            oaCode = "OA04",
                            questionText = "¿Para qué sirve un plano sencillo de la escuela o la sala?",
                            optionA = "Para dibujar paisajes", optionB = "Para ubicarse y encontrar lugares", optionC = "Para pintar",
                            correctAnswer = "B",
                            explanation = "Un plano ayuda a orientarse y ubicar elementos en el espacio."
                        ),
                        QuestionEntity(
                            id = 14,
                            oaCode = "OA04",
                            questionText = "¿Cuál de las siguientes opciones es un Punto Cardinal?",
                            optionA = "Norte", optionB = "Arriba", optionC = "Derecha",
                            correctAnswer = "A",
                            explanation = "Los puntos cardinales principales son Norte, Sur, Este y Oeste."
                        )
                    )
                    questions.forEach { db.questionDao().insertQuestion(it) }

                    // 3. Configuración Inicial de Pruebas
                    db.evaluationConfigDao().insertOrUpdateConfig(EvaluationConfigEntity(oaCode = "OA01", maxAttempts = 3, isEnabled = true))
                    db.evaluationConfigDao().insertOrUpdateConfig(EvaluationConfigEntity(oaCode = "OA02", maxAttempts = 3, isEnabled = true))
                    db.evaluationConfigDao().insertOrUpdateConfig(EvaluationConfigEntity(oaCode = "OA03", maxAttempts = 3, isEnabled = true))
                    db.evaluationConfigDao().insertOrUpdateConfig(EvaluationConfigEntity(oaCode = "OA04", maxAttempts = 3, isEnabled = true))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}