package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.Country
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton

@Composable
fun QuizHudCard(
    targetCountry: Country,
    score: Int,
    streak: Int,
    feedback: String?,
    isCorrect: Boolean?,
    onNextQuestion: () -> Unit,
    onEndQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xF00B1220),
        border = BorderStroke(
            1.dp,
            when (isCorrect) {
                true -> Color(0xFF10B981)
                false -> Color(0xFFEF4444)
                null -> Color(0x5538BDF8)
            }
        ),
        shadowElevation = 16.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Score & Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🎯 GEOGRAPHY CHALLENGE", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    if (streak > 1) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x33F59E0B),
                            border = BorderStroke(1.dp, Color(0x66F59E0B))
                        ) {
                            Text(
                                text = "🔥 $streak STREAK",
                                color = Color(0xFFFDE68A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Score: $score",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    MinimalistCloseButton(onClick = onEndQuiz)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prompt Question
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(targetCountry.flagEmoji, fontSize = 34.sp)
                Column {
                    Text("Touch the country on the globe:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text(
                        text = targetCountry.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Interactive Feedback Message
            if (feedback != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCorrect == true) Color(0x2210B981) else Color(0x22EF4444),
                    border = BorderStroke(1.dp, if (isCorrect == true) Color(0x4410B981) else Color(0x44EF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = feedback,
                        color = if (isCorrect == true) Color(0xFF6EE7B7) else Color(0xFFFCA5A5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEndQuiz,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0x44EF4444)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Exit Quiz", fontSize = 12.sp)
                }

                Button(
                    onClick = onNextQuestion,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Skip / Next", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
