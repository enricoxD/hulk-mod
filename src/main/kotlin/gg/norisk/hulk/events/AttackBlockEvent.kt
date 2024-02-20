package gg.norisk.hulk.events

import me.obsilabor.alert.Cancellable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class AttackBlockEvent(val player: PlayerEntity, val blockPos: BlockPos, val direction: Direction): Cancellable()