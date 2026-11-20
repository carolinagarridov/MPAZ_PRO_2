package com.example.mpaz_pro_2.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_2.data.local.StudentAttemptEntity
import com.example.mpaz_pro_2.data.local.UserEntity
import com.example.mpaz_pro_2.ui.viewmodel.MainViewModel
import com.example.mpaz_pro_2.utils.AudioHelper
import com.example.mpaz_pro_2.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(
    username: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val audioHelper = remember { AudioHelper(context) }
    DisposableEffect(Unit) { onDispose { audioHelper.shutdown() } }

    val user by produceState<UserEntity?>(null, username) { value = viewModel.getUserByUsername(username) }
    val displayName = user?.name ?: username

    val publishedOAs by viewModel.publishedOAs.collectAsState()

    // OA elegido: si no hay selección válida, se usa el primero publicado
    var chosenOa by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedOaCode = publishedOAs.firstOrNull { it.oaCode == chosenOa }?.oaCode
        ?: publishedOAs.firstOrNull()?.oaCode
        ?: ""

    // Los Flow se recuerdan por OA: crear uno nuevo en cada recomposición reiniciaba la colección
    val questionsFlow = remember(selectedOaCode) { viewModel.getQuestionsForOa(selectedOaCode) }
    val attemptsFlow = remember(username, selectedOaCode) { viewModel.getAttemptsForStudent(username, selectedOaCode) }
    val configFlow = remember(selectedOaCode) { viewModel.getConfigForOa(selectedOaCode) }
    val questions by questionsFlow.collectAsState(initial = emptyList())
    val attempts by attemptsFlow.collectAsState(initial = emptyList())
    val config by configFlow.collectAsState(initial = null)

    // Estado del cuestionario (sobrevive a rotaciones)
    var currentQIndex by rememberSaveable { mutableStateOf(0) }
    var selectedAnswer by rememberSaveable { mutableStateOf("") }
    var correctAnswersCount by rememberSaveable { mutableStateOf(0) }
    var isAnsweredCurrent by rememberSaveable { mutableStateOf(false) }
    var quizFinished by rememberSaveable { mutableStateOf(false) }
    var answersLog by rememberSaveable { mutableStateOf("") }

    fun resetQuiz() {
        currentQIndex = 0
        selectedAnswer = ""
        correctAnswersCount = 0
        isAnsweredCurrent = false
        quizFinished = false
        answersLog = ""
    }

    val usedAttemptsCount = attempts.size
    val maxAllowedAttempts = config?.maxAttempts ?: 3
    val isBlocked = usedAttemptsCount >= maxAllowedAttempts
    val isDisabled = config?.isEnabled == false
    val isExpired = config?.endDate?.let { DateUtils.today() > it } ?: false

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Estudiante (2° Básico)", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("¡Hola, $displayName! 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Curso: 2° Básico | Escuela Escritora Marcela Paz", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (publishedOAs.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aún no hay cuestionarios publicados por UTP.", modifier = Modifier.padding(16.dp))
                }
            } else {
                Text("Selecciona una Asignatura para Responder:", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    publishedOAs.forEach { oa ->
                        FilterChip(
                            selected = selectedOaCode == oa.oaCode,
                            onClick = {
                                chosenOa = oa.oaCode
                                resetQuiz()
                            },
                            label = { Text("${oa.subject} (${oa.oaCode})") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Intentos Realizados: $usedAttemptsCount de $maxAllowedAttempts permitidos", fontWeight = FontWeight.Bold)
                        config?.let {
                            Text(
                                "Plazo: ${it.endDate} · ${if (it.isGraded) "Con nota" else "Formativa"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Se evalúa primero "terminado" para que el resultado no desaparezca al usar el último intento
                if (quizFinished) {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎉 ¡Cuestionario Finalizado!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            val scorePct = if (questions.isNotEmpty()) (correctAnswersCount * 100) / questions.size else 0
                            Text("Respondiste correctamente $correctAnswersCount de ${questions.size} preguntas ($scorePct%).", style = MaterialTheme.typography.bodyLarge)

                            if (!isBlocked) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { resetQuiz() }, shape = RoundedCornerShape(12.dp)) {
                                    Text("Rendir Cuestionario Nuevamente")
                                }
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Ya usaste todos tus intentos para este cuestionario.", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else if (isDisabled) {
                    InfoCard("⛔ Este cuestionario está deshabilitado por tu docente.")
                } else if (isExpired) {
                    InfoCard("⌛ El plazo de este cuestionario terminó el ${config?.endDate}.")
                } else if (isBlocked) {
                    InfoCard("⚠️ Has completado el máximo de intentos permitidos ($maxAllowedAttempts) para este cuestionario.")
                } else if (questions.isNotEmpty() && currentQIndex < questions.size) {
                    val q = questions[currentQIndex]

                    ElevatedCard(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Pregunta ${currentQIndex + 1} de ${questions.size}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Button(onClick = { audioHelper.speak(q.questionText) }, shape = RoundedCornerShape(10.dp)) {
                                    Text("🔊 Leer")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(q.questionText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Opciones A, B, C
                    listOf("A" to q.optionA, "B" to q.optionB, "C" to q.optionC).forEach { (code, text) ->
                        if (text.isNotBlank() && text != "N/A") {
                            OutlinedButton(
                                onClick = {
                                    if (!isAnsweredCurrent) {
                                        selectedAnswer = code
                                        isAnsweredCurrent = true
                                        val isCorrect = code == q.correctAnswer
                                        if (isCorrect) correctAnswersCount++
                                        answersLog += "P${currentQIndex + 1}:$code${if (isCorrect) "✓" else "✗"} "
                                        audioHelper.speak(if (isCorrect) "¡Correcto!" else "Inténtalo en la próxima.")
                                    }
                                },
                                enabled = !isAnsweredCurrent,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("$code) $text")
                            }
                        }
                    }

                    // Retroalimentación y botón siguiente
                    AnimatedVisibility(visible = isAnsweredCurrent) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedAnswer == q.correctAnswer) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selectedAnswer == q.correctAnswer) "¡Muy bien! ${q.explanation}" else "Incorrecto. ${q.explanation}",
                                    modifier = Modifier.padding(14.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    if (currentQIndex + 1 < questions.size) {
                                        currentQIndex++
                                        selectedAnswer = ""
                                        isAnsweredCurrent = false
                                    } else {
                                        // Guardar el intento final en Room
                                        quizFinished = true
                                        val attempt = StudentAttemptEntity(
                                            studentUsername = username,
                                            oaCode = selectedOaCode,
                                            attemptNumber = usedAttemptsCount + 1,
                                            score = (correctAnswersCount * 100) / questions.size,
                                            totalQuestions = questions.size,
                                            correctAnswersCount = correctAnswersCount,
                                            timestamp = DateUtils.now(),
                                            answersSummary = answersLog.trim()
                                        )
                                        viewModel.saveStudentAttempt(attempt)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (currentQIndex + 1 < questions.size) "Siguiente Pregunta ➔" else "Finalizar y Guardar Intento")
                            }
                        }
                    }
                } else {
                    Text("No hay preguntas registradas para esta asignatura.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { audioHelper.stop(); onNavigateBack() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
private fun InfoCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(message, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
    }
}