package com.thedrofdoctoring.synthetics.util;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class Helper {

    public static final float MIN_BOUND_POS = 0.29f;
    public static final float MAX_BOUND_POS = 0.31f;
    public static final float MIN_BOUND_NEG = 0.69f;
    public static final float MAX_BOUND_NEG = 0.71f;

    public static @Nullable <T> T retrieveDataObject(ResourceLocation location, ResourceKey<Registry<T>> registryKey, HolderGetter<T> lookup, boolean shouldThrow) {
        if(location == null) return null;
        var objectOpt = lookup.get(ResourceKey.create(registryKey, location));
        if(objectOpt.isPresent()) {
            return objectOpt.get().value();
        } else if(shouldThrow) {
            Synthetics.LOGGER.error("Unable to retrieve data registry object with resource location {}", location);
        } else {
            Synthetics.LOGGER.warn("Unable to retrieve data registry object with resource location {}", location);
        }
        return null;

    }
    public static @Nullable <T> T retrieveDataObject(ResourceLocation location, ResourceKey<Registry<T>> registryKey, HolderGetter<T> lookup) {
        return retrieveDataObject(location, registryKey, lookup, true);
    }
    public static @Nullable <T> T retrieveDataObject(String string, ResourceKey<Registry<T>> registryKey, HolderGetter<T> lookup) {
        ResourceLocation location = ResourceLocation.tryParse(string);
        return retrieveDataObject(location, registryKey, lookup);
    }
    public static @Nullable<T> T retrieveDataObject(String string, ResourceKey<Registry<T>> registryKey, HolderGetter<T> lookup, boolean shouldThrow) {
        ResourceLocation location = ResourceLocation.tryParse(string);
        return retrieveDataObject(location, registryKey, lookup, shouldThrow);
    }


    /**
     * from net.minecraft.client.gui.advancements.AdvancementEntryGui#findOptimalLines(ITextComponent, int)
     */
    public static List<FormattedText> findOptimalLines(Minecraft minecraft, @NotNull Component component, int width, int[] splits) {
        StringSplitter characterManager = minecraft.font.getSplitter();
        List<FormattedText> list = Collections.emptyList();
        float f = Float.MAX_VALUE;

        for (int i : splits) {
            List<FormattedText> list1 = characterManager.splitLines(component, width - i, Style.EMPTY);
            float f1 = Math.abs(getMaxWidth(characterManager, list1) - (float) width);
            if (f1 <= 10.0F) {
                return list1;
            }

            if (f1 < f) {
                f = f1;
                list = list1;
            }
        }

        return list;
    }

    public static float getMaxWidth(@NotNull StringSplitter splitter, @NotNull List<FormattedText> text) {
        return (float) text.stream().mapToDouble(splitter::stringWidth).max().orElse(0.0D);
    }

    // Only works for full-blocks
    public static boolean onBlockBorder(LivingEntity entity, Vec3 pos) {
        float z = (float) (pos.z - Math.floor(pos.z));
        float x = (float) (pos.x - Math.floor(pos.x));
        if( (z > MIN_BOUND_POS && z < MAX_BOUND_POS) || (z > MIN_BOUND_NEG) && (z < MAX_BOUND_NEG)) {
            return hasNearbyBlock(entity);
        } else if((x > MIN_BOUND_POS && x < MAX_BOUND_POS) || (x > MIN_BOUND_NEG) && (x < MAX_BOUND_NEG)) {
            return hasNearbyBlock(entity);
        }
        return false;
    }

    /**
     * This is an easy solution, but isn't perfect. Maybe look for something better in the future.
     * @return A 6-float array. First 3 values represent directional axis, last 3 represent translation offset
     */
    public static float[] getRotDirection(Vec3 playerPos) {
        float[] pos = new float[]{0, 1, 0, 0, 0, 0};
        float z = (float) (playerPos.z - Math.floor(playerPos.z));
        float x = (float) (playerPos.x - Math.floor(playerPos.x));
        if( (z > MIN_BOUND_POS && z < MAX_BOUND_POS) || (z > MIN_BOUND_NEG) && (z < MAX_BOUND_NEG)) {
            pos[1] = 0;
            if(z < MAX_BOUND_POS) {
                pos[5] = -0.5f;
                pos[2] = 1;
            } else {
                pos[5] = 0.5f;
                pos[2] = -1;
            }
        } else if((x > MIN_BOUND_POS && x < MAX_BOUND_POS) || (x > MIN_BOUND_NEG) && (x < MAX_BOUND_NEG)) {
            pos[1] = 0;
            if(x < MAX_BOUND_POS) {
                pos[3] = -0.5f;
                pos[0] = 1;
            } else {
                pos[0] = -1;
                pos[3] = 0.5f;
            }
        }
        return pos;
    }

    private static boolean hasNearbyBlock(LivingEntity entity) {
        AABB bb = entity.getBoundingBox().inflate(0.35, 0, 0.35);
        int mX = Mth.floor(bb.minX);
        int mY = Mth.floor(bb.minY);
        int mZ = Mth.floor(bb.minZ);
        for (int y2 = mY; y2 < bb.maxY; y2++) {
            for (int x2 = mX; x2 < bb.maxX; x2++) {
                for (int z2 = mZ; z2 < bb.maxZ; z2++) {
                    BlockPos tmp = new BlockPos(x2, y2, z2);
                    BlockState state = entity.level().getBlockState(tmp);
                    if (!state.is(BlockTags.AIR) && !state.getCollisionShape(entity.level(), tmp).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    // Below is taken from Framed Blocks, licensed under LGPL. https://github.com/XFactHD/FramedBlocks/blob/1.21/src/main/java/xfacthd/framedblocks/api/shapes/ShapeUtils.java#L34

    public static VoxelShape rotateShapeAroundY(Direction from, Direction to, VoxelShape shape)
    {
        return rotateShapeUnoptimizedAroundY(from, to, shape).optimize();
    }

    public static VoxelShape rotateShapeUnoptimizedAroundY(Direction from, Direction to, VoxelShape shape)
    {
        if (isY(from) || isY(to))
        {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to)
        {
            return shape;
        }

        List<AABB> sourceBoxes = shape.toAabbs();
        VoxelShape rotatedShape = Shapes.empty();
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (AABB box : sourceBoxes)
        {
            for (int i = 0; i < times; i++)
            {
                box = new AABB(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
            }
            rotatedShape = orUnoptimized(rotatedShape, Shapes.create(box));
        }

        return rotatedShape;
    }

    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape second)
    {
        return Shapes.joinUnoptimized(first, second, BooleanOp.OR);
    }

    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape... others)
    {
        for (VoxelShape shape : others)
        {
            first = orUnoptimized(first, shape);
        }
        return first;
    }

    public static boolean isY(Direction dir) {
        return dir.getAxis() == Direction.Axis.Y;
    }

}
