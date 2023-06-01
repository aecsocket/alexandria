package io.github.aecsocket.alexandria.fabric

import com.mojang.math.Transformation
import io.github.aecsocket.alexandria.*
import io.github.aecsocket.alexandria.fabric.extension.*
import io.github.aecsocket.alexandria.fabric.mixin.DisplayAccess
import io.github.aecsocket.alexandria.fabric.mixin.DisplayAccess.*
import io.github.aecsocket.alexandria.fabric.mixin.TextDisplayAccess
import io.github.aecsocket.klam.*
import net.kyori.adventure.platform.fabric.FabricAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Display.BillboardConstraints
import net.minecraft.world.entity.Display.ItemDisplay
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf

private fun BillboardConstraints.convert() = when (this) {
    BillboardConstraints.FIXED      -> Billboard.NONE
    BillboardConstraints.VERTICAL   -> Billboard.VERTICAL
    BillboardConstraints.HORIZONTAL -> Billboard.HORIZONTAL
    BillboardConstraints.CENTER     -> Billboard.ALL
}

private fun Billboard.convert() = when (this) {
    Billboard.NONE       -> BillboardConstraints.FIXED
    Billboard.VERTICAL   -> BillboardConstraints.VERTICAL
    Billboard.HORIZONTAL -> BillboardConstraints.HORIZONTAL
    Billboard.ALL        -> BillboardConstraints.CENTER
}

var Display.mTransform: FAffine3
    get() = FAffine3(
        entityData.get(getTranslation()).toFVec(),
        entityData.get(getLeftRotation()).toFQuat(),
        entityData.get(getScale()).toFVec(),
    )
    set(value) = (this as DisplayAccess).invokeSetTransformation(Transformation(
        value.translation.toVector3f(),
        value.rotation.toQuaternionf(),
        value.scale.toVector3f(),
        Quaternionf(0.0f, 0.0f, 0.0f, 1.0f),
    ))

var Display.mBillboard: Billboard
    get() = billboardConstraints.convert()
    set(value) = (this as DisplayAccess).invokeSetBillboardConstraints(value.convert())

var Display.mViewRange: Float
    get() = (this as DisplayAccess).invokeGetViewRange()
    set(value) {
        require(mViewRange >= 0.0) { "requires viewRange >= 0.0" }
        (this as DisplayAccess).invokeSetViewRange(value)
    }

var Display.mInterpolationDelay: Int
    get() = (this as DisplayAccess).invokeGetInterpolationDelay()
    set(value) {
        require(value >= 0) { "requires interpolationDelay >= 0" }
        (this as DisplayAccess).invokeSetInterpolationDelay(value)
    }

var Display.mInterpolationDuration: Int
    get() = (this as DisplayAccess).invokeGetInterpolationDuration()
    set(value) {
        require(value >= 0) { "requires interpolationDuration >= 0" }
        (this as DisplayAccess).invokeSetInterpolationDuration(value)
    }

var Display.mGlowColor: TextColor?
    get() = TextColor.color((this as DisplayAccess).invokeGetGlowColorOverride())
    set(value) {
        (this as DisplayAccess).invokeSetGlowColorOverride(value?.value() ?: -1)
    }

var ItemDisplay.mItem: ItemStack
    get() = itemStack
    set(value) {
        getSlot(0).set(value)
    }

var TextDisplay.mLineWidth: Int
    get() = lineWidth
    set(value) {
        require(value > 0) { "requires lineWidth > 0" }
        (this as TextDisplayAccess).invokeSetLineWidth(value)
    }

var TextDisplay.mBackgroundColor: ARGB
    get() = fromARGB((this as TextDisplayAccess).invokeGetBackgroundColor())
    set(value) {
        (this as TextDisplayAccess).invokeSetBackgroundColor(asARGB(value))
    }

var TextDisplay.mHasShadow: Boolean
    get() = (flags.toInt() and 1) != 0
    set(value) {
        val flags = if (value) {
            flags.toInt() or 1
        } else {
            flags.toInt() and (1.inv())
        }
        (this as TextDisplayAccess).invokeSetFlags(flags.toByte())
    }

var TextDisplay.mIsSeeThrough: Boolean
    get() = (flags.toInt() and 2) != 0
    set(value) {
        val flags = if (value) {
            flags.toInt() or 2
        } else {
            flags.toInt() and (2.inv())
        }
        (this as TextDisplayAccess).invokeSetFlags(flags.toByte())
    }

sealed interface DisplayRender : Render {
    val entity: Display

    var billboard: Billboard
    var viewRange: Float
    var interpolationDelay: Int
    var interpolationDuration: Int
    var glowColor: TextColor?

    fun remove()
}

interface ItemRender : DisplayRender {
    override val entity: ItemDisplay

    var item: ItemStack
}

interface TextRender : DisplayRender {
    override val entity: TextDisplay

    var text: Component
    var lineWidth: Int
    var backgroundColor: ARGB
    var hasShadow: Boolean
    var isSeeThrough: Boolean
    var alignment: TextAlignment
}

class DisplayRenders(private val audiences: FabricAudiences) {
    private fun setUp(desc: DisplayRenderDesc, target: ByDisplay) {
        // todo make non persistent
        target.billboard = desc.billboard
        target.viewRange = desc.viewRange
        target.interpolationDelay = desc.interpolationDelay
        target.interpolationDuration = desc.interpolationDuration
    }

    fun createItem(
        world: ServerLevel,
        position: DVec3,
        transform: FAffine3,
        item: ItemStack,
        desc: ItemRenderDesc,
    ): ItemRender {
        val entity = ItemDisplay(EntityType.ITEM_DISPLAY, world)
        entity.moveTo(position.toVec3())
        world.addFreshEntity(entity)
        return OfItem(entity).also {
            it.transform = transform
            it.item = item
            setUp(desc, it)
        }
    }

    fun createText(
        world: ServerLevel,
        position: DVec3,
        transform: FAffine3,
        text: Component,
        desc: TextRenderDesc,
    ): TextRender {
        val entity = TextDisplay(EntityType.TEXT_DISPLAY, world)
        entity.moveTo(position.toVec3())
        world.addFreshEntity(entity)
        return OfText(entity).also {
            it.transform = transform
            it.text = text
            setUp(desc, it)
            it.lineWidth = desc.lineWidth
            it.backgroundColor = desc.backgroundColor
            it.hasShadow = desc.hasShadow
            it.isSeeThrough = desc.isSeeThrough
            it.alignment = desc.alignment
        }
    }

    private open inner class ByDisplay(
        override val entity: Display,
    ) : DisplayRender {
        override var position: DVec3
            get() = entity.position().toDVec()
            set(value) {
                entity.teleportTo(value.x, value.y, value.z)
            }

        override var transform: FAffine3
            get() = entity.mTransform
            set(value) {
                entity.mTransform = value
            }

        override var billboard: Billboard
            get() = entity.mBillboard
            set(value) {
                entity.mBillboard = value
            }

        override var viewRange: Float
            get() = entity.mViewRange
            set(value) {
                entity.mViewRange = value
            }

        override var interpolationDelay: Int
            get() = entity.mInterpolationDelay
            set(value) {
                entity.mInterpolationDelay = value
            }

        override var interpolationDuration: Int
            get() = entity.mInterpolationDuration
            set(value) {
                entity.mInterpolationDuration = value
            }

        override var glowColor: TextColor?
            get() = entity.mGlowColor
            set(value) {
                entity.mGlowColor = value
            }

        override fun remove() {
            entity.kill()
        }
    }

    private inner class OfItem(override val entity: ItemDisplay) : ByDisplay(entity), ItemRender {
        override var item: ItemStack
            get() = entity.mItem
            set(value) {
                entity.mItem = value
            }
    }

    private inner class OfText(override val entity: TextDisplay) : ByDisplay(entity), TextRender {
        override var text: Component
            get() = entity.text.asComponent()
            set(value) {
                (entity as TextDisplayAccess).invokeSetText(audiences.toNative(value))
            }

        override var lineWidth: Int
            get() = entity.mLineWidth
            set(value) {
                entity.mLineWidth = value
            }

        override var backgroundColor: ARGB
            get() = entity.mBackgroundColor
            set(value) {
                entity.mBackgroundColor = value
            }

        override var hasShadow: Boolean
            get() = entity.mHasShadow
            set(value) {
                entity.mHasShadow = value
            }

        override var isSeeThrough: Boolean
            get() = entity.mIsSeeThrough
            set(value) {
                entity.mIsSeeThrough = value
            }

        // todo PAIN!!!
        override var alignment: TextAlignment
            get() = TODO("Not yet implemented")
            set(value) {}
    }
}
