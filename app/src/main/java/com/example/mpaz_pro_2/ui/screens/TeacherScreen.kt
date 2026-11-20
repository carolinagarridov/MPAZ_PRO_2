package com.example.mpaz_pro_2.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_2.data.local.EvaluationConfigEntity
import com.example.mpaz_pro_2.data.local.PedagogicalReportEntity
import com.example.mpaz_pro_2.data.local.QuestionEntity
import com.example.mpaz_pro_2.data.local.UserEntity
import com.example.mpaz_pro_2.data.model.ObjetivoDto
import com.example.mpaz_pro_2.data.model.Post
import com.example.mpaz_pro_2.model.ValidationEngine
import com.example.mpaz_pro_2.ui.components.StudentDetailDialog
import com.example.mpaz_pro_2.ui.components.UserProfileHeader
import com.example.mpaz_pro_2.ui.viewmodel.MainViewModel
import com.example.mpaz_pro_2.utils.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScreen(
    username: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val authorizedOAs by viewModel.authorizedOAs.collectAsState()
    val allReports by viewModel.allReports.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allAttempts by viewModel.allAttempts.collectAsState()

    // Estados remotos (API externa + Microservicio Spring Boot)
    val postList by viewModel.postList.collectAsState()
    val backendObjetivos by viewModel.backendObjetivos.collectAsState()
    val backendMsg by viewModel.backendStatusMsg.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Docente actual (se busca por username, no por nombre visible)
    val currentTeacherUser by produceState<UserEntity?>(null, username) {
        value = viewModel.getUserByUsername(username)
    }
    val teacherName = currentTeacherUser?.name ?: username

    // Modal de detalle del estudiante
    var selectedStudentForDetail by remember { mutableStateOf<UserEntity?>(null) }

    // OA elegido: si no hay selección válida, se usa el primero autorizado
    var chosenOa by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedOa = authorizedOAs.firstOrNull { it.oaCode == chosenOa } ?: authorizedOAs.firstOrNull()
    val selectedOaCode = selectedOa?.oaCode ?: ""

    // Flows recordados por OA (evita reiniciar la colección en cada recomposición)
    val currentConfigFlow = remember(selectedOaCode) { viewModel.getConfigForOa(selectedOaCode) }
    val currentConfig by currentConfigFlow.collectAsState(initial = null)
    val questionsFlow = remember(selectedOaCode) { viewModel.getQuestionsForOa(selectedOaCode) }
    val questions by questionsFlow.collectAsState(initial = emptyList())

    // Parámetros de la prueba
    var maxAttempts by rememberSaveable(selectedOaCode) { mutableStateOf("3") }
    var endDate by rememberSaveable(selectedOaCode) { mutableStateOf("2026-11-30") }
    var isGraded by rememberSaveable(selectedOaCode) { mutableStateOf(true) }
    var isEnabled by rememberSaveable(selectedOaCode) { mutableStateOf(true) }
    var attemptsError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    // Formulario de nueva pregunta
    var showAddQuestion by rememberSaveable { mutableStateOf(false) }
    var newQText by rememberSaveable { mutableStateOf("") }
    var newOpA by rememberSaveable { mutableStateOf("") }
    var newOpB by rememberSaveable { mutableStateOf("") }
    var newOpC by rememberSaveable { mutableStateOf("") }
    var newCorrect by rememberSaveable { mutableStateOf("A") }
    var newExplanation by rememberSaveable { mutableStateOf("") }
    var questionError by remember { mutableStateOf<String?>(null) }

    // Informe pedagógico
    var reportStudentUsername by rememberSaveable { mutableStateOf<String?>(null) }
    var strengths by rememberSaveable { mutableStateOf("") }
    var reinforcements by rememberSaveable { mutableStateOf("") }
    var observations by rememberSaveable { mutableStateOf("") }
    var reportError by remember { mutableStateOf<String?>(null) }

    val reportStudent = allStudents.firstOrNull { it.username == reportStudentUsername }
    val bestScore: Int? = reportStudent?.let { st ->
        allAttempts
            .filter { it.studentUsername == st.username && it.oaCode == selectedOaCode }
            .maxOfOrNull { it.score }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel Docente - Marcela Paz", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = onNavigateBack) { Text("← Salir") } }
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
            // 1. Perfil propio del docente
            UserProfileHeader(
                user = currentTeacherUser,
                fallbackName = teacherName,
                fallbackRole = "DOCENTE"
            )

            // 2. Monitoreo de alumnos
            Text(
                "👥 Estudiantes del Nivel (Ver Perfiles y Avance):",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            if (allStudents.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No hay estudiantes registrados en la base de datos.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    allStudents.forEach { student ->
                        ElevatedCard(
                            onClick = { selectedStudentForDetail = student },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(student.name, fontWeight = FontWeight.Bold)
                                    Text("@${student.username}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("Ver Avance ➔", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // 3. Selección de OA autorizado
            Text("1. Seleccionar Objetivo Autorizado por UTP:", fontWeight = FontWeight.Bold)
            if (authorizedOAs.isEmpty()) {
                Text("UTP aún no autoriza objetivos de aprendizaje.", modifier = Modifier.padding(8.dp))
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    authorizedOAs.forEach { oa ->
                        FilterChip(
                            selected = selectedOaCode == oa.oaCode,
                            onClick = { chosenOa = oa.oaCode },
                            label = { Text("${oa.oaCode} - ${oa.subject}") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // 4. Parámetros de evaluación
            Text("2. Configurar Parámetros de la Prueba Final:", fontWeight = FontWeight.Bold)
            currentConfig?.let {
                Text(
                    "Vigente: ${it.maxAttempts} intentos · plazo ${it.endDate} · " +
                            (if (it.isEnabled) "habilitada" else "deshabilitada"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = maxAttempts,
                    onValueChange = { maxAttempts = it; attemptsError = null },
                    label = { Text("Máx. Intentos") },
                    isError = attemptsError != null,
                    supportingText = { attemptsError?.let { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it; dateError = null },
                    label = { Text("Fecha Plazo (AAAA-MM-DD)") },
                    isError = dateError != null,
                    supportingText = { dateError?.let { Text(it) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.4f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isGraded, onCheckedChange = { isGraded = it })
                Text("Evaluación Sumativa (Con nota)")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isEnabled, onCheckedChange = { isEnabled = it })
                Text("Prueba habilitada para estudiantes")
            }

            Button(
                enabled = selectedOa != null,
                onClick = {
                    attemptsError = ValidationEngine.validateMaxAttempts(maxAttempts)
                    dateError = ValidationEngine.validateDate(endDate)
                    if (attemptsError == null && dateError == null) {
                        viewModel.saveEvaluationConfig(
                            EvaluationConfigEntity(
                                oaCode = selectedOaCode,
                                maxAttempts = maxAttempts.trim().toInt(),
                                endDate = endDate.trim(),
                                isGraded = isGraded,
                                isEnabled = isEnabled,
                                teacherName = teacherName
                            )
                        )
                        scope.launch { snackbarHostState.showSnackbar("✓ Parámetros de $selectedOaCode guardados.") }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Parámetros de Prueba")
            }

            HorizontalDivider()

            // 5. Preguntas del cuestionario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("3. Cuestionario de $selectedOaCode (${questions.size} preguntas)", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Button(
                    enabled = selectedOa != null,
                    onClick = { showAddQuestion = !showAddQuestion; questionError = null },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (showAddQuestion) "Cerrar" else "+ Nueva Pregunta")
                }
            }

            questions.forEachIndexed { index, q ->
                Text("${index + 1}. ${q.questionText}", style = MaterialTheme.typography.bodyMedium)
            }

            AnimatedVisibility(visible = showAddQuestion) {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Añadir Pregunta para $selectedOaCode", fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = newQText, onValueChange = { newQText = it; questionError = null }, label = { Text("Enunciado de la Pregunta") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newOpA, onValueChange = { newOpA = it; questionError = null }, label = { Text("Alternativa A") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newOpB, onValueChange = { newOpB = it; questionError = null }, label = { Text("Alternativa B") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newOpC, onValueChange = { newOpC = it; questionError = null }, label = { Text("Alternativa C (opcional)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newExplanation, onValueChange = { newExplanation = it }, label = { Text("Explicación Didáctica") }, modifier = Modifier.fillMaxWidth())

                        Text("Selecciona la Respuesta Correcta:", fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = newCorrect == "A", onClick = { newCorrect = "A"; questionError = null }, label = { Text("Correcta: A") })
                            FilterChip(selected = newCorrect == "B", onClick = { newCorrect = "B"; questionError = null }, label = { Text("Correcta: B") })
                            FilterChip(selected = newCorrect == "C", onClick = { newCorrect = "C"; questionError = null }, label = { Text("Correcta: C") })
                        }

                        questionError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }

                        Button(
                            onClick = {
                                questionError = ValidationEngine.validateRequired(newQText, "El enunciado", 5)
                                    ?: ValidationEngine.validateRequired(newOpA, "La alternativa A")
                                            ?: ValidationEngine.validateRequired(newOpB, "La alternativa B")
                                            ?: ValidationEngine.validateCorrectOption(newCorrect, newOpA, newOpB, newOpC)
                                if (questionError == null) {
                                    viewModel.saveQuestion(
                                        QuestionEntity(
                                            oaCode = selectedOaCode,
                                            questionText = newQText.trim(),
                                            optionA = newOpA.trim(),
                                            optionB = newOpB.trim(),
                                            optionC = newOpC.trim().ifBlank { "N/A" },
                                            correctAnswer = newCorrect,
                                            explanation = newExplanation.ifBlank { "¡Buen trabajo!" }
                                        )
                                    )
                                    showAddQuestion = false
                                    newQText = ""; newOpA = ""; newOpB = ""; newOpC = ""
                                    newExplanation = ""; newCorrect = "A"
                                    scope.launch { snackbarHostState.showSnackbar("✓ Pregunta guardada en $selectedOaCode.") }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar Pregunta en Cuestionario")
                        }
                    }
                }
            }

            HorizontalDivider()

            // 6. Informe pedagógico
            Text("4. Emitir Informe Pedagógico Reservado:", fontWeight = FontWeight.Bold)
            if (allStudents.isEmpty()) {
                Text("Se necesita al menos un estudiante registrado para emitir informes.", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Estudiante:", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allStudents.forEach { st ->
                        FilterChip(
                            selected = reportStudentUsername == st.username,
                            onClick = { reportStudentUsername = st.username; reportError = null },
                            label = { Text(st.name) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                if (reportStudent != null && selectedOa != null) {
                    Text(
                        "OA: ${selectedOa.oaCode} (${selectedOa.subject}) · Mejor puntaje del alumno: " +
                                (bestScore?.let { "$it%" } ?: "sin intentos"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            OutlinedTextField(value = strengths, onValueChange = { strengths = it; reportError = null }, label = { Text("Fortalezas Diagnosticadas") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = reinforcements, onValueChange = { reinforcements = it; reportError = null }, label = { Text("Áreas de Refuerzo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = observations, onValueChange = { observations = it; reportError = null }, label = { Text("Observaciones Docentes") }, modifier = Modifier.fillMaxWidth())

            reportError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                enabled = selectedOa != null && allStudents.isNotEmpty(),
                onClick = {
                    reportError = when {
                        reportStudent == null -> "Seleccione un estudiante."
                        else -> ValidationEngine.validateRequired(strengths, "Las fortalezas", 5)
                            ?: ValidationEngine.validateRequired(reinforcements, "Las áreas de refuerzo", 5)
                            ?: ValidationEngine.validateRequired(observations, "Las observaciones", 5)
                    }
                    if (reportError == null && reportStudent != null && selectedOa != null) {
                        viewModel.saveReport(
                            PedagogicalReportEntity(
                                studentUsername = reportStudent.username,
                                studentName = reportStudent.name,
                                subject = selectedOa.subject,
                                oaCode = selectedOa.oaCode,
                                globalScore = bestScore ?: 0,
                                strengths = strengths.trim(),
                                reinforcementAreas = reinforcements.trim(),
                                teacherObservations = observations.trim(),
                                validationDate = DateUtils.today()
                            )
                        )
                        strengths = ""; reinforcements = ""; observations = ""
                        scope.launch { snackbarHostState.showSnackbar("✓ Informe registrado.") }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Informe Pedagógico")
            }

            HorizontalDivider()

            // 7. Informes registrados
            Text("📋 Informes Pedagógicos Emitidos (${allReports.size}):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (allReports.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
                    Text("Aún no se han registrado informes pedagógicos en la base de datos.", modifier = Modifier.padding(14.dp))
                }
            } else {
                allReports.asReversed().forEach { rep ->
                    ElevatedCard(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Alumno: ${rep.studentName}", fontWeight = FontWeight.Bold)
                                Text("Fecha: ${rep.validationDate}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text("OA Evaluado: ${rep.oaCode} (${rep.subject})", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            Text("Puntaje Global: ${rep.globalScore}%", fontWeight = FontWeight.SemiBold)
                            Text("💪 Fortalezas: ${rep.strengths}", style = MaterialTheme.typography.bodyMedium)
                            Text("🎯 Áreas de Refuerzo: ${rep.reinforcementAreas}", style = MaterialTheme.typography.bodyMedium)
                            Text("📝 Observaciones: ${rep.teacherObservations}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            HorizontalDivider()

            // 8. CRUD Microservicio Spring Boot
            MicroservicioCrudSection(
                objetivos = backendObjetivos,
                statusMessage = backendMsg,
                onCreate = { subj, code, title, desc -> viewModel.addBackendObjetivo(subj, code, title, desc) },
                onToggleUpdate = { oa -> viewModel.toggleStatusBackendObjetivo(oa) },
                onDelete = { id -> viewModel.removeBackendObjetivo(id) },
                onRefresh = { viewModel.loadBackendObjetivos() }
            )

            HorizontalDivider()

            // 9. API Externa (Circulares Mineduc)
            CircularesMineducSection(posts = postList)
        }
    }

    // Ficha completa del estudiante
    selectedStudentForDetail?.let { student ->
        StudentDetailDialog(
            student = student,
            viewModel = viewModel,
            onDismiss = { selectedStudentForDetail = null }
        )
    }
}

@Composable
fun MicroservicioCrudSection(
    objetivos: List<ObjetivoDto>,
    statusMessage: String?,
    onCreate: (String, String, String, String) -> Unit,
    onToggleUpdate: (ObjetivoDto) -> Unit,
    onDelete: (Long) -> Unit,
    onRefresh: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "☁️ Gestión Curricular Remota (Spring Boot CRUD)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onRefresh) { Text("↻ Sincronizar") }
        }

        statusMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Código OA (Ej: OA05)") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título del Objetivo") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Descripción") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (code.isNotBlank() && title.isNotBlank()) {
                    onCreate("Lenguaje y Comunicación", code.trim(), title.trim(), desc.ifBlank { "Objetivo 2° Básico" })
                    code = ""; title = ""; desc = ""
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Guardar en Microservicio (POST)")
        }

        objetivos.forEach { oa ->
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${oa.oaCode}: ${oa.oaTitle}", fontWeight = FontWeight.Bold)
                        Text(oa.status, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                    }
                    Text(oa.description, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onToggleUpdate(oa) }) {
                            Text("Cambiar Estado (PUT)")
                        }
                        oa.id?.let { id ->
                            OutlinedButton(onClick = { onDelete(id) }) {
                                Text("Eliminar (DELETE)", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CircularesMineducSection(posts: List<Post>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "📢 Circulares y Comunicados Externos (API REST)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (posts.isEmpty()) {
            Text("Cargando comunicados desde servidor...", style = MaterialTheme.typography.bodySmall)
        } else {
            posts.take(3).forEach { post ->
                ElevatedCard(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(post.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(post.body, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}