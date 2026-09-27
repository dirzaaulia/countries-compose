package com.dirzaaulia.countries.ui.overlay

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import com.dirzaaulia.countries.domain.country.AdministrativeDivision
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.globe.toDegrees
import com.dirzaaulia.countries.domain.globe.toRadians
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Deterministic celestial starfield positions in deep space
val CELESTIAL_STARS: List<Triple<Float, Float, Float>> =
    (0..260).map { i ->
        val rng = Random(i * 7919)
        Triple(rng.nextFloat(), rng.nextFloat(), 0.7f + rng.nextFloat() * 1.6f)
    }

internal fun DrawScope.drawDeepSpaceStarfield(
    starTwinkle: Float,
    earthR2: Float,
    canvasCenter: Offset,
    canvasSize: Size,
) {
    CELESTIAL_STARS.forEachIndexed { idx, (normX, normY, starRadius) ->
        val sx = normX * canvasSize.width
        val sy = normY * canvasSize.height
        val dx = sx - canvasCenter.x
        val dy = sy - canvasCenter.y
        if (dx * dx + dy * dy > earthR2 + 10f) {
            val alpha = if (idx % 2 == 0) starTwinkle else (1.4f - starTwinkle).coerceIn(0.25f, 1f)
            val starColor =
                when {
                    idx % 7 == 0 -> Color(0xFF90CAF9)
                    idx % 11 == 0 -> Color(0xFFFFE082)
                    idx % 13 == 0 -> Color(0xFFFFCCBC)
                    else -> Color.White
                }
            if (starRadius > 1.6f) {
                drawCircle(
                    color = starColor.copy(alpha = alpha * 0.35f),
                    radius = starRadius * 2.5f,
                    center = Offset(sx, sy),
                )
            }
            drawCircle(
                color = starColor.copy(alpha = alpha * 0.92f),
                radius = starRadius * 1.3f,
                center = Offset(sx, sy),
            )
        }
    }
}

internal fun DrawScope.drawCartographicBorders(
    countries: List<Country>,
    selectedCountryId: String?,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    sunVector: Point3D,
    daylightBordersPath: Path,
    nightBordersPath: Path,
) {
    daylightBordersPath.reset()
    nightBordersPath.reset()

    val colorDayCore = Color(0xFF0F172A)
    val colorDayHalo = Color(0x66FFFFFF)
    val colorNightCore = Color(0xFFF8FAFC)
    val colorNightHalo = Color(0x800B1320)

    countries.forEach { country ->
        if (country.id != selectedCountryId) {
            var cp = latLngToCartesian(country.center.lat, country.center.lng, 1.0)
            cp = rotateY(cp, cosY, sinY)
            cp = rotateX(cp, cosX, sinX)
            if (cp.z < -0.45) return@forEach

            country.polygons.forEach { polygon ->
                for (i in polygon.indices) {
                    val latLngA = polygon[i]
                    val latLngB = polygon[(i + 1) % polygon.size]

                    var pA = latLngToCartesian(latLngA.lat, latLngA.lng, currentRadius.toDouble())
                    pA = rotateY(pA, cosY, sinY)
                    pA = rotateX(pA, cosX, sinX)

                    var pB = latLngToCartesian(latLngB.lat, latLngB.lng, currentRadius.toDouble())
                    pB = rotateY(pB, cosY, sinY)
                    pB = rotateX(pB, cosX, sinX)

                    if (pA.z > 0.0 && pB.z > 0.0) {
                        val sxA = canvasCenter.x + pA.x.toFloat()
                        val syA = canvasCenter.y - pA.y.toFloat()
                        val sxB = canvasCenter.x + pB.x.toFloat()
                        val syB = canvasCenter.y - pB.y.toFloat()

                        val normA = latLngToCartesian(latLngA.lat, latLngA.lng, 1.0)
                        val normB = latLngToCartesian(latLngB.lat, latLngB.lng, 1.0)
                        val sA = normA.x * sunVector.x + normA.y * sunVector.y + normA.z * sunVector.z
                        val sB = normB.x * sunVector.x + normB.y * sunVector.y + normB.z * sunVector.z

                        if (sA >= 0.04 && sB >= 0.04) {
                            daylightBordersPath.moveTo(sxA, syA)
                            daylightBordersPath.lineTo(sxB, syB)
                        } else if (sA <= -0.04 && sB <= -0.04) {
                            nightBordersPath.moveTo(sxA, syA)
                            nightBordersPath.lineTo(sxB, syB)
                        } else {
                            val tA = ((sA + 0.04) / 0.08).coerceIn(0.0, 1.0).toFloat()
                            val tB = ((sB + 0.04) / 0.08).coerceIn(0.0, 1.0).toFloat()

                            val coreA = lerp(colorNightCore, colorDayCore, tA)
                            val coreB = lerp(colorNightCore, colorDayCore, tB)
                            val haloA = lerp(colorNightHalo, colorDayHalo, tA)
                            val haloB = lerp(colorNightHalo, colorDayHalo, tB)

                            val p1 = Offset(sxA, syA)
                            val p2 = Offset(sxB, syB)
                            val haloBrush = Brush.linearGradient(listOf(haloA, haloB), start = p1, end = p2)
                            drawLine(brush = haloBrush, start = p1, end = p2, strokeWidth = 2.4f, cap = StrokeCap.Round)
                            val coreBrush = Brush.linearGradient(listOf(coreA, coreB), start = p1, end = p2)
                            drawLine(brush = coreBrush, start = p1, end = p2, strokeWidth = 1.3f, cap = StrokeCap.Round)
                        }
                    }
                }
            }
        }
    }

    drawPath(daylightBordersPath, colorDayHalo, style = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(daylightBordersPath, colorDayCore, style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))

    drawPath(nightBordersPath, colorNightHalo, style = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(nightBordersPath, colorNightCore, style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

internal fun DrawScope.drawCountryHighlights(
    countries: List<Country>,
    highlightCountryId: String?,
    selectedAdministrativeDivision: AdministrativeDivision?,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    strobeAlpha: Float,
    isQuizSuccess: Boolean,
    isQuizTarget: Boolean,
) {
    val highlightCountry = countries.find { it.id == highlightCountryId }

    val auraFillColor =
        when {
            isQuizSuccess -> Color(0x5510B981)
            isQuizTarget -> Color(0x44F59E0B)
            else -> Color(0x4438BDF8)
        }
    val auraOuterGlow =
        when {
            isQuizSuccess -> Color(0x6610B981)
            isQuizTarget -> Color(0x66F59E0B)
            else -> Color(0x5538BDF8)
        }
    val auraCoreStroke =
        when {
            isQuizSuccess -> Color(0xFF10B981)
            isQuizTarget -> Color(0xFFF59E0B)
            else -> Color(0xFF38BDF8)
        }

    highlightCountry?.let { country ->
        country.polygons.forEach { polygon ->
            val selectedPath = Path()
            var inPath = false

            for (i in polygon.indices) {
                val latLng = polygon[i]
                var p = latLngToCartesian(latLng.lat, latLng.lng, currentRadius.toDouble())
                p = rotateY(p, cosY, sinY)
                p = rotateX(p, cosX, sinX)

                if (p.z > 0.0) {
                    val sx = canvasCenter.x + p.x.toFloat()
                    val sy = canvasCenter.y - p.y.toFloat()
                    if (!inPath) {
                        selectedPath.moveTo(sx, sy)
                        inPath = true
                    } else {
                        selectedPath.lineTo(sx, sy)
                    }
                } else {
                    inPath = false
                }
            }

            if (inPath) {
                selectedPath.close()
                drawPath(selectedPath, auraFillColor.copy(alpha = strobeAlpha * 0.45f), style = Fill)
            }

            drawPath(
                path = selectedPath,
                color = auraOuterGlow,
                style = Stroke(width = 5.0f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawPath(
                path = selectedPath,
                color = auraCoreStroke,
                style = Stroke(width = 2.6f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    }

    selectedAdministrativeDivision?.let { division ->
        division.boundaryPolygons.forEach { polygon ->
            val divisionPath = Path()
            var inPath = false
            polygon.forEach { latLng ->
                var p = latLngToCartesian(latLng.lat, latLng.lng, currentRadius.toDouble())
                p = rotateY(p, cosY, sinY)
                p = rotateX(p, cosX, sinX)
                if (p.z > 0.0) {
                    val sx = canvasCenter.x + p.x.toFloat()
                    val sy = canvasCenter.y - p.y.toFloat()
                    if (!inPath) {
                        divisionPath.moveTo(sx, sy)
                        inPath = true
                    } else {
                        divisionPath.lineTo(sx, sy)
                    }
                } else {
                    inPath = false
                }
            }
            if (inPath) divisionPath.close()
            drawPath(divisionPath, Color(0x44A855F7), style = Fill)
            drawPath(divisionPath, Color(0x80A855F7), style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(divisionPath, Color(0xFFF0ABFC), style = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

internal fun DrawScope.drawFlightPathSimulator(
    flightRoute: List<LatLng>?,
    planeProgress: Float,
    isSupersonic: Boolean,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
) {
    if (flightRoute == null || flightRoute.size < 2) return

    val arcPath = Path()
    var inArc = false

    for (pt in flightRoute) {
        var p = latLngToCartesian(pt.lat, pt.lng, (currentRadius * 1.01).toDouble())
        p = rotateY(p, cosY, sinY)
        p = rotateX(p, cosX, sinX)

        if (p.z > 0.0) {
            val sx = canvasCenter.x + p.x.toFloat()
            val sy = canvasCenter.y - p.y.toFloat()
            if (!inArc) {
                arcPath.moveTo(sx, sy)
                inArc = true
            } else {
                arcPath.lineTo(sx, sy)
            }
        } else {
            inArc = false
        }
    }

    drawPath(arcPath, Color(0x40F59E0B), style = Stroke(width = 5f, cap = StrokeCap.Round))
    drawPath(
        arcPath,
        Color(0xFFF59E0B),
        style =
            Stroke(
                width = 2.5f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f),
            ),
    )

    val departure = flightRoute.first()
    var depP = latLngToCartesian(departure.lat, departure.lng, (currentRadius * 1.008).toDouble())
    depP = rotateY(depP, cosY, sinY)
    depP = rotateX(depP, cosX, sinX)
    if (depP.z > 0.0) {
        val dx = canvasCenter.x + depP.x.toFloat()
        val dy = canvasCenter.y - depP.y.toFloat()
        val depPulse = (planeProgress * 3f) % 1f
        drawCircle(color = Color(0x6610B981), radius = 4f + depPulse * 12f, center = Offset(dx, dy), style = Stroke(width = 1.5f))
        drawCircle(color = Color(0xFF10B981), radius = 3.5f, center = Offset(dx, dy))
    }

    val arrival = flightRoute.last()
    var arrP = latLngToCartesian(arrival.lat, arrival.lng, (currentRadius * 1.008).toDouble())
    arrP = rotateY(arrP, cosY, sinY)
    arrP = rotateX(arrP, cosX, sinX)
    if (arrP.z > 0.0) {
        val ax = canvasCenter.x + arrP.x.toFloat()
        val ay = canvasCenter.y - arrP.y.toFloat()
        val arrPulse = ((planeProgress * 3f) + 0.5f) % 1f
        drawCircle(color = Color(0x6638BDF8), radius = 4f + arrPulse * 12f, center = Offset(ax, ay), style = Stroke(width = 1.5f))
        drawCircle(color = Color(0xFF38BDF8), radius = 3.5f, center = Offset(ax, ay))
    }

    val altitudeFactor = 1.012 + 0.026 * sin(planeProgress * PI)
    val indexFloat = planeProgress * (flightRoute.size - 1)
    val idx = indexFloat.toInt().coerceIn(0, flightRoute.size - 2)
    val frac = indexFloat - idx
    val planeLat = flightRoute[idx].lat + (flightRoute[idx + 1].lat - flightRoute[idx].lat) * frac
    val planeLng = flightRoute[idx].lng + (flightRoute[idx + 1].lng - flightRoute[idx].lng) * frac

    var planeP = latLngToCartesian(planeLat, planeLng, (currentRadius * altitudeFactor).toDouble())
    planeP = rotateY(planeP, cosY, sinY)
    planeP = rotateX(planeP, cosX, sinX)

    if (planeP.z > 0.0) {
        val px = canvasCenter.x + planeP.x.toFloat()
        val py = canvasCenter.y - planeP.y.toFloat()

        val nextProgress = (planeProgress + 0.015f).coerceAtMost(1.0f)
        val nIndexFloat = nextProgress * (flightRoute.size - 1)
        val nIdx = nIndexFloat.toInt().coerceIn(0, flightRoute.size - 2)
        val nFrac = nIndexFloat - nIdx
        val nLat = flightRoute[nIdx].lat + (flightRoute[nIdx + 1].lat - flightRoute[nIdx].lat) * nFrac
        val nLng = flightRoute[nIdx].lng + (flightRoute[nIdx + 1].lng - flightRoute[nIdx].lng) * nFrac

        var nextP = latLngToCartesian(nLat, nLng, (currentRadius * altitudeFactor).toDouble())
        nextP = rotateY(nextP, cosY, sinY)
        nextP = rotateX(nextP, cosX, sinX)
        val nx = canvasCenter.x + nextP.x.toFloat()
        val ny = canvasCenter.y - nextP.y.toFloat()

        val headingRad = atan2(ny - py, nx - px)
        val headingDeg = (headingRad * 180.0 / PI).toFloat()

        for (step in 1..6) {
            val trailProg = (planeProgress - step * 0.012f).coerceAtLeast(0f)
            val tIndexFloat = trailProg * (flightRoute.size - 1)
            val tIdx = tIndexFloat.toInt().coerceIn(0, flightRoute.size - 2)
            val tFrac = tIndexFloat - tIdx
            val tLat = flightRoute[tIdx].lat + (flightRoute[tIdx + 1].lat - flightRoute[tIdx].lat) * tFrac
            val tLng = flightRoute[tIdx].lng + (flightRoute[tIdx + 1].lng - flightRoute[tIdx].lng) * tFrac

            var trailP = latLngToCartesian(tLat, tLng, (currentRadius * (1.012 + 0.026 * sin(trailProg * PI))).toDouble())
            trailP = rotateY(trailP, cosY, sinY)
            trailP = rotateX(trailP, cosX, sinX)
            if (trailP.z > 0.0) {
                val tx = canvasCenter.x + trailP.x.toFloat()
                val ty = canvasCenter.y - trailP.y.toFloat()
                val alpha = (0.55f * (1f - step / 7f)).coerceIn(0f, 1f)
                val trailColor = if (isSupersonic) Color(0xFFEF4444) else Color(0xFFF59E0B)
                drawCircle(
                    color = trailColor.copy(alpha = alpha),
                    radius = (4.5f - step * 0.5f).coerceAtLeast(1.5f),
                    center = Offset(tx, ty),
                )
            }
        }

        withTransform({
            rotate(degrees = headingDeg, pivot = Offset(px, py))
        }) {
            val airlinerPath =
                Path().apply {
                    moveTo(px + 18f, py)
                    cubicTo(px + 13f, py - 3f, px + 5f, py - 3.2f, px + 2f, py - 3.2f)
                    lineTo(px - 7f, py - 19f)
                    lineTo(px - 9.5f, py - 19f)
                    lineTo(px - 7f, py - 3.2f)
                    lineTo(px - 14f, py - 2.2f)
                    lineTo(px - 18.5f, py - 8f)
                    lineTo(px - 20.5f, py - 8f)
                    lineTo(px - 19f, py)
                    lineTo(px - 20.5f, py + 8f)
                    lineTo(px - 18.5f, py + 8f)
                    lineTo(px - 14f, py + 2.2f)
                    lineTo(px - 7f, py + 3.2f)
                    lineTo(px - 9.5f, py + 19f)
                    lineTo(px - 7f, py + 19f)
                    lineTo(px + 2f, py + 3.2f)
                    cubicTo(px + 5f, py + 3.2f, px + 13f, py + 3f, px + 18f, py)
                    close()
                }

            drawPath(airlinerPath, color = Color(0x66F59E0B), style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(airlinerPath, color = Color(0xFF0F172A), style = Fill)
            drawPath(airlinerPath, color = Color(0xFFFDE68A), style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))

            val enginePulse = (sin(planeProgress * 30.0) * 0.5 + 0.5).toFloat()
            drawCircle(color = Color(0xFF1E293B), radius = 2.4f, center = Offset(px - 2f, py - 8f))
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.7f + 0.3f * enginePulse),
                radius = 1.8f,
                center = Offset(px - 3.5f, py - 8f),
            )
            drawCircle(color = Color(0xFF1E293B), radius = 2.4f, center = Offset(px - 2f, py + 8f))
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.7f + 0.3f * enginePulse),
                radius = 1.8f,
                center = Offset(px - 3.5f, py + 8f),
            )

            drawLine(
                color = Color(0xFF38BDF8),
                start = Offset(px + 10f, py - 1.8f),
                end = Offset(px + 10f, py + 1.8f),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round,
            )

            val strobeOn = sin(planeProgress * 20.0) > 0.0
            if (strobeOn) {
                drawCircle(color = Color(0xFFEF4444), radius = 2.2f, center = Offset(px - 8f, py - 19f))
                drawCircle(color = Color(0xFF10B981), radius = 2.2f, center = Offset(px - 8f, py + 19f))
                drawCircle(color = Color.White, radius = 2.0f, center = Offset(px - 19f, py))
            }
        }
    }
}

internal fun DrawScope.drawNasaHazards(
    hazards: List<NasaNaturalEvent>,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    strobeAlpha: Float,
    beaconPulse: Float,
) {
    hazards.forEach { hazard ->
        var p = latLngToCartesian(hazard.lat, hazard.lng, currentRadius.toDouble())
        p = rotateY(p, cosY, sinY)
        p = rotateX(p, cosX, sinX)

        if (p.z > 0.0) {
            val hx = canvasCenter.x + p.x.toFloat()
            val hy = canvasCenter.y - p.y.toFloat()

            val hazardColor =
                when {
                    hazard.category.contains("Volcano", ignoreCase = true) -> Color(0xFFEF4444)
                    hazard.category.contains("Fire", ignoreCase = true) -> Color(0xFFF97316)
                    hazard.category.contains(
                        "Storm",
                        ignoreCase = true,
                    ) ||
                        hazard.category.contains("Cyclone", ignoreCase = true) -> Color(0xFFA855F7)
                    hazard.category.contains("Ice", ignoreCase = true) -> Color(0xFF38BDF8)
                    else -> Color(0xFF06B6D4)
                }

            drawCircle(
                color = hazardColor.copy(alpha = 0.28f * strobeAlpha),
                radius = beaconPulse * 2.2f + 6f,
                center = Offset(hx, hy),
            )
            drawCircle(
                color = hazardColor.copy(alpha = 0.65f),
                radius = 11f,
                center = Offset(hx, hy),
                style = Stroke(width = 2.0f),
            )
            drawCircle(
                color = hazardColor,
                radius = 7.5f,
                center = Offset(hx, hy),
            )
            drawCircle(
                color = Color.White,
                radius = 3.5f,
                center = Offset(hx, hy),
            )
        }
    }
}

internal fun DrawScope.drawISSTracker(
    issTelemetry: ISSTelemetry?,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    strobeAlpha: Float,
    onIssPosCalculated: (Offset?) -> Unit,
) {
    if (issTelemetry == null) {
        onIssPosCalculated(null)
        return
    }

    val issRadius = currentRadius * 1.066
    val orbitPath = Path()
    var inOrbit = false
    val incRad = 51.6.toRadians
    val lat0Rad = issTelemetry.latitude.toRadians.coerceIn(-incRad + 0.001, incRad - 0.001)
    val u0 = asin((sin(lat0Rad) / sin(incRad)).coerceIn(-1.0, 1.0))

    for (step in 0..72) {
        val t = step * (92.9 / 72.0)
        val u = u0 + (step * (2 * PI / 72.0))
        val orbitLat = asin((sin(incRad) * sin(u)).coerceIn(-1.0, 1.0)).toDegrees
        val earthRotDeg = t * (360.0 / 1440.0)
        val orbitLng = ((issTelemetry.longitude + (u - u0).toDegrees * cos(incRad) - earthRotDeg + 540.0) % 360.0) - 180.0

        var op = latLngToCartesian(orbitLat, orbitLng, issRadius.toDouble())
        op = rotateY(op, cosY, sinY)
        op = rotateX(op, cosX, sinX)

        if (op.z > 0.0) {
            val ox = canvasCenter.x + op.x.toFloat()
            val oy = canvasCenter.y - op.y.toFloat()
            if (!inOrbit) {
                orbitPath.moveTo(ox, oy)
                inOrbit = true
            } else {
                orbitPath.lineTo(ox, oy)
            }
        } else {
            inOrbit = false
        }
    }

    drawPath(
        path = orbitPath,
        color = Color(0x3338BDF8),
        style = Stroke(width = 3.5f, cap = StrokeCap.Round),
    )
    drawPath(
        path = orbitPath,
        color = Color(0x8838BDF8),
        style =
            Stroke(
                width = 1.8f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f),
            ),
    )

    var p = latLngToCartesian(issTelemetry.latitude, issTelemetry.longitude, issRadius.toDouble())
    p = rotateY(p, cosY, sinY)
    p = rotateX(p, cosX, sinX)

    if (p.z > 0.0) {
        val ix = canvasCenter.x + p.x.toFloat()
        val iy = canvasCenter.y - p.y.toFloat()
        val pos = Offset(ix, iy)
        onIssPosCalculated(pos)

        drawCircle(
            color = Color(0x3338BDF8).copy(alpha = strobeAlpha * 0.7f),
            radius = 28f,
            center = pos,
        )
        drawCircle(
            color = Color(0x8838BDF8),
            radius = 18f,
            center = pos,
            style = Stroke(width = 2.0f),
        )
        drawCircle(
            color = Color(0xAA38BDF8),
            radius = 11f,
            center = pos,
        )
        drawCircle(
            color = Color.White,
            radius = 5.0f,
            center = pos,
        )
        drawCircle(
            color = Color.White,
            radius = 2.2f,
            center = pos,
        )
    } else {
        onIssPosCalculated(null)
    }
}
