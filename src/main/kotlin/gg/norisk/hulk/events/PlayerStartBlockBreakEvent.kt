package gg.norisk.hulk.events

import me.obsilabor.alert.Event
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class PlayerStartBlockBreakEvent(val player: PlayerEntity, val blockState: BlockState, val pos: BlockPos): Event()