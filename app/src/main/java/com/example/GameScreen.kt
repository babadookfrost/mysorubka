package com.example

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.isActive
import kotlin.math.*

@OptIn(ExperimentalTextApi::class)
@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val gameState = remember { GameState() }
    val highscores by viewModel.topHighscores.collectAsStateWithLifecycle()
    var tickCount by remember { mutableStateOf(0) }
    var upgradeChoices by remember { mutableStateOf(emptyList<SkillDef>()) }
    LaunchedEffect(Unit) {
        viewModel.initSound()
        var lastTime = System.nanoTime()
        while (isActive) {
            withFrameNanos { ft ->
                val dt = ((ft - lastTime) / 1_000_000_000f).coerceAtMost(0.1f)
                lastTime = ft
                if (gameState.status == "play" && gameState.player.skillPoints > 0 && upgradeChoices.isEmpty()) {
                    upgradeChoices = UpgradeSystem.generateSkillChoices(gameState)
                    if (upgradeChoices.isNotEmpty()) gameState.status = "upgrade"
                }
                gameState.update(dt)
                tickCount++
            }
        }
    }
    val topScores = remember(highscores) { highscores.sortedByDescending { it.score }.take(10) }
    val tm = rememberTextMeasurer()
    Box(Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
        Canvas(Modifier.fillMaxSize().pointerInput(gameState.status) {
            if (gameState.status == "play") {
                detectDragGestures(
                    onDragStart = { o -> gameState.fireRequest = true; gameState.aimX = o.x + gameState.camera.x; gameState.aimY = o.y + gameState.camera.y },
                    onDrag = { c, _ -> c.consume(); gameState.aimX = c.position.x + gameState.camera.x; gameState.aimY = c.position.y + gameState.camera.y },
                    onDragEnd = { gameState.fireRequest = false },
                    onDragCancel = { gameState.fireRequest = false }
                )
            } else {
                detectTapGestures { if (gameState.status == "menu" || gameState.status == "death") gameState.start() }
            }
        }) {
            @Suppress("UNUSED_VARIABLE") val tick = tickCount; val cw = size.width; val ch = size.height; gameState.viewW = cw; gameState.viewH = ch
            if (gameState.status == "menu") {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF141824), Color(0xFF0A0A0A))), size = size)
                val tl = tm.measure("BE3DNA", TextStyle(color = Color(0xFFC41E3A), fontSize = min(80f, cw / 12f).sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                drawText(tl, topLeft = Offset((cw - tl.size.width) / 2f, ch * 0.20f))
                val sl = tm.measure("Dark Horror Shooter", TextStyle(color = Color(0xFFE8E2D0), fontSize = 18.sp, fontFamily = FontFamily.Monospace))
                drawText(sl, topLeft = Offset((cw - sl.size.width) / 2f, ch * 0.20f + tl.size.height + 20f))
                val pl = tm.measure("TAP TO START", TextStyle(color = Color(0xFFF5C518), fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                drawText(pl, topLeft = Offset((cw - pl.size.width) / 2f, ch * 0.20f + tl.size.height + sl.size.height + 50f))
                return@Canvas
            }
            clipRect(0f, 0f, cw, ch) {
                val cx = gameState.camera.x; val cy = gameState.camera.y; val ts = Cfg.TILE
                val x0 = max(0, (cx / ts).toInt() - 1); val y0 = max(0, (cy / ts).toInt() - 1)
                val x1 = min(Cfg.ROOM_W, ((cx + cw) / ts).toInt() + 1); val y1 = min(Cfg.ROOM_H, ((cy + ch) / ts).toInt() + 1)
                for (ty in y0 until y1) for (tx in x0 until x1) {
                    val wall = gameState.map.grid[gameState.map.idx(tx, ty)] == 1
                    val px = tx * ts - cx; val py = ty * ts - cy
                    if (wall) {
                        drawRect(if ((tx + ty) % 2 == 0) Color(0xFF1E1418) else Color(0xFF1A1014), topLeft = Offset(px, py), size = Size(ts, ts))
                        drawRect(Color(0x50000000), topLeft = Offset(px + 1f, py + 1f), size = Size(ts - 2f, ts - 2f), style = Stroke(1f))
                    } else drawRect(if ((tx + ty) % 2 == 0) Color(0xFF0D0F14) else Color(0xFF0B0D12), topLeft = Offset(px, py), size = Size(ts, ts))
                }
                for (pt in gameState.particles) { drawCircle(Color(pt.color).copy(alpha = (pt.life * 2f).coerceIn(0f, 1f)), radius = pt.size, center = Offset(pt.x - cx, pt.y - cy)) }
                for (b in gameState.bullets) { drawCircle(if (b.friendly) Color(0xFFF5C518) else Color(0xFFFF4D6D), radius = b.r, center = Offset(b.x - cx, b.y - cy)) }
                for (orb in gameState.xpOrbs) { drawCircle(Color(0xFF5AD1FF).copy(alpha = 0.8f), radius = 5f, center = Offset(orb.x - cx, orb.y - cy)) }
                for (di in gameState.droppedItems) { val col = Color(Items.rarityColor(di.def.rarity)); drawCircle(col, radius = 8f, center = Offset(di.x - cx, di.y - cy)); drawCircle(col.copy(alpha = 0.3f), radius = 14f, center = Offset(di.x - cx, di.y - cy)) }
                for (e in gameState.enemies) {
                    if (e.dead) continue
                    val c = if (e.flash > 0f) Color.White else (e.eliteMod?.let { Color(it.color) } ?: Color(e.def.color))
                    drawCircle(c, radius = e.r, center = Offset(e.x - cx, e.y - cy))
                    drawCircle(Color(0xFFE8E2D0), radius = e.r, center = Offset(e.x - cx, e.y - cy), style = Stroke(if (e.isBoss) 4f else 2f))
                    if (e.maxHp > 1) { val bw = e.r * 2f; drawRect(Color(0x80000000), topLeft = Offset(e.x - cx - bw / 2f, e.y - cy - e.r - 12f), size = Size(bw, 5f)); drawRect(Color(0xFFF5C518), topLeft = Offset(e.x - cx - bw / 2f, e.y - cy - e.r - 12f), size = Size(bw * (e.hp.toFloat() / e.maxHp), 5f)) }
                }
                for (portal in gameState.portalFxs) {
                    val px2 = portal.x - cx; val py2 = portal.y - cy; portal.t += 0.02f
                    val pulse = (sin(portal.t * 3f) * 0.3f + 0.7f)
                    val col = if (portal.isSecret) Color(0xFFFFD700) else Color(0xFF8A5CF6)
                    drawCircle(col.copy(alpha = pulse), radius = 20f, center = Offset(px2, py2))
                    drawCircle(col.copy(alpha = pulse * 0.5f), radius = 30f, center = Offset(px2, py2))
                }
                for (cfx in gameState.chestFxs) { if (!cfx.opened) { val cx2 = cfx.x - cx; val cy2 = cfx.y - cy; drawRect(Color(0xFFCD8032), topLeft = Offset(cx2 - 12f, cy2 - 10f), size = Size(24f, 20f)); drawRect(Color(0xFF8B5E3C), topLeft = Offset(cx2 - 3f, cy2 - 3f), size = Size(6f, 6f)) } }
                val p = gameState.player
                drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFE8C840).copy(alpha = 0.14f), Color.Transparent), center = Offset(p.x - cx, p.y - cy), radius = p.totalLightRadius()), radius = p.totalLightRadius(), center = Offset(p.x - cx, p.y - cy))
                drawCircle(if (p.invuln > 0f) Color(0xFF5AD1FF) else Color(0xFFE8DFD0), radius = p.r, center = Offset(p.x - cx, p.y - cy))
                drawRect(Color.Black.copy(alpha = 0.7f), topLeft = Offset(0f, 0f), size = size)
            }
            gameState.flash = max(0f, gameState.flash - 0.066f)
            if (gameState.flash > 0.01f) drawRect(Color.White.copy(alpha = gameState.flash * 0.5f), size = size)
            if (gameState.banners.isNotEmpty()) {
                val b = gameState.banners[0]; val pp = b.t / b.duration; var sc = 1f; var al = 1f
                if (pp < 0.25f) sc = 3f - 2f * pp / 0.25f else if (pp > 0.8f) al = 1f - (pp - 0.8f) / 0.2f
                val bx = cw / 2f; val by = ch * 0.3f
                val t2 = tm.measure(b.title, TextStyle(color = Color(b.color), fontSize = (48 * sc).sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                drawText(t2, topLeft = Offset(bx - t2.size.width / 2f, by - t2.size.height / 2f))
                val s2 = tm.measure(b.subtitle, TextStyle(color = Color(0xFFE8E2D0).copy(alpha = al), fontSize = (24 * sc).sp, fontFamily = FontFamily.Monospace))
                drawText(s2, topLeft = Offset(bx - s2.size.width / 2f, by + t2.size.height / 2f + 10f))
            }
            if (gameState.status == "death") {
                drawRect(Color.Black.copy(alpha = 0.72f), size = size)
                val wl = tm.measure("WASTED", TextStyle(color = Color(0xFFC41E3A), fontSize = min(72f, cw / 14f).sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                drawText(wl, topLeft = Offset((cw - wl.size.width) / 2f, ch * 0.25f))
                val dl = tm.measure("Kills: " + gameState.totalKills + "  Room: " + (gameState.roomIndex + 1), TextStyle(color = Color(0xFFE8E2D0), fontSize = 20.sp, fontFamily = FontFamily.Monospace))
                drawText(dl, topLeft = Offset((cw - dl.size.width) / 2f, ch * 0.25f + wl.size.height + 15f))
                val rl = tm.measure("TAP TO RESTART", TextStyle(color = Color(0xFFF5C518), fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                drawText(rl, topLeft = Offset((cw - rl.size.width) / 2f, ch * 0.25f + wl.size.height + dl.size.height + 40f))
            }
        }
        if (gameState.status == "play" || gameState.status == "upgrade") {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { val w = gameState.player.currentWeapon(); Text("Room " + (gameState.roomIndex + 1) + "  Wave: " + gameState.wave + "  Kills: " + gameState.totalKills, color = Color(0xFFE8E2D0), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) }
                Column(horizontalAlignment = Alignment.End) { val w = gameState.player.currentWeapon(); Text((w?.icon ?: "?") + " " + (w?.name ?: "?"), color = Color(0xFFE8E2D0), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) }
            }
            Column(Modifier.padding(16.dp).padding(top = 36.dp)) {
                Box(Modifier.width(180.dp).height(10.dp).background(Color(0xFF2A0A0A))) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth((gameState.player.hp.toFloat() / gameState.player.maxHp).coerceIn(0f, 1f)).background(Color(0xFFC41E3A)))
                }
                Text(gameState.player.hp.toString() + "/" + gameState.player.maxHp, color = Color(0xFFE8E2D0), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            if (gameState.player.skillPoints > 0) Text("SKILL PTS: " + gameState.player.skillPoints, color = Color(0xFFF5C518), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.TopCenter).padding(top = 36.dp))
            Text(if (gameState.timeScale <= 0.1f) "* TIME FROZEN" else "> TIME FLOWS", color = if (gameState.timeScale <= 0.1f) Color(0xFF7A7A7A) else Color(0xFFF5C518), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
            Box(Modifier.align(Alignment.BottomStart).padding(32.dp).size(140.dp).background(Color(0x1F222530), CircleShape).border(2.dp, Color(0x50FFFFFF), CircleShape).pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { o -> gameState.joystickActive = true; GameInput.updateJoystick(o, size.width / 2f, size.height / 2f, gameState) },
                    onDrag = { c, _ -> c.consume(); GameInput.updateJoystick(c.position, size.width / 2f, size.height / 2f, gameState) },
                    onDragEnd = { gameState.joystickActive = false; gameState.joystickDX = 0f; gameState.joystickDY = 0f },
                    onDragCancel = { gameState.joystickActive = false; gameState.joystickDX = 0f; gameState.joystickDY = 0f }
                )
            }, contentAlignment = Alignment.Center) {
                val ko = remember(gameState.joystickDX, gameState.joystickDY) { IntOffset((gameState.joystickDX * 45f).toInt(), (gameState.joystickDY * 45f).toInt()) }
                Box(Modifier.offset { ko }.size(45.dp).background(Color(0xBFCCCCCC), CircleShape).border(1.dp, Color.White, CircleShape))
            }
            Column(Modifier.align(Alignment.BottomEnd).padding(bottom = 32.dp, end = 180.dp)) {
                Button(onClick = { gameState.switchWeapon() }, Modifier.size(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A5CF6), contentColor = Color.White), shape = CircleShape, contentPadding = PaddingValues(0.dp)) { Text("W", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { gameState.reloadWeapon() }, Modifier.size(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5C518), contentColor = Color.Black), shape = CircleShape, contentPadding = PaddingValues(0.dp)) { Text("R", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) }
            }
        }
        if (gameState.status == "upgrade" && upgradeChoices.isNotEmpty()) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text("UPGRADE", color = Color(0xFFF5C518), fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.height(8.dp))
                    Text("Skill Points: " + gameState.player.skillPoints, color = Color(0xFFE8E2D0), fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.height(16.dp))
                    for (skill in upgradeChoices) {
                        Card(Modifier.fillMaxWidth().padding(4.dp).clickable {
                            UpgradeSystem.applyChoice(gameState, skill.id); upgradeChoices = emptyList()
                        }, colors = CardDefaults.cardColors(containerColor = Color(0xFF1C202C)), shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text(skill.icon + " " + skill.name, color = Color(0xFFF5C518), fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(skill.desc, color = Color(0xFFA8A196), fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                                Text("Rank: " + ((gameState.player.skillRanks[skill.id] ?: 0) + 1) + "/" + skill.maxRank, color = Color(0xFF5AD1FF), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}
