package com.gitlab.aecsocket.alexandria.paper.extension

import com.jme3.bullet.collision.PhysicsCollisionObject
import com.jme3.bullet.objects.PhysicsBody
import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.math.Vector3f

val staticMass = PhysicsBody.massForStatic

fun Vector3f.asString(fmt: String = "%.3f") = "($fmt, $fmt, $fmt)".format(x, y, z)

val PhysicsCollisionObject.position: Vector3f
    get() = getPhysicsLocation(null)

var PhysicsRigidBody.position: Vector3f
    get() = getPhysicsLocation(null)
    set(value) { setPhysicsLocation(value) }
