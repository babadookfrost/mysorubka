package com.example

import kotlin.math.*

interface Collidable {
    var x: Float
    var y: Float
    val r: Float
}

class Camera {
    var x = 0f; var y = 0f
    fun follow(tx: Float, ty: Float, vw: Float, vh: Float, dt: Float) {
        val ecx = clamp(tx - vw / 2f, 0f, max(0f, Cfg.ROOM_PX_W - vw))
        val ecy = clamp(ty - vh / 2f, 0f, max(0f, Cfg.ROOM_PX_H - vh))
        val e = min(1f, dt * 8f)
        x += (ecx - x) * e; y += (ecy - y) * e
    }
    private fun clamp(v: Float, a: Float, b: Float) = max(a, min(v, b))
}

class GameMap(
    val grid: IntArray,
    val portals: List<Pair<Float, Float>>,
    val secretPortal: Pair<Float, Float>?,
    val chests: MutableList<Pair<Float, Float>>,
    val spawnX: Float,
    val spawnY: Float,
    val isSecret: Boolean = false
) {
    fun idx(x: Int, y: Int): Int = y * Cfg.ROOM_W + x

    fun isWall(tx: Int, ty: Int): Boolean {
        if (tx < 0 || ty < 0 || tx >= Cfg.ROOM_W || ty >= Cfg.ROOM_H) return true
        return grid[idx(tx, ty)] == 1
    }

    fun isWallWorld(wx: Float, wy: Float): Boolean =
        isWall((wx / Cfg.TILE).toInt(), (wy / Cfg.TILE).toInt())

    fun circleBlocked(x: Float, y: Float, r: Float): Boolean =
        isWallWorld(x - r, y) || isWallWorld(x + r, y) ||
        isWallWorld(x, y - r) || isWallWorld(x, y + r)

    fun moveWithCollision(e: Collidable, dx: Float, dy: Float) {
        val margin = Cfg.TILE * 0.6f
        if (dx != 0f) {
            val nx = clamp(e.x + dx, margin, Cfg.ROOM_PX_W - margin)
            if (!circleBlocked(nx, e.y, e.r)) e.x = nx
        }
        if (dy != 0f) {
            val ny = clamp(e.y + dy, margin, Cfg.ROOM_PX_H - margin)
            if (!circleBlocked(e.x, ny, e.r)) e.y = ny
        }
    }

    fun raycastClear(x0: Float, y0: Float, x1: Float, y1: Float): Boolean {
        val dist = hypot(x1 - x0, y1 - y0)
        val steps = ceil(dist / (Cfg.TILE * 0.5f)).toInt()
        for (i in 1 until steps) {
            val t = i.toFloat() / steps
            if (isWallWorld(x0 + (x1 - x0) * t, y0 + (y1 - y0) * t)) return false
        }
        return true
    }

    fun randomFloorFar(fromX: Float, fromY: Float, minDist: Float): Pair<Float, Float> {
        for (att in 0 until 80) {
            val tx = 2 + (Math.random() * (Cfg.ROOM_W - 4)).toInt()
            val ty = 2 + (Math.random() * (Cfg.ROOM_H - 4)).toInt()
            if (isWall(tx, ty)) continue
            val wx = (tx + 0.5f) * Cfg.TILE
            val wy = (ty + 0.5f) * Cfg.TILE
            if (hypot(wx - fromX, wy - fromY) >= minDist) return Pair(wx, wy)
        }
        return Pair(fromX + minDist, fromY)
    }

    fun isNearPortal(wx: Float, wy: Float, threshold: Float = 40f): Boolean =
        portals.any { hypot(wx - it.first, wy - it.second) < threshold }

    fun isNearSecretPortal(wx: Float, wy: Float, threshold: Float = 40f): Boolean {
        val sp = secretPortal ?: return false
        return hypot(wx - sp.first, wy - sp.second) < threshold
    }

    fun isNearChest(wx: Float, wy: Float, threshold: Float = 40f): Int {
        for ((i, pair) in chests.withIndex()) {
            if (hypot(wx - pair.first, wy - pair.second) < threshold) return i
        }
        return -1
    }

    private fun clamp(v: Float, a: Float, b: Float) = max(a, min(v, b))
    private fun hypot(dx: Float, dy: Float) = sqrt(dx * dx + dy * dy)
}

object MapGenerator {
    fun generateRoom(roomIndex: Int, isSecret: Boolean = false): GameMap {
        val w = Cfg.ROOM_W; val h = Cfg.ROOM_H
        val grid = IntArray(w * h)
        val idx = { x: Int, y: Int -> y * w + x }

        for (y in 0 until h) for (x in 0 until w)
            grid[idx(x, y)] = if (x == 0 || y == 0 || x == w - 1 || y == h - 1) 1 else 0

        val blockSz = 6
        val bx = ceil(w.toFloat() / blockSz).toInt()
        val by = ceil(h.toFloat() / blockSz).toInt()
        for (bby in 0 until by) for (bbx in 0 until bx) {
            if (Math.random() > 0.50) continue
            val pat = Math.random()
            val y0 = bby * blockSz + 2; val y1 = min(bby * blockSz + blockSz, h - 1)
            val x0 = bbx * blockSz + 2; val x1 = min(bbx * blockSz + blockSz, w - 1)
            for (y in y0 until y1) for (x in x0 until x1) {
                val wall = when {
                    pat < 0.4 -> Math.random() < 0.65
                    pat < 0.7 -> (x + y) % 2 == 0
                    else -> Math.random() < 0.30
                }
                if (wall) grid[idx(x, y)] = 1
            }
        }

        val cx = w / 2; val cy = h / 2
        for (y in cy - 3..cy + 3) for (x in cx - 3..cx + 3)
            if (x > 0 && y > 0 && x < w - 1 && y < h - 1) grid[idx(x, y)] = 0

        val portals = mutableListOf<Pair<Float, Float>>()
        var secretPortal: Pair<Float, Float>? = null
        val spots = listOf(
            Pair(w / 2, 2), Pair(w - 3, h / 2),
            Pair(w / 2, h - 3), Pair(3, h / 2)
        ).shuffled()

        if (!isSecret && Math.random() < 0.33f) {
            val sp = spots[0]
            secretPortal = Pair((sp.first + 0.5f) * Cfg.TILE, (sp.second + 0.5f) * Cfg.TILE)
            clearAround(grid, w, h, sp.first, sp.second)
        }
        for (i in 1..min(3, spots.size)) {
            val pp = spots[i % spots.size]
            portals.add(Pair((pp.first + 0.5f) * Cfg.TILE, (pp.second + 0.5f) * Cfg.TILE))
            clearAround(grid, w, h, pp.first, pp.second)
        }

        val chests = mutableListOf<Pair<Float, Float>>()
        val nc = if (isSecret) 2 else (Math.random() * 2).toInt()
        for (c in 0 until nc) {
            val spot = findOpen(grid, w, h, cx * Cfg.TILE, cy * Cfg.TILE, 200f)
            if (spot != null) chests.add(spot)
        }

        return GameMap(grid, portals, secretPortal, chests,
            (cx + 0.5f) * Cfg.TILE, (cy + 0.5f) * Cfg.TILE, isSecret)
    }

    private fun clearAround(grid: IntArray, w: Int, h: Int, tx: Int, ty: Int) {
        val idx = { x: Int, y: Int -> y * w + x }
        for (dy in -1..1) for (dx in -1..1) {
            val nx = tx + dx; val ny = ty + dy
            if (nx > 0 && ny > 0 && nx < w - 1 && ny < h - 1) grid[idx(nx, ny)] = 0
        }
    }

    private fun findOpen(grid: IntArray, w: Int, h: Int, fromX: Float, fromY: Float, minDist: Float): Pair<Float, Float>? {
        for (att in 0 until 60) {
            val tx = 3 + (Math.random() * (w - 6)).toInt()
            val ty = 3 + (Math.random() * (h - 6)).toInt()
            if (grid[ty * w + tx] == 1) continue
            val wx = (tx + 0.5f) * Cfg.TILE; val wy = (ty + 0.5f) * Cfg.TILE
            if (sqrt((wx - fromX) * (wx - fromX) + (wy - fromY) * (wy - fromY)) >= minDist)
                return Pair(wx, wy)
        }
        return null
    }
}
