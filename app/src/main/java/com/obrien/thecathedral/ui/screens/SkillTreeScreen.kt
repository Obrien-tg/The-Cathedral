package com.obrien.thecathedral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thecathedral.model.SkillNode
import com.obrien.thecathedral.model.SkillProgress
import com.obrien.thecathedral.model.SkillTreeData
import com.obrien.thecathedral.ui.skilltree.SkillEdge
import com.obrien.thecathedral.ui.skilltree.SkillNode as UiSkillNode
import com.obrien.thecathedral.ui.skilltree.SkillTreeGraph
import com.obrien.thecathedral.ui.skilltree.SkillTreeLayout
import com.obrien.thecathedral.ui.theme.*
import com.obrien.thecathedral.viewmodel.SkillTreeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillTreeScreen(
    viewModel: SkillTreeViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = MonasteryBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Formation Path",
                        color = CathedralGold,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Parchment
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MonasteryBlack)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val progressMap = uiState.skillProgress.associateBy { it.nodeId }

            SkillTreeGraph(
                nodes = SkillTreeData.nodes.map { node ->
                    val prog = progressMap[node.id]
                    UiSkillNode(
                        id = node.id,
                        name = node.title,
                        position = SkillTreeLayout.positionFor(node.id),
                        unlocked = prog?.unlocked ?: false,
                        completed = prog?.completed ?: false,
                        pillar = node.pillar,
                        progress = prog?.progress ?: 0f,
                        tier = node.tier,
                        description = node.description
                    )
                },
                edges = SkillTreeData.edges.map { SkillEdge(it.from, it.to) },
                onNodeClick = { nodeId -> viewModel.selectNode(nodeId) }
            )

            // Subtle hint at the bottom
            Text(
                text = "Tap a node to inspect · Pinch to zoom",
                color = Parchment.copy(alpha = 0.35f),
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }

    // Detail bottom sheet
    if (uiState.selectedNodeId != null) {
        val node = uiState.selectedNode
        val progress = uiState.selectedProgress
        if (node != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.clearSelection() },
                sheetState = sheetState,
                containerColor = MonasteryBlack,
                contentColor = Parchment,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .background(Bronze.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                    )
                }
            ) {
                NodeDetailContent(
                    node = node,
                    progress = progress,
                    currentCounts = uiState.currentCountsFor(node),
                    onDismiss = { viewModel.clearSelection() }
                )
            }
        }
    }
}

@Composable
private fun NodeDetailContent(
    node: SkillNode,
    progress: SkillProgress?,
    currentCounts: Triple<Int, Int, Int>,
    onDismiss: () -> Unit
) {
    val (alarmHits, focusSessions, journalDays) = currentCounts
    val unlocked = progress?.unlocked ?: false
    val completed = progress?.completed ?: false
    val progressValue = progress?.progress ?: 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        // Status chip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusChip(
                text = when {
                    completed -> "COMPLETED"
                    unlocked -> "UNLOCKED"
                    else -> "LOCKED"
                },
                color = when {
                    completed -> RitualSuccess
                    unlocked -> CathedralGold
                    else -> Bronze
                }
            )
            Text(
                text = "Tier ${node.tier} · ${node.pillar}",
                color = MutedStone,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = node.title,
            color = CathedralGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        if (node.description.isNotBlank()) {
            Text(
                text = node.description,
                color = Parchment.copy(alpha = 0.85f),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Overall progress bar
        Text(
            text = "Overall Progress",
            color = MutedStone,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progressValue },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = CathedralGold,
            trackColor = Bronze.copy(alpha = 0.3f),
        )
        Text(
            text = "${(progressValue * 100).toInt()}%",
            color = Parchment.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Requirements breakdown
        Text(
            text = "Requirements",
            color = MutedStone,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))

        RequirementRow(
            label = "Ritual Completions",
            current = alarmHits,
            required = node.requiredCompletions
        )
        if (node.requiredFocusSessions > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            RequirementRow(
                label = "Focus Sessions",
                current = focusSessions,
                required = node.requiredFocusSessions
            )
        }
        if (node.requiredJournalDays > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            RequirementRow(
                label = "Journal Days",
                current = journalDays,
                required = node.requiredJournalDays
            )
        }

        // Locked message
        if (!unlocked) {
            Spacer(modifier = Modifier.height(20.dp))
            val parents = SkillTreeData.parentsOf(node.id)
            val parentNames = parents.mapNotNull { SkillTreeData.nodeById(it)?.title }
            Text(
                text = if (parentNames.isEmpty()) {
                    "This is the root of the path."
                } else {
                    "Locked until completed: ${parentNames.joinToString(" · ")}"
                },
                color = Bronze,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun StatusChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun RequirementRow(
    label: String,
    current: Int,
    required: Int
) {
    val ratio = (current.toFloat() / required.coerceAtLeast(1)).coerceIn(0f, 1f)
    val met = current >= required

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Parchment.copy(alpha = 0.8f), fontSize = 13.sp)
            Text(
                text = "$current / $required",
                color = if (met) RitualSuccess else CathedralGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = if (met) RitualSuccess else CathedralGold,
            trackColor = Bronze.copy(alpha = 0.25f),
        )
    }
}
