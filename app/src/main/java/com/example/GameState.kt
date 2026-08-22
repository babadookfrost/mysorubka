
package com.example
import kotlin.math.*

class Player(override var x: Float, override var y: Float, override val r: Float = Cfg.PLAYER_R) : Collidable {
    var hp: Int = Cfg.PLAYER_MAX_HP; var maxHp: Int = Cfg.PLAYER_MAX_HP
    var weaponId: String = "knife"; var weapon2Id: String? = "revolver"
    var ammo: Int = -1; var reloading: Float = 0f; var fireCd: Float = 0f
    var speed: Float = Cfg.PLAYER_BASE_SPD; var invuln: Float = 0f
    var invisTimer: Float = 0f; var barrier: Int = 0; var barrierCd: Float = 0f
    var bloodRitualActive: Boolean = false; var bloodRitualTimer: Float = 0f
    var timeStopActive: Boolean = false; var timeStopTimer: Float = 0f; var timeStopCd: Float = 0f
    var undyingUsed: Boolean = false; var shadowStepCd: Float = 0f; var shadowStep2Cd: Float = 0f
    var xp: Int = 0; var level: Int = 1; var xpToNext: Int = Cfg.XP_PER_LVL_BASE; var skillPoints: Int = 0
    val skillRanks = mutableMapOf<String, Int>(); val items = mutableListOf<PlayerItem>()
    val weapons = mutableListOf<String>(); var currentSlot: Int = 0
    fun currentWeapon(): WeaponDef? = weapons.getOrNull(currentSlot)?.let { Weapons[it] }
    fun totalDmgMul(): Float { var m = 1f; for (pi in items) when (pi.def.id) { "dmg_pill" -> m += 0.08f * pi.count; "power_gloves" -> m += 0.20f * pi.count; "berserker_belt" -> if (hp < maxHp * 0.5f) m += 0.25f * pi.count }; if ((skillRanks["hunger"] ?: 0) > 0) m += (1f - hp.toFloat() / maxHp) * 10f * (skillRanks["hunger"] ?: 0) * 0.02f; if ((skillRanks["berserker_rage"] ?: 0) > 0 && hp < maxHp * 0.3f) m += 0.5f; if (bloodRitualActive) m *= 3f; return m }
    fun totalDefMul(): Float { var m = 1f; for (pi in items) if (pi.def.id == "armor_pill") m -= 0.08f * pi.count; m *= SkillTree.calcStats(skillRanks).defMul; return m.coerceAtLeast(0.1f) }
    fun totalLifesteal(): Float { var ls = SkillTree.calcStats(skillRanks).lifesteal; for (pi in items) when (pi.def.id) { "blood_amulet" -> ls += 0.08f * pi.count; "vampire_ring" -> ls += 0.12f * pi.count }; return ls }
    fun totalSpeed(): Float { var s = Cfg.PLAYER_BASE_SPD; for (pi in items) when (pi.def.id) { "speed_pill" -> s *= 1.08f; "speed_boots" -> s *= 1.15f; "bone_crown" -> s *= 0.90f }; val stats = SkillTree.calcStats(skillRanks); s *= stats.spdMul; if (stats.hasBerserkerRage && hp < maxHp * 0.3f) s *= 1.3f; return s }
    fun totalLightRadius(): Float { var r = Cfg.BASE_LIGHT_R; for (pi in items) when (pi.def.id) { "light_pill" -> r *= 1.15f; "crimson_eye" -> r *= 1.30f }; return r + SkillTree.calcStats(skillRanks).lightBonus }
    fun totalDodgeChance(): Float { var d = SkillTree.calcStats(skillRanks).dodgeChance; for (pi in items) if (pi.def.id == "shadow_cloak") d += 0.15f * pi.count; return d.coerceAtMost(0.75f) }
    fun gainXp(amount: Int) { val actual = (amount * SkillTree.calcStats(skillRanks).xpMul).toInt(); xp += actual; while (xp >= xpToNext) { xp -= xpToNext; level++; skillPoints += Cfg.SKILL_POINTS_PER_LVL; xpToNext = (Cfg.XP_PER_LVL_BASE * Cfg.XP_GROWTH.pow((level - 1).toFloat())).toInt() } }
    fun applyItemStats() { var bonusHp = 0; for (pi in items) when (pi.def.id) { "hp_pill" -> bonusHp += 5 * pi.count; "iron_ring" -> bonusHp += 20 * pi.count; "bone_crown" -> bonusHp += 50 * pi.count }; maxHp = Cfg.PLAYER_MAX_HP + bonusHp + SkillTree.calcStats(skillRanks).hpBonus; hp = hp.coerceAtMost(maxHp) }
    fun hasItem(id: String): Boolean = items.any { it.def.id == id }
}

class Enemy(override var x: Float, override var y: Float, override val r: Float, val def: EnemyDef, var hp: Int, val maxHp: Int, val speed: Float, val dmg: Int, val xpValue: Int, var fireCd: Float = 0f, var dead: Boolean = false, var flash: Float = 0f, var eliteMod: Enemies.EliteMod? = null, var dotTimer: Float = 0f, var dotDmg: Int = 0, var burstCd: Float = 0f, val isBoss: Boolean = false) : Collidable
class Bullet(var x: Float, var y: Float, val vx: Float, val vy: Float, val r: Float, val friendly: Boolean, val dmg: Int, var pierce: Int, val splash: Float = 0f, var dead: Boolean = false, val dot: Int = 0, val dotDur: Float = 0f)
class Particle(var x: Float, var y: Float, val vx: Float, val vy: Float, var life: Float, val maxLife: Float, val color: Long, val size: Float)
class XpOrb(var x: Float, var y: Float, val value: Int, var life: Float = 8f)
class DroppedItem(var x: Float, var y: Float, val def: ItemDef, var life: Float = 15f)
class Banner(val title: String, val subtitle: String, val color: Long, var t: Float = 0f, val duration: Float = 2.0f)
class PortalFx(var x: Float, var y: Float, val isSecret: Boolean, var t: Float = 0f)
class ChestFx(var x: Float, var y: Float, var opened: Boolean = false, var t: Float = 0f)


// GameState class
class GameState {
    var status = "menu"
    var viewW = 1000f; var viewH = 600f
    lateinit var map: GameMap; val camera = Camera()
    lateinit var player: Player
    var enemies = mutableListOf<Enemy>()
    var bullets = mutableListOf<Bullet>()
    var particles = mutableListOf<Particle>()
    var xpOrbs = mutableListOf<XpOrb>()
    var droppedItems = mutableListOf<DroppedItem>()
    var banners = mutableListOf<Banner>()
    var portalFxs = mutableListOf<PortalFx>()
    var chestFxs = mutableListOf<ChestFx>()
    var roomIndex = 0; var wave = 1; var waveKills = 0
    var kills = 0; var totalKills = 0; var spawnT = 0f
    var timeScale = 1f; var shotSlowmo = 0f; var flash = 0f
    var isSecretRoom = false; var waveEnemiesLeft = 0
    var waveComplete = false; var roomCleared = false
    var upgradeChoices = mutableListOf<String>()
    var joystickActive = false; var joystickDX = 0f; var joystickDY = 0f
    var fireRequest = false; var aimX = 0f; var aimY = 0f
    var regenTimer = 0f

    init { reset() }

    fun reset() { status = "menu"; roomIndex = 0; wave = 1; kills = 0; totalKills = 0; bullets.clear(); particles.clear(); xpOrbs.clear(); droppedItems.clear(); banners.clear(); enemies.clear(); player = Player(Cfg.ROOM_PX_W / 2f, Cfg.ROOM_PX_H / 2f); player.weapons.addAll(Weapons.starterIds()); player.ammo = Weapons[player.weaponId].ammoMax; enterRoom(false); camera.x = player.x - viewW / 2f; camera.y = player.y - viewH / 2f; flash = 0f }
    fun start() { reset(); status = "play" }
    fun die() { if (status != "play") return; status = "death"; burst(player.x, player.y, 50, 0xFFC41E3AL, 500f); flash = 0.8f }
    fun spawnBanner(title: String, subtitle: String, color: Long) { banners.add(Banner(title, subtitle, color)) }
    fun burst(x: Float, y: Float, count: Int, color: Long, speed: Float) { for (i in 0 until count) { val a = (Math.random() * PI * 2).toFloat(); val sp = (0.3f + Math.random().toFloat() * 0.7f) * speed; val life = 0.3f + Math.random().toFloat() * 0.3f; particles.add(Particle(x, y, cos(a) * sp, sin(a) * sp, life, life, color, 2f + Math.random().toFloat() * 3f)) } }
    fun muzzleFlash(x: Float, y: Float, ang: Float) { particles.add(Particle(x + cos(ang) * 16f, y + sin(ang) * 16f, cos(ang) * 40f, sin(ang) * 40f, 0.1f, 0.1f, 0xFFFFF6C8L, 8f)) }
    fun damageEnemy(e: Enemy, rawDmg: Int) { e.hp -= rawDmg; e.flash = 1f; burst(e.x, e.y, 4, 0xFFC41E3AL, 80f); if (e.hp <= 0) killEnemy(e) }
    fun killEnemy(e: Enemy) { if (e.dead) return; e.dead = true; val p = player; val ls = p.totalLifesteal(); if (ls > 0f) { val heal = (e.maxHp * ls).toInt().coerceAtLeast(1); p.hp = (p.hp + heal).coerceAtMost(p.maxHp) }; xpOrbs.add(XpOrb(e.x, e.y, (e.xpValue * (e.eliteMod?.xpMul ?: 1f)).toInt())); burst(e.x, e.y, 12, 0xFFC41E3AL, 200f); kills++; waveKills++; totalKills++; Items.rollForEnemy(roomIndex)?.let { droppedItems.add(DroppedItem(e.x, e.y, it)) }; waveEnemiesLeft--; if (waveEnemiesLeft <= 0 && enemies.all { it.dead }) { if (isSecretRoom || roomIndex % 5 == 4) roomCleared = true else { wave++; waveEnemiesLeft = 3 + wave * 2; waveComplete = false } } }
    fun reloadWeapon() { val w = player.currentWeapon() ?: return; if (w.ammoMax > 0 && player.ammo < w.ammoMax && player.reloading <= 0f) player.reloading = w.reload }
    fun switchWeapon() { val p = player; if (p.weapons.size > 1) { p.currentSlot = (p.currentSlot + 1) % p.weapons.size; val w = p.currentWeapon(); p.ammo = if (w != null && w.ammoMax > 0) w.ammoMax else -1; p.reloading = 0f } }
    fun tryTimeStop() { val p = player; val stats = SkillTree.calcStats(p.skillRanks); if (!stats.hasTimeStop || p.timeStopCd > 0f || p.timeStopActive) return; p.timeStopActive = true; p.timeStopTimer = 2f; p.timeStopCd = 30f; flash = 0.4f }
    fun tryShadowStep() { val p = player; val stats = SkillTree.calcStats(p.skillRanks); if (!stats.hasShadowStep || p.shadowStepCd > 0f) return; val dx = joystickDX; val dy = joystickDY; if (dx == 0f && dy == 0f) return; val mag = sqrt(dx * dx + dy * dy); val nx = (p.x + dx / mag * 120f).coerceIn(Cfg.TILE, Cfg.ROOM_PX_W - Cfg.TILE); val ny = (p.y + dy / mag * 120f).coerceIn(Cfg.TILE, Cfg.ROOM_PX_H - Cfg.TILE); if (!map.circleBlocked(nx, ny, p.r)) { p.x = nx; p.y = ny; p.shadowStepCd = stats.shadowStepCd; if (stats.hasInvisibility) p.invisTimer = 3f; burst(p.x, p.y, 12, 0xFF8A5CF6L, 180f) } }
    fun applySkillUpgrade(skillId: String) { val p = player; p.skillRanks[skillId] = (p.skillRanks[skillId] ?: 0) + 1; p.skillPoints--; p.applyItemStats(); if (skillId == "bone_shield") p.barrier = 3 * (p.skillRanks["bone_shield"] ?: 0) }
    fun damagePlayer(rawDmg: Int) { val p = player; if (Math.random() < p.totalDodgeChance()) { burst(p.x, p.y, 8, 0xFF5AD1FFL, 100f); return }; if (p.barrier > 0) { p.barrier--; return }; p.hp -= (rawDmg * p.totalDefMul()).toInt().coerceAtLeast(1); p.invuln = 0.5f; flash = 0.3f; if (p.hp <= 0) { if (SkillTree.calcStats(p.skillRanks).hasUndying && !p.undyingUsed) { p.hp = 1; p.undyingUsed = true; p.invuln = 2f; burst(p.x, p.y, 30, 0xFF5AD1FFL, 300f); return }; die() } }

    fun enterRoom(secret: Boolean) { isSecretRoom = secret; map = MapGenerator.generateRoom(roomIndex, secret); enemies.clear(); bullets.clear(); portalFxs.clear(); chestFxs.clear(); for ((px, py) in map.portals) portalFxs.add(PortalFx(px, py, false)); map.secretPortal?.let { portalFxs.add(PortalFx(it.first, it.second, true)) }; for ((cx, cy) in map.chests) chestFxs.add(ChestFx(cx, cy)); player.x = map.spawnX; player.y = map.spawnY; wave = 1; waveKills = 0; waveComplete = false; roomCleared = false; spawnT = 1.0f; waveEnemiesLeft = 5 + roomIndex * 2 + (Math.random() * 5).toInt(); if (secret) waveEnemiesLeft /= 2; if (secret) spawnBanner("SECRET", "LOOT", 0xFFFFD700L) else spawnBanner("ROOM " + (roomIndex + 1), "WAVE " + wave, 0xFF8A5CF6L) }
    fun fireWeapon(aimWX: Float, aimWY: Float) { val p = player; val w = p.currentWeapon() ?: return; if (p.fireCd > 0f || (w.ammoMax > 0 && p.ammo <= 0) || p.reloading > 0f) return; p.fireCd = w.cd; if (w.ammoMax > 0) { p.ammo--; if (p.ammo == 0) p.reloading = w.reload }; shotSlowmo = 0.4f; val ang = atan2((aimWY - p.y).toDouble(), (aimWX - p.x).toDouble()).toFloat(); val dmgMul = p.totalDmgMul(); if (w.meleeRange > 0f) { for (e in enemies) { if (e.dead) continue; val dist = sqrt((e.x - p.x) * (e.x - p.x) + (e.y - p.y) * (e.y - p.y)); if (dist <= w.meleeRange + e.r) { val eAng = atan2((e.y - p.y).toDouble(), (e.x - p.x).toDouble()).toFloat(); var ad = abs(eAng - ang); if (ad > PI.toFloat()) ad = 2 * PI.toFloat() - ad; if (ad <= w.meleeArc / 2f) damageEnemy(e, (w.dmg * dmgMul).toInt().coerceAtLeast(1)) } }; burst(p.x + cos(ang) * 25f, p.y + sin(ang) * 25f, 6, 0xFFF5C518L, 120f); muzzleFlash(p.x, p.y, ang) } else { val mid = (w.pellets - 1) / 2f; for (i in 0 until w.pellets) { val a = ang + (i - mid) * w.spread + (Math.random().toFloat() - 0.5f) * w.jitter; bullets.add(Bullet(p.x, p.y, cos(a) * w.speed, sin(a) * w.speed, if (w.splash > 0f) 6f else 3.5f, true, (w.dmg * dmgMul).toInt().coerceAtLeast(1), w.pierce, w.splash, dot = w.dot, dotDur = w.dotDur)) }; muzzleFlash(p.x, p.y, ang) } }
    fun spawnEnemy() { val pool = Enemies.waveEnemies(roomIndex); val base = pool[(Math.random() * pool.size).toInt()]; val spot = map.randomFloorFar(player.x, player.y, Cfg.ENEMY_SPAWN_MIN_DIST); val hpMul = 1f + roomIndex * 0.3f; val spdMul = 1f + roomIndex * 0.08f; val hp = (base.hp * hpMul).toInt().coerceAtLeast(1); val elite = if (Math.random() < 0.12f + roomIndex * 0.02f) Enemies.randomElite() else null; enemies.add(Enemy(spot.first, spot.second, base.r, base, if (elite != null) (hp * elite.hpMul).toInt() else hp, hp, base.speed * spdMul * (elite?.spdMul ?: 1f), (base.dmg * (1f + roomIndex * 0.15f) * (elite?.dmgMul ?: 1f)).toInt().coerceAtLeast(1), (base.xp * (elite?.xpMul ?: 1f)).toInt(), fireCd = base.fireCd, eliteMod = elite)) }
    fun spawnBoss() { val bd = Enemies.bossForRoom(roomIndex); val spot = map.randomFloorFar(player.x, player.y, 400f); enemies.add(Enemy(spot.first, spot.second, bd.r, bd, bd.hp, bd.hp, bd.speed, bd.dmg, bd.xp, isBoss = true, burstCd = 2.5f)); spawnBanner("BOSS", "SURVIVE", 0xFFFF4D6DL); flash = 0.6f }

    fun update(dt: Float) {
        if (status != "play") { if (status == "death") { for (pt in particles) { pt.x += pt.vx * dt; pt.y += pt.vy * dt; pt.life -= dt }; particles.removeAll { it.life <= 0f } }; return }
        val p = player; p.fireCd = max(0f, p.fireCd - dt); p.invuln = max(0f, p.invuln - dt); p.invisTimer = max(0f, p.invisTimer - dt)
        p.shadowStepCd = max(0f, p.shadowStepCd - dt); p.shadowStep2Cd = max(0f, p.shadowStep2Cd - dt); p.timeStopCd = max(0f, p.timeStopCd - dt)
        p.bloodRitualTimer = max(0f, p.bloodRitualTimer - dt); if (p.bloodRitualTimer <= 0f) p.bloodRitualActive = false
        p.timeStopTimer = max(0f, p.timeStopTimer - dt)
        if (p.timeStopTimer <= 0f && p.timeStopActive) { p.timeStopActive = false; if ((p.skillRanks["time_strike"] ?: 0) > 0) { for (e in enemies) if (!e.dead) { val d = sqrt((e.x - p.x) * (e.x - p.x) + (e.y - p.y) * (e.y - p.y)); if (d < 200f) damageEnemy(e, 3) }; burst(p.x, p.y, 30, 0xFF5AD1FFL, 300f) } }
        if (p.reloading > 0f) { p.reloading -= dt; if (p.reloading <= 0f) { val w = p.currentWeapon(); if (w != null) p.ammo = w.ammoMax } }
        val bsRank = p.skillRanks["bone_shield"] ?: 0; if (bsRank > 0 && p.barrier <= 0 && p.barrierCd <= 0f) { p.barrier = 3; p.barrierCd = 15f }; p.barrierCd = max(0f, p.barrierCd - dt)
        val stats = SkillTree.calcStats(p.skillRanks)
        if (stats.regenRate > 0f) { regenTimer += dt * stats.regenRate; if (regenTimer >= 3f) { regenTimer -= 3f; p.hp = (p.hp + 1).coerceAtMost(p.maxHp) } }
        if (p.hasItem("void_heart")) { p.hp = max(1, p.hp - 1); for (e in enemies) if (!e.dead) { val d = sqrt((e.x - p.x) * (e.x - p.x) + (e.y - p.y) * (e.y - p.y)); if (d < 60f) damageEnemy(e, 1) } }
        var moving = false
        if (joystickActive && (joystickDX != 0f || joystickDY != 0f)) { val mag = sqrt(joystickDX * joystickDX + joystickDY * joystickDY); map.moveWithCollision(p, joystickDX / mag * p.totalSpeed() * dt, joystickDY / mag * p.totalSpeed() * dt); moving = true }
        val btStats = SkillTree.calcStats(p.skillRanks); timeScale = if (moving) 1f else (if (shotSlowmo > 0f) 0.3f else Cfg.BT_BASE_SPEED_FACTOR)
        if (!moving && timeScale < 0.5f) timeScale = (timeScale + btStats.moveInSlow * 0.5f).coerceAtMost(0.8f)
        shotSlowmo = max(0f, shotSlowmo - dt); val sdt = dt * if (p.timeStopActive) 0f else timeScale
        camera.follow(p.x, p.y, viewW, viewH, dt)
        if (stats.auraDmg > 0f) for (e in enemies) if (!e.dead) { val d = sqrt((e.x - p.x) * (e.x - p.x) + (e.y - p.y) * (e.y - p.y)); if (d < stats.auraRadius) damageEnemy(e, stats.auraDmg.toInt().coerceAtLeast(1)) }
        if (fireRequest) { fireWeapon(aimX, aimY); val w = p.currentWeapon(); if (w?.auto == false) fireRequest = false }
        if (!roomCleared && enemies.size < 30 && waveEnemiesLeft > 0) { spawnT -= sdt; if (spawnT <= 0f) { spawnT = 0.8f - min(0.5f, wave * 0.03f); spawnEnemy() } }
        if (wave > 10 && !roomCleared && enemies.none { it.isBoss }) spawnBoss()

        for (e in enemies) { if (e.dead) continue; e.flash = max(0f, e.flash - sdt * 4f); e.dotTimer = max(0f, e.dotTimer - dt); if (e.dotTimer > 0f && e.dotDmg > 0) damageEnemy(e, e.dotDmg)
            val ang = atan2((p.y - e.y).toDouble(), (p.x - e.x).toDouble()).toFloat() + (Math.random().toFloat() - 0.5f) * 0.3f; val cv = cos(ang); val sv = sin(ang)
            var fearMul = 1f; if (stats.hasFearAura) { val d = sqrt((e.x - p.x) * (e.x - p.x) + (e.y - p.y) * (e.y - p.y)); if (d < stats.auraRadius * 1.5f) fearMul = 0.7f }
            if (e.isBoss) { map.moveWithCollision(e, cv * e.speed * sdt * fearMul, sv * e.speed * sdt * fearMul); e.burstCd -= sdt; if (e.burstCd <= 0f) { e.burstCd = 2.5f; for (k in 0 until 8) { val a = (k / 8f) * PI.toFloat() * 2f; bullets.add(Bullet(e.x, e.y, cos(a) * 350f, sin(a) * 350f, 5f, false, e.dmg, 1)) } } }
            else if (e.def.fireCd > 0f) { val los = map.raycastClear(e.x, e.y, p.x, p.y); val near = sqrt((p.x - e.x) * (p.x - e.x) + (p.y - e.y) * (p.y - e.y)) < 80f; if (!los || !near) map.moveWithCollision(e, cv * e.speed * sdt * fearMul, sv * e.speed * sdt * fearMul); e.fireCd -= sdt; if (e.fireCd <= 0f && los) { e.fireCd = e.def.fireCd; bullets.add(Bullet(e.x, e.y, cv * e.def.bulletSpeed, sv * e.def.bulletSpeed, 4f, false, e.dmg, 1)) } }
            else map.moveWithCollision(e, cv * e.speed * sdt * fearMul, sv * e.speed * sdt * fearMul)
            if (p.invuln <= 0f && p.invisTimer <= 0f && sqrt((e.x - p.x) * (e.x - p.x) + (e.y - p.y) * (e.y - p.y)) < e.r + p.r) damagePlayer(e.dmg) }
        val cellW = 96f; val bgrid = HashMap<String, MutableList<Enemy>>()
        for (e in enemies) { if (!e.dead) { val cx = (e.x / cellW).toInt(); val cy = (e.y / cellW).toInt(); val k = cx.toString() + "," + cy.toString(); bgrid.getOrPut(k) { mutableListOf() }.add(e) } }
        for (b in bullets) { b.x += b.vx * sdt; b.y += b.vy * sdt
            if (map.isWallWorld(b.x, b.y)) { b.dead = true; burst(b.x, b.y, 3, 0xFF888888L, 50f); continue }
            if (b.x < 0f || b.y < 0f || b.x > Cfg.ROOM_PX_W || b.y > Cfg.ROOM_PX_H) { b.dead = true; continue }
            if (b.friendly) { val bcx = (b.x / cellW).toInt(); val bcy = (b.y / cellW).toInt(); var hit = false
                for (ox in -1..1) for (oy in -1..1) { if (hit) break; val bucket = bgrid[(bcx + ox).toString() + "," + (bcy + oy).toString()] ?: continue
                    for (e in bucket) { if (e.dead || b.dead) continue
                        if (sqrt((b.x - e.x) * (b.x - e.x) + (b.y - e.y) * (b.y - e.y)) < b.r + e.r) {
                            damageEnemy(e, b.dmg); b.pierce--; if (b.pierce <= 0) { b.dead = true; hit = true }
                            if (b.splash > 0f) { for (e2 in enemies) if (!e2.dead && e2 != e) { val d = sqrt((e2.x - b.x) * (e2.x - b.x) + (e2.y - b.y) * (e2.y - b.y)); if (d < b.splash) damageEnemy(e2, (b.dmg * 0.5f).toInt().coerceAtLeast(1)) }; burst(b.x, b.y, 15, 0xFFFF8800L, 150f) }
                            if (hit) break } } } }
            else { if (p.invuln <= 0f && p.invisTimer <= 0f && sqrt((b.x - p.x) * (b.x - p.x) + (b.y - p.y) * (b.y - p.y)) < b.r + p.r * 0.5f) { damagePlayer(b.dmg); b.dead = true } } }
        for (orb in xpOrbs) { orb.life -= dt; val d = sqrt((orb.x - p.x) * (orb.x - p.x) + (orb.y - p.y) * (orb.y - p.y)); if (d < Cfg.PLAYER_PICKUP_R) { p.gainXp(orb.value); orb.life = 0f } else if (d < 120f) { val spd = 300f * dt; val ang = atan2((p.y - orb.y).toDouble(), (p.x - orb.x).toDouble()).toFloat(); orb.x += cos(ang) * spd; orb.y += sin(ang) * spd } }
        for (cfx in chestFxs) { if (!cfx.opened && sqrt((cfx.x - p.x) * (cfx.x - p.x) + (cfx.y - p.y) * (cfx.y - p.y)) < 50f) { cfx.opened = true; val item = Items.rollForChest(); droppedItems.add(DroppedItem(cfx.x, cfx.y, item)); burst(cfx.x, cfx.y, 15, Items.rarityColor(item.rarity), 150f); spawnBanner("CHEST", item.name, Items.rarityColor(item.rarity)) } }
        if (roomCleared || isSecretRoom) { if (map.isNearPortal(p.x, p.y)) { roomIndex++; isSecretRoom = false; enterRoom(false) }; if (map.isNearSecretPortal(p.x, p.y)) enterRoom(true) }
        for (di in droppedItems) { if (sqrt((di.x - p.x) * (di.x - p.x) + (di.y - p.y) * (di.y - p.y)) < Cfg.PLAYER_PICKUP_R) { if (p.weapons.size < 4 && Weapons.dropPool().any { it == di.def.id }) { p.weapons.add(di.def.id); spawnBanner("NEW WEAPON", di.def.name, 0xFFF5C518L) } else { val ex = p.items.find { it.def.id == di.def.id }; if (ex != null) ex.count++ else p.items.add(PlayerItem(di.def)); p.applyItemStats(); spawnBanner("ITEM", di.def.name, Items.rarityColor(di.def.rarity)) }; di.life = 0f } }
        enemies.removeAll { it.dead }; bullets.removeAll { it.dead }; xpOrbs.removeAll { it.life <= 0f }; droppedItems.removeAll { it.life <= 0f }
        for (pt in particles) { pt.x += pt.vx * dt; pt.y += pt.vy * dt; pt.life -= dt }; particles.removeAll { it.life <= 0f }
        if (banners.isNotEmpty()) { val b = banners[0]; b.t += dt; if (b.t >= b.duration) banners.removeAt(0) }
        flash = max(0f, flash - dt * 2f)
    }
}
