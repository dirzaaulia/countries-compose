package com.dirzaaulia.countries.domain.globe

import com.dirzaaulia.countries.domain.country.LatLng
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class Point3D(
    val x: Double,
    val y: Double,
    val z: Double,
)

val Double.toRadians: Double get() = this * PI / 180.0
val Double.toDegrees: Double get() = this * 180.0 / PI

fun latLngToCartesian(
    lat: Double,
    lng: Double,
    radius: Double,
): Point3D {
    val phi = lat.toRadians
    val lambda = lng.toRadians

    val x = radius * cos(phi) * sin(lambda)
    val y = radius * sin(phi)
    val z = radius * cos(phi) * cos(lambda)

    return Point3D(x, y, z)
}

fun rotateX(
    p: Point3D,
    cosA: Double,
    sinA: Double,
): Point3D =
    Point3D(
        p.x,
        p.y * cosA - p.z * sinA,
        p.y * sinA + p.z * cosA,
    )

fun rotateY(
    p: Point3D,
    cosA: Double,
    sinA: Double,
): Point3D =
    Point3D(
        p.x * cosA + p.z * sinA,
        p.y,
        -p.x * sinA + p.z * cosA,
    )

fun rotateX(
    p: Point3D,
    angleRad: Double,
): Point3D = rotateX(p, cos(angleRad), sin(angleRad))

fun rotateY(
    p: Point3D,
    angleRad: Double,
): Point3D = rotateY(p, cos(angleRad), sin(angleRad))

fun forwardProject(
    lat: Double,
    lng: Double,
    rotationX: Double,
    rotationY: Double,
    zoom: Float,
    radius: Float,
    centerX: Float,
    centerY: Float,
): Pair<Float, Float>? {
    val point = latLngToCartesian(lat, lng, 1.0)
    val rotated = rotateX(rotateY(point, rotationY.toRadians), rotationX.toRadians)
    if (rotated.z <= 0.0) return null
    val scale = radius * zoom
    return (centerX + rotated.x.toFloat() * scale) to (centerY - rotated.y.toFloat() * scale)
}

fun forwardProjectPerspective(
    lat: Double,
    lng: Double,
    rotationX: Double,
    rotationY: Double,
    zoom: Float,
    radius: Float,
    centerX: Float,
    centerY: Float,
    viewportHeight: Float = centerY * 2f,
    fovyDeg: Float = 45f,
): Pair<Float, Float>? {
    val point = latLngToCartesian(lat, lng, 1.0)
    val rotated = rotateX(rotateY(point, rotationY.toRadians), rotationX.toRadians)
    val fovyRad = fovyDeg * (PI.toFloat() / 360f)
    val focalLength = (viewportHeight / 2f) / kotlin.math.tan(fovyRad)
    val cameraDist = (focalLength / zoom.coerceAtLeast(0.1f)).coerceAtLeast(radius * 1.05f)
    val horizonZ = (radius / cameraDist).toDouble()
    if (rotated.z < horizonZ) return null

    val zDist = cameraDist.toDouble() - rotated.z * radius.toDouble()
    if (zDist <= 0.001) return null
    val factor = focalLength.toDouble() / zDist
    val sx = (centerX.toDouble() + rotated.x * radius.toDouble() * factor).toFloat()
    val sy = (centerY.toDouble() - rotated.y * radius.toDouble() * factor).toFloat()
    return Pair(sx, sy)
}

fun isPointInPolygon(
    point: LatLng,
    polygon: List<LatLng>,
): Boolean {
    var intersectCount = 0
    for (i in polygon.indices) {
        val a = polygon[i]
        val b = polygon[(i + 1) % polygon.size]

        if (((a.lat > point.lat) != (b.lat > point.lat)) &&
            (point.lng < (b.lng - a.lng) * (point.lat - a.lat) / (b.lat - a.lat) + a.lng)
        ) {
            intersectCount++
        }
    }
    return intersectCount % 2 != 0
}
