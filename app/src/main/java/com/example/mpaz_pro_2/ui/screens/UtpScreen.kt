package com.example.mpaz_pro_2.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_2.data.local.CurricularOAEntity
import com.example.mpaz_pro_2.data.local.UserEntity
import com.example.mpaz_pro_2.model.ValidationEngine
import com.example.mpaz_pro_2.ui.viewmodel.MainViewModel
import com.example.mpaz_pro_2.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtpScreen(
    username: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val allOAs by viewModel.allOAs.collectAsState()
    val user by produceState<UserEntity?>(null, username) { value = viewModel.getUserByUsername(username) }
    val displayName = user?.name ?: username
    var selectedSubject by rememberSaveable { mutableStateOf("Lenguaje y Comunicación") }

    // Formulario para crear nuevo OA
    var showCreateForm by rememberSaveable { mutableStateOf(false) }
    var newOaCode by rememberSaveable { mutableStateOf("") }
    var newOaTitle by rememberSaveable { mutableStateOf("") }
    var newOaDesc by rememberSaveable { mutableStateOf("") }
    var codeError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf<String?>(null) }

    val subjects = listOf(
        "Lenguaje y Comunicación",
        "Matemática",
        "Ciencias Naturales",
        "Historia, Geografía y C. Sociales"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel UTP - Escuela Marcela Paz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) { Text("← Salir") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Jefatura UTP: $displayName 🏛️", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Gestión Curricular y Aprobación de las 4 Asignaturas Mineduc", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text("Seleccionar Asignatura:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                subjects.forEach { subj ->
                    FilterChip(
                        selected = selectedSubject == subj,
                        onClick = { selectedSubject = subj },
                        label = { Text(subj) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Objetivos de Aprendizaje (OAs):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = {
                        showCreateForm = !showCreateForm
                        if (showCreateForm && newOaCode.isBlank()) {
                            // Sugiere el siguiente código libre (OA05, OA06, ...)
                            var n = allOAs.size + 1
                            while (allOAs.any { it.oaCode.equals("OA%02d".format(n), ignoreCase = true) }) n++
                            newOaCode = "OA%02d".format(n)
                        }
                        codeError = null
                        titleError = null
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (showCreateForm) "Cerrar" else "+ Crear OA")
                }
            }

            // Formulario opcional para agregar un nuevo OA
            AnimatedVisibility(visible = showCreateForm) {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Nuevo Objetivo para $selectedSubject", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = newOaCode,
                            onValueChange = { newOaCode = it; codeError = null },
                            label = { Text("Código (Ej: OA05)") },
                            isError = codeError != null,
                            supportingText = { codeError?.let { Text(it) } },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newOaTitle,
                            onValueChange = { newOaTitle = it; titleError = null },
                            label = { Text("Título del OA") },
                            isError = titleError != null,
                            supportingText = { titleError?.let { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(value = newOaDesc, onValueChange = { newOaDesc = it }, label = { Text("Descripción Curricular") }, modifier = Modifier.fillMaxWidth())

                        Button(
                            onClick = {
                                codeError = ValidationEngine.validateOaCode(newOaCode, allOAs.map { it.oaCode })
                                titleError = ValidationEngine.validateRequired(newOaTitle, "El título", 3)
                                if (codeError == null && titleError == null) {
                                    val newOa = CurricularOAEntity(
                                        subject = selectedSubject,
                                        oaCode = newOaCode.trim().uppercase(),
                                        oaTitle = newOaTitle.trim(),
                                        description = newOaDesc.ifBlank { "Descripción estándar Mineduc." },
                                        status = "AUTORIZADO",
                                        utpResponsible = displayName
                                    )
                                    // Inserta realmente el OA (antes se usaba @Update con id 0 y no guardaba nada)
                                    viewModel.createOa(newOa)
                                    showCreateForm = false
                                    newOaCode = ""
                                    newOaTitle = ""
                                    newOaDesc = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar y Autorizar OA")
                        }
                    }
                }
            }

            HorizontalDivider()

            val filteredOAs = allOAs.filter { it.subject == selectedSubject }

            if (filteredOAs.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No hay OAs registrados para $selectedSubject. Haz clic en '+ Crear OA' arriba para registrar uno.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                filteredOAs.forEach { oa ->
                    OaUtpCard(
                        oa = oa,
                        onStatusChange = { newStatus ->
                            viewModel.updateOaStatus(oa.copy(status = newStatus, utpResponsible = displayName, lastModified = DateUtils.now()))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OaUtpCard(
    oa: CurricularOAEntity,
    onStatusChange: (String) -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${oa.oaCode}: ${oa.oaTitle}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(oa.status, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text(oa.description, style = MaterialTheme.typography.bodyMedium)
            Text("Responsable UTP: ${oa.utpResponsible}", style = MaterialTheme.typography.bodySmall)

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onStatusChange("AUTORIZADO") },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Autorizar")
                }
                OutlinedButton(
                    onClick = { onStatusChange("PUBLICADO") },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Publicar")
                }
                OutlinedButton(
                    onClick = { onStatusChange("DESHABILITADO") },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Deshabilitar")
                }
            }
        }
    }
}