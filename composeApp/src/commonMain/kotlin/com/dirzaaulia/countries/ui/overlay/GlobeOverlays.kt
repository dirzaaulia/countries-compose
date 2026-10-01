package com.dirzaaulia.countries.ui.overlay

import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.astronomy.MarketStatus
import com.dirzaaulia.countries.domain.astronomy.calculateMarketStatus
import com.dirzaaulia.countries.domain.comparison.projectTransposedPolygon
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
import com.dirzaaulia.countries.domain.tectonic.Earthquake
import com.dirzaaulia.countries.domain.tectonic.TectonicPlate
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
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
        var p = latLngToCartesian(pt.lat, pt.lng, currentRadius * 1.01)
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
    var depP = latLngToCartesian(departure.lat, departure.lng, currentRadius * 1.008)
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
    var arrP = latLngToCartesian(arrival.lat, arrival.lng, currentRadius * 1.008)
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

    var planeP = latLngToCartesian(planeLat, planeLng, currentRadius * altitudeFactor)
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

        var nextP = latLngToCartesian(nLat, nLng, currentRadius * altitudeFactor)
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

            var trailP = latLngToCartesian(tLat, tLng, currentRadius * (1.012 + 0.026 * sin(trailProg * PI)))
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

        var op = latLngToCartesian(orbitLat, orbitLng, issRadius)
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

    var p = latLngToCartesian(issTelemetry.latitude, issTelemetry.longitude, issRadius)
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

internal fun DrawScope.drawTrueSizeComparisonOverlay(
    countryA: Country,
    countryB: Country,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    strobeAlpha: Float = 1.0f,
) {
    var centerB = latLngToCartesian(countryB.center.lat, countryB.center.lng, 1.0)
    centerB = rotateY(centerB, cosY, sinY)
    centerB = rotateX(centerB, cosX, sinX)
    if (centerB.z <= -0.1) return

    val transposedPolygons = projectTransposedPolygon(countryA, countryB.center)
    val overlayFillColor = Color(0x2638BDF8)
    val overlayGlowColor = Color(0x6638BDF8)
    val overlayStrokeColor = Color(0xFF38BDF8)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

    transposedPolygons.forEach { polygon ->
        val overlayPath = Path()
        var inPath = false

        for (i in polygon.indices) {
            val point = polygon[i]
            var p = latLngToCartesian(point.lat, point.lng, currentRadius.toDouble())
            p = rotateY(p, cosY, sinY)
            p = rotateX(p, cosX, sinX)

            if (p.z > 0.0) {
                val sx = canvasCenter.x + p.x.toFloat()
                val sy = canvasCenter.y - p.y.toFloat()
                if (!inPath) {
                    overlayPath.moveTo(sx, sy)
                    inPath = true
                } else {
                    overlayPath.lineTo(sx, sy)
                }
            } else {
                inPath = false
            }
        }

        if (inPath) {
            overlayPath.close()
            drawPath(
                path = overlayPath,
                color = overlayFillColor.copy(alpha = 0.25f * strobeAlpha),
                style = Fill,
            )
            drawPath(
                path = overlayPath,
                color = overlayGlowColor,
                style = Stroke(width = 5.0f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawPath(
                path = overlayPath,
                color = overlayStrokeColor,
                style =
                    Stroke(
                        width = 2.5f,
                        pathEffect = dashEffect,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
            )
        }
    }
}

internal fun DrawScope.drawTimezoneMeridians(
    selectedMeridianOffset: Int?,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    textMeasurer: TextMeasurer,
) {
    val primeColor = Color(0xFF38BDF8)
    val dateLineColor = Color(0xFFF59E0B)
    val standardColor = Color(0x4438BDF8)
    val selectedColor = Color(0xFF10B981)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)

    for (m in -12..11) {
        val lng = m * 15.0
        val isPrime = m == 0
        val isDateLine = m == -12 || m == 12
        val isSelected = selectedMeridianOffset == m

        val path = Path()
        var inPath = false

        for (latDeg in -85..85 step 5) {
            var p = latLngToCartesian(latDeg.toDouble(), lng, currentRadius.toDouble())
            p = rotateY(p, cosY, sinY)
            p = rotateX(p, cosX, sinX)

            if (p.z > 0.0) {
                val sx = canvasCenter.x + p.x.toFloat()
                val sy = canvasCenter.y - p.y.toFloat()
                if (!inPath) {
                    path.moveTo(sx, sy)
                    inPath = true
                } else {
                    path.lineTo(sx, sy)
                }
            } else {
                inPath = false
            }
        }

        if (inPath) {
            val strokeColor =
                when {
                    isSelected -> selectedColor
                    isPrime -> primeColor
                    isDateLine -> dateLineColor
                    else -> standardColor
                }
            val strokeWidth =
                when {
                    isSelected -> 3.0f
                    isPrime || isDateLine -> 2.2f
                    else -> 1.0f
                }
            val effect = if (isPrime || isDateLine || isSelected) dashEffect else null

            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(width = strokeWidth, pathEffect = effect, cap = StrokeCap.Round),
            )
        }

        var pEq = latLngToCartesian(0.0, lng, currentRadius.toDouble())
        pEq = rotateY(pEq, cosY, sinY)
        pEq = rotateX(pEq, cosX, sinX)

        if (pEq.z > 0.15) {
            val sx = canvasCenter.x + pEq.x.toFloat()
            val sy = canvasCenter.y - pEq.y.toFloat()
            val label =
                if (m == 0) {
                    "UTC+0"
                } else if (m > 0) {
                    "UTC+$m"
                } else {
                    "UTC$m"
                }

            val textResult =
                textMeasurer.measure(
                    text = label,
                    style =
                        TextStyle(
                            color = if (isSelected) selectedColor else Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                )
            val badgeWidth = textResult.size.width + 12f
            val badgeHeight = textResult.size.height + 6f
            val badgeOffset = Offset(sx - badgeWidth / 2f, sy - badgeHeight / 2f)

            drawRoundRect(
                color = if (isSelected) Color(0xDD0F172A) else Color(0xAA09111E),
                topLeft = badgeOffset,
                size = Size(badgeWidth, badgeHeight),
                cornerRadius = CornerRadius(4f, 4f),
            )
            drawRoundRect(
                color = if (isSelected) selectedColor else Color(0x6638BDF8),
                topLeft = badgeOffset,
                size = Size(badgeWidth, badgeHeight),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = 1f),
            )
            drawText(
                textLayoutResult = textResult,
                topLeft = Offset(badgeOffset.x + 6f, badgeOffset.y + 3f),
            )
        }
    }
}

internal fun DrawScope.drawTwilightBands(
    sunVector: Point3D,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
) {
    val normS = sqrt(sunVector.x * sunVector.x + sunVector.y * sunVector.y + sunVector.z * sunVector.z)
    if (normS < 0.001) return
    val s = Point3D(sunVector.x / normS, sunVector.y / normS, sunVector.z / normS)

    val a = if (abs(s.y) < 0.9) Point3D(0.0, 1.0, 0.0) else Point3D(1.0, 0.0, 0.0)
    val uX = a.y * s.z - a.z * s.y
    val uY = a.z * s.x - a.x * s.z
    val uZ = a.x * s.y - a.y * s.x
    val normU = sqrt(uX * uX + uY * uY + uZ * uZ)
    val u = Point3D(uX / normU, uY / normU, uZ / normU)

    val v =
        Point3D(
            s.y * u.z - s.z * u.y,
            s.z * u.x - s.x * u.z,
            s.x * u.y - s.y * u.x,
        )

    val goldenTopRad = 6.0.toRadians
    val goldenBottomRad = (-4.0).toRadians
    val blueBottomRad = (-6.0).toRadians

    drawTwilightRibbon(
        alphaTop = goldenTopRad,
        alphaBottom = goldenBottomRad,
        color = Color(0x33F59E0B),
        u = u,
        v = v,
        s = s,
        currentRadius = currentRadius,
        canvasCenter = canvasCenter,
        cosX = cosX,
        sinX = sinX,
        cosY = cosY,
        sinY = sinY,
    )

    drawTwilightRibbon(
        alphaTop = goldenBottomRad,
        alphaBottom = blueBottomRad,
        color = Color(0x442563EB),
        u = u,
        v = v,
        s = s,
        currentRadius = currentRadius,
        canvasCenter = canvasCenter,
        cosX = cosX,
        sinX = sinX,
        cosY = cosY,
        sinY = sinY,
    )
}

private fun DrawScope.drawTwilightRibbon(
    alphaTop: Double,
    alphaBottom: Double,
    color: Color,
    u: Point3D,
    v: Point3D,
    s: Point3D,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
) {
    val cosTop = cos(alphaTop)
    val sinTop = sin(alphaTop)
    val cosBot = cos(alphaBottom)
    val sinBot = sin(alphaBottom)

    val steps = 90
    val stepAngle = 2.0 * PI / steps

    for (i in 0 until steps) {
        val t1 = i * stepAngle
        val t2 = (i + 1) * stepAngle

        val p1Top = computeRibbonPoint(t1, cosTop, sinTop, u, v, s, currentRadius, cosY, sinY, cosX, sinX)
        val p2Top = computeRibbonPoint(t2, cosTop, sinTop, u, v, s, currentRadius, cosY, sinY, cosX, sinX)
        val p2Bot = computeRibbonPoint(t2, cosBot, sinBot, u, v, s, currentRadius, cosY, sinY, cosX, sinX)
        val p1Bot = computeRibbonPoint(t1, cosBot, sinBot, u, v, s, currentRadius, cosY, sinY, cosX, sinX)

        if (p1Top != null && p2Top != null && p2Bot != null && p1Bot != null) {
            val quadPath =
                Path().apply {
                    moveTo(canvasCenter.x + p1Top.x.toFloat(), canvasCenter.y - p1Top.y.toFloat())
                    lineTo(canvasCenter.x + p2Top.x.toFloat(), canvasCenter.y - p2Top.y.toFloat())
                    lineTo(canvasCenter.x + p2Bot.x.toFloat(), canvasCenter.y - p2Bot.y.toFloat())
                    lineTo(canvasCenter.x + p1Bot.x.toFloat(), canvasCenter.y - p1Bot.y.toFloat())
                    close()
                }
            drawPath(path = quadPath, color = color, style = Fill)
        }
    }
}

private fun computeRibbonPoint(
    theta: Double,
    cosAlpha: Double,
    sinAlpha: Double,
    u: Point3D,
    v: Point3D,
    s: Point3D,
    radius: Float,
    cosY: Double,
    sinY: Double,
    cosX: Double,
    sinX: Double,
): Point3D? {
    val cosT = cos(theta)
    val sinT = sin(theta)

    val x = cosAlpha * (cosT * u.x + sinT * v.x) + sinAlpha * s.x
    val y = cosAlpha * (cosT * u.y + sinT * v.y) + sinAlpha * s.y
    val z = cosAlpha * (cosT * u.z + sinT * v.z) + sinAlpha * s.z

    var p = Point3D(x * radius, y * radius, z * radius)
    p = rotateY(p, cosY, sinY)
    p = rotateX(p, cosX, sinX)

    return if (p.z > 0.0) p else null
}

internal fun DrawScope.drawTectonicLayer(
    plates: List<TectonicPlate>,
    earthquakes: List<Earthquake>,
    selectedPlateId: String?,
    selectedEarthquakeId: String?,
    minMagnitudeFilter: Double,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    strobeAlpha: Float = 1.0f,
    beaconPulse: Float = 8.0f,
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

    plates.forEach { plate ->
        val isSelected = plate.id == selectedPlateId
        val strokeColor =
            when (plate.id) {
                "pacific" -> Color(0xFFEF4444)
                "nazca", "cocos", "philippine_sea" -> Color(0xFFF97316)
                else -> Color(0xFFEAB308)
            }

        plate.boundaries.forEach { boundary ->
            val path = Path()
            var inPath = false

            for (i in boundary.indices) {
                val point = boundary[i]
                var p = latLngToCartesian(point.lat, point.lng, currentRadius.toDouble())
                p = rotateY(p, cosY, sinY)
                p = rotateX(p, cosX, sinX)

                if (p.z > 0.0) {
                    val sx = canvasCenter.x + p.x.toFloat()
                    val sy = canvasCenter.y - p.y.toFloat()
                    if (!inPath) {
                        path.moveTo(sx, sy)
                        inPath = true
                    } else {
                        path.lineTo(sx, sy)
                    }
                } else {
                    inPath = false
                }
            }

            if (!path.isEmpty) {
                if (isSelected || plate.id == "pacific") {
                    drawPath(
                        path = path,
                        color = Color(0x44EF4444),
                        style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                    )
                }
                drawPath(
                    path = path,
                    color = if (isSelected) Color.White else strokeColor,
                    style =
                        Stroke(
                            width = if (isSelected) 3.5f else 2.2f,
                            pathEffect = dashEffect,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                )
            }
        }
    }

    earthquakes.forEach { quake ->
        if (quake.magnitude < minMagnitudeFilter) return@forEach

        var p = latLngToCartesian(quake.lat, quake.lng, currentRadius.toDouble())
        p = rotateY(p, cosY, sinY)
        p = rotateX(p, cosX, sinX)

        if (p.z > 0.0) {
            val sx = canvasCenter.x + p.x.toFloat()
            val sy = canvasCenter.y - p.y.toFloat()
            val pos = Offset(sx, sy)

            val isSelected = quake.id == selectedEarthquakeId

            val depthColor =
                when {
                    quake.depthKm < 70.0 -> Color(0xFFEF4444)
                    quake.depthKm <= 300.0 -> Color(0xFFF59E0B)
                    else -> Color(0xFF6366F1)
                }

            val baseRadius = (8.0f + (quake.magnitude.toFloat() - 4.5f) * 4.0f).coerceIn(8.0f, 30.0f)

            if (quake.tsunamiAlert) {
                drawCircle(
                    color = Color(0x66EF4444),
                    radius = baseRadius + beaconPulse,
                    center = pos,
                    style = Stroke(width = 2.0f),
                )
            }

            drawCircle(
                color = depthColor.copy(alpha = 0.25f * strobeAlpha),
                radius = if (isSelected) baseRadius * 2.2f else baseRadius * 1.5f,
                center = pos,
            )
            drawCircle(
                color = if (isSelected) Color.White else depthColor,
                radius = if (isSelected) baseRadius * 1.2f else baseRadius,
                center = pos,
                style = Stroke(width = 2.0f),
            )
            drawCircle(
                color = Color.White,
                radius = 3.5f,
                center = pos,
            )
        }
    }
}

internal fun DrawScope.drawStockExchangesLayer(
    markets: List<FinancialMarket>,
    selectedMarketId: String?,
    currentUtcMillis: Long,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    strobeAlpha: Float = 1.0f,
    beaconPulse: Float = 8.0f,
) {
    val utcMillis = ((currentUtcMillis % 86_400_000L) + 86_400_000L) % 86_400_000L
    val utcHour = utcMillis / 3_600_000.0

    markets.forEach { market ->
        var p = latLngToCartesian(market.lat, market.lng, currentRadius.toDouble())
        p = rotateY(p, cosY, sinY)
        p = rotateX(p, cosX, sinX)

        if (p.z > 0.0) {
            val sx = canvasCenter.x + p.x.toFloat()
            val sy = canvasCenter.y - p.y.toFloat()
            val pos = Offset(sx, sy)

            val isSelected = market.id == selectedMarketId
            val status = calculateMarketStatus(market, utcHour)

            val (statusColor, baseRadius) =
                when (status) {
                    MarketStatus.OPEN -> Color(0xFF10B981) to 12.0f
                    MarketStatus.OPENING_SOON -> Color(0xFFF59E0B) to 9.0f
                    MarketStatus.CLOSED -> Color(0xFF64748B) to 7.0f
                }

            if (status == MarketStatus.OPEN) {
                drawCircle(
                    color = statusColor.copy(alpha = 0.35f * strobeAlpha),
                    radius = baseRadius + beaconPulse,
                    center = pos,
                )
            }

            drawCircle(
                color = statusColor.copy(alpha = if (isSelected) 0.5f else 0.3f),
                radius = if (isSelected) baseRadius * 1.8f else baseRadius * 1.3f,
                center = pos,
            )
            drawCircle(
                color = if (isSelected) Color.White else statusColor,
                radius = if (isSelected) baseRadius * 1.1f else baseRadius,
                center = pos,
                style = Stroke(width = 2.0f),
            )
            drawCircle(
                color = Color.White,
                radius = 3.0f,
                center = pos,
            )
        }
    }
}
