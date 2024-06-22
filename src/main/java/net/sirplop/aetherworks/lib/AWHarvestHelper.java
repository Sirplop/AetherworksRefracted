package net.sirplop.aetherworks.lib;

import com.rekindled.embers.particle.GlowParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import org.joml.Random;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class AWHarvestHelper {

    private static final Map<UUID, AWHarvestNode> nodes = new HashMap<UUID, AWHarvestNode>();

    public static ItemStack getValidItemStackFor(BlockState state)
    {
        Block b = state.getBlock();
        Item i = b.asItem();
        if (i instanceof BlockItem)
        {
            return new ItemStack(i, 1);
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    public static void onServerTick(TickEvent.LevelTickEvent event)
    {
        if (event.side.isClient())
            return;
        if (event.phase != TickEvent.Phase.END)
        {
            return;
        }
        Stack<UUID> toRemove = new Stack<>();
        for (Map.Entry<UUID, AWHarvestNode> uuidawHarvestNodeEntry : nodes.entrySet())
        {
            uuidawHarvestNodeEntry.getValue().tick();
            if (uuidawHarvestNodeEntry.getValue().isInvalid())
            {
                toRemove.add(uuidawHarvestNodeEntry.getKey());
            }
        }

        while (!toRemove.isEmpty())
        {
            nodes.remove(toRemove.pop());
        }
    }

    public static void onLevelUnload(LevelEvent.Unload event)
    {
        if (!(event.getLevel() instanceof ServerLevel))
            return;
        Stack<UUID> toRemove = new Stack<>();
        for (Map.Entry<UUID, AWHarvestNode> uuidawHarvestNodeEntry : nodes.entrySet())
        {
            if (uuidawHarvestNodeEntry.getValue().level == event.getLevel())
            {
                toRemove.add(uuidawHarvestNodeEntry.getKey());
            }
        }

        while (!toRemove.isEmpty())
        {
            nodes.remove(toRemove.pop());
        }
    }

    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        Stack<UUID> toRemove = new Stack<>();
        for (Map.Entry<UUID, AWHarvestNode> uuidawHarvestNodeEntry : nodes.entrySet())
        {
            if (uuidawHarvestNodeEntry.getValue().harvester == player)
            {
                toRemove.add(uuidawHarvestNodeEntry.getKey());
            }
        }

        while (!toRemove.isEmpty())
        {
            nodes.remove(toRemove.pop());
        }
    }

    public static boolean addNode(Player invoker, AWExchangeNode node)
    {
        UUID playerID = invoker.getUUID();
        if (nodes.containsKey(playerID))
        {
            return false;
        }

        node.initNode();
        if (node.isInvalid())
        {
            return false;
        }

        nodes.put(playerID, node);
        return true;
    }

    public static boolean addNode(Player invoker, AWHarvestNode node)
    {
        UUID playerID = invoker.getUUID();
        if (nodes.containsKey(playerID))
        {
            return false;
        }

        node.initNode();
        if (node.isInvalid())
        {
            return false;
        }

        nodes.put(playerID, node);
        return true;
    }

    public class AWExchangeNode extends AWHarvestNode
    {
        public final ItemStack stackConsumed;
        private BlockState baseState;
        private int currentIteration;
        private Direction[] faces;

        public AWExchangeNode(Player harvester, Level level, BlockPos beginning, int range,
                              Predicate<Player> canHarvest, @Nullable GlowParticleOptions particle, ItemStack is)
        {
            super(harvester, level, beginning, range, canHarvest, particle);
            this.stackConsumed = is;
        }

        @Override
        public void initNode()
        {
            if (!isLoaded(this.beginning))
            {
                this.invalid = true;
                return;
            }

            this.baseState = this.level.getBlockState(this.beginning);
            this.traverseRecursive(this.beginning);
            this.toHarvest.sort((BlockPos l, BlockPos r) -> (int) (distanceToSqr(r) - distanceToSqr(l)));
            if (this.toHarvest.isEmpty())
            {
                this.invalid = true;
            }
        }


        private void shuffleFacesArray()
        {
            int index;
            Direction temp;
            Random random = new Random();
            faces = Direction.values();
            for (int i = faces.length - 1; i > 0; i--)
            {
                index = random.nextInt(i + 1);
                temp = faces[i];
                faces[index] = faces[i];
                faces[i] = temp;
            }
        }

        @Override
        public void traverseRecursive(BlockPos from)
        {
            if (currentIteration >= this.range)
            {
                return;
            }

            this.shuffleFacesArray();
            for (Direction facing : faces)
            {
                BlockPos offset = from.relative(facing);
                if (this.toHarvest.contains(offset))
                {
                    continue;
                }

                if (!this.isLoaded(offset))
                {
                    continue;
                }

                BlockState state = this.level.getBlockState(offset);
                if (state.equals(this.baseState))
                {
                    this.toHarvest.add(0, offset);
                    ++currentIteration;
                    this.traverseRecursive(offset);
                }
            }
        }

        @Override
        public void tick()
        {
            BlockPos pos = this.toHarvest.pop();
            if (this.isLoaded(pos))
            {
                BlockState state = this.level.getBlockState(pos);
                if (state.equals(this.baseState))
                {
                    if (this.canHarvest.test(this.harvester))
                    {
                        int index = harvester.getInventory().getSlotWithRemainingSpace(stackConsumed);
                        if (index == -1)
                        {
                            this.invalid = true;
                        }
                        else
                        {
                            ItemStack is = this.harvester.getInventory().getItem(index);
                            if (is.getItem() instanceof BlockItem)
                            {
                                BlockState currentState = this.level.getBlockState(pos);
                                List<ItemStack> stacks = Block.getDrops(currentState, (ServerLevel) this.level, pos, level.getBlockEntity(pos));
                                BlockState toSet = ((BlockItem) is.getItem()).getBlock().defaultBlockState();

                                SoundType type = currentState.getBlock().getSoundType(state, this.level, pos, this.harvester);
                                this.level.playSound(null, pos, type.getBreakSound(), SoundSource.BLOCKS, type.getVolume(), type.getPitch());
                                this.level.setBlock(pos, toSet, 0);
                                SoundType type1 = toSet.getBlock().getSoundType(state, this.level, pos, this.harvester);
                                this.level.playSound(null, pos, type1.getBreakSound(), SoundSource.BLOCKS, type1.getVolume(), type1.getPitch());
                                for (ItemStack stack : stacks)
                                {
                                    if (!this.harvester.getInventory().add(stack))
                                    {
                                        this.harvester.drop(stack, true, false);
                                    }
                                }

                                is.shrink(1);
                                this.harvester.getMainHandItem().hurt(1, RandomSource.create(), (ServerPlayer) harvester);
                            }
                            else
                            {
                                this.invalid = true;
                            }
                        }
                    }
                    else
                    {
                        this.invalid = true;
                    }
                }
            }

            if (this.toHarvest.isEmpty())
            {
                this.invalid = true;
            }
        }
    }
}
